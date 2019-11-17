package com.im.aty;

import java.io.File;
import java.util.ArrayList;

import com.google.zxing.Result;
import com.im.adapter.ViewPagerAdapter;
import com.im.db.IMDBUtil;
import com.im.model.EventInfoModel;
import com.im.model.FriendRelationModel;
import com.im.net.NetworkRequest;
import com.im.net.NetworkRequestUtil;
import com.im.service.IMService;
import com.im.service.IMService.MyBinder;
import com.im.util.Paramters;
import com.im.util.QRUtil;
import com.im.util.SharedPreferencesUtil;
import com.im.util.SystemBarTintManager;
import com.im.util.Util;
import com.im.view.MyDialog;
import com.im.view.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MainFrameActivity extends FragmentActivity implements
		OnClickListener {

	// Handler
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				MainFrameActivity.beOverdue = true;
				SharedPreferencesUtil.shared.removeUser();
				dialog = new MyDialog(MainFrameActivity.this, "授权过期",
						"您的授权已经过期，请您重新登入重新获取授权！", "重新登入", "退出", true, true);
				// 处理确定事件
				dialog.setConfrimOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						MainFrameActivity.this.finish();
						dialog.dismiss();
						startActivity(new Intent(MainFrameActivity.this,
								LoginActivity.class));
					}
				});
				// 处理取消事件
				dialog.setCancelOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						MainFrameActivity.this.finish();
						dialog.dismiss();
					}
				});
				// 展示Dialog
				dialog.show();
			} else {
				// 请求一遍好友
				getFriend();
			}
		};
	};

	// View
	private ViewPager vp_Pager;
	private ImageView img_Msg, img_Contacts, img_Me;
	private TextView txt_Msg, txt_Contacts, txt_Me;
	private MsgActivity msgAty = new MsgActivity();
	private ContactsActivity contactsAty = new ContactsActivity();
	private MeActivity meAty = new MeActivity();
	private MyDialog dialog = null;

	// Adapter
	private ViewPagerAdapter adapter_ViewPager;

	// Data
	private ArrayList<Fragment> list = new ArrayList<Fragment>();

	// IMService 后台服务 保持与服务器的连接
	private IMService imService = null;
	private ServiceConnection serviceConnection = null;

	// 授权是否过期
	public static volatile boolean beOverdue = false;
	// 本页面是否关闭 如果关闭那么将停止轮询
	public volatile boolean isClosed = false;
	// 是否首次打开 避免重复添加轮询线程
	private static volatile boolean isFirst = true;

	// 路径
	private String path;
	private File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉logo
		getActionBar().setDisplayShowHomeEnabled(false);
		// 设置actionbar标题
		Util.setActionBarTilte(getActionBar(), "消息");
		setContentView(R.layout.activity_main_frame);
		// 初始化
		initData();
		initView();
		initEvent();
		initServiceConnection();
		// 授权过期设置为false
		MainFrameActivity.beOverdue = false;
		// 临时路径
		path = Environment.getExternalStorageDirectory() + "/" + "temp.jpg";
		file = new File(path);
		// 打印Log
		Log.d("IMClient", MainFrameActivity.class.getCanonicalName() + ":初始化完毕");
		SystemBarTintManager.setStatusColor(this, getWindow());
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 绑定服务
		bindService();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 解绑服务
		unbindService(serviceConnection);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 停止轮询 并关闭Dialog避免窗口溢出
		MainFrameActivity.beOverdue = true;
		if (dialog != null)
			dialog.dismiss();
		isClosed = true;
	}

	private void bindService() {
		// MainFrameActivity与Service绑定，当MainFrameActivity销毁，Service也会被销毁
		Intent intent = new Intent(MainFrameActivity.this, IMService.class);
		bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
	}

	private void initData() {
		// 初始化数据源
		list.add(msgAty);
		list.add(contactsAty);
		list.add(meAty);
	}

	// 初始化View
	private void initView() {
		vp_Pager = (ViewPager) findViewById(R.id.vp_main_Pager);
		img_Contacts = (ImageView) findViewById(R.id.img_contacts);
		img_Msg = (ImageView) findViewById(R.id.img_msg);
		img_Me = (ImageView) findViewById(R.id.img_me);
		txt_Contacts = (TextView) findViewById(R.id.txt_contacts);
		txt_Me = (TextView) findViewById(R.id.txt_me);
		txt_Msg = (TextView) findViewById(R.id.txt_msg);
		// 为ViewPager绑定适配器
		adapter_ViewPager = new ViewPagerAdapter(list,
				getSupportFragmentManager());
		vp_Pager.setAdapter(adapter_ViewPager);
		// 同时加载三个页面
		vp_Pager.setOffscreenPageLimit(2);
	}

	// 初始化回调
	private void initEvent() {
		findViewById(R.id.ll_msg).setOnClickListener(this);
		findViewById(R.id.ll_contacts).setOnClickListener(this);
		findViewById(R.id.ll_me).setOnClickListener(this);
		// 监听ViewPager变化
		vp_Pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// 设置每个Tab显示的图片
				img_Contacts
						.setImageResource(R.drawable.tabbar_contacts_noselect);
				img_Me.setImageResource(R.drawable.tabbar_me_noselect);
				img_Msg.setImageResource(R.drawable.tabbar_msg_noselect);
				// 设置字体颜色
				txt_Contacts.setTextColor(0xFFADADAD);
				txt_Me.setTextColor(0xFFADADAD);
				txt_Msg.setTextColor(0xFFADADAD);
				switch (arg0) {
				case 0:
					img_Msg.setImageResource(R.drawable.tabbar_msg_select);
					txt_Msg.setTextColor(0xFF09BB07);
					Util.setActionBarTilte(getActionBar(), "消息");
					break;
				case 1:
					img_Contacts
							.setImageResource(R.drawable.tabbar_contacts_select);
					txt_Contacts.setTextColor(0xFF09BB07);
					Util.setActionBarTilte(getActionBar(), "联系人");
					break;
				case 2:
					img_Me.setImageResource(R.drawable.tabbar_me_select);
					txt_Me.setTextColor(0xFF09BB07);
					Util.setActionBarTilte(getActionBar(), "我");
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// 监听Tab 点击Tab跳转到对应界面
		switch (arg0.getId()) {
		case R.id.ll_msg:
			vp_Pager.setCurrentItem(0);
			break;
		case R.id.ll_contacts:
			vp_Pager.setCurrentItem(1);
			break;
		case R.id.ll_me:
			vp_Pager.setCurrentItem(2);
			break;
		default:
			break;
		}
	}

	private void initServiceConnection() {
		serviceConnection = new ServiceConnection() {

			// 断开连接回调
			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				Log.d("IMClient", "onServiceDisconnected");
			}

			// 连接回调
			@Override
			public void onServiceConnected(ComponentName arg0, IBinder arg1) {
				Log.d("IMClient", "onServiceConnected");
				if (!isFirst)
					return;
				isFirst = false;
				MyBinder binder = (MyBinder) arg1;
				imService = binder.getService();
				final Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						// 请求服务器
						NetworkRequestUtil.GetUnHandlerEvent(
								Paramters.ServerHostAddress,
								SharedPreferencesUtil.shared.getToken(),
								new Handler() {
									public void handleMessage(
											android.os.Message msg) {
										// 如果授权未过期
										if (!MainFrameActivity.beOverdue)
											// 如果请求成功
											if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
												@SuppressWarnings("unchecked")
												// 获取未处理事件列表
												ArrayList<EventInfoModel> events = (ArrayList<EventInfoModel>) msg.obj;
												// 处理未处理事件
												if (events != null) {
													getData(events);
													for (int i = 0; i < events
															.size(); i++) {
														Log.d("IMClient",
																"type:"
																		+ events.get(
																				i)
																				.getMsgType());
													}
												} else {
													Log.d("IMClient", "没有未处理事件");
												}
											} else {
												if (msg.obj.toString().equals(
														"请求非法")) {
													mHandler.sendEmptyMessage(0);
												}
												Log.d("IMClient",
														msg.obj.toString());
											}
									};
								});
					}
				};
				// 添加到服务执行
				imService.addThread(new Thread() {
					@Override
					public void run() {
						while (!MainFrameActivity.beOverdue) {
							if (isClosed) {
								return;
							}
							try {
								Thread.sleep(Paramters.PollingTimeSpan);
								handler.sendEmptyMessage(0);
							} catch (Exception e) {
								Log.d("IMClient", e.getMessage());
							}
						}
					}
				});
			}
		};
	}

	@SuppressLint("HandlerLeak")
	private void getMsg() {
		final ArrayList<FriendRelationModel> relation = IMDBUtil
				.getFriendList(SharedPreferencesUtil.shared.getUid());
		if (relation != null) {
			for (int j = 0; j < relation.size(); j++) {
				// 根据好友列表获取一遍信息
				final String frienduid = relation.get(j).getFriendUid();
				NetworkRequestUtil.getMessage(
						SharedPreferencesUtil.shared.getUid(),
						Paramters.ServerHostAddress,
						SharedPreferencesUtil.shared.getToken(), frienduid,
						false, new Handler() {
							@Override
							public void handleMessage(Message msg) {
								Log.d("IMClient",
										"getMsg:" + msg.obj.toString());
							}
						}, getCacheDir().toString());
			}
		}

	}

	@SuppressLint("HandlerLeak")
	private void getFriend() {
		Log.d("IMClient", "getFriend");
		NetworkRequestUtil.getFriendList(SharedPreferencesUtil.shared.getUid(),
				Paramters.ServerHostAddress,
				SharedPreferencesUtil.shared.getToken(), new Handler() {
					@Override
					public void handleMessage(Message msg) {
						Log.d("IMClient", "getFriend:" + msg.obj.toString());
					}
				});
	}

	private void getData(ArrayList<EventInfoModel> events) {
		// 根据消息类型去请求数据
		for (int i = 0; i < events.size(); i++) {
			EventInfoModel model = events.get(i);
			switch (model.getMsgType()) {
			case EventInfoModel.EVENT_FRIEND_CHANGE:
				mHandler.sendEmptyMessage(1);
				break;
			case EventInfoModel.EVENT_MESSAGE:
				// 获取未获取过得信息
				getMsg();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_frame, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 跳转到拍照界面获取结果并解析二维码
		if (file.exists()) {
			file.delete();
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
		startActivityForResult(intent, 0);
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg1 == RESULT_OK) {
			if (new File(path).exists()) {
				// 解析图片
				Result result = QRUtil.parseQRcodeBitmap(path);
				if (result != null) {
					String resultStr = result.getText();
					String[] arr = resultStr.split(":");
					if (arr.length == 2) {
						if (arr[0].equals("Uid")) {
							String uid = arr[1].toString().trim();
							Intent intent = new Intent(this,
									UserInfoActivity.class);
							intent.putExtra("Uid", uid);
							startActivity(intent);
						} else {
							ToastUtil.makeText(this, "不合法的二维码！",
									Toast.LENGTH_LONG, false);
						}
					} else {
						ToastUtil.makeText(this, "不合法的二维码！", Toast.LENGTH_LONG,
								false);
					}
				} else {
					ToastUtil.makeText(this, "解析失败，请尽量拍清楚！", Toast.LENGTH_LONG,
							false);
				}
			} else {
				ToastUtil.makeText(this, "异常错误，无法完成识别！", Toast.LENGTH_LONG,
						false);
			}
		}
	}
}
