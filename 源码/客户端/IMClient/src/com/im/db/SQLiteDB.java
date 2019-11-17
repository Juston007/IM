package com.im.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//���ݿ�
public class SQLiteDB extends SQLiteOpenHelper {

	public SQLiteDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	// �����ص�����
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// ������ر�
		arg0.execSQL("Create Table UserInfo (Uid Text Primary Key,Alias Text NOT NULL,FacePath Text,RegTime LONG,BirthDay LONG,Sex INERGER);");
		arg0.execSQL("Create Table FriendRelation (Uid Text NOT NULL,FriendUid Text NOT NULL,IsDisplay INTEGER);");
		arg0.execSQL("Create Table MsgRecord(Uid Text,MsgId Text Primary Key,SendUid Text NOT NULL,ReceiverUid Text NOT NULL,Time LONG NOT NULL,MsgType INTEGER NOT NULL,IsRead INTEGER NOT NULL,Msg Text NOT NULL);");
	}

	// ������ط���
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		Log.d("IMClient", "db�汾����");
	}
}
