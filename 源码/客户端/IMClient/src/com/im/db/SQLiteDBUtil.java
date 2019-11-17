package com.im.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

//���ݿ⹤����
public class SQLiteDBUtil {

	public static SQLiteDBUtil util = null;
	private SQLiteDB db = null;

	// ��ȡ������Ķ���
	public static SQLiteDBUtil getInterface(Context context, String name,
			CursorFactory factory, int version) {
		util = new SQLiteDBUtil(context, name, factory, version);
		return util;
	}

	private SQLiteDBUtil(Context context, String name, CursorFactory factory,
			int version) {
		db = new SQLiteDB(context, name, factory, version);
		SQLiteDatabase data = db.getReadableDatabase();
		Log.d("IMClient", "����db�ɹ���" + data.toString());
		data.close();
	}

	// ִ��SQL���
	public void excuteSQL(String sqlstr) {
		if (sqlstr.equals(""))
			return;
		SQLiteDatabase writedb = db.getWritableDatabase();
		writedb.execSQL(sqlstr);
		writedb.close();
	}

	// ��ѯ����
	public ArrayList<HashMap<String, Object>> rawQuery(String sqlstr,
			String[] selecttionArgs) {
		if (sqlstr.equals(""))
			return null;
		SQLiteDatabase readdb = db.getReadableDatabase();
		Cursor cursor = readdb.rawQuery(sqlstr, selecttionArgs);
		if (cursor.getCount() == 0)
			return null;
		ArrayList<HashMap<String, Object>> arr = new ArrayList<HashMap<String, Object>>();
		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; cursor.getColumnCount() > i; i++) {
				map.put(cursor.getColumnName(i), cursor.getString(i));
			}
			arr.add(map);
		}
		readdb.close();
		return arr;
	}
}
