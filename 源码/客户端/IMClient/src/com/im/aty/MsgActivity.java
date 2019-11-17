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

	// �Ƿ�Ϊ�״������б�Ϊ��(����App����)
	private boolean isfirst = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_msg, null);
		// ��ʼ��View
		initView();
		// ��������
		initAdapter();
		// ��ʼ���¼�
		initEvent();
		Log.d("IMClient", MsgActivity.class.getCanonicalName() + ":��ʼ�����");
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
		// �������ݱ仯 ����б仯��ô���³�ʼ������
		OnDataChangeListener listener = new OnDataChangeListener() {

			@Override
			public void OnDataChnage() {
				// ���¼���
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
		// ����ListViewItem�ĵ���ͳ����¼� ����ɾ������ �����������
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
				final MyDialog dialog = new MyDialog(getActivity(), "�������б���ɾ��",
						"ȷ��Ҫ��������������б���ɾ���𣿵������߶Է�����ѶϢʱ�����³����������б��С�", "ȷ��",
						"ȡ��", false, true);
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
		// �������ǰ������
		msgList.clear();
		userList.clear();
		unReadCountList.clear();
		// ��ȡ����
		String uid = SharedPreferencesUtil.shared.getUid();
		ArrayList<UserInfoModel> infos = IMDBUtil.getDisplayUserInfoList(uid);
		if (infos == null) {
			if (isfirst) {
				ToastUtil.makeText(getActivity(), "����û������Ŷ����ȥ����ɣ�",
						Toast.LENGTH_SHORT);
				isfirst = false;
			}
			return;
		}
		// ��ʱ����Դ
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
		// ����ʱ���������
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
				// ���Ӧ��ֵҲ����
				Date datei = msgs.get(i) != null ? msgs.get(i).getTime()
						: new Date();
				Date datej = msgs.get(j) != null ? msgs.get(j).getTime()
						: new Date();
				if (datei.getTime() < datej.getTime()) {
					UserInfoModel info = users.get(i);
					MessageInfoModel msg = msgs.get(i);
					Integer unread = unreads.get(i);
					// ����ֵ
					users.set(i, users.get(j));
					msgs.set(i, msgs.get(j));
					unreads.set(i, unreads.get(j));
					users.set(j, info);
					msgs.set(j, msg);
					unreads.set(j, unread);
				}
			}
		}
		// ��ӵ�����������Դ����
		userList.addAll(users);
		msgList.addAll(msgs);
		unReadCountList.addAll(unreads);
	}

	private void displayData() {
		// ��������
		initData();
		// ֪ͨ����Դ�����˸ı�
		msgInfoAdapter.notifyDataSetChanged();
	}

	private void initAdapter() {
		// ��ʼ��������
		msgInfoAdapter = new MessageInfoAdapter(msgList, userList,
				unReadCountList, getActivity());
		// ΪListView����������
		lvMsg.setAdapter(msgInfoAdapter);
	}
}
