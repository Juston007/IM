package com.im.aty;

import java.util.ArrayList;
import java.util.Date;

import com.im.adapter.MessageInfoAdapter;
import com.im.db.IMDBUtil;
import com.im.model.FriendRelationModel;
import com.im.model.MessageInfoModel;
import com.im.model.OnDataChangeListener;
import com.im.model.UserInfoModel;
import com.im.util.MediaUtil;
import com.im.util.SharedPreferencesUtil;
import com.im.view.MyDialog;
import com.im.view.ToastUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MsgActivity extends Fragment {

	// View
	private ListView lvMsg;
	private View view;
	// Adapter
	private MessageInfoAdapter msgInfoAdapter;
	// Model
	private ArrayList<MessageInfoModel> msgList = new ArrayList<MessageInfoModel>();
	private ArrayList<UserInfoModel> userList = new ArrayList<UserInfoModel>();
	private ArrayList<Integer> unReadCountList = new ArrayList<Integer>();

	// 是否为首次聊天列表为空(本次App启动)
	private boolean isfirst = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_msg, null);
		// 初始化View
		initView();
		// 绑定适配器
		initAdapter();
		// 初始化事件
		initEvent();
		Log.d("IMClient", MsgActivity.class.getCanonicalName() + ":初始化完毕");
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		displayData();
	}

	private void initView() {
		lvMsg = (ListView) view.findViewById(R.id.list_msginfo);
	}

	private void initEvent() {
		// 监听数据变化 如果有变化那么重新初始化数据
		OnDataChangeListener listener = new OnDataChangeListener() {

			@Override
			public void OnDataChnage() {
				// 重新加载
				displayData();
			}
		};
		IMDBUtil.setOnDisplayListener(listener);
		IMDBUtil.setOnMessageChangeListener(listener);
		IMDBUtil.setOnInsertMsgListener(new OnDataChangeListener() {

			@Override
			public void OnDataChnage() {
				MediaUtil.playBell(getActivity());
			}
		});
		// 监听ListViewItem的点击和长按事件 长按删除好友 点击资料详情
		lvMsg.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(), MessageActivity.class);
				intent.putExtra("Uid", userList.get(arg2).getUid());
				startActivity(intent);
			}
		});
		lvMsg.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final int itemid = arg2;
				final MyDialog dialog = new MyDialog(getActivity(), "从聊天列表中删除",
						"确定要将此聊天从聊天列表中删除吗？当您或者对方发送讯息时会重新出现在聊天列表当中。", "确定",
						"取消", false, true);
				dialog.setCancelOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				dialog.setConfrimOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
						IMDBUtil.setDisplay(new FriendRelationModel(
								SharedPreferencesUtil.shared.getUid(), userList
										.get(itemid).getUid(), false));
					}
				});
				dialog.show();
				return true;
			}
		});
	}

	private void initData() {
		// 清除掉以前的数据
		msgList.clear();
		userList.clear();
		unReadCountList.clear();
		// 获取数据
		String uid = SharedPreferencesUtil.shared.getUid();
		ArrayList<UserInfoModel> infos = IMDBUtil.getDisplayUserInfoList(uid);
		if (infos == null) {
			if (isfirst) {
				ToastUtil.makeText(getActivity(), "您还没有聊天哦！快去聊天吧！",
						Toast.LENGTH_SHORT);
				isfirst = false;
			}
			return;
		}
		// 临时数据源
		ArrayList<UserInfoModel> users = new ArrayList<UserInfoModel>();
		ArrayList<MessageInfoModel> msgs = new ArrayList<MessageInfoModel>();
		ArrayList<Integer> unreads = new ArrayList<Integer>();
		users.addAll(infos);
		for (int i = 0; i < users.size(); i++) {
			String firenduid = users.get(i).getUid();
			MessageInfoModel msgmodel = IMDBUtil.getLastMsgInfo(uid, firenduid);
			msgs.add(msgmodel);
			unreads.add(IMDBUtil.getUnReadCount(uid, firenduid));
		}
		// 根据时间进行排序
		for (int i = 0; i < users.size(); i++) {
			for (int j = i; j < users.size(); j++) {
				if (msgs.get(i) == null) {
					users.remove(i);
					msgs.remove(i);
					unreads.remove(i);
					if (i != 0) {
						i--;
						j--;
					}
					continue;
				}
				if (msgs.get(j) == null) {
					users.remove(j);
					msgs.remove(j);
					unreads.remove(j);
					if (i != 0) {
						i--;
						j--;
					}
					continue;
				}
				// 相对应的值也排序
				Date datei = msgs.get(i) != null ? msgs.get(i).getTime()
						: new Date();
				Date datej = msgs.get(j) != null ? msgs.get(j).getTime()
						: new Date();
				if (datei.getTime() < datej.getTime()) {
					UserInfoModel info = users.get(i);
					MessageInfoModel msg = msgs.get(i);
					Integer unread = unreads.get(i);
					// 交换值
					users.set(i, users.get(j));
					msgs.set(i, msgs.get(j));
					unreads.set(i, unreads.get(j));
					users.set(j, info);
					msgs.set(j, msg);
					unreads.set(j, unread);
				}
			}
		}
		// 添加到真正的数据源当中
		userList.addAll(users);
		msgList.addAll(msgs);
		unReadCountList.addAll(unreads);
	}

	private void displayData() {
		// 更新数据
		initData();
		// 通知数据源发生了改变
		msgInfoAdapter.notifyDataSetChanged();
	}

	private void initAdapter() {
		// 初始化适配器
		msgInfoAdapter = new MessageInfoAdapter(msgList, userList,
				unReadCountList, getActivity());
		// 为ListView设置适配器
		lvMsg.setAdapter(msgInfoAdapter);
	}
}
