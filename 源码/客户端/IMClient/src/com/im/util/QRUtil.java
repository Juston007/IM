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
	// ����QRCode
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

	// ����QRCode
	public static Result parseQRcodeBitmap(String bitmapPath) {
		// ����ת������UTF-8
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
		// ��ȡ����������ͼƬ
		BitmapFactory.Options options = new BitmapFactory.Options();
		// ������ǰ�inJustDecodeBounds��Ϊtrue����ôBitmapFactory.decodeFile(String path,
		// Options opt)
		// ��������ķ���һ��Bitmap���㣬������������Ŀ���ȡ��������
		options.inJustDecodeBounds = true;
		// ��ʱ��bitmap��null����δ���֮��options.outWidth �� options.outHeight����������Ҫ�Ŀ�͸���
		Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
		options.inSampleSize = options.outHeight / 400;
		if (options.inSampleSize <= 0) {
			options.inSampleSize = 1; // ��ֹ��ֵС�ڻ����0
		}
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(bitmapPath, options);
		// �½�һ��RGBLuminanceSource���󣬽�bitmapͼƬ�����˶���
		int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(
				bitmap.getWidth(), bitmap.getHeight(), data);
		// ��ͼƬת���ɶ�����ͼƬ
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
				rgbLuminanceSource));
		// ��ʼ����������
		QRCodeReader reader = new QRCodeReader();
		// ��ʼ����
		Result result = null;
		try {
			result = reader.decode(binaryBitmap, hints);
		} catch (Exception e) {
		}
		return result;
	}
}
