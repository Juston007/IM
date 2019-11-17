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
		// 初始化View
		initView();
		// 初始化时间
		initEvent();
		// 初始化联系人适配器
		contactsAdapter = ContactsAdapter.getInterface();
		contactsAdapter.setParamter(getActivity(), data);
		// 为ListView设置适配器
		listContacts.setAdapter(contactsAdapter);
		// 打印Log
		Log.d("IMClient", ContactsActivity.class.getCanonicalName() + ":初始化完毕");
		return view;
	}

	// 初始化View
	private void initView() {
		listContacts = (ListView) view.findViewById(R.id.list_contacts);
		progressdialog = new ProgressDialog(getActivity(), "查找用户中");
	}

	// 每次界面可见时重新加载一次数据
	@Override
	public void onStart() {
		super.onStart();
		initData();
		contactsAdapter.notifyDataSetChanged();
	}

	// 获取数据
	private void initData() {
		// 清除原先数据
		data.clear();
		// 重新查询数据
		ArrayList<FriendRelationModel> fr = IMDBUtil
				.getFriendList(SharedPreferencesUtil.shared.getUid());
		if (fr != null)
			for (int i = 0; i < fr.size(); i++) {
				data.add(IMDBUtil.getUserInfo(fr.get(i).getFriendUid()));
			}
	}

	// 初始化事件
	private void initEvent() {
		// 监听好友关系变化
		IMDBUtil.setOnFriendChangeListener(new OnDataChangeListener() {

			@Override
			public void OnDataChnage() {
				// 初始化数据
				initData();
				contactsAdapter.notifyDataSetChanged();
				Log.d("IMClient", "重新加载了好友列表");
			}
		});
		// 长按删除好友
		listContacts.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// 删除好友
				final MyDialog dialog = new MyDialog(getActivity(), "删除好友",
						"确定要删除此好友吗，删除后你们将解除好友关系，您也将从对方的好友列表当中删除。", "删除", "取消",
						true, true);
				dialog.setConfrimOnClickListener(new OnClickListener() {

					@SuppressLint("HandlerLeak")
					@Override
					public void onClick(View arg0) {
						// 隐藏Dialog
						dialog.dismiss();
						// 获取实例
						progressdialog = new ProgressDialog(getActivity(),
								"正在请求中....");
						// 展示
						progressdialog.show();
						// 删除好友
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
													"删除成功！",
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
		// 点击跳转到资料详情页
		listContacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(),
						UserInfoActivity.class);
				// 附加数据UID 代表了好友的Uid 如果没有 那么将会区别显示
				intent.putExtra("Uid", data.get(arg2).getUid());
				startActivity(intent);
			}
		});
		// 好友请求按钮
		view.findViewById(R.id.ll_addfriend).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// 弹出对话框 根据Uid跳转到对应用户的资料界面
						final MyDialog adddialog = new MyDialog(getActivity(),
								"根据Uid查找用户", "查找", "取消", "10001", false);
						adddialog
								.setConfrimOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										final String uid = adddialog
												.getEdText().toString();
										// Uid不可为空
										if (!uid.equals(""))
											// Uid不可以为自己
											if (!uid.equals(SharedPreferencesUtil.shared
													.getUid())) {
												// 加载框
												progressdialog.show();
												// 请求数据并保存到本地
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
																		//隐藏加载框
																		progressdialog
																				.dismiss();
																		//处理数据
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
																							"不存在的Uid",
																							Toast.LENGTH_SHORT);
																		}
																	}
																});
											} else {
												ToastUtil.makeText(
														getActivity(),
														"不可以添加自己哦~",
														Toast.LENGTH_SHORT);
											}
									}
								});
						// 隐藏对话框
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
		// 处理好友请求
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
