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

	// ����¼�
	private OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			dialog.dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����ActionBar����
		Util.setActionBarTilte(getActionBar(), "��������");
		// ���ذ�ť
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_me_info);
		// ��ʼ��View
		initView();
		// չʾ����
		displayData();
		// ��ʼ���¼�
		initEvent();
		Log.d("IMClient", MeInfoActivity.class.getCanonicalName() + ":��ʼ�����");
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
		txtSex.setText(model.getSex() ? "��" : "Ů");
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
					ToastUtil.makeText(MeInfoActivity.this, "����ʧ��!",
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
		// �ǳ�
		case R.id.ll_info_alias:
			dialog = new MyDialog(this, "�ǳ�", "ȷ��", "ȡ��", txtAlias.getText()
					.toString().trim(), true);
			dialog.setCancelOnClickListener(onclick);
			dialog.setConfrimOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String str = dialog.getEdText();
					if (str.equals("")) {
						ToastUtil.makeText(MeInfoActivity.this, "����Ϊ��",
								Toast.LENGTH_SHORT);
					} else {
						if (str.length() >= 20) {
							ToastUtil.makeText(MeInfoActivity.this,
									"�ǳƳ��Ȳ����Դ��ڵ���20", Toast.LENGTH_SHORT);
						} else {
							txtAlias.setText(str);
							dialog.dismiss();
						}
					}
				}
			});
			dialog.show();
			break;
		// �Ա�
		case R.id.ll_info_sex:
			dialog = new MyDialog(this, "�Ա�", "��ǰ�Ա�"
					+ txtSex.getText().toString().trim(), "��", "Ů", true, true);
			dialog.setConfrimOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					txtSex.setText("��");
					dialog.dismiss();
				}
			});
			dialog.setCancelOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					txtSex.setText("Ů");
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		// ����
		case R.id.ll_info_birthday:
			dialog = new MyDialog(this, "����", "ȷ��", "ȡ��", txtBirthDay.getText()
					.toString().trim(), true);
			dialog.setCancelOnClickListener(onclick);
			dialog.setConfrimOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String str = dialog.getEdText();
					if (str.equals("")) {
						ToastUtil.makeText(MeInfoActivity.this, "����Ϊ��",
								Toast.LENGTH_SHORT);
					} else {
						try {
							Util.getDateFromDateString(str);
							txtBirthDay.setText(str);
							dialog.dismiss();
						} catch (Exception ex) {
							ToastUtil.makeText(MeInfoActivity.this, "���ڸ�ʽ����",
									Toast.LENGTH_SHORT);
						}
					}
				}
			});
			dialog.show();
			break;
		// ��������
		case R.id.btn_Update:
			final ProgressDialog progressdialog = new ProgressDialog(this,
					"����������...");
			progressdialog.show();
			try {
				UserInfoModel model = new UserInfoModel(txtUid.getText()
						.toString(), txtAlias.getText().toString(),
						MeInfoActivity.this.model.getFacePath(),
						Util.getDateFromDateString(txtRegTime.getText()
								.toString()),
						Util.getDateFromDateString(txtBirthDay.getText()
								.toString()), txtSex.getText().toString()
								.equals("��"));
				NetworkRequestUtil.UpdateUserInfo(Paramters.ServerHostAddress,
						SharedPreferencesUtil.shared.getToken(), model,
						new Handler() {
							@Override
							public void handleMessage(Message msg) {
								progressdialog.dismiss();
								if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
									ToastUtil
											.makeText(MeInfoActivity.this,
													"�������ϳɹ���",
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

	// ѡ��ͼƬ��Aty�ص�����
	@SuppressLint("HandlerLeak")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data == null)
		{
			ToastUtil.makeText(this, "�����޷���ȡ�����", Toast.LENGTH_LONG,false);
			return;
		}
		switch (requestCode) {
		case 0:
			// ��ȡ�ɹ�����ͼƬ�����򵯳���ʾ
			if (resultCode == RESULT_OK) {
				cropImage(data.getData());
			} else
				ToastUtil.makeText(this, "�û�ȡ��", Toast.LENGTH_SHORT);
			break;
		case 1:
			// ��ȡͼƬ
			Bundle bundle = data.getExtras();
			head = bundle.getParcelable("data");
			final ProgressDialog dialog = new ProgressDialog(this, "�����ϴ���...");
			dialog.show();
			// �ϴ���������
			NetworkRequestUtil.UpdateFace(Paramters.ServerHostAddress,
					SharedPreferencesUtil.shared.getToken(), head, this
							.getCacheDir().toString(),
					SharedPreferencesUtil.shared.getUid(), new Handler() {
						@Override
						public void handleMessage(Message msg) {
							dialog.dismiss();
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								ToastUtil.makeText(MeInfoActivity.this,
										"����ͷ��ɹ���", Toast.LENGTH_SHORT, true);
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
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
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
