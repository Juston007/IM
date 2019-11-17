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
		// 设置ActionBar标题
		Util.setActionBarTilte(getActionBar(), "新的朋友");
		// 去掉Logo
		getActionBar().setDisplayShowHomeEnabled(false);
		// 展示返回按钮
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// 初始化View
		initView();
		// 初始化适配器
		adapter = new FriendRequestHandlerAdapter(this, list);
		// 绑定适配器
		lvFriendRequest.setAdapter(adapter);
		// 初始化数据源
		initData();
		SystemBarTintManager.setStatusColor(this, getWindow());
	}

	// 初始化View
	private void initView() {
		lvFriendRequest = (ListView) findViewById(R.id.lv_friend_request);
	}

	// 初始化数据
	private void initData() {
		// 获取好友请求列表
		NetworkRequestUtil.getFriendRequestList(Paramters.ServerHostAddress,
				SharedPreferencesUtil.shared.getToken(), new Handler() {
					public void handleMessage(Message msg) {
						if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
							String[] uids = msg.obj.toString().split(",");
							// 清除原本数据，将新数据添加到数据源当中
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
