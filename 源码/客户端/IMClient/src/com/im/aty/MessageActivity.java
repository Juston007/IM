package com.im.aty;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import com.im.adapter.MessageAdapter;
import com.im.db.IMDBUtil;
import com.im.model.FriendRelationModel;
import com.im.model.MessageInfoModel;
import com.im.model.OnDataChangeListener;
import com.im.model.UserInfoModel;
import com.im.net.NetworkRequest;
import com.im.net.NetworkRequestUtil;
import com.im.util.MediaUtil;
import com.im.util.Paramters;
import com.im.util.SharedPreferencesUtil;
import com.im.util.SystemBarTintManager;
import com.im.util.Util;
import com.im.view.MyDialog;
import com.im.view.ProgressDialog;
import com.im.view.ToastUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MessageActivity extends Activity {

	private String friendUid = "";
	private UserInfoModel friendInfo, meInfo;

	// Data
	private ArrayList<MessageInfoModel> msgData = new ArrayList<MessageInfoModel>();

	// View
	private ListView lvMsg;
	private EditText etInput;
	private ImageView imgPic, imgVoice;
	private ProgressDialog dialogProgress;

	// Adapter
	private MessageAdapter msgAdapter = null;

	// 页面是否关闭
	private boolean isClosed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		Intent intent = getIntent();
		friendUid = intent.getStringExtra("Uid");
		// 显示返回
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// 获取用户信息
		getUserInfo();
		// 设置标题
		Util.setActionBarTilte(getActionBar(), friendInfo.getAlias());
		// 初始化View
		initView();
		// 初始化事件
		initEvent();
		// 绑定适配器
		msgAdapter = new MessageAdapter(this, msgData,
				SharedPreferencesUtil.shared.getUid());
		lvMsg.setAdapter(msgAdapter);
		// 显示数据
		displayData();
		// 通知适配器数据源改变
		msgAdapter.notifyDataSetChanged();
		// 更新消息状态
		IMDBUtil.updateMessageStatus(meInfo.getUid(), friendInfo.getUid());
		// 增大摩擦力，减慢滑动速度
		lvMsg.setFriction(ViewConfiguration.getScrollFriction() * 6);
		Log.d("IMClient", MessageActivity.class.getCanonicalName() + ":初始化完毕");
		SystemBarTintManager.setStatusColor(this, getWindow());
	}

	private void getUserInfo() {
		friendInfo = IMDBUtil.getUserInfo(friendUid);
		meInfo = IMDBUtil.getUserInfo(SharedPreferencesUtil.shared.getUid());
	}

	private void initData() {
		ArrayList<MessageInfoModel> msg = IMDBUtil.getMessageList(
				SharedPreferencesUtil.shared.getUid(), friendUid);
		if (msg != null) {
			// 按照时间进行升序
			for (int i = 0; i < msg.size(); i++) {
				for (int j = i; j < msg.size(); j++) {
					if (msg.get(i).getTime().getTime() > msg.get(j).getTime()
							.getTime()) {
						MessageInfoModel temp = msg.get(i);
						msg.set(i, msg.get(j));
						msg.set(j, temp);
					}
				}
			}
			msgData.addAll(msg);
		}
		if (msgAdapter != null) {
			msgAdapter.notifyDataSetChanged();
		}
	}

	private void initView() {
		lvMsg = (ListView) findViewById(R.id.list_message);
		lvMsg.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		etInput = (EditText) findViewById(R.id.et_msg);
		imgPic = (ImageView) findViewById(R.id.img_Pic);
		imgVoice = (ImageView) findViewById(R.id.img_Voice);
	}

	private void initEvent() {
		etInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				findViewById(R.id.btn_Send).setEnabled(
						!arg0.toString().equals(""));
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		// 发送图片
		imgPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, 0);
			}
		});
		// 发送语音
		imgVoice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final MyDialog dialog = new MyDialog(MessageActivity.this,
						"录制声音", "点击录制即可开始录制。如果弹出录音权限授权提示框，请点击允许。", "录制", "取消",
						true, true);
				dialog.setCancelOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
						MediaUtil.stopRecord();
						File file = new File(getCacheDir()
								+ "/linshiwenjian1.3gp");
						if (file.exists())
							file.delete();
					}
				});
				dialog.setConfrimOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						TextView txt = (TextView) arg0;
						File file = new File(getCacheDir()
								+ "/linshiwenjian1.3gp");
						Log.d("IMClient", file.toString());
						if (txt.getText().toString().equals("录制")) {
							txt.setText("停止录制");
							try {
								MediaUtil.startRecord(file.toString());
							} catch (Exception e) {
								Log.d("IMClient", e.getMessage());
								ToastUtil.makeText(MessageActivity.this,
										e.getMessage(), Toast.LENGTH_SHORT);
							}
						} else {
							dialog.dismiss();
							MediaUtil.stopRecord();
							dialogProgress = new ProgressDialog(
									MessageActivity.this, "发送中...");
							dialogProgress.show();
							// 获取base64str
							FileInputStream fis = null;
							try {
								fis = new FileInputStream(file);
								byte[] buffer = new byte[(int) file.length()];
								fis.read(buffer);
								String base64str = Util
										.ToBaseStringFromByte(buffer);
								MessageInfoModel msginfo = new MessageInfoModel(
										meInfo.getUid(), friendUid, meInfo
												.getUid()
												+ "FenGe"
												+ friendUid
												+ new Date().getTime(),
										new Date(),
										MessageInfoModel.MSG_TYPE_SOUND, true,
										base64str);
								IMDBUtil.insertMessageInfo(meInfo.getUid(),
										msginfo, getCacheDir().toString());
								NetworkRequestUtil.SendMessage(
										Paramters.ServerHostAddress,
										meInfo.getUid(),
										SharedPreferencesUtil.shared.getToken(),
										msginfo, new Handler() {
											@Override
											public void handleMessage(
													Message msg) {
												dialogProgress.dismiss();
												if (msg.what != NetworkRequest.REQUEST_SUCCESS) {
													ToastUtil
															.makeText(
																	MessageActivity.this,
																	msg.obj.toString(),
																	Toast.LENGTH_SHORT);
												}
											}
										}, getCacheDir().toString());
							} catch (Exception e) {
								Log.d("IMClient", e.getMessage());
								if (fis != null) {
									try {
										fis.close();
									} catch (Exception e1) {
										Log.d("IMClient", e.getMessage());
									}
								}
								dialogProgress.dismiss();
							} finally {
								file.delete();
							}
						}
					}
				});
				dialog.show();
			}
		});
		// 监听信息变化
		IMDBUtil.setOnMessageChangeListener(new OnDataChangeListener() {

			@Override
			public void OnDataChnage() {
				displayData();
				if (!isClosed)
					IMDBUtil.updateMessageStatus(meInfo.getUid(),
							friendInfo.getUid());
				Log.d("IMClient", "OnDataChnage");
			}
		});
		// 发送文字
		findViewById(R.id.btn_Send).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String str = etInput.getText().toString();
				if (str.equals("")) {
					ToastUtil.makeText(MessageActivity.this, "请输入内容",
							Toast.LENGTH_SHORT);
					return;
				}
				MessageInfoModel msginfo = new MessageInfoModel(
						meInfo.getUid(), friendUid, meInfo.getUid() + "FenGe"
								+ friendUid + new Date().getTime(), new Date(),
						MessageInfoModel.MSG_TYPE_TEXT, true, str);
				etInput.setText("");
				NetworkRequestUtil.SendMessage(Paramters.ServerHostAddress,
						SharedPreferencesUtil.shared.getUid(),
						SharedPreferencesUtil.shared.getToken(), msginfo,
						new Handler() {
							@Override
							public void handleMessage(Message msg) {
								if (msg.what != NetworkRequest.REQUEST_SUCCESS)
									ToastUtil.makeText(MessageActivity.this,
											msg.obj.toString(),
											Toast.LENGTH_SHORT);
								else
									IMDBUtil.setDisplay(new FriendRelationModel(
											meInfo.getUid(), friendUid, true));
							}
						}, getCacheDir().toString());
			}
		});
	}

	private void displayData() {
		msgData.clear();
		initData();
		Log.d("TestTag", "displayData");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			NetworkRequestUtil.getMessage(
					SharedPreferencesUtil.shared.getUid(),
					Paramters.ServerHostAddress,
					SharedPreferencesUtil.shared.getToken(), friendUid, false,
					new Handler() {
						@Override
						public void handleMessage(Message msg) {
							Log.d("IMClient", msg.obj.toString());
						}
					}, getCacheDir().toString());
			break;
		case android.R.id.home:
			this.finish();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			dialogProgress = new ProgressDialog(MessageActivity.this, "发送中...");
			dialogProgress.show();
			ByteArrayOutputStream bos = null;
			InputStream is = null;
			try {
				// 将Uri资源转换为byte[]
				bos = new ByteArrayOutputStream();
				is = getContentResolver().openInputStream(data.getData());
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				bitmap.compress(CompressFormat.JPEG, 100, bos);
				bitmap.recycle();
				byte[] buffer = bos.toByteArray();
				// 转换为base64字符串
				String base64 = Util.ToBaseStringFromByte(buffer);
				// 创建Msg对象
				MessageInfoModel msg = new MessageInfoModel(meInfo.getUid(),
						friendUid, meInfo.getUid() + "FenGe" + friendUid
								+ new Date().getTime(), new Date(),
						MessageInfoModel.MSG_TYPE_IMAGE, true, base64);
				// 发送图片
				NetworkRequestUtil.SendMessage(Paramters.ServerHostAddress,
						SharedPreferencesUtil.shared.getUid(),
						SharedPreferencesUtil.shared.getToken(), msg,
						new Handler() {
							@Override
							public void handleMessage(Message msg) {
								dialogProgress.dismiss();
								if (msg.what != NetworkRequest.REQUEST_SUCCESS) {
									ToastUtil.makeText(MessageActivity.this,
											msg.obj.toString(),
											Toast.LENGTH_SHORT);
								}
							}
						}, getCacheDir().toString());

			} catch (Exception e) {
				Log.d("IMClient", e.getMessage());
				dialogProgress.dismiss();
				ToastUtil.makeText(MessageActivity.this, e.getMessage(),
						Toast.LENGTH_SHORT);
			} finally {
				try {
					if (bos != null)
						bos.close();
					if (is != null)
						is.close();
				} catch (IOException e) {
					Log.d("IMClient", e.getMessage());
				}

			}
		} else {
			ToastUtil
					.makeText(MessageActivity.this, "用户取消", Toast.LENGTH_SHORT);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		MediaUtil.stopPlay();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isClosed = true;
	}
}
