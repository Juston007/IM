package com.im.aty;

import com.im.util.SystemBarTintManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.app.Activity;

public class TextBrowseActivity extends Activity {

	private TextView txtContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_browse);
		//����ActionBar
		getActionBar().hide();
		//������ʾ������
		txtContent = (TextView) findViewById(R.id.txt_browse_content);
		txtContent.setText(getIntent().getStringExtra("Content"));
		Log.d("IMClient", TextBrowseActivity.class.getCanonicalName() + ":��ʼ�����");
		SystemBarTintManager.setStatusColor(this, getWindow());
	}
}
