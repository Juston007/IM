package com.im.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

//绘制叉号
public class DrawHookView extends View {

	// 线1的x轴
	private int line1_x = 0;
	// 线1的y轴
	private int line1_y = 0;
	// 线2的x轴
	private int line2_x = 0;
	// 线2的y轴
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

	// 绘制

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		// 设置画笔颜色
		paint.setColor(0xFFFFFFFF);
		// 设置圆弧的宽度
		paint.setStrokeWidth(5);
		// 设置圆弧为空心
		paint.setStyle(Paint.Style.STROKE);
		// 消除锯齿
		paint.setAntiAlias(true);
		int center = getWidth() / 2;
		int center1 = center - getWidth() / 5;
		// 圆弧半径
		int radius = getWidth() / 2 - 5;

		/**
		 * 绘制圆弧
		 */
		// 定义的圆弧的形状和大小的界限
		RectF rectF = new RectF(center - radius - 1, center - radius - 1,
				center + radius + 1, center + radius + 1);
		canvas.drawArc(rectF, 235, -360, false, paint);

		/**
		 * 绘制对勾
		 */
		// 先等圆弧画完，才话对勾
		if (line1_x < radius / 3) {
			line1_x++;
			line1_y++;
		}
		// 画第一根线
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
		// 画第二根线
		canvas.drawLine(center1 + line1_x - 1, center + line1_y, center1
				+ line2_x, center + line2_y, paint);

		// 每隔1毫秒界面刷新
		postInvalidateDelayed(1);
	}
}