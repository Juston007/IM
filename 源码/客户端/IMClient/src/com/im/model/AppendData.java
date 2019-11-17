package com.im.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.im.util.Util;
import android.annotation.SuppressLint;

public class AppendData {

	private String dataStr;

	public AppendData(String data) {
		// ȥ������ ��\���ַ�
		dataStr = data.replace('\\', ' ');
	}

	// ��ȡ�����б�
	@SuppressLint("SimpleDateFormat")
	public ArrayList<UserInfoModel> getFriendList() throws Exception,
			ParseException {
		if (dataStr.equals(""))
			return null;
		JSONArray jsonarr = new JSONArray(dataStr);
		if (jsonarr.length() == 0)
			return null;
		ArrayList<UserInfoModel> arr = new ArrayList<UserInfoModel>();
		for (int i = 0; i < jsonarr.length(); i++) {
			UserInfoModel userinfo = getUserInfo(jsonarr.getJSONObject(i));
			arr.add(userinfo);
		}
		return arr;
	}

	// ��ȡToken
	public String getToken() {
		return getString();
	}

	// ��ȡ�ַ���
	public String getString() {
		return dataStr;
	}

	// ��дtoString����
	@Override
	public String toString() {
		return getString();
	}

	// �û�������Ϣ���ط���
	@SuppressLint("SdCardPath")
	public UserInfoModel getUserInfo(JSONObject userjson) throws JSONException,
			ParseException {
		String uid = userjson.getString("Uid");
		String facepath = "/data/data/com.im.aty/cache/Face/" + uid + ".jpg";
		UserInfoModel userinfo = new UserInfoModel(uid,
				userjson.getString("Alias"), facepath,
				Util.getDateFromUTCString(userjson.getString("RegTime")),
				Util.getDateFromUTCString(userjson.getString("BirthDay")),
				userjson.getBoolean("Sex"));
		return userinfo;
	}

	// �û�������Ϣ
	public UserInfoModel getUserInfo() throws JSONException, ParseException {
		JSONObject userjson = new JSONObject(dataStr);
		return getUserInfo(userjson);
	}

	// ���������б�
	public ArrayList<FriendRequestModel> getFriendRequestList()
			throws JSONException {
		if (dataStr.equals(""))
			return null;
		JSONArray jsonarr = new JSONArray(dataStr);
		if (jsonarr.length() == 0)
			return null;
		ArrayList<FriendRequestModel> arr = new ArrayList<FriendRequestModel>();
		for (int i = 0; i < jsonarr.length(); i++) {
			JSONObject userjson = jsonarr.getJSONObject(i);
			FriendRequestModel request = new FriendRequestModel(
					userjson.getString("AcceptUid"),
					userjson.getString("SendUid"));
			arr.add(request);
		}
		return arr;
	}

	// ѶϢ������Ϣ�б�
	public ArrayList<MessageInfoModel> getMessageInfoList()
			throws JSONException, ParseException {
		if (dataStr.equals(""))
			return null;
		JSONArray jsonarr = new JSONArray(dataStr);
		if (jsonarr.length() == 0)
			return null;
		ArrayList<MessageInfoModel> arr = new ArrayList<MessageInfoModel>();
		for (int i = 0; i < jsonarr.length(); i++) {
			// ȡֵ
			JSONObject msgjsonobj = jsonarr.getJSONObject(i);
			String senduid = msgjsonobj.getString("SendUid");
			String receiveruid = msgjsonobj.getString("ReceiverUid");
			String msgid = msgjsonobj.getString("MsgId");
			Date time = Util.getDateFromUTCString(msgjsonobj.getString("Time"));
			int msgtype = msgjsonobj.getInt("MsgType");
			// ReceiverStatus
			boolean isread = msgjsonobj.getString("ReceiverStatus").equals(
					"true");
			// ��ȡMessageInfoModel����
			MessageInfoModel msginfo = new MessageInfoModel(senduid,
					receiveruid, msgid, time, msgtype, isread, "NULL");
			arr.add(msginfo);
		}
		return arr;
	}

	// δ�����¼��б�
	public ArrayList<EventInfoModel> getUnHandlerEventList()
			throws JSONException, ParseException {
		if (dataStr.equals(""))
			return null;
		JSONArray jsonarr = new JSONArray(dataStr);
		if (jsonarr.length() == 0)
			return null;
		ArrayList<EventInfoModel> arr = new ArrayList<EventInfoModel>();
		for (int i = 0; i < jsonarr.length(); i++) {
			// ȡֵ
			JSONObject msgjsonobj = jsonarr.getJSONObject(i);
			String uid = msgjsonobj.getString("Uid");
			int msgtype = msgjsonobj.getInt("MsgType");
			EventInfoModel model = new EventInfoModel(uid, msgtype);
			arr.add(model);
		}
		return arr;
	}
}
