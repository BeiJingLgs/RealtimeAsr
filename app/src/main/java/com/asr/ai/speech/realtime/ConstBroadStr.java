package com.asr.ai.speech.realtime;

import android.content.Context;
import android.media.AudioFormat;
import android.os.Environment;

import com.hanvon.speech.realtime.util.hvFileCommonUtils;

import java.io.File;
import java.text.SimpleDateFormat;

public class ConstBroadStr {
    // 音频文件路径 /storage/sdcard0/Audio/hello.txt
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String AUDIO_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Audio/";

    public static final String ASR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hwsys/asr.file";

    public static String GetAudioRootPath(Context context, boolean isSd) {
        //hvFileCommonUtils.hasSdcard(context) && isSd
        if (false) {
            return hvFileCommonUtils.getSdcardPath(context) + "/Audio/";
        } else {
            return AUDIO_ROOT_PATH;
        }
    }

    /**
     * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
     */
    public static final int SAMPLE_RATE_INHZ = 16000;
    /**
     * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
     */
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static String ROOT_OCR_BIN_PATH = File.separator + "device" + File.separator + "hanvon_resources" + File.separator + "ocr" + File.separator + "dict_cn_hw_cluster" ;



    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");



    public static final String SHOW_BACKLOGO = "android.intent.action.SHOW_BACKLOGO";
    public static final String UPDATERECOG = "com.hanvon.realtime.UPDATERECOG";
    public static final String UPDATEALSPEECHRECOG = "com.hanvon.realtime.UPDATEALSPEECHRECOG";
    public static final String ACTION_HOME_PAGE = "intent.hanvon.homepage";
    public static final String HIDE_BACKLOGO = "android.intent.action.HIDE_BACKLOGO";
    public static final String SPEENCH_AUTH = "android.intent.action.Grant_Authorization";

    public static final String SPEENCH_CLOSE = "android.intent.action.SPEECH_CLOSE";
}
