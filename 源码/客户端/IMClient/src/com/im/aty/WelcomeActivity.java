package com.im.aty;

import com.im.db.SQLiteDBUtil;
import com.im.util.Paramters;
import com.im.util.SharedPreferencesUtil;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

@SuppressLint("HandlerLeak")
public class WelcomeActivity extends Activity {

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 判断要进入哪一个界面 如果未登入用户那么将会切换到登入界面 否则切换到主页面
			boolean islogin = SharedPreferencesUtil.shared.isLogin();
			Intent intent = new Intent(WelcomeActivity.this,
					islogin ? MainFrameActivity.class : LoginActivity.class);
			startActivity(intent);
			// 将本aty在aty栈当中出栈
			WelcomeActivity.this.finish();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_welcome);
		// 初始化
		init();
		// 测试方法
		testMethod();
		// 延时2秒
		new Thread() {
			public void run() {
				try {
					Thread.sleep(2000);
					handler.sendEmptyMessage(0);
				} catch (Exception ex) {
					Log.d("IMClient", WelcomeActivity.class.getCanonicalName()
							+ ":" + ex.getMessage());
				}
			};
		}.start();
		Log.d("IMClient", WelcomeActivity.class.getCanonicalName() + ":初始化完毕");
	}

	// 初始化相关
	private void init() {
		// 初始化SharedPreferences相关
		SharedPreferencesUtil sharedutil = new SharedPreferencesUtil();
		sharedutil.setSharedPreferces(getSharedPreferences("Preferences",
				Context.MODE_PRIVATE));
		
		// 将操作类对象赋值到静态变量当中 方便调用
		SharedPreferencesUtil.shared = sharedutil;

		// 初始化基本参数
		if (sharedutil.readValue("ServerHostAddress", "").equals("")) {
			Log.d("IMClient", "InitParamter");
			sharedutil.writeValue("ServerHostAddress",
					Paramters.ServerHostAddress);
			sharedutil.writeValue("ConnectionTimeOut",
					String.valueOf(Paramters.ConnectionTimeOut));
			sharedutil.writeValue("PollingTimeSpan",
					String.valueOf(Paramters.PollingTimeSpan));
			sharedutil.writeValue("ReadTimeOut",
					String.valueOf(Paramters.ReadTimeOut));
		}
		
		// 读取基本参数
		Paramters.ServerHostAddress = sharedutil.readValue("ServerHostAddress",
				String.valueOf(Paramters.ServerHostAddress));
		Paramters.PollingTimeSpan = Integer.parseInt(sharedutil.readValue(
				"PollingTimeSpan", String.valueOf(Paramters.PollingTimeSpan)));
		Paramters.ConnectionTimeOut = Integer.parseInt(sharedutil.readValue(
				"ConnectionTimeOut",
				String.valueOf(Paramters.ConnectionTimeOut)));
		Paramters.ReadTimeOut = Integer.parseInt(sharedutil.readValue(
				"ReadTimeOut", String.valueOf(Paramters.ReadTimeOut)));
		
		// 初始化数据库
		SQLiteDBUtil.getInterface(WelcomeActivity.this, "im.db", null, 1);
	}

	// 测试方法
	private void testMethod() {}
}
