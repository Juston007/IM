package com.im.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class IMService extends Service {

	// �󶨻ص�
	@Override
	public IBinder onBind(Intent arg0) {
		return new MyBinder();
	}

	// ���ص�����
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	// ���һ���߳���ִ��
	public void addThread(Thread thread) {
		if (thread != null)
			thread.start();
	}

	// ���һ��������ִ��
	public void addMethod(IMethod i) {
		i.method();
	}

	// �Զ���Binder���� �˶����������ⲿ��ȡService����
	public class MyBinder extends Binder {
		public IMService getService() {
			return IMService.this;
		}
	}

	public interface IMethod {
		public void method();
	}
}
