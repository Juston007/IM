package com.im.view;

import com.im.aty.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//自定义Toast 可以显示带对号、叉号、和文字的Toast
public class ToastUtil {

	// 对号、叉号Toast
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

	// 只显示文字Toast
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
