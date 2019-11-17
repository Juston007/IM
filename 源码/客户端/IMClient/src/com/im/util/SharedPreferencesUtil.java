package com.im.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

//SharedPreferences工具类
public class SharedPreferencesUtil {
	private SharedPreferences mSharedPreferences = null;
	// 工具类对象 静态方便调用
	public static SharedPreferencesUtil shared = null;

	// 设置对象
	public void setSharedPreferces(SharedPreferences arg) {
		mSharedPreferences = arg;
	}

	// 写入值
	public void writeValue(String key, String value) {
		if (mSharedPreferences == null)
			return;
		Editor editor = mSharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	// 读取值
	public String readValue(String key, String defaultvalue) {
		if (key.equals("") || mSharedPreferences == null)
			return null;
		return mSharedPreferences.getString(key, defaultvalue);
	}

	// 删除值
	public void removeValue(String key) {
		if (mSharedPreferences == null)
			return;
		Editor editor = mSharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}

	// 清除全部值
	public void clearValue() {
		if (mSharedPreferences == null)
			return;
		Editor editor = mSharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

	// 是否首次进入
	public boolean isLogin() {
		if (mSharedPreferences == null)
			return false;
		return mSharedPreferences.contains("isLogin");
	}

	// 设置登入的用户
	public void setLoginUser(String user, String token) {
		writeValue(user, token);
		writeValue("isLogin", user);
	}

	// 获取Token
	public String getToken() {
		String uid = readValue("isLogin", "uid");
		if (uid != "uid")
			return readValue(uid, "token");
		return null;
	}

	// 获取Uid
	public String getUid() {
		String uid = readValue("isLogin", "uid");
		return uid;
	}

	// 登出账号
	public void removeUser() {
		clearValue();
	}
}
