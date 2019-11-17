package com.im.view;

import com.im.aty.R;
import com.im.util.Util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

//Ô²È¦½ø¶ÈÌõDialog
public class ProgressDialog extends Dialog {

	private LVCircularRing ring;
	private TextView txt;

	public ProgressDialog(Context context, String str) {
		super(context);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_progress, null);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		txt = (TextView) view.findViewById(R.id.txt_progress_content);
		ring = (LVCircularRing) view.findViewById(R.id.progress);
		txt.setText(str);
		Window window = this.getWindow();
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = window.getAttributes();
		Point point = Util.getDialogSize(context);
		lp.width = point.x;
		lp.height = point.y;
        lp.dimAmount =0f;
		window.setAttributes(lp);
		setContentView(view);
		setCancelable(false);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}

	public void setText(String str) {
		txt.setText(str);
	}

	@Override
	public void show() {
		super.show();
		ring.startAnim();
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (ring != null)
			ring.stopAnim();
	}
}
