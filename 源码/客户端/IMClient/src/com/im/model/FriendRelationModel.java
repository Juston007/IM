package com.im.model;

public class FriendRelationModel {

	public FriendRelationModel(String uid, String frienduid, boolean isdisplay) {
		this.uid = uid;
		this.friendUid = frienduid;
		this.isDisplay = isdisplay;
	}

	// Uid:自己Uid，FriendUid：好友Uid
	private String uid, friendUid;
	// 是否展示在聊天列表当中
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
