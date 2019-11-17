package com.im.aty;

import java.util.ArrayList;

import com.im.adapter.ContactsAdapter;
import com.im.db.IMDBUtil;
import com.im.model.FriendRelationModel;
import com.im.model.OnDataChangeListener;
import com.im.model.UserInfoModel;
import com.im.net.NetworkRequest;
import com.im.net.NetworkRequestUtil;
import com.im.util.Paramters;
import com.im.util.SharedPreferencesUtil;
import com.im.view.MyDialog;
import com.im.view.ProgressDialog;
import com.im.view.ToastUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

@SuppressLint("HandlerLeak")
public class ContactsActivity extends Fragment {

	// View
	private View view;
	private ListView listContacts;

	// Data
	private ArrayList<UserInfoModel> data = new ArrayList<UserInfoModel>();

	// Adapter
	private ContactsAdapter contactsAdapter = null;

	// ProgressDialog
	private ProgressDialog progressdialog = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_contacts, null);
		// ��ʼ��View
		initView();
		// ��ʼ��ʱ��
		initEvent();
		// ��ʼ����ϵ��������
		contactsAdapter = ContactsAdapter.getInterface();
		contactsAdapter.setParamter(getActivity(), data);
		// ΪListView����������
		listContacts.setAdapter(contactsAdapter);
		// ��ӡLog
		Log.d("IMClient", ContactsActivity.class.getCanonicalName() + ":��ʼ�����");
		return view;
	}

	// ��ʼ��View
	private void initView() {
		listContacts = (ListView) view.findViewById(R.id.list_contacts);
		progressdialog = new ProgressDialog(getActivity(), "�����û���");
	}

	// ÿ�ν���ɼ�ʱ���¼���һ������
	@Override
	public void onStart() {
		super.onStart();
		initData();
		contactsAdapter.notifyDataSetChanged();
	}

	// ��ȡ����
	private void initData() {
		// ���ԭ������
		data.clear();
		// ���²�ѯ����
		ArrayList<FriendRelationModel> fr = IMDBUtil
				.getFriendList(SharedPreferencesUtil.shared.getUid());
		if (fr != null)
			for (int i = 0; i < fr.size(); i++) {
				data.add(IMDBUtil.getUserInfo(fr.get(i).getFriendUid()));
			}
	}

	// ��ʼ���¼�
	private void initEvent() {
		// �������ѹ�ϵ�仯
		IMDBUtil.setOnFriendChangeListener(new OnDataChangeListener() {

			@Override
			public void OnDataChnage() {
				// ��ʼ������
				initData();
				contactsAdapter.notifyDataSetChanged();
				Log.d("IMClient", "���¼����˺����б�");
			}
		});
		// ����ɾ������
		listContacts.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// ɾ������
				final MyDialog dialog = new MyDialog(getActivity(), "ɾ������",
						"ȷ��Ҫɾ���˺�����ɾ�������ǽ�������ѹ�ϵ����Ҳ���ӶԷ��ĺ����б���ɾ����", "ɾ��", "ȡ��",
						true, true);
				dialog.setConfrimOnClickListener(new OnClickListener() {

					@SuppressLint("HandlerLeak")
					@Override
					public void onClick(View arg0) {
						// ����Dialog
						dialog.dismiss();
						// ��ȡʵ��
						progressdialog = new ProgressDialog(getActivity(),
								"����������....");
						// չʾ
						progressdialog.show();
						// ɾ������
						NetworkRequestUtil.DeleteFriend(
								Paramters.ServerHostAddress,
								SharedPreferencesUtil.shared.getToken(),
								new FriendRelationModel(
										SharedPreferencesUtil.shared.getUid(),
										data.get(arg2).getUid(), false),
								new Handler() {
									@Override
									public void handleMessage(Message msg) {
										progressdialog.dismiss();
										if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
											contactsAdapter
													.notifyDataSetChanged();
											ToastUtil.makeText(getActivity(),
													"ɾ���ɹ���",
													Toast.LENGTH_SHORT, true);
										} else {
											ToastUtil.makeText(getActivity(),
													msg.obj.toString(),
													Toast.LENGTH_SHORT, false);
										}
									}
								});
					}
				});
				//
				dialog.setCancelOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				dialog.show();
				return true;
			}
		});
		// �����ת����������ҳ
		listContacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(),
						UserInfoActivity.class);
				// ��������UID �����˺��ѵ�Uid ���û�� ��ô����������ʾ
				intent.putExtra("Uid", data.get(arg2).getUid());
				startActivity(intent);
			}
		});
		// ��������ť
		view.findViewById(R.id.ll_addfriend).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// �����Ի��� ����Uid��ת����Ӧ�û������Ͻ���
						final MyDialog adddialog = new MyDialog(getActivity(),
								"����Uid�����û�", "����", "ȡ��", "10001", false);
						adddialog
								.setConfrimOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										final String uid = adddialog
												.getEdText().toString();
										// Uid����Ϊ��
										if (!uid.equals(""))
											// Uid������Ϊ�Լ�
											if (!uid.equals(SharedPreferencesUtil.shared
													.getUid())) {
												// ���ؿ�
												progressdialog.show();
												// �������ݲ����浽����
												NetworkRequestUtil
														.getUserInfo(
																Paramters.ServerHostAddress,
																SharedPreferencesUtil.shared
																		.getToken(),
																uid,
																new Handler() {
																	@Override
																	public void handleMessage(
																			Message msg) {
																		//���ؼ��ؿ�
																		progressdialog
																				.dismiss();
																		//��������
																		if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
																			adddialog
																					.dismiss();
																			Intent intent = new Intent(
																					getActivity(),
																					UserInfoActivity.class);
																			intent.putExtra(
																					"Uid",
																					uid);
																			startActivity(intent);
																		} else {
																			ToastUtil
																					.makeText(
																							getActivity(),
																							"�����ڵ�Uid",
																							Toast.LENGTH_SHORT);
																		}
																	}
																});
											} else {
												ToastUtil.makeText(
														getActivity(),
														"����������Լ�Ŷ~",
														Toast.LENGTH_SHORT);
											}
									}
								});
						// ���ضԻ���
						adddialog
								.setCancelOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										adddialog.dismiss();
									}
								});
						adddialog.show();
					}
				});
		// �����������
		view.findViewById(R.id.ll_handler_request).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(getActivity(),
								HandlerRequestActivity.class);
						startActivity(intent);
					}
				});
	}
}
