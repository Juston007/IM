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

//工具类 包含常用的转换方法
@SuppressLint("NewApi")
public class Util {

	// UTC字符串转换为准确时间
	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromUTCString(String datestr)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		// 注意格式化的表达式
		Date date = format.parse(datestr);
		return date;
	}

	// Long转换为准确时间
	@SuppressLint("SimpleDateFormat")
	public static String ToTimeStringFromLong(long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date(time);
		return df.format(date);
	}

	// Long转换为日期
	@SuppressLint("SimpleDateFormat")
	public static String ToDateStringFromLong(long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(time);
		return df.format(date);
	}

	// 日期字符串转换为日期
	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromDateString(String datestr)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// 注意格式化的表达式
		Date date = format.parse(datestr);
		return date;
	}

	// base64字符串转byte[]
	public static byte[] ToByteFromBase64String(String base64Str) {
		return Base64.decode(base64Str, Base64.DEFAULT);
	}

	// byte[]转base64字符串
	public static String ToBaseStringFromByte(byte[] b) {
		return Base64.encodeToString(b, Base64.DEFAULT);
	}

	// 计算两个时间点相差的天数
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
