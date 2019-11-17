package com.im.db;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.im.model.FriendRelationModel;
import com.im.model.MessageInfoModel;
import com.im.model.OnDataChangeListener;
import com.im.model.UserInfoModel;
import com.im.util.Util;

import android.annotation.SuppressLint;
import android.util.Log;

//���ݹ����� �������ط��������˷�װ
@SuppressLint("DefaultLocale")
public class IMDBUtil {

	// �����б�䶯��ִ�д˽ӿڵĻص�����
	private static OnDataChangeListener friendListener = null;
	// �����Ƿ�չʾ������䶯��ִ�д˽ӿڵĻص�����
	private static OnDataChangeListener displayListener = null;
	// ��Ϣ�����䶯��ִ�д˽ӿڵĻص�����
	private static OnDataChangeListener msglistener = null;
	// ������Ϣ�ص�����
	private static OnDataChangeListener insertMsgListener = null;

	// ���ûص�����
	public static void setOnFriendChangeListener(OnDataChangeListener listener) {
		friendListener = listener;
	}

	public static void setOnDisplayListener(OnDataChangeListener listener) {
		displayListener = listener;
	}

	public static void setOnMessageChangeListener(OnDataChangeListener listener) {
		msglistener = listener;
	}

	public static void setOnInsertMsgListener(OnDataChangeListener listener) {
		insertMsgListener = listener;
	}

	// *************************�û���Ϣ��Ļ�������*************************
	// �����û���Ϣ
	public static void insertUserInfo(UserInfoModel userinfo) {
		if (userinfo != null)
			if (!existUserInfo(userinfo.getUid())) {
				String sql = "Insert Into UserInfo (Uid,Alias,FacePath,RegTime,BirthDay,Sex) Values('"
						+ userinfo.getUid()
						+ "','"
						+ userinfo.getAlias()
						+ "','"
						+ userinfo.getFacePath()
						+ "',"
						+ userinfo.getRegTime().getTime()
						+ ","
						+ userinfo.getBirthDay().getTime()
						+ ","
						+ (userinfo.getSex() ? 1 : 0) + ")";
				SQLiteDBUtil.util.excuteSQL(sql);
				Log.d("IMClient", "�����ݿ⵱�в����û���Ϣ,uid:" + userinfo.getUid());
			}
	}

	// ɾ���û���Ϣ
	public static void removeUserInfo(String uid) {
		if (existUserInfo(uid)) {
			String sql = "DELETE FROM UserInfo WHERE Uid = " + uid + ";";
			SQLiteDBUtil.util.excuteSQL(sql);
			Log.d("IMClient", "�����ݿ⵱��ɾ���û���Ϣ,uid:" + uid);
		}
	}

	// �Ƿ����ָ���û���Ϣ
	public static boolean existUserInfo(String uid) {
		ArrayList<HashMap<String, Object>> result = SQLiteDBUtil.util.rawQuery(
				"Select * From UserInfo Where Uid = ?", new String[] { uid });
		return result != null;
	}

	// ��ѯָ���û�����Ϣ
	public static UserInfoModel getUserInfo(String uid) {
		if (existUserInfo(uid)) {
			ArrayList<HashMap<String, Object>> queryresult = SQLiteDBUtil.util
					.rawQuery("Select * From UserInfo Where Uid = ?",
							new String[] { uid });
			if (queryresult == null)
				return null;
			if (queryresult.size() == 0)
				return null;
			// ��ȡ�±�Ϊ0����Ϣ����������ȡ��
			HashMap<String, Object> map = queryresult.get(0);
			String alias = map.get("Alias").toString();
			String face = map.get("FacePath").toString();
			Date regtime = new Date(Long.parseLong(map.get("RegTime")
					.toString()));
			Date birthday = new Date(Long.parseLong(map.get("BirthDay")
					.toString()));
			boolean sex = map.get("Sex").toString().equals("1");
			// ����UserInfoModel����
			UserInfoModel model = new UserInfoModel(uid, alias, face, regtime,
					birthday, sex);
			return model;
		} else {
			return null;
		}
	}

	// *************************������Ϣ��Ļ�������*************************

	// ���Ӻ���
	public static void addFriend(FriendRelationModel model) {
		if (model != null)
			if (!isExistFriendRelation(model)) {
				String sql = "Insert Into FriendRelation (Uid,FriendUid,IsDisplay) Values('"
						+ model.getUid()
						+ "','"
						+ model.getFriendUid()
						+ "',"
						+ (model.isDisplay() ? 1 : 0) + ")";
				SQLiteDBUtil.util.excuteSQL(sql);
				if (friendListener != null)
					friendListener.OnDataChnage();
				if (displayListener != null)
					displayListener.OnDataChnage();
				Log.d("IMClient",
						"����uid:" + model.getUid() + "�ĺ���:"
								+ model.getFriendUid());
			}
	}

