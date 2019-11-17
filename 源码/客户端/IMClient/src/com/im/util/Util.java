package com.im.util;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Point;
import android.text.Html;
import android.util.Base64;
import android.view.Display;
import android.view.WindowManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//������ �������õ�ת������
@SuppressLint("NewApi")
public class Util {

	// UTC�ַ���ת��Ϊ׼ȷʱ��
	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromUTCString(String datestr)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		// ע���ʽ���ı��ʽ
		Date date = format.parse(datestr);
		return date;
	}

	// Longת��Ϊ׼ȷʱ��
	@SuppressLint("SimpleDateFormat")
	public static String ToTimeStringFromLong(long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date(time);
		return df.format(date);
	}

	// Longת��Ϊ����
	@SuppressLint("SimpleDateFormat")
	public static String ToDateStringFromLong(long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(time);
		return df.format(date);
	}

	// �����ַ���ת��Ϊ����
	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromDateString(String datestr)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// ע���ʽ���ı��ʽ
		Date date = format.parse(datestr);
		return date;
	}

	// base64�ַ���תbyte[]
	public static byte[] ToByteFromBase64String(String base64Str) {
		return Base64.decode(base64Str, Base64.DEFAULT);
	}

	// byte[]תbase64�ַ���
	public static String ToBaseStringFromByte(byte[] b) {
		return Base64.encodeToString(b, Base64.DEFAULT);
	}

	// ��������ʱ�����������
	public static int differentDaysByMillisecond(Date date1, Date date2) {
		int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
		days = Math.abs(days);
		return days;
	}

	public static void setActionBarTilte(ActionBar bar, String str) {
		bar.setTitle(Html.fromHtml("<font color='#FFFFFF'>" + str + "</font>"));
	}

	public static Point getDisplaySize(Context context) {
		if (context == null)
			return null;
		WindowManager windowManage = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManage.getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		return point;
	}

	public static Point getDialogSize(Context context) {
		Point point = getDisplaySize(context);
		if (point == null)
			return null;
		point.x = (point.x /= 10) * 8;
		point.y = (int) ((point.y /= 100) * 27);
		return point;
	}
}
