package com.im.util;

import java.io.File;

import com.im.aty.R;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

//¼������������
public class MediaUtil {

	// ¼���Ķ���
	private static MediaRecorder mediaRecorder = null;
	// ���������Ķ���
	private static MediaPlayer mediaPlayer = null;

	// ��ʼ¼��
	public static void startRecord(String path) throws Exception {
		if (mediaRecorder == null) {
			File file = new File(path);
			file.createNewFile();
			mediaRecorder = new MediaRecorder();
			// ������ԴMic
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// ���÷�װ��ʽ
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setOutputFile(path);
			// ���ñ����ʽ
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			// ׼��
			mediaRecorder.prepare();
			// ��ʼ
			mediaRecorder.start();
		}
	}

	// ֹͣ¼��
	public static void stopRecord() {
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		}
	}

	// ��ʼ���� ���˴�����Զ�β���
	public static void startPlay(String path) throws Exception {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} else {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();
			mediaPlayer.start();
		}
	}

	// ֹͣ����
	public static void stopPlay() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	public static void playBell(Context context) {
		MediaPlayer player = MediaPlayer.create(context, R.raw.tz);
		player.start();
	}
}
