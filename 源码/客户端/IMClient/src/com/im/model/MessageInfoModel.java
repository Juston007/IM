package com.im.model;

import java.util.Date;

public class MessageInfoModel {
	public static final int MSG_TYPE_TEXT = 0, MSG_TYPE_IMAGE = 1,
			MSG_TYPE_SOUND = 2, MSG_TYPE_OTHER = 3;

	// sendUid��������Uid��receiverUid��������Uid��msgId����ϢId��msg����Ϣ����
	private String sendUid, receiverUid, msgId, msg;
	// ����ʱ��
	private Date time;
	// ��Ϣ����
	private int msgType;
	// �Ƿ��Ѷ�
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
