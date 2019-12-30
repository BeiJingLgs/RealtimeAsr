package com.hanvon.speech.realtime.model;

import android.media.AudioFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class IatThread implements Runnable{
	final int EVENT_PLAY_OVER = 0x100;

	byte []data;
	Handler mHandler;

	public IatThread(byte []data, Handler handler) {
		// TODO Auto-generated constructor stub
		this.data = data;
		mHandler = handler;
	}

	public void run() {
		Log.i("MyThread", "run..");

		if (data == null || data.length == 0){
			return ;
		}

		// MyAudioTrack:   锟斤拷AudioTrack锟斤拷锟叫简单凤拷装锟斤拷锟斤拷
		IatAudioTrack myAudioTrack = new IatAudioTrack(16000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

		myAudioTrack.init();

		int playSize = myAudioTrack.getPrimePlaySize();

		Log.i("MyThread", "total data size = " + data.length + ", playSize = " + playSize);

		int index = 0;
		int offset = 0;
		while(true){
			try {
				Thread.sleep(0);

				offset = index * playSize;


				if (offset >= data.length){
					break;
				}

				myAudioTrack.playAudioTrack(data, offset, playSize);

			} catch (Exception e) {
				// TODO: handle exception
				break;
			}

			index++;
		}

		myAudioTrack.release();

		Message msg = Message.obtain(mHandler, EVENT_PLAY_OVER);
	}
}