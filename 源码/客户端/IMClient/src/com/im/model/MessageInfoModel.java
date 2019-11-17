package com.im.model;

import java.util.Date;

public class MessageInfoModel {
	public static final int MSG_TYPE_TEXT = 0, MSG_TYPE_IMAGE = 1,
			MSG_TYPE_SOUND = 2, MSG_TYPE_OTHER = 3;

	// sendUid：发送者Uid，receiverUid：接受者Uid，msgId：信息Id，msg：信息详情
	private String sendUid, receiverUid, msgId, msg;
	// 发送时间
	private Date time;
	// 信息类型
	private int msgType;
	// 是否已读
	private boolean isRead = false;

	public MessageInfoModel(String senduid, String receiveruid, String msgid,
			Date time, int msgtype, boolean isread, String msg) {
		this.sendUid = senduid;
		this.receiverUid = receiveruid;
		this.msgId = msgid;
		this.time = time;
		this.msgType = msgtype;
		this.isRead = isread;
		this.msg = msg;
	}

	public String getMsgId() {
		return msgId;
	}

	public int getMsgType() {
		return msgType;
	}

	public String getReceiverUid() {
		return receiverUid;
	}

	public String getSendUid() {
		return sendUid;
	}

	public Date getTime() {
		return time;
	}

	public boolean isRead() {
		return isRead;
	}

	public String getMsg() {
		return msg;
	}
}
