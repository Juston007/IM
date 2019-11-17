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

//��ϵ���б�������
public class ContactsAdapter extends BaseAdapter {

	private static ContactsAdapter mAdapter = new ContactsAdapter();
	
	private Context context;
	// �û���Ϣ
	private ArrayList<UserInfoModel> list;
	// HashMap �����洢ͷ��
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
		// ViewHolder ���������ԣ����ÿ�ζ����»�ȡ�ؼ������ºܿ�
		ViewHolder vh = null;
		if (arg1 == null) {
			// ��ȡ�ؼ�����
			vh = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.list_contacts,
					null);
			vh.imgFace = (ImageView) arg1.findViewById(R.id.img_list_face);
			vh.txtAlias = (TextView) arg1.findViewById(R.id.txt_list_alias);
			// ����ViewHolderΪ��View��Tag
			arg1.setTag(vh);
		} else {
			// ֱ��ȡ��ViewHolder
			vh = (ViewHolder) arg1.getTag();
		}
		// ��ȡ�û���Ϣ
		UserInfoModel info = list.get(arg0);
		// ����洢ͷ���HashMap�в������Ѽ��ع���Bitmap������ô��ȡBitmap���浽HashMap��,����ֱ����HashMap�л�ȡ
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
		// �ǳ�
		vh.txtAlias.setText(info.getAlias());
		return arg1;
	}

	private class ViewHolder {
		public ImageView imgFace;
		public TextView txtAlias;
	}
}