	// �Ƿ���ں��ѹ�ϵ
	public static boolean isExistFriendRelation(FriendRelationModel model) {
		String sql = String
				.format("Select * From FriendRelation Where Uid = '%s' And FriendUid = '%s'",
						model.getUid(), model.getFriendUid());
		return SQLiteDBUtil.util.rawQuery(sql, null) != null;
	}

	// ɾ������
	public static void removeFriend(FriendRelationModel model) {
		if (isExistFriendRelation(model)) {
			String sql = "DELETE FROM FriendRelation WHERE Uid = '"
					+ model.getUid() + "' And FriendUid = '"
					+ model.getFriendUid() + "';";
			SQLiteDBUtil.util.excuteSQL(sql);
			if (friendListener != null)
				friendListener.OnDataChnage();
			if (displayListener != null)
				displayListener.OnDataChnage();
			Log.d("IMClient",
					"ɾ������uid:" + model.getUid() + "�ĺ���:" + model.getFriendUid());
		}
	}

	// �Ƿ���ʾ�������б�����ʾ
	public static void setDisplay(FriendRelationModel model) {
		if (isExistFriendRelation(model)) {
			String sql = "Update FriendRelation Set IsDisplay = "
					+ (model.isDisplay() ? 1 : 0) + " Where Uid = '"
					+ model.getUid() + "' And FriendUid = '"
					+ model.getFriendUid() + "'";
			SQLiteDBUtil.util.excuteSQL(sql);
			if (displayListener != null)
				displayListener.OnDataChnage();
			Log.d("IMClient",
					"��uid:" + model.getUid() + "�ĺ���:" + model.getFriendUid()
							+ "����Ϊ�������б���" + (model.isDisplay() ? "չʾ" : "��չʾ"));
		}
	}

	// ��ȡ�����б�
	public static ArrayList<FriendRelationModel> getFriendList(String uid) {
		String sql = "Select * From FriendRelation Where Uid = '" + uid + "'";
		ArrayList<HashMap<String, Object>> result = SQLiteDBUtil.util.rawQuery(
				sql, null);
		if (result == null)
			return null;
		else {
			// ��ArrayList�洢������Ϣ
			ArrayList<FriendRelationModel> arr = new ArrayList<FriendRelationModel>();
			for (int i = 0; i < result.size(); i++) {
				// ��ȡ������Ϣ
				HashMap<String, Object> map = result.get(i);
				String frienduid = map.get("FriendUid").toString();
				boolean isdisplay = map.get("IsDisplay").equals("1");
				// ��ú��ѹ�ϵ����
				FriendRelationModel relation = new FriendRelationModel(uid,
						frienduid, isdisplay);
				// �����ArrayList��
				arr.add(relation);
			}
			return arr;
		}
	}

	// ��ȡչʾ�ں��������б��еĺ�����Ϣ
	public static ArrayList<UserInfoModel> getDisplayUserInfoList(String uid) {
		String sql = "Select FriendUid From FriendRelation Where Uid = '" + uid
				+ "' And IsDisplay = 1";
		// ��ȡ����Uid����
		ArrayList<HashMap<String, Object>> result = SQLiteDBUtil.util.rawQuery(
				sql, null);
		if (result == null)
			return null;
		else {
			ArrayList<UserInfoModel> infos = new ArrayList<UserInfoModel>();
			for (int i = 0; i < result.size(); i++) {
				// ��ѯչʾ�������б��еĺ�����Ϣ��������ArrayList��
				String frienduid = result.get(i).get("FriendUid").toString();
				infos.add(IMDBUtil.getUserInfo(frienduid));
			}
			return infos;
		}
	}

	// *************************�����¼��Ļ�������*************************

	public static boolean isExistMsg(String msgid) {
		String sql = "Select * From MsgRecord Where MsgId = '" + msgid + "'";
		ArrayList<HashMap<String, Object>> arr = SQLiteDBUtil.util.rawQuery(
				sql, null);
		return arr != null;
	}

