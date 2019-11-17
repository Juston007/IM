package com.im.view;

import com.im.aty.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//�Զ���Toast ������ʾ���Ժš���š������ֵ�Toast
public class ToastUtil {

	// �Ժš����Toast
	public static void makeText(Context context, String text, int duration,
			boolean issuccess) {
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		View view = LayoutInflater.from(context).inflate(R.layout.toast_view,
				null);
		view.findViewById(!issuccess ? R.id.view_hook : R.id.view_cross)
				.setVisibility(View.GONE);
		TextView txt = (TextView) view.findViewById(R.id.txt_Toast);
		txt.setText(text);
		toast.setView(view);
		toast.show();
	}

	// ֻ��ʾ����Toast
	public static void makeText(Context context, String text, int duration) {
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.BOTTOM, 0, 110);
		View view = LayoutInflater.from(context).inflate(R.layout.toast_view,
				null);
		view.findViewById(R.id.view_cross).setVisibility(View.GONE);
		view.findViewById(R.id.view_hook).setVisibility(View.GONE);
		TextView txt = (TextView) view.findViewById(R.id.txt_Toast);
		txt.setText(text);
		toast.setView(view);
		toast.show();
	}
}
