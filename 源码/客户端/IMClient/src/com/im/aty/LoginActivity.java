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

	// ���ؿ�
	private ProgressDialog dialogProgress = null;
	// �˺����������
	private EditText etUid, etPwd;
	// δ����Ϣ���Ѷ���Ϣ
	private boolean msg1 = false, msg2 = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����Ϊ�ޱ���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ȫ����ʾ
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);
		// ��ʼ��
		init();
		// ��ʼ��ʱ��
		initEvent();
		Log.d("IMClient", LoginActivity.class.getCanonicalName() + ":��ʼ�����");
	}

	// ��ʼ��View
	private void init() {
		dialogProgress = new ProgressDialog(LoginActivity.this, "���ڵ���...");
		etUid = (EditText) findViewById(R.id.edit_login_userid);
		etPwd = (EditText) findViewById(R.id.edit_login_pwd);
	}

	private void initEvent() {
		// ��ʼ�����밴ť�¼�
		findViewById(R.id.btn_login).setOnClickListener(new OnClickListener() {

			@SuppressLint("HandlerLeak")
			@Override
			public void onClick(View arg0) {
				// �˺����벻��Ϊ��
				final String uid = etUid.getText().toString().trim();
				final String pwd = etPwd.getText().toString().trim();
				if (uid.equals("") || pwd.equals("")) {
					ToastUtil.makeText(LoginActivity.this, "�û����ƻ������벻����Ϊ�գ�",
							Toast.LENGTH_SHORT);
					return;
				}
				// ��ʾ���ؿ�
				dialogProgress.show();
				// �����������
				NetworkRequestUtil.Login(Paramters.ServerHostAddress, uid, pwd,
						new Handler() {
							@Override
							public void handleMessage(Message msg) {
								if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
									// ��ȡ�ɹ����ı���ؿ���ʾ�����֣���ȡ���Ѻ���������
									dialogProgress.setText("������������...");
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
		// ��������ַ
		findViewById(R.id.txt_server).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				final MyDialog dialog = new MyDialog(LoginActivity.this,
						"��������ַ", "ȷ��", "ȡ��", Paramters.ServerHostAddress, false);
				// ����ȷ����ť�¼�
				dialog.setConfrimOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						String input = dialog.getEdText().toString();
						if (!input.equals("")) {
							// ����Shared�ķ�������ַ
							SharedPreferencesUtil.shared.writeValue(
									"ServerHostAddress", input);
							// ���£���������������ַ
							Paramters.ServerHostAddress = input;
							// ��ʾ�û�
							ToastUtil.makeText(LoginActivity.this,
									"��������ַ�Ѿ�����!", Toast.LENGTH_LONG, true);
							// �رնԻ���
							dialog.dismiss();
						} else {
							ToastUtil.makeText(LoginActivity.this, "������Ϊ��Ŷ~",
									Toast.LENGTH_SHORT);
						}
					}
				});
				// ����ȡ����ť�¼�
				dialog.setCancelOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				dialog.show();

			}
		});
		// �޸�����
		findViewById(R.id.txt_resetpwd).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// ��ת���޸�������ҳ
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						Uri content_url = Uri.parse("http://"
								+ Paramters.ServerHostAddress
								+ "/Account/ResetPwd.aspx");
						intent.setData(content_url);
						startActivity(intent);
					}
				});
		// ע���˺�
		findViewById(R.id.txt_reg).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// ��ת��ע���˺���ҳ
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse("http://"
						+ Paramters.ServerHostAddress + "/Account/Reg.aspx");
				intent.setData(content_url);
				startActivity(intent);
			}
		});
	}

	// ��ȡ��������
	private void getFriendData(final String uid) {
		final String token = SharedPreferencesUtil.shared.getToken();
		NetworkRequestUtil.getFriendList(uid, Paramters.ServerHostAddress,
				token, new Handler() {

					@Override
					public void handleMessage(Message msg) {
						// ��ӡLog
						Log.d("IMClient", ""
								+ (msg.what == NetworkRequest.REQUEST_SUCCESS));
						// �������ɹ���ô��������
						if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
							// ��ȡ���غ�������
							final ArrayList<FriendRelationModel> relation = IMDBUtil
									.getFriendList(uid);
							// ����к��� ��Ϊ��
							if (relation != null) {
								for (int i = 0; i < relation.size(); i++) {
									// �Ƿ�Ϊ���һ������
									final boolean islast = (i + 1) == relation
											.size();
									// ���ݺ����б��ȡһ����Ϣ
									final String frienduid = relation.get(i)
											.getFriendUid();
									// �����Ѷ�����
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
													// ������һ����ȡ��� ��ô��ת����ҳ��
													if (islast) {
														msg1 = true;
														if (msg1 && msg2)
															startAty();
													}
												}
											}, getCacheDir().toString());
									// ����δ������
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
													// ������һ����ȡ��� ��ô��ת����ҳ��
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
		// ��ȡ��������
		NetworkRequestUtil.getUserInfo(Paramters.ServerHostAddress, token, uid,
				new Handler() {
					@Override
					public void handleMessage(Message msg) {
						Log.d("IMClient", msg.obj.toString());
					}
				});
		// ��ȡ����ͷ��
		NetworkRequestUtil.getFace(this.getCacheDir().toString(),
				Paramters.ServerHostAddress, token, uid, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						Log.d("IMClient", msg.obj.toString());
					}
				});
	}

	public void startAty() {
		// �������ȡ�����ô��ת��������
		dialogProgress.dismiss();
		Intent intent = new Intent(LoginActivity.this, MainFrameActivity.class);
		startActivity(intent);
		LoginActivity.this.finish();
	}
}
