package com.im.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.im.aty.R;
import com.im.model.UserInfoModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//联系人列表适配器
public class ContactsAdapter extends BaseAdapter {

	private static ContactsAdapter mAdapter = new ContactsAdapter();
	
	private Context context;
	// 用户信息
	private ArrayList<UserInfoModel> list;
	// HashMap 用来存储头像
	private HashMap<String, Bitmap> faceMap = new HashMap<String, Bitmap>();

	private ContactsAdapter() {
	}

	public static ContactsAdapter getInterface() {
		return mAdapter;
	}

	public void setParamter(Context context, ArrayList<UserInfoModel> list) {
		this.context = context;
		this.list = list;
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
		// ViewHolder 增加流畅性，免得每次都重新获取控件对象导致很卡
		ViewHolder vh = null;
		if (arg1 == null) {
			// 获取控件对象
			vh = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.list_contacts,
					null);
			vh.imgFace = (ImageView) arg1.findViewById(R.id.img_list_face);
			vh.txtAlias = (TextView) arg1.findViewById(R.id.txt_list_alias);
			// 设置ViewHolder为此View的Tag
			arg1.setTag(vh);
		} else {
			// 直接取出ViewHolder
			vh = (ViewHolder) arg1.getTag();
		}
		// 获取用户信息
		UserInfoModel info = list.get(arg0);
		// 如果存储头像的HashMap中不存在已加载过的Bitmap对象，那么获取Bitmap并存到HashMap中,否则直接在HashMap中获取
		if (faceMap.containsKey(info.getFacePath())) {
			vh.imgFace.setImageBitmap(faceMap.get(info.getFacePath()));
		} else {
			File file = new File(info.getFacePath());
			if (file.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
				faceMap.put(info.getFacePath(), bitmap);
				vh.imgFace.setImageBitmap(bitmap);
			} else {
				vh.imgFace.setImageResource(R.drawable.face_icon_people);
			}
		}
		// 昵称
		vh.txtAlias.setText(info.getAlias());
		return arg1;
	}

	private class ViewHolder {
		public ImageView imgFace;
		public TextView txtAlias;
	}
}
