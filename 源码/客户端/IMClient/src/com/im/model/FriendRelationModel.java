package com.im.model;

public class FriendRelationModel {

	public FriendRelationModel(String uid, String frienduid, boolean isdisplay) {
		this.uid = uid;
		this.friendUid = frienduid;
		this.isDisplay = isdisplay;
	}

	// Uid:�Լ�Uid��FriendUid������Uid
	private String uid, friendUid;
	// �Ƿ�չʾ�������б���
	private boolean isDisplay = true;

	public String getFriendUid() {
		return friendUid;
	}

	public String getUid() {
		return uid;
	}

	public boolean isDisplay() {
		return isDisplay;
	}
}
