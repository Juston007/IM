package com.im.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.im.aty.R;
import com.im.model.MessageInfoModel;
import com.im.model.UserInfoModel;
import com.im.util.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//�����б�������
public class MessageInfoAdapter extends BaseAdapter {

	private ArrayList<MessageInfoModel> msgList;
	private ArrayList<UserInfoModel> userList;
	private ArrayList<Integer> unReadCountList;

	private Context context;

	public MessageInfoAdapter(ArrayList<MessageInfoModel> msgList,
			ArrayList<UserInfoModel> userList,
			ArrayList<Integer> unReadCountList, Context context) {
		this.msgList = msgList;
		this.userList = userList;
		this.unReadCountList = unReadCountList;
		this.context = context;
	}

	@Override
	public int getCount() {
		return userList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return userList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder vh = null;
		if (arg1 == null) {
			vh = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.list_msginfo,
					null);
			vh.imgFace = (ImageView) arg1.findViewById(R.id.msg_info_face);
			vh.txtAlias = (TextView) arg1.findViewById(R.id.msg_info_alias);
			vh.txtContent = (TextView) arg1.findViewById(R.id.msg_info_content);
			vh.txtTime = (TextView) arg1.findViewById(R.id.msg_info_time);
			vh.txtCount = (TextView) arg1.findViewById(R.id.msg_info_count);
			arg1.setTag(vh);
		} else {
			vh = (ViewHolder) arg1.getTag();
		}
		// ����ֵ
		UserInfoModel user = userList.get(arg0);
		MessageInfoModel msginfo = msgList.get(arg0);

		// ����洢ͷ���HashMap�в������Ѽ��ع���Bitmap������ô��ȡBitmap���浽HashMap��,����ֱ����HashMap�л�ȡ
		File file = new File(user.getFacePath().toString());
		if (file.exists()) {
			Bitmap face = BitmapFactory.decodeFile(file.toString());
			vh.imgFace.setImageBitmap(face);
		} else {
			vh.imgFace.setImageResource(R.drawable.face_icon_people);
		}
		// �ǳ�
		vh.txtAlias.setText(user.getAlias());
		// ʱ��
		String datestr = "";
		Date date = new Date(msginfo != null ? msginfo.getTime().getTime()
				: new Date().getTime());
		int daycount = Util.differentDaysByMillisecond(date, new Date());
		switch (daycount) {
		case 0:
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			datestr = df.format(date);
			break;
		case 1:
			datestr = "����";
			break;
		case 2:
			datestr = "ǰ��";
			break;
		default:
			datestr = Util.ToDateStringFromLong(date.getTime());
			break;
		}
		vh.txtTime.setText(datestr);
		// ��ʾ��Ϣ���� ����ֱ����ʾ
		if (msginfo != null) {
			int count = unReadCountList.get(arg0);
			vh.txtCount.setVisibility(View.VISIBLE);
			if (count > 0)
				vh.txtCount.setText(String.valueOf(count));
			else
				vh.txtCount.setVisibility(View.INVISIBLE);
			String str = "";
			switch (msginfo.getMsgType()) {
			case MessageInfoModel.MSG_TYPE_IMAGE:
				str = "[ͼƬ��Ϣ]";
				break;
			case MessageInfoModel.MSG_TYPE_OTHER:
				str = "[������Ϣ]";
				break;
			case MessageInfoModel.MSG_TYPE_SOUND:
				str = "[������Ϣ]";
				break;
			case MessageInfoModel.MSG_TYPE_TEXT:
				str = msginfo.getMsg();
				break;

			default:
				break;
			}
			vh.txtContent.setText(str);
		} else {
			vh.txtCount.setVisibility(View.INVISIBLE);
			vh.txtContent.setText("û�������¼");
		}
		return arg1;
	}

	private class ViewHolder {
		public ImageView imgFace;
		public TextView txtAlias, txtContent, txtTime, txtCount;
	}
}
