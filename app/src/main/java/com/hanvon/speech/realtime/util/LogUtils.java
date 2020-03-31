package com.hanvon.speech.realtime.util;

import android.util.Log;

import com.baidu.ai.speech.realtime.android.HvApplication;

import static com.baidu.ai.speech.realtime.android.HvApplication.ISDEBUG;

/**
 * Created by ebook on 2018/8/16.
 */

public class LogUtils {


    public static void printLog(String tag, String msg) {
        if (ISDEBUG)
            Log.i(tag, msg);
    }

    public static void printErrorLog(String tag, String msg) {
        if (ISDEBUG)
            Log.e(tag, msg);
    }
}
