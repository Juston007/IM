package com.im.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.im.aty.ImageActivity;
import com.im.aty.R;
import com.im.aty.TextBrowseActivity;
import com.im.aty.UserInfoActivity;
import com.im.db.IMDBUtil;
import com.im.model.MessageInfoModel;
import com.im.util.MediaUtil;
import com.im.util.Util;
import com.im.view.MyDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//��Ϣ�б�������
public class MessageAdapter extends BaseAdapter {

	private ArrayList<MessageInfoModel> msgData;
	private Context context;
	private String uid;
	private HashMap<String, Bitmap> faceMap = new HashMap<String, Bitmap>();

	public MessageAdapter(Context context, ArrayList<MessageInfoModel> data,
			String uid) {
		msgData = data;
		this.context = context;
		this.uid = uid;
	}

	@Override
	public int getCount() {
		return msgData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return msgData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// ���ﲻ����д�ˣ����Բο���ϵ����������getView����
		ViewHolder vh = null;
		final MessageInfoModel msginfo = msgData.get(arg0);
		boolean meissend = uid.equals(msginfo.getSendUid().toString().trim());
		if (arg1 == null) {
			vh = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.list_message,
					null);
			vh.llLeft = arg1.findViewById(R.id.msg_left);
			vh.llRigth = arg1.findViewById(R.id.msg_rigth);
			vh.txtTime = (TextView) arg1.findViewById(R.id.msg_time);
			arg1.setTag(vh);
		} else {
			vh = (ViewHolder) arg1.getTag();
		}
		// �Լ�Ϊ��������ô�����ұ�
		if (meissend) {
			vh.llLeft.setVisibility(View.GONE);
			vh.llRigth.setVisibility(View.VISIBLE);
			vh.txtContent = (TextView) arg1
					.findViewById(R.id.msg_rigth_content);
			vh.imgFace = (ImageView) arg1.findViewById(R.id.msg_rigth_face);
		} else {
			vh.llLeft.setVisibility(View.VISIBLE);
			vh.llRigth.setVisibility(View.GONE);
			vh.txtContent = (TextView) arg1.findViewById(R.id.msg_left_content);
			vh.imgFace = (ImageView) arg1.findViewById(R.id.msg_left_face);
		}
		// ��ʼ���¼���View
		initEvent(vh, arg0, msginfo);
		try {
			initView(vh, arg0, msginfo);
		} catch (Exception e) {
			Log.d("IMClient", e.getMessage());
		}
		return arg1;
	}

	private class ViewHolder {
		public TextView txtTime, txtContent;
		public ImageView imgFace;
		public View llLeft, llRigth;
	}

	private void initEvent(ViewHolder vh, final int index,
			final MessageInfoModel msginfo) {
		// ������Ϣɾ����Ϣ
		vh.txtContent.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// ������ص���¼�
				final MyDialog dialog = new MyDialog(
						context,
						"ɾ����Ϣ",
						"ȷ��Ҫɾ������Ϣ�𣿴���Ϣֻ���ڱ���ɾ�����ƶ˼�¼������ɾ���������ٴ�ͬ�������¼ʱ����ѶϢ���������ص����ء�",
						"ȷ��", "ȡ��", true, true);
				dialog.setCancelOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				dialog.setConfrimOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						IMDBUtil.removeMessageInfo(msgData.get(index)
								.getMsgId());
						dialog.dismiss();
					}
				});
				dialog.show();
				return true;
			}
		});
		// ���ͷ�������Ϣ����
		vh.imgFace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, UserInfoActivity.class);
				intent.putExtra("Uid", msginfo.getSendUid());
				intent.putExtra("isMsg", "True");
				context.startActivity(intent);
			}
		});
		// �����Ϣ�鿴��Ϣ����
		vh.txtContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// ���֣��鿴����
				// ͼƬ���鿴ͼƬ
				// ��������������
				switch (msginfo.getMsgType()) {
				case MessageInfoModel.MSG_TYPE_TEXT:
					Intent txtintent = new Intent(context,
							TextBrowseActivity.class);
					txtintent.putExtra("Content", msginfo.getMsg());
					context.startActivity(txtintent);
					break;
				case MessageInfoModel.MSG_TYPE_IMAGE:
					Intent imgintent = new Intent(context, ImageActivity.class);
					imgintent.putExtra("Image", msginfo.getMsg());
					context.startActivity(imgintent);
					break;
				case MessageInfoModel.MSG_TYPE_SOUND:
					try {
						MediaUtil.startPlay(msginfo.getMsg());
					} catch (Exception e) {
						Log.d("IMClient", e.getMessage());
					}
					break;
				default:
					break;
				}
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	private void initView(ViewHolder vh, int arg0, MessageInfoModel msginfo)
			throws Exception {
		// �������ѶϢ����һ��ѶϢʱ��������1������ô��Ҫ��ʾ����ʱ��� ��ϢΪ��������ʱֻ��ʾʱ�� ����������ʾ ���� 19��00
		// ������ʾȫ������
		// ʱ�� �������һ��ʱ����1���� ��ô������ʾʱ��
		vh.txtTime.setVisibility(View.VISIBLE);
		long time = 1000 * 60;
		Date nextmsgdate = new Date();
		if ((arg0 - 1) != -1)
			nextmsgdate = msgData.get(arg0 - 1).getTime();
		String date = "";
		int daycount = 0;
		// ���ж�Ҫ��Ҫ��ʾʱ��
		if (Math.abs((nextmsgdate.getTime() - msginfo.getTime().getTime())) < time) {
			vh.txtTime.setVisibility(View.GONE);
		} else {
			// ������������ѶϢ����ʱ��Ȼ����ʾ����
			try {
				daycount = Util.differentDaysByMillisecond(Util
						.getDateFromDateString(Util
								.ToDateStringFromLong(new Date().getTime())),
						Util.getDateFromDateString(Util
								.ToDateStringFromLong(msginfo.getTime()
										.getTime())));
			} catch (Exception e) {
				Log.d("IMClient", e.getMessage());
			}
		}
		switch (daycount) {
		case 0:
			break;
		case 1:
			date = "����";
			break;
		default:
			date = Util.ToDateStringFromLong(msginfo.getTime().getTime());
			break;
		}

		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		date = ' ' + date + ' '
				+ df.format(new Date(msginfo.getTime().getTime())) + ' ';
		vh.txtTime.setText(date);
		// ͷ��
		String facepath = null;
		facepath = IMDBUtil.getUserInfo(msginfo.getSendUid()).getFacePath();
		// ��HashMap��Bitmap����洢���������ظ���ȡ����
		if (faceMap.containsKey(facepath)) {
			vh.imgFace.setImageBitmap(faceMap.get(facepath));
		} else {
			File file = new File(facepath);
			if (file.exists()) {
				Bitmap face = BitmapFactory.decodeFile(file.toString());
				faceMap.put(facepath, face);
				vh.imgFace.setImageBitmap(face);
			}
		}
		// ���� ��ͼ�� ���� ����
		String str = "";
		switch (msginfo.getMsgType()) {
		case MessageInfoModel.MSG_TYPE_TEXT:
			str = msginfo.getMsg();
			break;
		case MessageInfoModel.MSG_TYPE_IMAGE:
			str = "[ͼƬ��Ϣ-����鿴]";
			break;
		case MessageInfoModel.MSG_TYPE_SOUND:
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(msginfo.getMsg());
			mediaPlayer.prepare();
			int duration = mediaPlayer.getDuration() / 1000;
			mediaPlayer.release();
			str = "   " + duration + "'";
			break;
		default:
			str = "[������Ϣ]";
			break;
		}
		vh.txtContent.setText(str);
	}
}
