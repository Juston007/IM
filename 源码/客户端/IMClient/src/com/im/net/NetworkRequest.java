package com.im.net;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.im.util.Paramters;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetworkRequest {

	// 成功:0,异常:-1,失败:1
	public static final int REQUEST_SUCCESS = 0, REQUEST_EXCEPTION = -1,
			REQUEST_FAILED = 1;

	// 请求url POST参数 处理完成将会向此handler发送信息
	public NetworkRequest(final String url,
			final ArrayList<BasicNameValuePair> paramter, final Handler handler) {
		new Thread() {
			@SuppressWarnings("unused")
			public void run() {
				try {
					// 设置相应的Http连接参数
					HttpParams httpparamter = new BasicHttpParams();
					// 连接超时时长
					HttpConnectionParams.setConnectionTimeout(httpparamter,
							Paramters.ConnectionTimeOut);
					// 读取超时时长
					HttpConnectionParams.setSoTimeout(httpparamter,
							Paramters.ReadTimeOut);
					// 取得HttpClient实例
					HttpClient client = new DefaultHttpClient(httpparamter);
					HttpPost post = new HttpPost(url);
					if (paramter != null)
						if (!paramter.isEmpty())
							post.setEntity(new UrlEncodedFormEntity(paramter,
									"utf-8"));
					// 获得响应体
					HttpResponse response = client.execute(post);
					// 获取返回的Http代码
					int httpresponsecode = response.getStatusLine()
							.getStatusCode();
					if (httpresponsecode == 200) {
						// 请求成功 将响应实体转换为字符串并发送回Handler
						String result = EntityUtils.toString(response
								.getEntity());
						SendMsg(handler, REQUEST_SUCCESS, result);
					} else {
						// 请求失败打印Log
						Log.d("IMClient", NetworkRequest.class.getCanonicalName()
								+ ":请求url:" + url + "失败，返回的HttpCode:"
								+ httpresponsecode);
						// 发送Msg
						SendMsg(handler, REQUEST_FAILED, "请求失败,原因:HttpCode为"
								+ httpresponsecode);
					}
				} catch (Exception e) {
					String error = "";
					if (e != null) {
						if (e.getMessage() != null) {
							// 打印Log
							error = e.getMessage();
							Log.d("IMClient",
									NetworkRequest.class.getCanonicalName()
											+ ":" + e.getMessage());
						} else {
							Log.d("IMClient",
									NetworkRequest.class.getCanonicalName()
											+ "error:exception 为 null");
							error = "exception 为 null";
						}
					} else {
						Log.d("IMClient", NetworkRequest.class.getCanonicalName()
								+ "exception 为 null");
						error = "exception 为 null";
					}
					// 发送Msg
					SendMsg(handler, REQUEST_FAILED, "请求失败,异常详情：" + error);
				}
			};
		}.start();
	}

	// 发送Msg
	public void SendMsg(Handler handler, int what, Object obj) {
		Message msg = Message.obtain();
		msg.what = what;
		msg.obj = obj;
		handler.sendMessage(msg);
	}
}
