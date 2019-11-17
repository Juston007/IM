package com.im.aty;

import java.util.ArrayList;

import com.im.adapter.FriendRequestHandlerAdapter;
import com.im.net.NetworkRequest;
import com.im.net.NetworkRequestUtil;
import com.im.util.Paramters;
import com.im.util.SharedPreferencesUtil;
import com.im.util.SystemBarTintManager;
import com.im.util.Util;
import com.im.view.ToastUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;

@SuppressLint("HandlerLeak")
public class HandlerRequestActivity extends Activity {

	// View
	public ListView lvFriendRequest;

	// Data
	public ArrayList<String> list = new ArrayList<String>();

	// Adapter
	public FriendRequestHandlerAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handler_request);
		// ����ActionBar����
		Util.setActionBarTilte(getActionBar(), "�µ�����");
		// ȥ��Logo
		getActionBar().setDisplayShowHomeEnabled(false);
		// չʾ���ذ�ť
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// ��ʼ��View
		initView();
		// ��ʼ��������
		adapter = new FriendRequestHandlerAdapter(this, list);
		// ��������
		lvFriendRequest.setAdapter(adapter);
		// ��ʼ������Դ
		initData();
		SystemBarTintManager.setStatusColor(this, getWindow());
	}

	// ��ʼ��View
	private void initView() {
		lvFriendRequest = (ListView) findViewById(R.id.lv_friend_request);
	}

	// ��ʼ������
	private void initData() {
		// ��ȡ���������б�
		NetworkRequestUtil.getFriendRequestList(Paramters.ServerHostAddress,
				SharedPreferencesUtil.shared.getToken(), new Handler() {
					public void handleMessage(Message msg) {
						if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
							String[] uids = msg.obj.toString().split(",");
							// ���ԭ�����ݣ�����������ӵ�����Դ����
							list.clear();
							for (int i = 0; i < uids.length; i++) {
								list.add(uids[i]);
							}
							adapter.notifyDataSetChanged();
						} else {
							ToastUtil.makeText(HandlerRequestActivity.this,
									msg.obj.toString(), Toast.LENGTH_SHORT);
						}
					}
				});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
