package com.im.net;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.im.db.IMDBUtil;
import com.im.model.AppendData;
import com.im.model.EventInfoModel;
import com.im.model.FriendRelationModel;
import com.im.model.FriendRequestModel;
import com.im.model.MessageInfoModel;
import com.im.model.ResponseModel;
import com.im.model.UserInfoModel;
import com.im.util.SharedPreferencesUtil;
import com.im.util.Util;

//�����װ������������������ �����л�����
public class NetworkRequestUtil {

	// �����л�Ϊ��Ӧʵ��
	public static ResponseModel deSerializeResponse(String str)
			throws Exception {
		if (str.equals(""))
			return null;
		// ���JsonObj����
		JSONObject jsonobj = new JSONObject(str);
		// ��ȡ����״̬��
		int status = jsonobj.getInt("Status");
		// ��ȡ״̬��
		int statuscode = jsonobj.getInt("StatusCode");
		// ��ȡ��Ϣ
		String msg = jsonobj.getString("Messgae");
		// ��ȡ����
		String data = jsonobj.getString("Data");
		// ��ȡ������Ϣ
		String errormsg = jsonobj.getString("ErrorMsg");
		// ��ȡ��Ӧʵ��
		ResponseModel response = new ResponseModel(status, statuscode, msg,
				new AppendData(data), errormsg);
		return response;
	}

