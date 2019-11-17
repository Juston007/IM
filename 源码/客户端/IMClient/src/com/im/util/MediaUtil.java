package com.im.util;

import java.io.File;

import com.im.aty.R;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

//录音播音工具类
public class MediaUtil {

	// 录音的对象
	private static MediaRecorder mediaRecorder = null;
	// 播放声音的对象
	private static MediaPlayer mediaPlayer = null;

	// 开始录音
	public static void startRecord(String path) throws Exception {
		if (mediaRecorder == null) {
			File file = new File(path);
			file.createNewFile();
			mediaRecorder = new MediaRecorder();
			// 设置音源Mic
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置封装格式
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setOutputFile(path);
			// 设置编码格式
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			// 准备
			mediaRecorder.prepare();
			// 开始
			mediaRecorder.start();
		}
	}

	// 停止录音
	public static void stopRecord() {
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		}
	}

	// 开始播放 做了处理可以多次播放
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

	// 停止播放
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
