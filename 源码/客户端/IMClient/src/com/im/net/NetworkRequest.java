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

	// �ɹ�:0,�쳣:-1,ʧ��:1
	public static final int REQUEST_SUCCESS = 0, REQUEST_EXCEPTION = -1,
			REQUEST_FAILED = 1;

	// ����url POST���� ������ɽ������handler������Ϣ
	public NetworkRequest(final String url,
			final ArrayList<BasicNameValuePair> paramter, final Handler handler) {
		new Thread() {
			@SuppressWarnings("unused")
			public void run() {
				try {
					// ������Ӧ��Http���Ӳ���
					HttpParams httpparamter = new BasicHttpParams();
					// ���ӳ�ʱʱ��
					HttpConnectionParams.setConnectionTimeout(httpparamter,
							Paramters.ConnectionTimeOut);
					// ��ȡ��ʱʱ��
					HttpConnectionParams.setSoTimeout(httpparamter,
							Paramters.ReadTimeOut);
					// ȡ��HttpClientʵ��
					HttpClient client = new DefaultHttpClient(httpparamter);
					HttpPost post = new HttpPost(url);
					if (paramter != null)
						if (!paramter.isEmpty())
							post.setEntity(new UrlEncodedFormEntity(paramter,
									"utf-8"));
					// �����Ӧ��
					HttpResponse response = client.execute(post);
					// ��ȡ���ص�Http����
					int httpresponsecode = response.getStatusLine()
							.getStatusCode();
					if (httpresponsecode == 200) {
						// ����ɹ� ����Ӧʵ��ת��Ϊ�ַ��������ͻ�Handler
						String result = EntityUtils.toString(response
								.getEntity());
						SendMsg(handler, REQUEST_SUCCESS, result);
					} else {
						// ����ʧ�ܴ�ӡLog
						Log.d("IMClient", NetworkRequest.class.getCanonicalName()
								+ ":����url:" + url + "ʧ�ܣ����ص�HttpCode:"
								+ httpresponsecode);
						// ����Msg
						SendMsg(handler, REQUEST_FAILED, "����ʧ��,ԭ��:HttpCodeΪ"
								+ httpresponsecode);
					}
				} catch (Exception e) {
					String error = "";
					if (e != null) {
						if (e.getMessage() != null) {
							// ��ӡLog
							error = e.getMessage();
							Log.d("IMClient",
									NetworkRequest.class.getCanonicalName()
											+ ":" + e.getMessage());
						} else {
							Log.d("IMClient",
									NetworkRequest.class.getCanonicalName()
											+ "error:exception Ϊ null");
							error = "exception Ϊ null";
						}
					} else {
						Log.d("IMClient", NetworkRequest.class.getCanonicalName()
								+ "exception Ϊ null");
						error = "exception Ϊ null";
					}
					// ����Msg
					SendMsg(handler, REQUEST_FAILED, "����ʧ��,�쳣���飺" + error);
				}
			};
		}.start();
	}

	// ����Msg
	public void SendMsg(Handler handler, int what, Object obj) {
		Message msg = Message.obtain();
		msg.what = what;
		msg.obj = obj;
		handler.sendMessage(msg);
	}
}
