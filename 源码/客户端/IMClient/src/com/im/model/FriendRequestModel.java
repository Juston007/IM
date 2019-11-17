package com.im.model;

public class FriendRequestModel {

	public FriendRequestModel(String accept, String sender) {
		this.AcceptUid = accept;
		this.SendUid = sender;
	}

	// 接受：0，拒绝：1，忽略：2 其实忽略和拒绝一样的
	public static final int ACCEPT = 0, REFUSE = 1, IGNORE = 2;
	// AcceptUid：接受好友请求Uid,发送好友请求Uid
	private String AcceptUid, SendUid;

	public String getAcceptUid() {
		return AcceptUid;
	}

	public String getSendUid() {
		return SendUid;
	}
}
