package com.im.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

//���Ʋ��
public class DrawHookView extends View {

	// ��1��x��
	private int line1_x = 0;
	// ��1��y��
	private int line1_y = 0;
	// ��2��x��
	private int line2_x = 0;
	// ��2��y��
	private int line2_y = 0;

	public DrawHookView(Context context) {
		super(context);
	}

	public DrawHookView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawHookView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// ����

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		// ���û�����ɫ
		paint.setColor(0xFFFFFFFF);
		// ����Բ���Ŀ��
		paint.setStrokeWidth(5);
		// ����Բ��Ϊ����
		paint.setStyle(Paint.Style.STROKE);
		// �������
		paint.setAntiAlias(true);
		int center = getWidth() / 2;
		int center1 = center - getWidth() / 5;
		// Բ���뾶
		int radius = getWidth() / 2 - 5;

		/**
		 * ����Բ��
		 */
		// �����Բ������״�ʹ�С�Ľ���
		RectF rectF = new RectF(center - radius - 1, center - radius - 1,
				center + radius + 1, center + radius + 1);
		canvas.drawArc(rectF, 235, -360, false, paint);

		/**
		 * ���ƶԹ�
		 */
		// �ȵ�Բ�����꣬�Ż��Թ�
		if (line1_x < radius / 3) {
			line1_x++;
			line1_y++;
		}
		// ����һ����
		canvas.drawLine(center1, center, center1 + line1_x, center + line1_y,
				paint);

		if (line1_x == radius / 3) {
			line2_x = line1_x;
			line2_y = line1_y;
			line1_x++;
			line1_y++;
		}
		if (line1_x >= radius / 3 && line2_x <= radius) {
			line2_x++;
			line2_y--;
		}
		// ���ڶ�����
		canvas.drawLine(center1 + line1_x - 1, center + line1_y, center1
				+ line2_x, center + line2_y, paint);

		// ÿ��1�������ˢ��
		postInvalidateDelayed(1);
	}
}