package com.im.view;

import com.im.aty.R;
import com.im.util.Util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//自定义Dialog类 实现了几个常用的对话框功能
@SuppressLint("CutPasteId")
public class MyDialog extends Dialog {

	private TextView txtTilte, txtContent;
	private EditText edText;
	private Button btnConfrim, btnCancel;

	// 普通只带一个btn的Dialog
	public MyDialog(Context context, String tilte, String content,
			String confrimstr, String cancelstr, boolean cancel,
			boolean isdisplaycancel) {
		super(context);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_normal, null);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutParams lp = getWindow().getAttributes();
		getWindow().getDecorView().setPadding(0, 0, 0, 0);
		Point point = Util.getDialogSize(context);
		lp.width = point.x;
		lp.height = point.y;
		getWindow().setAttributes(lp);
		// 获取对象
		txtTilte = (TextView) view.findViewById(R.id.txt_dialog_tilte);
		txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
		btnConfrim = (Button) view.findViewById(R.id.btn_dialog_confrim);
		btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
		// 设置参数
		txtTilte.setText(tilte);
		txtContent.setText(content);
		btnConfrim.setText(confrimstr);
		btnCancel.setText(cancelstr);
		if (!isdisplaycancel)
			setHideCancleButton();
		setCancelable(cancel);
		setContentView(view);
	}

	// 输入框
	public MyDialog(Context context, String tilte, String confrimstr,
			String cancelstr, CharSequence edstr, boolean cancel) {
		super(context);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_input,
				null);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutParams lp = getWindow().getAttributes();
		getWindow().getDecorView().setPadding(0, 0, 0, 0);
		Point point = Util.getDialogSize(context);
		lp.width = point.x;
		lp.height = point.y;
		getWindow().setAttributes(lp);
		// 获取对象
		txtTilte = (TextView) view.findViewById(R.id.txt_dialog_tilte);
		btnConfrim = (Button) view.findViewById(R.id.btn_dialog_confrim);
		btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
		edText = (EditText) view.findViewById(R.id.ed_dialog_input);
		// 设置参数
		txtTilte.setText(tilte);
		btnConfrim.setText(confrimstr);
		btnCancel.setText(cancelstr);
		edText.setText(edstr);
		setCancelable(cancel);
		setContentView(view);
	}

	public void setConfrimOnClickListener(
			android.view.View.OnClickListener click) {
		if (click != null && btnConfrim != null)
			btnConfrim.setOnClickListener(click);
	}

	public void setCancelOnClickListener(android.view.View.OnClickListener click) {
		if (click != null && btnCancel != null)
			btnCancel.setOnClickListener(click);
	}

	public void setHideCancleButton() {
		btnCancel.setVisibility(View.GONE);
	}

	public void setInputType(int type) {
		if (edText != null)
			edText.setInputType(type);
	}

	public String getEdText() {

		if (edText != null)
			return edText.getText().toString().trim();
		else
			return null;
	}
}
