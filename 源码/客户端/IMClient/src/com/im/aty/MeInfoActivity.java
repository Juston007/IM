package com.im.aty;

import java.io.File;
import java.io.FileOutputStream;

import com.im.db.IMDBUtil;
import com.im.model.UserInfoModel;
import com.im.net.NetworkRequest;
import com.im.net.NetworkRequestUtil;
import com.im.util.Paramters;
import com.im.util.QRUtil;
import com.im.util.SharedPreferencesUtil;
import com.im.util.SystemBarTintManager;
import com.im.util.Util;
import com.im.view.MyDialog;
import com.im.view.ProgressDialog;
import com.im.view.ToastUtil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class MeInfoActivity extends Activity implements OnClickListener {

	// View
	private TextView txtUid, txtAlias, txtSex, txtBirthDay, txtRegTime;
	private ImageView imgFace;
	private MyDialog dialog = null;
	private UserInfoModel model;
	private Bitmap face, head;

	// 点击事件
	private OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			dialog.dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置ActionBar标题
		Util.setActionBarTilte(getActionBar(), "个人资料");
		// 返回按钮
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_me_info);
		// 初始化View
		initView();
		// 展示数据
		displayData();
		// 初始化事件
		initEvent();
		Log.d("IMClient", MeInfoActivity.class.getCanonicalName() + ":初始化完毕");
		SystemBarTintManager.setStatusColor(this, getWindow());
	}

	private void initView() {
		txtUid = (TextView) findViewById(R.id.txt_info_uid);
		txtAlias = (TextView) findViewById(R.id.txt_info_alias);
		txtSex = (TextView) findViewById(R.id.txt_info_sex);
		txtBirthDay = (TextView) findViewById(R.id.txt_info_birthday);
		txtRegTime = (TextView) findViewById(R.id.txt_info_regtime);
		imgFace = (ImageView) findViewById(R.id.img_info_face);
	}

	private void initEvent() {
		findViewById(R.id.ll_info_alias).setOnClickListener(this);
		findViewById(R.id.ll_info_sex).setOnClickListener(this);
		findViewById(R.id.ll_info_birthday).setOnClickListener(this);
		findViewById(R.id.btn_Update).setOnClickListener(this);
		findViewById(R.id.ll_info_face).setOnClickListener(this);
		findViewById(R.id.ll_qr).setOnClickListener(this);
	}

	private void displayData() {
		String uid = SharedPreferencesUtil.shared.getUid();
		model = IMDBUtil.getUserInfo(uid);
		txtUid.setText(model.getUid());
		txtAlias.setText(model.getAlias());
		txtSex.setText(model.getSex() ? "男" : "女");
		txtBirthDay.setText(Util.ToDateStringFromLong(model.getBirthDay()
				.getTime()));
		txtRegTime.setText(Util.ToDateStringFromLong(model.getRegTime()
				.getTime()));
		face = BitmapFactory.decodeFile(model.getFacePath());
		imgFace.setImageBitmap(face);
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ll_qr:
			String path = Environment.getExternalStorageDirectory() + "/"
					+ "temp.jpg";
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			try {
				Bitmap bitmap = QRUtil.createQRCode("Uid:" + model.getUid(),
						600);
				if (bitmap == null) {
					ToastUtil.makeText(MeInfoActivity.this, "生成失败!",
							Toast.LENGTH_SHORT, false);
					return;
				}
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				bitmap.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
				Intent imgintent = new Intent(this, ImageActivity.class);
				imgintent.putExtra("Image", path);
				startActivity(imgintent);
			} catch (Exception e1) {
				Log.d("IMClient", e1.getMessage());
			}
			break;
		// 昵称
		case R.id.ll_info_alias:
			dialog = new MyDialog(this, "昵称", "确认", "取消", txtAlias.getText()
					.toString().trim(), true);
			dialog.setCancelOnClickListener(onclick);
			dialog.setConfrimOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String str = dialog.getEdText();
					if (str.equals("")) {
						ToastUtil.makeText(MeInfoActivity.this, "不可为空",
								Toast.LENGTH_SHORT);
					} else {
						if (str.length() >= 20) {
							ToastUtil.makeText(MeInfoActivity.this,
									"昵称长度不可以大于等于20", Toast.LENGTH_SHORT);
						} else {
							txtAlias.setText(str);
							dialog.dismiss();
						}
					}
				}
			});
			dialog.show();
			break;
		// 性别
		case R.id.ll_info_sex:
			dialog = new MyDialog(this, "性别", "当前性别："
					+ txtSex.getText().toString().trim(), "男", "女", true, true);
			dialog.setConfrimOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					txtSex.setText("男");
					dialog.dismiss();
				}
			});
			dialog.setCancelOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					txtSex.setText("女");
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		// 生日
		case R.id.ll_info_birthday:
			dialog = new MyDialog(this, "生日", "确认", "取消", txtBirthDay.getText()
					.toString().trim(), true);
			dialog.setCancelOnClickListener(onclick);
			dialog.setConfrimOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String str = dialog.getEdText();
					if (str.equals("")) {
						ToastUtil.makeText(MeInfoActivity.this, "不可为空",
								Toast.LENGTH_SHORT);
					} else {
						try {
							Util.getDateFromDateString(str);
							txtBirthDay.setText(str);
							dialog.dismiss();
						} catch (Exception ex) {
							ToastUtil.makeText(MeInfoActivity.this, "日期格式错误",
									Toast.LENGTH_SHORT);
						}
					}
				}
			});
			dialog.show();
			break;
		// 更新资料
		case R.id.btn_Update:
			final ProgressDialog progressdialog = new ProgressDialog(this,
					"正在请求中...");
			progressdialog.show();
			try {
				UserInfoModel model = new UserInfoModel(txtUid.getText()
						.toString(), txtAlias.getText().toString(),
						MeInfoActivity.this.model.getFacePath(),
						Util.getDateFromDateString(txtRegTime.getText()
								.toString()),
						Util.getDateFromDateString(txtBirthDay.getText()
								.toString()), txtSex.getText().toString()
								.equals("男"));
				NetworkRequestUtil.UpdateUserInfo(Paramters.ServerHostAddress,
						SharedPreferencesUtil.shared.getToken(), model,
						new Handler() {
							@Override
							public void handleMessage(Message msg) {
								progressdialog.dismiss();
								if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
									ToastUtil
											.makeText(MeInfoActivity.this,
													"更新资料成功！",
													Toast.LENGTH_SHORT, true);
								} else {
									ToastUtil.makeText(MeInfoActivity.this,
											msg.obj.toString(),
											Toast.LENGTH_SHORT, false);
								}
							}
						});
			} catch (Exception e) {
				Log.d("IMClient", e.getMessage());
			}
			break;
		case R.id.ll_info_face:
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, 0);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 选择图片的Aty回调方法
	@SuppressLint("HandlerLeak")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data == null)
		{
			ToastUtil.makeText(this, "错误！无法获取结果！", Toast.LENGTH_LONG,false);
			return;
		}
		switch (requestCode) {
		case 0:
			// 获取成功剪切图片，否则弹出提示
			if (resultCode == RESULT_OK) {
				cropImage(data.getData());
			} else
				ToastUtil.makeText(this, "用户取消", Toast.LENGTH_SHORT);
			break;
		case 1:
			// 获取图片
			Bundle bundle = data.getExtras();
			head = bundle.getParcelable("data");
			final ProgressDialog dialog = new ProgressDialog(this, "正在上传中...");
			dialog.show();
			// 上传到服务器
			NetworkRequestUtil.UpdateFace(Paramters.ServerHostAddress,
					SharedPreferencesUtil.shared.getToken(), head, this
							.getCacheDir().toString(),
					SharedPreferencesUtil.shared.getUid(), new Handler() {
						@Override
						public void handleMessage(Message msg) {
							dialog.dismiss();
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								ToastUtil.makeText(MeInfoActivity.this,
										"更新头像成功！", Toast.LENGTH_SHORT, true);
								face = head;
								imgFace.setImageBitmap(face);
							} else {
								ToastUtil.makeText(MeInfoActivity.this,
										msg.obj.toString(), Toast.LENGTH_SHORT,
										false);
								head.recycle();
							}
						}
					});
			break;

		default:
			break;
		}
	}

	private void cropImage(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (face != null)
			face.recycle();
	}
}
