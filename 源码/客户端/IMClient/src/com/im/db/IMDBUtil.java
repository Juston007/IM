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

//数据工具类 此类对相关方法进行了封装
@SuppressLint("DefaultLocale")
public class IMDBUtil {

	// 好友列表变动会执行此接口的回调方法
	private static OnDataChangeListener friendListener = null;
	// 好友是否展示在聊天变动会执行此接口的回调方法
	private static OnDataChangeListener displayListener = null;
	// 信息发生变动会执行此接口的回调方法
	private static OnDataChangeListener msglistener = null;
	// 插入信息回调方法
	private static OnDataChangeListener insertMsgListener = null;

	// 设置回调方法
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

	// *************************用户信息表的基本操作*************************
	// 插入用户信息
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
				Log.d("IMClient", "在数据库当中插入用户信息,uid:" + userinfo.getUid());
			}
	}

	// 删除用户信息
	public static void removeUserInfo(String uid) {
		if (existUserInfo(uid)) {
			String sql = "DELETE FROM UserInfo WHERE Uid = " + uid + ";";
			SQLiteDBUtil.util.excuteSQL(sql);
			Log.d("IMClient", "在数据库当中删除用户信息,uid:" + uid);
		}
	}

	// 是否存在指定用户信息
	public static boolean existUserInfo(String uid) {
		ArrayList<HashMap<String, Object>> result = SQLiteDBUtil.util.rawQuery(
				"Select * From UserInfo Where Uid = ?", new String[] { uid });
		return result != null;
	}

	// 查询指定用户的信息
	public static UserInfoModel getUserInfo(String uid) {
		if (existUserInfo(uid)) {
			ArrayList<HashMap<String, Object>> queryresult = SQLiteDBUtil.util
					.rawQuery("Select * From UserInfo Where Uid = ?",
							new String[] { uid });
			if (queryresult == null)
				return null;
			if (queryresult.size() == 0)
				return null;
			// 获取下标为0的信息并将数据提取出
			HashMap<String, Object> map = queryresult.get(0);
			String alias = map.get("Alias").toString();
			String face = map.get("FacePath").toString();
			Date regtime = new Date(Long.parseLong(map.get("RegTime")
					.toString()));
			Date birthday = new Date(Long.parseLong(map.get("BirthDay")
					.toString()));
			boolean sex = map.get("Sex").toString().equals("1");
			// 创建UserInfoModel对象
			UserInfoModel model = new UserInfoModel(uid, alias, face, regtime,
					birthday, sex);
			return model;
		} else {
			return null;
		}
	}

	// *************************好友信息表的基本操作*************************

	// 增加好友
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
						"增加uid:" + model.getUid() + "的好友:"
								+ model.getFriendUid());
			}
	}

	// 是否存在好友关系
	public static boolean isExistFriendRelation(FriendRelationModel model) {
		String sql = String
				.format("Select * From FriendRelation Where Uid = '%s' And FriendUid = '%s'",
						model.getUid(), model.getFriendUid());
		return SQLiteDBUtil.util.rawQuery(sql, null) != null;
	}

	// 删除好友
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
					"删除好友uid:" + model.getUid() + "的好友:" + model.getFriendUid());
		}
	}

	// 是否显示在聊天列表当中显示
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
					"将uid:" + model.getUid() + "的好友:" + model.getFriendUid()
							+ "设置为在聊天列表中" + (model.isDisplay() ? "展示" : "不展示"));
		}
	}

	// 获取好友列表
	public static ArrayList<FriendRelationModel> getFriendList(String uid) {
		String sql = "Select * From FriendRelation Where Uid = '" + uid + "'";
		ArrayList<HashMap<String, Object>> result = SQLiteDBUtil.util.rawQuery(
				sql, null);
		if (result == null)
			return null;
		else {
			// 用ArrayList存储好友信息
			ArrayList<FriendRelationModel> arr = new ArrayList<FriendRelationModel>();
			for (int i = 0; i < result.size(); i++) {
				// 获取好友信息
				HashMap<String, Object> map = result.get(i);
				String frienduid = map.get("FriendUid").toString();
				boolean isdisplay = map.get("IsDisplay").equals("1");
				// 获得好友关系对象
				FriendRelationModel relation = new FriendRelationModel(uid,
						frienduid, isdisplay);
				// 添加至ArrayList中
				arr.add(relation);
			}
			return arr;
		}
	}

	// 获取展示在好友聊天列表当中的好友信息
	public static ArrayList<UserInfoModel> getDisplayUserInfoList(String uid) {
		String sql = "Select FriendUid From FriendRelation Where Uid = '" + uid
				+ "' And IsDisplay = 1";
		// 获取好友Uid集合
		ArrayList<HashMap<String, Object>> result = SQLiteDBUtil.util.rawQuery(
				sql, null);
		if (result == null)
			return null;
		else {
			ArrayList<UserInfoModel> infos = new ArrayList<UserInfoModel>();
			for (int i = 0; i < result.size(); i++) {
				// 查询展示在聊天列表中的好友信息并插入至ArrayList中
				String frienduid = result.get(i).get("FriendUid").toString();
				infos.add(IMDBUtil.getUserInfo(frienduid));
			}
			return infos;
		}
	}

	// *************************聊天记录表的基本操作*************************

	public static boolean isExistMsg(String msgid) {
		String sql = "Select * From MsgRecord Where MsgId = '" + msgid + "'";
		ArrayList<HashMap<String, Object>> arr = SQLiteDBUtil.util.rawQuery(
				sql, null);
		return arr != null;
	}

	// 插入信息
	@SuppressLint("DefaultLocale")
	public static void insertMessageInfo(String uid, MessageInfoModel msginfo,
			String path) throws Exception {
		if (isExistMsg(msginfo.getMsgId()))
			return;
		String msg = msginfo.getMsg();
		// 根据信息类型来存储数据 图片和声音存储到本地 文字消息直接存储在数据库中
		// 图片声音在数据库中存储的是路径 根据路径找到文件
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
			// 写入到本地
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
		// 写入到数据库当中
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
		Log.d("IMClient", "插入了一条信息,MsgId:" + msginfo.getMsgId());
	}

	// 获取与指定用户的未读数量
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

	// 获取与指定用户最后一条讯息的详情信息
	public static MessageInfoModel getLastMsgInfo(String uid, String frienduid) {
		if (isExistFriendRelation(new FriendRelationModel(uid, frienduid, false))) {
			// 根据时间进行排序
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

	// 删除信息
	public static void removeMessageInfo(String msgid) {
		if (!isExistMsg(msgid))
			return;
		String sql = String.format("Delete From MsgRecord Where MsgId = '%s'",
				msgid);
		SQLiteDBUtil.util.excuteSQL(sql);
		if (msglistener != null)
			msglistener.OnDataChnage();
		Log.d("IMClient", "删除了一条信息,MsgId:" + msgid);
	}

	// 更新信息内容
	public static void updateMessageContent(String msgid, String content,
			String path, int type) throws Exception {
		Log.d("IMClient", String.format("msgid:%s,content:%s,path:%s,type:%s",
				msgid, content, path, type + ""));
		if (!isExistMsg(msgid))
			return;
		String msg = content;
		// 与直接插入信息的那段代码相同 这里代码重复
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
			// 写入到本地
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
		Log.d("IMClient", "更新了一条信息,MsgId:" + msgid);
	}

	// 更新与指定用户的未读消息的状态
	public static void updateMessageStatus(String uid1, String uid2) {
		String sql = "Update MsgRecord Set IsRead = 1 Where SendUid = '" + uid2
				+ "' And ReceiverUid = '" + uid1 + "' And IsRead = 0";
		SQLiteDBUtil.util.excuteSQL(sql);
		if (displayListener != null)
			displayListener.OnDataChnage();
		Log.d("TestTag", "updateMessageStatus");
	}

	// 获取与指定用户消息集合
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
			// 获取信息详情并插入至ArrayList中
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
