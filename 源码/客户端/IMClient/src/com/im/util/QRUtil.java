package com.im.util;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

public class QRUtil {
	// 生成QRCode
	public static Bitmap createQRCode(String str, int widthAndHeight)
			throws WriterException {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xFF000000;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	// 解析QRCode
	public static Result parseQRcodeBitmap(String bitmapPath) {
		// 解析转换类型UTF-8
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
		// 获取到待解析的图片
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 如果我们把inJustDecodeBounds设为true，那么BitmapFactory.decodeFile(String path,
		// Options opt)
		// 并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
		options.inJustDecodeBounds = true;
		// 此时的bitmap是null，这段代码之后，options.outWidth 和 options.outHeight就是我们想要的宽和高了
		Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
		options.inSampleSize = options.outHeight / 400;
		if (options.inSampleSize <= 0) {
			options.inSampleSize = 1; // 防止其值小于或等于0
		}
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(bitmapPath, options);
		// 新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
		int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(
				bitmap.getWidth(), bitmap.getHeight(), data);
		// 将图片转换成二进制图片
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
				rgbLuminanceSource));
		// 初始化解析对象
		QRCodeReader reader = new QRCodeReader();
		// 开始解析
		Result result = null;
		try {
			result = reader.decode(binaryBitmap, hints);
		} catch (Exception e) {
		}
		return result;
	}
}