	// ������Ϣ
	@SuppressLint("DefaultLocale")
	public static void insertMessageInfo(String uid, MessageInfoModel msginfo,
			String path) throws Exception {
		if (isExistMsg(msginfo.getMsgId()))
			return;
		String msg = msginfo.getMsg();
		// ������Ϣ�������洢���� ͼƬ�������洢������ ������Ϣֱ�Ӵ洢�����ݿ���
		// ͼƬ���������ݿ��д洢����·�� ����·���ҵ��ļ�
		switch (msginfo.getMsgType()) {
		case MessageInfoModel.MSG_TYPE_IMAGE:
			msg = path + "/" + "Image/" + new Date().getTime() + ".jpg";
			break;
		case MessageInfoModel.MSG_TYPE_SOUND:
			msg = path + "/" + "Sound/" + 's' + new Date().getTime() + ".3gp";
			break;
		default:
			break;
		}
		if (msginfo.getMsgType() == MessageInfoModel.MSG_TYPE_IMAGE
				|| msginfo.getMsgType() == MessageInfoModel.MSG_TYPE_SOUND) {
			// д�뵽����
			byte[] buffer = Util.ToByteFromBase64String(msginfo.getMsg());
			File file = new File(msg);
			File imgpath = new File(path + "/" + "Image");
			File soundpath = new File(path + "/" + "Sound");
			imgpath.mkdirs();
			soundpath.mkdirs();
			file.createNewFile();
			FileOutputStream fs = new FileOutputStream(file);
			fs.write(buffer);
			fs.flush();
			fs.close();
			Log.d("IMClient", file.length() + "length");
		}
		// д�뵽���ݿ⵱��
		String sql = String
				.format("Insert Into MsgRecord (Uid,MsgId,SendUid,ReceiverUid,Time,MsgType,IsRead,Msg) Values('"
						+ uid
						+ "','"
						+ msginfo.getMsgId()
						+ "','"
						+ msginfo.getSendUid()
						+ "','"
						+ msginfo.getReceiverUid()
						+ "',"
						+ msginfo.getTime().getTime()
						+ ","
						+ msginfo.getMsgType()
						+ ","
						+ (msginfo.isRead() ? 1 : 0) + ",'" + msg + "')");
		SQLiteDBUtil.util.excuteSQL(sql);
		boolean issend = msginfo.getSendUid().equals(uid);
		setDisplay(new FriendRelationModel(issend ? msginfo.getSendUid()
				: msginfo.getReceiverUid(), issend ? msginfo.getReceiverUid()
				: msginfo.getSendUid(), true));
		if (msglistener != null) {
			msglistener.OnDataChnage();
			Log.d("TestTag", "msglistener.OnDataChnage");
		}
		if (insertMsgListener != null) {
			if (uid != msginfo.getSendUid())
				insertMsgListener.OnDataChnage();
		}
		Log.d("IMClient", "������һ����Ϣ,MsgId:" + msginfo.getMsgId());
	}

	// ��ȡ��ָ���û���δ������
	public static int getUnReadCount(String uid, String senduid) {
		String sql = "Select * From MsgRecord Where Uid = '" + uid
				+ "' And SendUid = '" + senduid + "' And IsRead = 0";
		ArrayList<HashMap<String, Object>> result = SQLiteDBUtil.util.rawQuery(
				sql, null);
		if (result == null)
			return 0;
		else
			return result.size();
	}

	// ��ȡ��ָ���û����һ��ѶϢ��������Ϣ
	public static MessageInfoModel getLastMsgInfo(String uid, String frienduid) {
		if (isExistFriendRelation(new FriendRelationModel(uid, frienduid, false))) {
			// ����ʱ���������
			String sql = "Select * From MsgRecord Where Uid = '" + uid
					+ "'  And ((ReceiverUid = '" + frienduid
					+ "') Or (SendUid = '" + frienduid
					+ "' And ReceiverUid = '" + uid
					+ "')) Order by Time desc limit 0,1";
			ArrayList<HashMap<String, Object>> arr = SQLiteDBUtil.util
					.rawQuery(sql, null);
			if (arr == null)
				return null;
			HashMap<String, Object> map = arr.get(0);
			String senduid = map.get("SendUid").toString();
			String receiveruid = map.get("ReceiverUid").toString();
			String msgid = map.get("MsgId").toString();
			Date date = new Date(Long.parseLong(map.get("Time").toString()));
			int type = Integer.parseInt(map.get("MsgType").toString());
			boolean israed = map.get("IsRead").toString().equals("1");
			String msg = map.get("Msg").toString();
			MessageInfoModel msginfomodel = new MessageInfoModel(senduid,
					receiveruid, msgid, date, type, israed, msg);
			return msginfomodel;
		} else
			return null;
	}