	// ��ȡͷ�� ��װ
	public static void getFace(final String path, String host, String token,
			final String queryuid, final Handler handler) {
		// �����б�
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		paramter.add(new BasicNameValuePair("queryuid", queryuid));
		// ����
		new NetworkRequest("http://" + host + "/" + MethodUrl.GetFace,
				paramter, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						boolean result = false;
						String errormsg = "";
						if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
							BufferedOutputStream bos = null;
							try {
								// ��ȡ��Ӧ�����
								ResponseModel model = NetworkRequestUtil
										.deSerializeResponse(msg.obj.toString());
								Log.d("IMClient", "GetModel");
								if (model.getStatus() == ResponseModel.STATUS_SUCCESS
										&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
									// תΪbyte[]�����浽����
									File file = new File(path + "/Face/"
											+ queryuid + ".jpg");
									// ����Ŀ¼
									file.mkdirs();
									// ����ļ�������ôɾ��������һ���µ��ļ�
									if (file.exists()) {
										file.delete();
									}
									file.createNewFile();
									// ��ȡ�����д������
									bos = new BufferedOutputStream(
											new FileOutputStream(file));
									byte[] data = Util
											.ToByteFromBase64String(model
													.getAppendData().toString());
									bos.write(data);
									bos.flush();
									result = true;
									errormsg = "�������";
									Log.d("IMClient",
											"NetworkRequestUtil-->����ͷ�����");
								} else {
									// ������Ϣ
									result = false;
									if (model.getStatus() != ResponseModel.STATUS_SUCCESS) {
										errormsg = model.getErrorMsg();
									} else {
										errormsg = model.getMessgae();
									}
								}
							} catch (Exception ex) {
								// ���������Ϣ
								Log.d("IMClient", ex.getMessage());
								errormsg = ex.getMessage();
							} finally {
								// ����������Ϊ�� ��ô�ͷ���Դ
								if (bos != null) {
									try {
										bos.close();
									} catch (IOException e) {
										Log.d("IMClient",
												"����ͷ��ʱ���������쳣:" + e.getMessage());
										errormsg = e.getMessage();
									}
								}
							}
						} else {
							errormsg = "����ʧ��";
						}
						// ֪ͨUI
						Message message = Message.obtain();
						message.what = result ? NetworkRequest.REQUEST_SUCCESS
								: NetworkRequest.REQUEST_FAILED;
						message.obj = errormsg;
						handler.sendMessage(message);
					}
				});
	}

	// ��ȡ�����б� ��װ
	public static void getFriendList(final String uid, String host,
			String token, final Handler handler) {
		// �����б�
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		// ����
		new NetworkRequest("http://" + host + "/" + MethodUrl.GetFriendList,
				paramter, new Handler() {
					@SuppressWarnings("unused")
					@Override
					public void handleMessage(Message msg) {
						boolean result = false;
						String errormsg = "";
						try {
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								ResponseModel model = NetworkRequestUtil
										.deSerializeResponse(msg.obj.toString());
								if (model.getStatus() == ResponseModel.STATUS_SUCCESS
										&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
									// ��ȡ�����б�
									ArrayList<UserInfoModel> infos = model
											.getAppendData().getFriendList();
									if (infos != null) {
										// ��ѯ������ȫ���ĺ����б� ���ƶ˲�ѯ������бȽ�
										// ����������ƶ�û�еĺ��ѽ��ᱻɾ�� �ƶ��б���û�еĽ�����ӵ�����
										ArrayList<FriendRelationModel> localinfos = IMDBUtil
												.getFriendList(uid);
										if (localinfos != null) {
											for (int i = 0; i < localinfos
													.size(); i++) {
												FriendRelationModel localinfo = localinfos
														.get(i);
												for (int j = 0; j < infos
														.size(); j++) {
													UserInfoModel info = infos
															.get(j);
													if (localinfo
															.getFriendUid()
															.equals(info
																	.getUid())) {
														break;
													}
													if (j + 1 == infos.size()) {
														IMDBUtil.removeFriend(localinfo);
													}
												}
											}
										}
										for (int i = 0; i < infos.size(); i++) {
											UserInfoModel info = infos.get(i);
											// �Ȳ����û���Ϣ�ٲ����ϵ
											// �����Ȳ����ϵ�ᴥ���ص��������¿�ָ���쳣
											IMDBUtil.insertUserInfo(info);
											IMDBUtil.addFriend(new FriendRelationModel(
													uid, info.getUid(), false));

										}
										result = true;
										errormsg = "��ȡ�����б�ɹ�";
									}
								} else {
									// ���������Ϣ
									if (model.getStatus() == ResponseModel.STATUS_SUCCESS
											&& model.getStatusCode() == ResponseModel.STATUS_FAILED) {
										ArrayList<FriendRelationModel> localinfos = IMDBUtil
												.getFriendList(uid);
										for (int i = 0; i < localinfos.size(); i++) {
											IMDBUtil.removeFriend(localinfos
													.get(i));
										}
									}
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "����ʧ��";
							}
						} catch (Exception ex) {
							// ���������Ϣ
							if (ex != null) {
								if (ex.getMessage() != null)
									Log.d("IMClient", ex.getMessage());
								else
									Log.d("IMClient",
											NetworkRequestUtil.class
													.getCanonicalName()
													+ " error��ex Ϊ null");
							} else {
								Log.d("IMClient",
										NetworkRequestUtil.class
												.getCanonicalName()
												+ " error��ex Ϊ null");
							}
						} finally {
							// ֪ͨUI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}
					}
				});
	}

	// ��ȡ��Ϣ���� ��װ
	public static void getMessage(final String uid, final String host,
			final String token, String touid, boolean isread,
			final Handler handler, final String path) {
		// �����б�
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		paramter.add(new BasicNameValuePair("touid", touid));
		paramter.add(new BasicNameValuePair("isread", String.valueOf(isread ? 1
				: 0)));
		// ��ʼ����
		new NetworkRequest("http://" + host + "/" + MethodUrl.GetMessage,
				paramter, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						boolean result = false;
						String errormsg = "";
						try {
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								ResponseModel model = NetworkRequestUtil
										.deSerializeResponse(msg.obj.toString());
								if (model.getStatus() == ResponseModel.STATUS_SUCCESS
										&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
									// ����Ϣ����֮���ݿ⵱��
									ArrayList<MessageInfoModel> infos = model
											.getAppendData()
											.getMessageInfoList();
									for (int i = 0; i < infos.size(); i++) {
										MessageInfoModel msginfo = infos.get(i);
										String id = msginfo.getMsgId();
										if (IMDBUtil.isExistMsg(id)) {
											continue;
										}
										IMDBUtil.insertMessageInfo(uid,
												msginfo, path);
										NetworkRequestUtil.getMsgContent(host,
												token, id,
												msginfo.getMsgType(), path,
												new Handler());
									}
									result = true;
									errormsg = "��ȡ�ɹ�";
								} else {
									// ������Ϣ
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "����ʧ��";
							}
						} catch (Exception ex) {
							// ���������Ϣ
							Log.d("IMClient", ex.getMessage());
						} finally {
							// ֪ͨUI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}

					}
				});
	}

	// ��ȡ��Ϣ���� ��װ
	public static void getMsgContent(String host, String token,
			final String msgid, final int msgtype, final String path,
			final Handler handler) {
		// �����б�
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		paramter.add(new BasicNameValuePair("msgid", msgid));
		paramter.add(new BasicNameValuePair("msgtype", String.valueOf(msgtype)));
		// ��ʼ����
		new NetworkRequest(
				"http://" + host + "/" + MethodUrl.GetMessageContent, paramter,
				new Handler() {
					@Override
					public void handleMessage(Message msg) {
						boolean result = false;
						String errormsg = "";
						try {
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								ResponseModel model = NetworkRequestUtil
										.deSerializeResponse(msg.obj.toString());
								if (model.getStatus() == ResponseModel.STATUS_SUCCESS
										&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
									// ������Ϣ����
									IMDBUtil.updateMessageContent(msgid, model
											.getAppendData().toString(), path,
											msgtype);
									result = true;
									errormsg = "��ȡ�ɹ�";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "����ʧ��";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// ֪ͨUI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}

					}
				});
	}

	// ��ȡ�û���Ϣ ��װ
	public static void getUserInfo(String host, String token, String queryuid,
			final Handler handler) {
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		paramter.add(new BasicNameValuePair("queryuid", queryuid));
		new NetworkRequest("http://" + host + "/" + MethodUrl.GetUserInfo,
				paramter, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						boolean result = false;
						String errormsg = "";
						try {
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								ResponseModel model = NetworkRequestUtil
										.deSerializeResponse(msg.obj.toString());
								if (model.getStatus() == ResponseModel.STATUS_SUCCESS
										&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
									// �����û���Ϣ
									IMDBUtil.insertUserInfo(model
											.getAppendData().getUserInfo());
									result = true;
									errormsg = "��ȡ�ɹ�";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "����ʧ��";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// ֪ͨUI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}

					}
				});
	}

	// ������Ϣ ��װ
	public static void SendMessage(String host, final String uid, String token,
			final MessageInfoModel msginfo, final Handler handler,
			final String path) {
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		paramter.add(new BasicNameValuePair("touid", msginfo.getReceiverUid()));
		paramter.add(new BasicNameValuePair("msgtype", String.valueOf(msginfo
				.getMsgType())));
		paramter.add(new BasicNameValuePair("msg", msginfo.getMsg()));
		new NetworkRequest("http://" + host + "/" + MethodUrl.SendMessage,
				paramter, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						boolean result = false;
						String errormsg = "";
						try {
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								ResponseModel model = NetworkRequestUtil
										.deSerializeResponse(msg.obj.toString());
								if (model.getStatus() == ResponseModel.STATUS_SUCCESS
										&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
									// �����ݿ⵱����Ӵ�����Ϣ
									IMDBUtil.insertMessageInfo(uid, msginfo,
											path);
									result = true;
									errormsg = "��ȡ�ɹ�";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "����ʧ��";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// ֪ͨUI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}
					}
				});
	}

	public static void Login(String host, final String uid, String pwd,
			final Handler handler) {
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("uid", uid));
		paramter.add(new BasicNameValuePair("pwd", pwd));
		new NetworkRequest("http://" + host + "/" + MethodUrl.Login, paramter,
				new Handler() {
					@Override
					public void handleMessage(Message msg) {
						boolean result = false;
						String errormsg = "";
						try {
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								ResponseModel model = NetworkRequestUtil
										.deSerializeResponse(msg.obj.toString());
								if (model.getStatus() == ResponseModel.STATUS_SUCCESS
										&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
									// ����
									SharedPreferencesUtil.shared.setLoginUser(
											uid, model.getAppendData()
													.getToken());
									result = true;
									errormsg = "����ɹ�";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "����ʧ��";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// ֪ͨUI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}
					}
				});
	}

	public static void GetUnHandlerEvent(String host, final String token,
			final Handler handler) {
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		new NetworkRequest("http://" + host + "/"
				+ MethodUrl.GetUnHandlerEventList, paramter, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				boolean result = false;
				Object errormsg = "";
				try {
					if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
						ResponseModel model = NetworkRequestUtil
								.deSerializeResponse(msg.obj.toString());
						if (model.getStatus() == ResponseModel.STATUS_SUCCESS
								&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
							// ȡ��δ�����¼�����ļ���
							ArrayList<EventInfoModel> list = model
									.getAppendData().getUnHandlerEventList();
							result = true;
							errormsg = list;
						} else {
							errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
									.getErrorMsg() : model.getMessgae();
						}
					} else {
						errormsg = "����ʧ��";
					}
				} catch (Exception ex) {
					Log.d("IMClient", ex.getMessage());
				} finally {
					// ֪ͨUI
					Message message = Message.obtain();
					message.what = result ? NetworkRequest.REQUEST_SUCCESS
							: NetworkRequest.REQUEST_FAILED;
					message.obj = errormsg;
					handler.sendMessage(message);
				}
			}
		});
	}

	// �����û���Ϣ ��װ
	public static void UpdateUserInfo(String host, String token,
			final UserInfoModel usermodel, final Handler handler) {
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		paramter.add(new BasicNameValuePair("date", Util
				.ToDateStringFromLong(usermodel.getBirthDay().getTime())));
		paramter.add(new BasicNameValuePair("sex", usermodel.getSex() ? "1"
				: "0"));
		paramter.add(new BasicNameValuePair("alias", usermodel.getAlias()));
		new NetworkRequest("http://" + host + "/" + MethodUrl.UpdateUserInfo,
				paramter, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						boolean result = false;
						String errormsg = "";
						try {
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								ResponseModel model = NetworkRequestUtil
										.deSerializeResponse(msg.obj.toString());
								if (model.getStatus() == ResponseModel.STATUS_SUCCESS
										&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
									// ���±������ݿ���Ϣ
									IMDBUtil.removeUserInfo(usermodel.getUid());
									IMDBUtil.insertUserInfo(usermodel);
									result = true;
									errormsg = "���³ɹ�";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "����ʧ��";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// ֪ͨUI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}

					}
				});
	}

	// ɾ������
	public static void DeleteFriend(String host, String token,
			final FriendRelationModel relation, final Handler handler) {
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		paramter.add(new BasicNameValuePair("uid1", relation.getFriendUid()));
		new NetworkRequest("http://" + host + "/" + MethodUrl.DeleteFriend,
				paramter, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						boolean result = false;
						String errormsg = "";
						try {
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								ResponseModel model = NetworkRequestUtil
										.deSerializeResponse(msg.obj.toString());
								if (model.getStatus() == ResponseModel.STATUS_SUCCESS
										&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
									// ���±������ݿ�
									IMDBUtil.removeFriend(relation);
									result = true;
									errormsg = "ɾ���ɹ�";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "����ʧ��";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// ֪ͨUI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}
					}
				});
	}

	// ����ͷ��
	public static void UpdateFace(String host, String token, final Bitmap head,
			final String path, final String uid, final Handler handler) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		head.compress(CompressFormat.JPEG, 100, bos);
		final byte[] buffer = bos.toByteArray();
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		// base64str
		paramter.add(new BasicNameValuePair("base64str", Util
				.ToBaseStringFromByte(buffer)));
		new NetworkRequest("http://" + host + "/" + MethodUrl.UpdateFace,
				paramter, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						boolean result = false;
						String errormsg = "";
						try {
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								ResponseModel model = NetworkRequestUtil
										.deSerializeResponse(msg.obj.toString());
								if (model.getStatus() == ResponseModel.STATUS_SUCCESS
										&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
									// ���±������ݿ�
									File file = new File(path + "/" + "Face/"
											+ uid + ".jpg");
									// ����ļ�������ôɾ�����½��ļ�
									file.mkdirs();
									if (file.exists()) {
										file.delete();
									}
									file.createNewFile();
									// ��ȡ�������д��
									FileOutputStream fos = new FileOutputStream(
											file);
									fos.write(buffer);
									fos.flush();
									fos.close();
									result = true;
									errormsg = "���³ɹ�";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "����ʧ��";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// ֪ͨUI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}
					}
				});
	}

	// ���ͺ�������
	public static void SendFriendRequest(String host, String token,
			final String uid, final Handler handler) {
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		paramter.add(new BasicNameValuePair("touid", uid));
		new NetworkRequest("http://" + host + "/" + MethodUrl.AddFriend,
				paramter, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						boolean result = false;
						String errormsg = "";
						try {
							if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
								ResponseModel model = NetworkRequestUtil
										.deSerializeResponse(msg.obj.toString());
								if (model.getStatus() == ResponseModel.STATUS_SUCCESS
										&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
									result = true;
									errormsg = "����ɹ�";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "����ʧ��";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// ֪ͨUI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}
					}
				});
	}

	public static void handlerFriendRequest(String host, String token,
			final String uid, boolean isaccept, final Handler handler) {

		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		paramter.add(new BasicNameValuePair("senduid", uid));
		paramter.add(new BasicNameValuePair("handler", isaccept ? String
				.valueOf(FriendRequestModel.ACCEPT) : String
				.valueOf(FriendRequestModel.IGNORE)));
		new NetworkRequest("http://" + host + "/"
				+ MethodUrl.HandlerFriendRequest, paramter, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				boolean result = false;
				String errormsg = "";
				try {
					if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
						ResponseModel model = NetworkRequestUtil
								.deSerializeResponse(msg.obj.toString());
						if (model.getStatus() == ResponseModel.STATUS_SUCCESS
								&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
							result = true;
							errormsg = uid;
						} else {
							errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
									.getErrorMsg() : model.getMessgae();
						}
					} else {
						errormsg = "����ʧ��";
					}
				} catch (Exception ex) {
					Log.d("IMClient", ex.getMessage());
				} finally {
					// ֪ͨUI
					Message message = Message.obtain();
					message.what = result ? NetworkRequest.REQUEST_SUCCESS
							: NetworkRequest.REQUEST_FAILED;
					message.obj = errormsg;
					handler.sendMessage(message);
				}
			}
		});
	}

	public static void getFriendRequestList(String host, String token,
			final Handler handler) {
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		new NetworkRequest("http://" + host + "/"
				+ MethodUrl.GetFriendRequestList, paramter, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				boolean result = false;
				String errormsg = "";
				try {
					if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
						ResponseModel model = NetworkRequestUtil
								.deSerializeResponse(msg.obj.toString());
						if (model.getStatus() == ResponseModel.STATUS_SUCCESS
								&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
							result = true;
							ArrayList<FriendRequestModel> requestlist = model
									.getAppendData().getFriendRequestList();
							String str = "";
							for (int i = 0; i < requestlist.size(); i++) {
								str += requestlist.get(i).getSendUid();
								if (i <= (requestlist.size() - 1))
									str += ",";
							}
							errormsg = str;
						} else {
							errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
									.getErrorMsg() : model.getMessgae();
						}
					} else {
						errormsg = "����ʧ��";
					}
				} catch (Exception ex) {
					Log.d("IMClient", ex.getMessage());
				} finally {
					// ֪ͨUI
					Message message = Message.obtain();
					message.what = result ? NetworkRequest.REQUEST_SUCCESS
							: NetworkRequest.REQUEST_FAILED;
					message.obj = errormsg;
					handler.sendMessage(message);
				}
			}
		});

	}
}
