package com.im.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class IMService extends Service {

	// 绑定回调
	@Override
	public IBinder onBind(Intent arg0) {
		return new MyBinder();
	}

	// 解绑回调方法
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	// 添加一个线程来执行
	public void addThread(Thread thread) {
		if (thread != null)
			thread.start();
	}

	// 添加一个方法来执行
	public void addMethod(IMethod i) {
		i.method();
	}

	// 自定义Binder对象 此对象用来让外部获取Service对象
	public class MyBinder extends Binder {
		public IMService getService() {
			return IMService.this;
		}
	}

	public interface IMethod {
		public void method();
	}
}
