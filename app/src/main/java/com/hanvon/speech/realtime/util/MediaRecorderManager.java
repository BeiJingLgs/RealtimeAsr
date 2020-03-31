package com.hanvon.speech.realtime.util;

import android.media.MediaRecorder;

public class MediaRecorderManager {
    public static final String TAG = "Recorder";
    private MediaRecorder mRecorder;
    private static MediaRecorderManager mInstance;

    public MediaRecorderManager() {

    }

    /**
     * ��ȡ��������
     *
     * @return
     */
    public static MediaRecorderManager getInstance() {
        if (mInstance == null) {
            synchronized (MediaRecorderManager.class) {
                if (mInstance == null) {
                    mInstance = new MediaRecorderManager();
                }
            }
        }
        return mInstance;
    }


    /**
     * ��ʼ¼��
     *
     * @param filePath
     */
    public void start(String filePath) {
        try {
            if(mRecorder==null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//������Ƶ�ɼ���ʽ
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);//������Ƶ�����ʽ
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//������Ƶ���뷽ʽ
            }
            mRecorder.setOutputFile(filePath);//����¼���ļ����·��
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
        }
    }

    /**
     * �ͷ�¼����Դ
     */
    public void stop() {
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
            }
        } catch (IllegalStateException e) {

        } catch (RuntimeException e) {

        } catch (Exception e) {

        }
        mRecorder = null;
    }

}