package com.hanvon.speech.realtime.model;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.widget.Button;
import com.baidu.ai.speech.realtime.ConstBroadStr;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class Recordutil {
    public static final int BUFFER_DATA = 1024 * 1024 * 20;
    public static int BUFFER_SIZE = 0;
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
           // BUFFER_SIZE = (int)(((size % BUFFER_DATA)) == 0 ? (size / BUFFER_DATA) : (size / BUFFER_DATA) + 1);

            data_pack = new byte[(int)size];
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



}
