package com.im.model;

public class FriendRequestModel {

	public FriendRequestModel(String accept, String sender) {
		this.AcceptUid = accept;
		this.SendUid = sender;
	}

	// ���ܣ�0���ܾ���1�����ԣ�2 ��ʵ���Ժ;ܾ�һ����
	public static final int ACCEPT = 0, REFUSE = 1, IGNORE = 2;
	// AcceptUid�����ܺ�������Uid,���ͺ�������Uid
	private String AcceptUid, SendUid;

	public String getAcceptUid() {
		return AcceptUid;
	}

	public String getSendUid() {
		return SendUid;
	}
}
