package com.im.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

//SharedPreferences������
public class SharedPreferencesUtil {
	private SharedPreferences mSharedPreferences = null;
	// ��������� ��̬�������
	public static SharedPreferencesUtil shared = null;

	// ���ö���
	public void setSharedPreferces(SharedPreferences arg) {
		mSharedPreferences = arg;
	}

	// д��ֵ
	public void writeValue(String key, String value) {
		if (mSharedPreferences == null)
			return;
		Editor editor = mSharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	// ��ȡֵ
	public String readValue(String key, String defaultvalue) {
		if (key.equals("") || mSharedPreferences == null)
			return null;
		return mSharedPreferences.getString(key, defaultvalue);
	}

	// ɾ��ֵ
	public void removeValue(String key) {
		if (mSharedPreferences == null)
			return;
		Editor editor = mSharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}

	// ���ȫ��ֵ
	public void clearValue() {
		if (mSharedPreferences == null)
			return;
		Editor editor = mSharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

	// �Ƿ��״ν���
	public boolean isLogin() {
		if (mSharedPreferences == null)
			return false;
		return mSharedPreferences.contains("isLogin");
	}

	// ���õ�����û�
	public void setLoginUser(String user, String token) {
		writeValue(user, token);
		writeValue("isLogin", user);
	}

	// ��ȡToken
	public String getToken() {
		String uid = readValue("isLogin", "uid");
		if (uid != "uid")
			return readValue(uid, "token");
		return null;
	}

	// ��ȡUid
	public String getUid() {
		String uid = readValue("isLogin", "uid");
		return uid;
	}

	// �ǳ��˺�
	public void removeUser() {
		clearValue();
	}
}