	// ɾ����Ϣ
	public static void removeMessageInfo(String msgid) {
		if (!isExistMsg(msgid))
			return;
		String sql = String.format("Delete From MsgRecord Where MsgId = '%s'",
				msgid);
		SQLiteDBUtil.util.excuteSQL(sql);
		if (msglistener != null)
			msglistener.OnDataChnage();
		Log.d("IMClient", "ɾ����һ����Ϣ,MsgId:" + msgid);
	}

	// ������Ϣ����
	public static void updateMessageContent(String msgid, String content,
			String path, int type) throws Exception {
		Log.d("IMClient", String.format("msgid:%s,content:%s,path:%s,type:%s",
				msgid, content, path, type + ""));
		if (!isExistMsg(msgid))
			return;
		String msg = content;
		// ��ֱ�Ӳ�����Ϣ���Ƕδ�����ͬ ��������ظ�
		switch (type) {
		case MessageInfoModel.MSG_TYPE_IMAGE:
			msg = path + "/" + "Image/" + new Date().getTime() + ".jpg";
			break;
		case MessageInfoModel.MSG_TYPE_SOUND:
			msg = path + "/" + "Sound/" + 's' + new Date().getTime() + ".3gp";
			break;
		default:
			break;
		}
		if (type == MessageInfoModel.MSG_TYPE_IMAGE
				|| type == MessageInfoModel.MSG_TYPE_SOUND) {
			// д�뵽����
			byte[] buffer = Util.ToByteFromBase64String(content);
			File file = new File(msg);
			File imgpath = new File(path + "/" + "Image");
			File soundpath = new File(path + "/" + "Sound");
			imgpath.mkdirs();
			soundpath.mkdirs();
			file.createNewFile();
			FileOutputStream fs = new FileOutputStream(file);
			fs.write(buffer);
			fs.flush();
			fs.close();
		}
		String sql = String.format(
				"Update MsgRecord Set Msg = '%s' Where msgid = '%s'", msg,
				msgid);
		SQLiteDBUtil.util.excuteSQL(sql);
		if (msglistener != null)
			msglistener.OnDataChnage();
		Log.d("IMClient", "������һ����Ϣ,MsgId:" + msgid);
	}

	// ������ָ���û���δ����Ϣ��״̬
	public static void updateMessageStatus(String uid1, String uid2) {
		String sql = "Update MsgRecord Set IsRead = 1 Where SendUid = '" + uid2
				+ "' And ReceiverUid = '" + uid1 + "' And IsRead = 0";
		SQLiteDBUtil.util.excuteSQL(sql);
		if (displayListener != null)
			displayListener.OnDataChnage();
		Log.d("TestTag", "updateMessageStatus");
	}

	// ��ȡ��ָ���û���Ϣ����
	public static ArrayList<MessageInfoModel> getMessageList(String uid,
			String frienduid) {
		String sql = "Select * From MsgRecord Where Uid = '" + uid
				+ "' And ((SendUid = '" + uid + "' And ReceiverUid = '"
				+ frienduid + "') Or (SendUid = '" + frienduid
				+ "' And ReceiverUid = '" + uid + "'))";
		ArrayList<HashMap<String, Object>> result = SQLiteDBUtil.util.rawQuery(
				sql, null);
		sql = "Select MsgId From MsgRecord Where Uid = '" + uid
				+ "' And ((SendUid = '" + uid + "' And ReceiverUid = '"
				+ frienduid + "') Or (SendUid = '" + frienduid
				+ "' And ReceiverUid = '" + uid + "'))";
		if (result == null)
			return null;
		else {
			// ��ȡ��Ϣ���鲢������ArrayList��
			ArrayList<MessageInfoModel> arr = new ArrayList<MessageInfoModel>();
			for (int i = 0; i < result.size(); i++) {
				HashMap<String, Object> map = result.get(i);
				String senduid = map.get("SendUid").toString();
				String receiveruid = map.get("ReceiverUid").toString();
				String msgid = map.get("MsgId").toString();
				Date date = new Date(Long.parseLong(map.get("Time").toString()));
				int type = Integer.parseInt(map.get("MsgType").toString());
				boolean israed = map.get("IsRead").toString().equals("1");
				String msg = map.get("Msg").toString();
				MessageInfoModel msginfomodel = new MessageInfoModel(senduid,
						receiveruid, msgid, date, type, israed, msg);
				arr.add(msginfomodel);
			}
			return arr;
		}
	}
}
