package com.hanvon.speech.realtime.util;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.ai.speech.realtime.android.HvApplication;


public class MediaPlayerManager {
    private static MediaPlayerManager mInstance;
    private MediaPlayer player;

    /**
     * ��ȡ��������
     *
     * @return
     */
    public static MediaPlayerManager getInstance() {
        if (mInstance == null) {
            synchronized (MediaPlayerManager.class) {
                if (mInstance == null) {

                    mInstance = new MediaPlayerManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * ����¼��
     *
     * @param url
     * @return
     */
    public boolean play(String url, int index, OnCompletionListener listener) {
        return play(url, index, listener, new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                ToastUtils.showLong(HvApplication.getContext(), "当前文件为空或已损坏");
                stop();
                return false;
            }
        });
    }

    /**
     * ����¼��
     * @param url
     * @return
     */
    public boolean play(String url, int index,  OnCompletionListener completionListener, OnErrorListener errorListener) {
        stop();
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        try {
            if (player == null) {
                player = new MediaPlayer();
                player.setDataSource(url);
                player.setVolume(0.9f, 0.9f);
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.prepare();
                player.start();
                player.seekTo(index);
                player.setOnCompletionListener(completionListener);
                player.setOnErrorListener(errorListener);
                //player.setOnPreparedListener(preparedListener);
                //player.set
                return true;
            }
        } catch (Exception e) {
            stop();
        }
        return false;
    }



    /**
     * �ͷ���Դ
     */
    public void stop() {
        if (player != null) {
            try {
                if (player.isPlaying())
                    player.stop();
                player.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                player = null;
            }
        }
    }

    public void pause() {
        if (player != null)
            player.pause();
    }

    public void seekTo(int i) {
        if (player != null)
            player.seekTo(i);
    }

    public boolean isPlaying() {
        if (player != null)
            return player.isPlaying();
        else return false;
    }

    public int getDuration() {
        if (player != null) {
            Log.e("", "player.getDuration(): " + player.getDuration());
            return player.getDuration();
        }
        else return 0;
    }

    public int getCurrentPosition() {
        if (player != null) {
            Log.e("", "player.getCurrentPosition(): " + player.getCurrentPosition());
            return player.getCurrentPosition();
        }
        else return 0;
    }

}
