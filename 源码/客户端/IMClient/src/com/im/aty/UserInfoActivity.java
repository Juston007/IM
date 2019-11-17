package com.im.aty;

import java.io.File;

import com.im.db.IMDBUtil;
import com.im.model.FriendRelationModel;
import com.im.model.OnDataChangeListener;
import com.im.model.UserInfoModel;
import com.im.net.NetworkRequest;
import com.im.net.NetworkRequestUtil;
import com.im.util.Paramters;
import com.im.util.SharedPreferencesUtil;
import com.im.util.SystemBarTintManager;
import com.im.util.Util;
import com.im.view.ProgressDialog;
import com.im.view.ToastUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;

public class UserInfoActivity extends Activity {

	// View
	private TextView txtUid, txtAlias, txtSex, txtBirthDay;
	private ImageView imgFace;
	private Button btn;

	// 是否为好友
	private boolean isFriend = false;
	// 对方Uid
	private String friendUid;

	// 对方的信息
	private UserInfoModel userModel = null;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 返回
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// 设置标题
		Util.setActionBarTilte(getActionBar(), "用户资料");
		setContentView(R.layout.activity_user_info);
		// 初始化View
		initView();
		// 是否在聊天界面进入此界面 如果是那么隐藏按钮
		String ismsg = getIntent().getStringExtra("isMsg");
		if (ismsg != null) {
			btn.setVisibility(View.INVISIBLE);
		}
		// 获取Uid
		friendUid = getIntent().getStringExtra("Uid");
		// 获取好友状态
		getFriendStatus();
		// 是否存在此用户资料 不存在则拉取资料 否则直接显示
		if (!IMDBUtil.existUserInfo(friendUid)) {
			NetworkRequestUtil.getUserInfo(Paramters.ServerHostAddress,
					SharedPreferencesUtil.shared.getToken(), friendUid,
					new Handler() {
						@Override
						public void handleMessage(Message msg) {
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								displayData();
							} else {
								ToastUtil.makeText(UserInfoActivity.this,
										msg.obj.toString(), Toast.LENGTH_SHORT);
								btn.setVisibility(View.GONE);
							}
						}
					});
		} else {
			displayData();
		}
		// 初始化事件
		initEvent();
		Log.d("IMClient", UserInfoActivity.class.getCanonicalName() + ":初始化完毕");
		SystemBarTintManager.setStatusColor(this, getWindow());
	}

	public void getFriendStatus() {
		isFriend = IMDBUtil.isExistFriendRelation(new FriendRelationModel(
				SharedPreferencesUtil.shared.getUid(), friendUid, true));
		setView();
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

	private void initView() {
		txtUid = (TextView) findViewById(R.id.txt_user_uid);
		txtAlias = (TextView) findViewById(R.id.txt_user_alias);
		txtSex = (TextView) findViewById(R.id.txt_user_sex);
		txtBirthDay = (TextView) findViewById(R.id.txt_user_birthday);
		imgFace = (ImageView) findViewById(R.id.img_user_face);
		btn = (Button) findViewById(R.id.btn_user_sendmsg);
	}

	@SuppressLint("HandlerLeak")
	private void displayData() {
		// 显示资料信息
		userModel = IMDBUtil.getUserInfo(friendUid);
		txtUid.setText(userModel.getUid());
		txtAlias.setText(userModel.getAlias());
		txtSex.setText(userModel.getSex() ? "男" : "女");
		txtBirthDay.setText(Util.ToDateStringFromLong(userModel.getBirthDay()
				.getTime()));
		final File file = new File(userModel.getFacePath());
		if (file.exists()) {
			imgFace.setImageBitmap(BitmapFactory.decodeFile(file.toString()));
		} else {
			imgFace.setImageResource(R.drawable.face_icon_people);
			// 下载头像
			NetworkRequestUtil.getFace(getCacheDir().toString(),
					Paramters.ServerHostAddress,
					SharedPreferencesUtil.shared.getToken(),
					userModel.getUid(), new Handler() {
						@Override
						public void handleMessage(Message msg) {
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								imgFace.setImageBitmap(BitmapFactory
										.decodeFile(file.toString()));
							}
						}
					});
		}
	}

	private void initEvent() {
		// 监听好友变化
		IMDBUtil.setOnFriendChangeListener(new OnDataChangeListener() {

			@Override
			public void OnDataChnage() {
				getFriendStatus();
			}
		});
		btn.setOnClickListener(new OnClickListener() {

			@SuppressLint("HandlerLeak")
			@Override
			public void onClick(View arg0) {
				if (isFriend) {
					String uid = userModel.getUid();
					Intent intent = new Intent(UserInfoActivity.this,
							MessageActivity.class);
					intent.putExtra("Uid", uid);
					startActivity(intent);
					UserInfoActivity.this.finish();
				} else {
					final ProgressDialog progress = new ProgressDialog(
							UserInfoActivity.this, "发送请求中...");
					progress.show();
					NetworkRequestUtil.SendFriendRequest(
							Paramters.ServerHostAddress,
							SharedPreferencesUtil.shared.getToken(), friendUid,
							new Handler() {
								public void handleMessage(Message msg) {
									progress.dismiss();
									ToastUtil
											.makeText(
													UserInfoActivity.this,
													msg.what == NetworkRequest.REQUEST_SUCCESS ? "发送好友请求成功"
															: msg.obj
																	.toString(),
													Toast.LENGTH_SHORT);
								};
							});
				}
			}
		});
	}

	private void setView() {
		btn.setText(isFriend ? "发消息" : "加为好友");
	}
}
