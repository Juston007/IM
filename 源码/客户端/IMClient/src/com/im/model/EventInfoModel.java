package com.im.model;

public class EventInfoModel {

	// 好友请求：0，好友变动：1，未获取的信息：2，令牌发生了改变3，资料更新：4
	public static final int EVENT_FRIEND_REQUEST = 0, EVENT_FRIEND_CHANGE = 1,
			EVENT_MESSAGE = 2, EVENT_TOKEN_CHANGE = 3, EVENT_DATA_CHANGE = 4;

	public EventInfoModel(String uid, int msgtype) {
		this.uid = uid;
		this.msgType = msgtype;
	}

	// Uid
	private String uid;
	
	// 信息类型
	private int msgType;

	public int getMsgType() {
		return msgType;
	}

	public String getUid() {
		return uid;
	}
}
