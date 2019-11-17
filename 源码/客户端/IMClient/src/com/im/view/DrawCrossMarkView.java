package com.im.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//���ƶԺ�
public class DrawCrossMarkView extends View {

	// ��1��x������
	private int line1X = 0;

	// ��1��y������
	private int line1Y = 0;

	// ��2��x������
	private int line2X = 0;

	// ��2��y������
	private int line2Y = 0;

	// �������
	int line1StartX;
	int line2StartX;
	int lineStartY;

	int step = 2;

	// ��ˮƽ�������
	int maxLineIncrement;

	// �ߵĿ��
	private int lineThick = 4;

	// ��ȡԲ�ĵ�x����
	int center;

	// Բ���뾶
	int radius;

	// �����Բ������״�ʹ�С�Ľ���
	RectF rectF;

	Paint paint;

	// �ؼ���С
	float totalWidth;

	public DrawCrossMarkView(Context context) {
		super(context);
	}

	public DrawCrossMarkView(Context context, AttributeSet attrs) {
		super(context, attrs);

		Pattern p = Pattern.compile("\\d*");

		Matcher m = p.matcher(attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/android", "layout_width"));

		if (m.find()) {
			totalWidth = Float.valueOf(m.group());
		}

		totalWidth = totalWidth
				* context.getResources().getDisplayMetrics().density;

		maxLineIncrement = (int) (totalWidth * 2 / 5);

		init();
	}

	public DrawCrossMarkView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	void init() {
		paint = new Paint();

		// ���û�����ɫ
		paint.setColor(0xFFFFFFFF);

		// ����Բ���Ŀ��
		paint.setStrokeWidth(lineThick);

		// ����Բ��Ϊ����
		paint.setStyle(Paint.Style.STROKE);

		// �������
		paint.setAntiAlias(true);

		// ��ȡԲ�ĵ�x����
		center = (int) (totalWidth / 2);

		// Բ���뾶
		radius = (int) (totalWidth / 2) - lineThick;

		// �������
		line1StartX = (int) (center + totalWidth / 5);
		lineStartY = (int) (center - totalWidth / 5);
		line2StartX = (int) (center - totalWidth / 5);

		rectF = new RectF(center - radius, center - radius, center + radius,
				center + radius);
	}

	// ����
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// ���ݽ��Ȼ�Բ��
		canvas.drawArc(rectF, 235, -360, false, paint);

		// �ȵ�Բ�����꣬����
		if (line1X < maxLineIncrement) {
			line1X += step;
			line1Y += step;
		}

		// ����һ����
		canvas.drawLine(line1StartX, lineStartY, line1StartX - line1X,
				lineStartY + line1Y, paint);

		if (line1X >= maxLineIncrement) {

			line2X += step;
			line2Y += step;

			// ���ڶ�����
			canvas.drawLine(line2StartX, lineStartY, line2StartX + line2X,
					lineStartY + line2Y, paint);
		}

		// ÿ��6�������ˢ��
		if (line2X < maxLineIncrement)
			postInvalidateDelayed(6);
	}
}