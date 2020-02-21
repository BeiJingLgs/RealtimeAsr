package com.hanvon.speech.realtime.model;

import android.media.AudioFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class IatThread implements Runnable{
	public static final int EVENT_PLAY_OVER = 0x100;
    public static final int EVENT_PLAY_PROGRESS = 0x101;
	public static final int EVENT_AVAILABLEE_MEMO = 0x102;
	public static final int EVENT_PLAY_STOP = 0x103;
	//AvailableInternalMemorySize
	byte []data;
	Handler mHandler;
	private int begin;

	public IatThread(byte []data, Handler handler, int begin) {
		// TODO Auto-generated constructor stub
		this.data = data;
		mHandler = handler;
		this.begin = begin;
	}

	public void run() {
		Log.i("MyThread", "run..");

		if (data == null || data.length == 0){
			return ;
		}
		IatAudioTrack myAudioTrack = new IatAudioTrack(16000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

		myAudioTrack.init();

		int playSize = myAudioTrack.getPrimePlaySize();

		Log.e("MyThread", "total data size = " + data.length + ", playSize = " + playSize);

		int index = 0;
		int offset = 0;
		while(true){
			try {
				Thread.sleep(1);

				offset = index * playSize + begin;

                Message msg = Message.obtain(mHandler, EVENT_PLAY_PROGRESS);
                msg.arg1 = offset;
                mHandler.sendMessage(msg);

				Log.e("MyThread", "total data size = " + data.length + ", offset = " + offset);
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
		if (offset >= data.length) {
			Message msg = Message.obtain(mHandler, EVENT_PLAY_OVER);
			mHandler.sendMessage(msg);
		}
	}
}