package com.im.aty;

import com.im.util.SystemBarTintManager;
import com.im.view.ProgressDialog;
import com.im.view.ToastUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageActivity extends Activity {

	private ImageView imgContent;
	private volatile Bitmap img;

	ProgressDialog dialog = null;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (dialog != null)
				dialog.dismiss();
			imgContent.setImageBitmap(img);
		};
	};

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		// ����ActionBar
		getActionBar().hide();
		// ��ʼ��View
		imgContent = (ImageView) findViewById(R.id.img_browse_content);
		// �������ؿ�
		dialog = new ProgressDialog(this, "������...");
		dialog.show();
		// ��ȡͼƬ·��
		final String data = getIntent().getStringExtra("Image");
		// ��ȡ�ļ�
		new Thread() {
			public void run() {
				img = BitmapFactory.decodeFile(data);
				handler.sendEmptyMessage(0);
			};
		}.start();
		// ��ʼ���¼�
		final Handler shandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ToastUtil.makeText(ImageActivity.this, "�Ѿ����浽ϵͳ���Ŀ¼���У�",
						Toast.LENGTH_SHORT);
			}
		};
		// ���浽���
		findViewById(R.id.btn_Save).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new Thread() {
					public void run() {
						MediaStore.Images.Media.insertImage(
								getContentResolver(), img, "�����ͼƬ", "photo");
						shandler.sendEmptyMessage(0);
					};
				}.start();
			}
		});
		Log.d("IMClient", ImageActivity.class.getCanonicalName() + ":��ʼ�����");
		SystemBarTintManager.setStatusColor(this, getWindow());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (img != null)
			img.recycle();
	}
}
