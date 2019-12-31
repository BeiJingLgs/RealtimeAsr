package com.hanvon.speech.realtime.model;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Button;

import com.baidu.ai.speech.realtime.ConstBroadStr;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.hanvon.speech.realtime.util.DocumentsUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class Recordutil {
    private static Logger logger = Logger.getLogger("Recordutil");
    private static Recordutil instance = new Recordutil();

//1：私有化构造方法，好在内部控制创建实例的数目

    private Recordutil(){
    }

//2：定义一个方法来为客户端提供类实例

//3：这个方法需要定义成类方法，也就是要加static

//这个方法里面就不需要控制代码了

    public static Recordutil getInstance(){
//5：直接使用已经创建好的实例
        return instance;
    }

    public static byte[] getPCMData(String filePath){

        File file = new File(filePath);
        if (file == null){
            return null;
        }

        FileInputStream inStream;
        try {
            inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        byte[] data_pack = null;
        if (inStream != null){
            long size = file.length();

            data_pack = new byte[(int) size];
            try {
                inStream.read(data_pack);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }

        }

        return data_pack;
    }


    public void stopRecord() {
        isRecording = false;
        // 释放资源
        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            //recordingThread = null;
        }
    }

    private boolean isRecording;
    private AudioRecord audioRecord;
    private Button mBtnConvert;
    private AudioTrack audioTrack;
    private byte[] audioData;
    private FileInputStream fileInputStream;

    public void startRecord(String path) {
        final int minBufferSize = AudioRecord.getMinBufferSize(ConstBroadStr.SAMPLE_RATE_INHZ, ConstBroadStr.CHANNEL_CONFIG, ConstBroadStr.AUDIO_FORMAT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, ConstBroadStr.SAMPLE_RATE_INHZ,
                ConstBroadStr.CHANNEL_CONFIG, ConstBroadStr.AUDIO_FORMAT, minBufferSize);

        final byte data[] = new byte[minBufferSize];
        File direc = new File(ConstBroadStr.AUDIO_ROOT_PATH + path.trim());
        if(direc.exists()){
            //创建文件夹
            direc.mkdirs();
        }

        final File file = new File(ConstBroadStr.AUDIO_ROOT_PATH + path.trim() +
                ConstBroadStr.AUDIO_PATH);
        if (!file.mkdirs()) {
        }
        if (file.exists()) {
            file.delete();
        }
        boolean is = file.exists();
        logger.info("file.exists(): " + file.exists());
        audioRecord.startRecording();
        isRecording = true;

        // TODO: 2018/3/10 pcm数据无法直接播放，保存为WAV格式。

        new Thread(new Runnable() {
            @Override
            public void run() {
                //BufferedOutputStream os = null;
                OutputStream os = null;
                try {
                    //os = new BufferedOutputStream(new FileOutputStream(file));
                    os = new FileOutputStream(file);
                    logger.info("file.getPath(): " + file.getPath());
                    //os = DocumentsUtils.getOutputStream(HvApplication.getContext(), new File(file.getPath()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (null != os) {
                    while (isRecording) {
                        int read = audioRecord.read(data, 0, minBufferSize);
                        // 如果读取音频数据没有出现错误，就将数据写入到文件
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                os.write(data);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
