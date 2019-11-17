package com.im.adapter;

import java.util.ArrayList;

import com.im.aty.R;
import com.im.net.NetworkRequest;
import com.im.net.NetworkRequestUtil;
import com.im.util.Paramters;
import com.im.util.SharedPreferencesUtil;
import com.im.view.ProgressDialog;
import com.im.view.ToastUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class FriendRequestHandlerAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> list;
	private ProgressDialog progressDialog = null;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			progressDialog.dismiss();
			if (msg.what == NetworkRequest.REQUEST_SUCCESS) {
				list.remove(msg.obj.toString());
				// 通知数据源发生了改变
				notifyDataSetChanged();
				ToastUtil.makeText(context, "处理请求成功！", Toast.LENGTH_LONG, true);
			} else {
				ToastUtil.makeText(context, msg.obj.toString(),
						Toast.LENGTH_LONG, false);
			}
		};
	};

	public FriendRequestHandlerAdapter(Context context, ArrayList<String> list) {
		this.context = context;
		this.list = list;
		progressDialog = new ProgressDialog(context, "正在处理请求...");
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder vh = null;
		if (arg1 == null) {
			vh = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(
					R.layout.list_friend_handler, null);
			vh.imgFace = (ImageView) arg1
					.findViewById(R.id.img_list_handler_face);
			vh.txtAlias = (TextView) arg1
					.findViewById(R.id.txt_list_handler_alias);
			vh.btnAccept = (Button) arg1
					.findViewById(R.id.btn_list_handler_accept);
			vh.btnRefuse = (Button) arg1
					.findViewById(R.id.btn_list_handler_refuse);
			arg1.setTag(vh);
		} else {
			vh = (ViewHolder) arg1.getTag();
		}
		final String uid = list.get(arg0);
		vh.txtAlias.setText(uid);
		vh.btnAccept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				handlerFriendRequest(uid, true, handler);
			}
		});
		vh.btnRefuse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				handlerFriendRequest(uid, false, handler);
			}
		});
		return arg1;
	}

	public class ViewHolder {
		public ImageView imgFace;
		public TextView txtAlias;
		public Button btnAccept, btnRefuse;
	}

	private void handlerFriendRequest(String uid, boolean isaccept,
			Handler handler) {
		progressDialog.show();
		// 处理请求
		NetworkRequestUtil
				.handlerFriendRequest(Paramters.ServerHostAddress,
						SharedPreferencesUtil.shared.getToken(), uid, isaccept,
						handler);
	}

}
