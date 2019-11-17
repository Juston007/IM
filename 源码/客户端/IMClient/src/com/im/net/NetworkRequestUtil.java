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

//此类封装各个方法的网络请求 反序列化操作
public class NetworkRequestUtil {

	// 反序列化为响应实体
	public static ResponseModel deSerializeResponse(String str)
			throws Exception {
		if (str.equals(""))
			return null;
		// 获得JsonObj对象
		JSONObject jsonobj = new JSONObject(str);
		// 获取请求状态码
		int status = jsonobj.getInt("Status");
		// 获取状态码
		int statuscode = jsonobj.getInt("StatusCode");
		// 获取信息
		String msg = jsonobj.getString("Messgae");
		// 获取数据
		String data = jsonobj.getString("Data");
		// 获取错误信息
		String errormsg = jsonobj.getString("ErrorMsg");
		// 获取响应实体
		ResponseModel response = new ResponseModel(status, statuscode, msg,
				new AppendData(data), errormsg);
		return response;
	}

	// 获取头像 封装
	public static void getFace(final String path, String host, String token,
			final String queryuid, final Handler handler) {
		// 参数列表
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		paramter.add(new BasicNameValuePair("queryuid", queryuid));
		// 请求
		new NetworkRequest("http://" + host + "/" + MethodUrl.GetFace,
				paramter, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						boolean result = false;
						String errormsg = "";
						if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
							BufferedOutputStream bos = null;
							try {
								// 获取响应体对象
								ResponseModel model = NetworkRequestUtil
										.deSerializeResponse(msg.obj.toString());
								Log.d("IMClient", "GetModel");
								if (model.getStatus() == ResponseModel.STATUS_SUCCESS
										&& model.getStatusCode() == ResponseModel.STATUS_SUCCESS) {
									// 转为byte[]并保存到本地
									File file = new File(path + "/Face/"
											+ queryuid + ".jpg");
									// 创建目录
									file.mkdirs();
									// 如果文件存在那么删除并创建一个新的文件
									if (file.exists()) {
										file.delete();
									}
									file.createNewFile();
									// 获取输出流写入数据
									bos = new BufferedOutputStream(
											new FileOutputStream(file));
									byte[] data = Util
											.ToByteFromBase64String(model
													.getAppendData().toString());
									bos.write(data);
									bos.flush();
									result = true;
									errormsg = "下载完成";
									Log.d("IMClient",
											"NetworkRequestUtil-->下载头像完成");
								} else {
									// 错误信息
									result = false;
									if (model.getStatus() != ResponseModel.STATUS_SUCCESS) {
										errormsg = model.getErrorMsg();
									} else {
										errormsg = model.getMessgae();
									}
								}
							} catch (Exception ex) {
								// 输出错误信息
								Log.d("IMClient", ex.getMessage());
								errormsg = ex.getMessage();
							} finally {
								// 如果输出流不为空 那么释放资源
								if (bos != null) {
									try {
										bos.close();
									} catch (IOException e) {
										Log.d("IMClient",
												"下载头像时发送如下异常:" + e.getMessage());
										errormsg = e.getMessage();
									}
								}
							}
						} else {
							errormsg = "请求失败";
						}
						// 通知UI
						Message message = Message.obtain();
						message.what = result ? NetworkRequest.REQUEST_SUCCESS
								: NetworkRequest.REQUEST_FAILED;
						message.obj = errormsg;
						handler.sendMessage(message);
					}
				});
	}

	// 获取好友列表 封装
	public static void getFriendList(final String uid, String host,
			String token, final Handler handler) {
		// 参数列表
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		// 请求
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
									// 获取好友列表
									ArrayList<UserInfoModel> infos = model
											.getAppendData().getFriendList();
									if (infos != null) {
										// 查询出本地全部的好友列表 与云端查询结果进行比较
										// 如果本地有云端没有的好友将会被删除 云端有本地没有的将会添加到本地
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
											// 先插入用户信息再插入关系
											// 否则先插入关系会触发回调方法导致空指针异常
											IMDBUtil.insertUserInfo(info);
											IMDBUtil.addFriend(new FriendRelationModel(
													uid, info.getUid(), false));

										}
										result = true;
										errormsg = "获取好友列表成功";
									}
								} else {
									// 输出错误信息
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
								errormsg = "请求失败";
							}
						} catch (Exception ex) {
							// 输出错误信息
							if (ex != null) {
								if (ex.getMessage() != null)
									Log.d("IMClient", ex.getMessage());
								else
									Log.d("IMClient",
											NetworkRequestUtil.class
													.getCanonicalName()
													+ " error：ex 为 null");
							} else {
								Log.d("IMClient",
										NetworkRequestUtil.class
												.getCanonicalName()
												+ " error：ex 为 null");
							}
						} finally {
							// 通知UI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}
					}
				});
	}

	// 获取信息详情 封装
	public static void getMessage(final String uid, final String host,
			final String token, String touid, boolean isread,
			final Handler handler, final String path) {
		// 参数列表
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		paramter.add(new BasicNameValuePair("touid", touid));
		paramter.add(new BasicNameValuePair("isread", String.valueOf(isread ? 1
				: 0)));
		// 开始请求
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
									// 把信息插入之数据库当中
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
									errormsg = "获取成功";
								} else {
									// 错误信息
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "请求失败";
							}
						} catch (Exception ex) {
							// 输出错误信息
							Log.d("IMClient", ex.getMessage());
						} finally {
							// 通知UI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}

					}
				});
	}

	// 获取信息内容 封装
	public static void getMsgContent(String host, String token,
			final String msgid, final int msgtype, final String path,
			final Handler handler) {
		// 参数列表
		ArrayList<BasicNameValuePair> paramter = new ArrayList<BasicNameValuePair>();
		paramter.add(new BasicNameValuePair("token", token));
		paramter.add(new BasicNameValuePair("msgid", msgid));
		paramter.add(new BasicNameValuePair("msgtype", String.valueOf(msgtype)));
		// 开始请求
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
									// 更新消息详情
									IMDBUtil.updateMessageContent(msgid, model
											.getAppendData().toString(), path,
											msgtype);
									result = true;
									errormsg = "获取成功";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "请求失败";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// 通知UI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}

					}
				});
	}

	// 获取用户信息 封装
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
									// 插入用户信息
									IMDBUtil.insertUserInfo(model
											.getAppendData().getUserInfo());
									result = true;
									errormsg = "获取成功";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "请求失败";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// 通知UI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}

					}
				});
	}

	// 发送信息 封装
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
									// 向数据库当中添加此条信息
									IMDBUtil.insertMessageInfo(uid, msginfo,
											path);
									result = true;
									errormsg = "获取成功";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "请求失败";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// 通知UI
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
									// 登入
									SharedPreferencesUtil.shared.setLoginUser(
											uid, model.getAppendData()
													.getToken());
									result = true;
									errormsg = "登入成功";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "请求失败";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// 通知UI
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
							// 取得未处理事件对象的集合
							ArrayList<EventInfoModel> list = model
									.getAppendData().getUnHandlerEventList();
							result = true;
							errormsg = list;
						} else {
							errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
									.getErrorMsg() : model.getMessgae();
						}
					} else {
						errormsg = "请求失败";
					}
				} catch (Exception ex) {
					Log.d("IMClient", ex.getMessage());
				} finally {
					// 通知UI
					Message message = Message.obtain();
					message.what = result ? NetworkRequest.REQUEST_SUCCESS
							: NetworkRequest.REQUEST_FAILED;
					message.obj = errormsg;
					handler.sendMessage(message);
				}
			}
		});
	}

	// 更新用户信息 封装
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
									// 更新本地数据库信息
									IMDBUtil.removeUserInfo(usermodel.getUid());
									IMDBUtil.insertUserInfo(usermodel);
									result = true;
									errormsg = "更新成功";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "请求失败";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// 通知UI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}

					}
				});
	}

	// 删除好友
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
									// 更新本地数据库
									IMDBUtil.removeFriend(relation);
									result = true;
									errormsg = "删除成功";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "请求失败";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// 通知UI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}
					}
				});
	}

	// 更新头像
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
									// 更新本地数据库
									File file = new File(path + "/" + "Face/"
											+ uid + ".jpg");
									// 如果文件存在那么删除并新建文件
									file.mkdirs();
									if (file.exists()) {
										file.delete();
									}
									file.createNewFile();
									// 获取输出流并写入
									FileOutputStream fos = new FileOutputStream(
											file);
									fos.write(buffer);
									fos.flush();
									fos.close();
									result = true;
									errormsg = "更新成功";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "请求失败";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// 通知UI
							Message message = Message.obtain();
							message.what = result ? NetworkRequest.REQUEST_SUCCESS
									: NetworkRequest.REQUEST_FAILED;
							message.obj = errormsg;
							handler.sendMessage(message);
						}
					}
				});
	}

	// 发送好友请求
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
									errormsg = "请求成功";
								} else {
									errormsg = model.getStatus() != ResponseModel.STATUS_SUCCESS ? model
											.getErrorMsg() : model.getMessgae();
								}
							} else {
								errormsg = "请求失败";
							}
						} catch (Exception ex) {
							Log.d("IMClient", ex.getMessage());
						} finally {
							// 通知UI
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
						errormsg = "请求失败";
					}
				} catch (Exception ex) {
					Log.d("IMClient", ex.getMessage());
				} finally {
					// 通知UI
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
						errormsg = "请求失败";
					}
				} catch (Exception ex) {
					Log.d("IMClient", ex.getMessage());
				} finally {
					// 通知UI
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
