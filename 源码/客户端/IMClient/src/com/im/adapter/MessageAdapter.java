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

//信息列表适配器
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
		// 这里不具体写了，可以参考联系人适配器的getView方法
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
		// 自己为发送者那么就是右边
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
		// 初始化事件与View
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
		// 长按信息删除信息
		vh.txtContent.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// 设置相关点击事件
				final MyDialog dialog = new MyDialog(
						context,
						"删除消息",
						"确定要删除此消息吗？此消息只会在本地删除，云端记录并不会删除。当您再次同步聊天记录时，此讯息会重新下载到本地。",
						"确定", "取消", true, true);
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
		// 点击头像进入信息详情
		vh.imgFace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, UserInfoActivity.class);
				intent.putExtra("Uid", msginfo.getSendUid());
				intent.putExtra("isMsg", "True");
				context.startActivity(intent);
			}
		});
		// 点击信息查看信息详情
		vh.txtContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 文字：查看文字
				// 图片：查看图片
				// 语音：播放语音
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
		// 如果此条讯息与上一条讯息时间相差大于1分钟那么就要显示他的时间点 消息为今天所发时只显示时间 昨天所发显示 昨天 19：00
		// 否则显示全部日期
		// 时间 如果与上一条时间差距1分钟 那么将会显示时间
		vh.txtTime.setVisibility(View.VISIBLE);
		long time = 1000 * 60;
		Date nextmsgdate = new Date();
		if ((arg0 - 1) != -1)
			nextmsgdate = msgData.get(arg0 - 1).getTime();
		String date = "";
		int daycount = 0;
		// 先判断要不要显示时间
		if (Math.abs((nextmsgdate.getTime() - msginfo.getTime().getTime())) < time) {
			vh.txtTime.setVisibility(View.GONE);
		} else {
			// 计算今天与此条讯息相差的时间然后显示出来
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
			date = "昨天";
			break;
		default:
			date = Util.ToDateStringFromLong(msginfo.getTime().getTime());
			break;
		}

		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		date = ' ' + date + ' '
				+ df.format(new Date(msginfo.getTime().getTime())) + ' ';
		vh.txtTime.setText(date);
		// 头像
		String facepath = null;
		facepath = IMDBUtil.getUserInfo(msginfo.getSendUid()).getFacePath();
		// 用HashMap把Bitmap对象存储起来避免重复读取卡顿
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
		// 内容 有图像 文字 语音
		String str = "";
		switch (msginfo.getMsgType()) {
		case MessageInfoModel.MSG_TYPE_TEXT:
			str = msginfo.getMsg();
			break;
		case MessageInfoModel.MSG_TYPE_IMAGE:
			str = "[图片信息-点击查看]";
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
			str = "[其他信息]";
			break;
		}
		vh.txtContent.setText(str);
	}
}
