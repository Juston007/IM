package com.im.model;

public class EventInfoModel {

	// ��������0�����ѱ䶯��1��δ��ȡ����Ϣ��2�����Ʒ����˸ı�3�����ϸ��£�4
	public static final int EVENT_FRIEND_REQUEST = 0, EVENT_FRIEND_CHANGE = 1,
			EVENT_MESSAGE = 2, EVENT_TOKEN_CHANGE = 3, EVENT_DATA_CHANGE = 4;

	public EventInfoModel(String uid, int msgtype) {
		this.uid = uid;
		this.msgType = msgtype;
	}

	// Uid
	private String uid;
	
	// ��Ϣ����
	private int msgType;

	public int getMsgType() {
		return msgType;
	}

	public String getUid() {
		return uid;
	}
}
