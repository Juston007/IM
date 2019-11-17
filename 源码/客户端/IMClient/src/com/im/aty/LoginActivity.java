package com.im.aty;

import java.util.ArrayList;

import com.im.db.IMDBUtil;
import com.im.model.FriendRelationModel;
import com.im.net.NetworkRequest;
import com.im.net.NetworkRequestUtil;
import com.im.util.Paramters;
import com.im.util.SharedPreferencesUtil;
import com.im.view.MyDialog;
import com.im.view.ProgressDialog;
import com.im.view.ToastUtil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class LoginActivity extends Activity {

	// 加载框
	private ProgressDialog dialogProgress = null;
	// 账号密码输入框
	private EditText etUid, etPwd;
	// 未读信息与已读信息
	private boolean msg1 = false, msg2 = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置为无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);
		// 初始化
		init();
		// 初始化时间
		initEvent();
		Log.d("IMClient", LoginActivity.class.getCanonicalName() + ":初始化完毕");
	}

	// 初始化View
	private void init() {
		dialogProgress = new ProgressDialog(LoginActivity.this, "正在登入...");
		etUid = (EditText) findViewById(R.id.edit_login_userid);
		etPwd = (EditText) findViewById(R.id.edit_login_pwd);
	}

	private void initEvent() {
		// 初始化登入按钮事件
		findViewById(R.id.btn_login).setOnClickListener(new OnClickListener() {

			@SuppressLint("HandlerLeak")
			@Override
			public void onClick(View arg0) {
				// 账号密码不可为空
				final String uid = etUid.getText().toString().trim();
				final String pwd = etPwd.getText().toString().trim();
				if (uid.equals("") || pwd.equals("")) {
					ToastUtil.makeText(LoginActivity.this, "用户名称或者密码不可以为空！",
							Toast.LENGTH_SHORT);
					return;
				}
				// 显示加载框
				dialogProgress.show();
				// 发起登入请求
				NetworkRequestUtil.Login(Paramters.ServerHostAddress, uid, pwd,
						new Handler() {
							@Override
							public void handleMessage(Message msg) {
								if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
									// 获取成功，改变加载框显示的文字，获取好友和聊天数据
									dialogProgress.setText("正在请求数据...");
									getFriendData(uid);
									MainFrameActivity.beOverdue = false;
								} else {
									dialogProgress.dismiss();
									ToastUtil.makeText(LoginActivity.this,
											msg.obj.toString(),
											Toast.LENGTH_SHORT, false);
								}
							}
						});
			}
		});
		// 服务器地址
		findViewById(R.id.txt_server).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				final MyDialog dialog = new MyDialog(LoginActivity.this,
						"服务器地址", "确定", "取消", Paramters.ServerHostAddress, false);
				// 处理确定按钮事件
				dialog.setConfrimOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						String input = dialog.getEdText().toString();
						if (!input.equals("")) {
							// 更新Shared的服务器地址
							SharedPreferencesUtil.shared.writeValue(
									"ServerHostAddress", input);
							// 更新（参数）服务器地址
							Paramters.ServerHostAddress = input;
							// 提示用户
							ToastUtil.makeText(LoginActivity.this,
									"服务器地址已经更新!", Toast.LENGTH_LONG, true);
							// 关闭对话框
							dialog.dismiss();
						} else {
							ToastUtil.makeText(LoginActivity.this, "不可以为空哦~",
									Toast.LENGTH_SHORT);
						}
					}
				});
				// 处理取消按钮事件
				dialog.setCancelOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				dialog.show();

			}
		});
		// 修改密码
		findViewById(R.id.txt_resetpwd).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// 跳转到修改密码网页
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						Uri content_url = Uri.parse("http://"
								+ Paramters.ServerHostAddress
								+ "/Account/ResetPwd.aspx");
						intent.setData(content_url);
						startActivity(intent);
					}
				});
		// 注册账号
		findViewById(R.id.txt_reg).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 跳转到注册账号网页
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse("http://"
						+ Paramters.ServerHostAddress + "/Account/Reg.aspx");
				intent.setData(content_url);
				startActivity(intent);
			}
		});
	}

	// 获取好友数据
	private void getFriendData(final String uid) {
		final String token = SharedPreferencesUtil.shared.getToken();
		NetworkRequestUtil.getFriendList(uid, Paramters.ServerHostAddress,
				token, new Handler() {

					@Override
					public void handleMessage(Message msg) {
						// 打印Log
						Log.d("IMClient", ""
								+ (msg.what == NetworkRequest.REQUEST_SUCCESS));
						// 如果请求成功那么解析数据
						if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
							// 获取本地好友数据
							final ArrayList<FriendRelationModel> relation = IMDBUtil
									.getFriendList(uid);
							// 如果有好友 不为空
							if (relation != null) {
								for (int i = 0; i < relation.size(); i++) {
									// 是否为最后一条好友
									final boolean islast = (i + 1) == relation
											.size();
									// 根据好友列表获取一遍信息
									final String frienduid = relation.get(i)
											.getFriendUid();
									// 请求已读数据
									NetworkRequestUtil.getMessage(uid,
											Paramters.ServerHostAddress, token,
											frienduid, true, new Handler() {
												@Override
												public void handleMessage(
														Message msg) {
													if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
														Log.d("IMClient",
																"readgetmsg");
													} else {
														Log.d("IMClient",
																"ReadMsg:"
																		+ msg.obj
																				.toString());
													}
													// 如果最后一条获取完毕 那么跳转到主页面
													if (islast) {
														msg1 = true;
														if (msg1 && msg2)
															startAty();
													}
												}
											}, getCacheDir().toString());
									// 请求未读数据
									NetworkRequestUtil.getMessage(uid,
											Paramters.ServerHostAddress, token,
											frienduid, false, new Handler() {
												@Override
												public void handleMessage(
														Message msg) {
													if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
														Log.d("IMClient",
																"unreadgetmsg");
													} else {
														Log.d("IMClient",
																"UnReadMsg:"
																		+ msg.obj
																				.toString());
													}
													// 如果最后一条获取完毕 那么跳转到主页面
													if (islast) {
														msg2 = true;
														if (msg1 && msg2)
															startAty();
													}
												}
											}, getCacheDir().toString());
								}
							} else {
								startAty();
							}
						} else {
							startAty();
						}
					}
				});
		// 获取个人资料
		NetworkRequestUtil.getUserInfo(Paramters.ServerHostAddress, token, uid,
				new Handler() {
					@Override
					public void handleMessage(Message msg) {
						Log.d("IMClient", msg.obj.toString());
					}
				});
		// 获取个人头像
		NetworkRequestUtil.getFace(this.getCacheDir().toString(),
				Paramters.ServerHostAddress, token, uid, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						Log.d("IMClient", msg.obj.toString());
					}
				});
	}

	public void startAty() {
		// 如果都获取完毕那么跳转到主界面
		dialogProgress.dismiss();
		Intent intent = new Intent(LoginActivity.this, MainFrameActivity.class);
		startActivity(intent);
		LoginActivity.this.finish();
	}
}
