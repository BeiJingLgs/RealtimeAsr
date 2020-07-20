package com.asr.ai.speech.realtime.android;

import android.app.Application;
import android.content.Context;

import com.hanvon.speech.realtime.util.CrashHandler;

public class HvApplication extends Application {

    public static Context mContext;
    public static String TOKEN;
    public static String SESSION;

    public static int Recognition_Engine = 0;//0为百度，1为思必驰

    public static final boolean ISDEBUG = true;
    public static boolean IS_NEEDIALOG = true;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        if (!ISDEBUG) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            // 注册crashHandler
            crashHandler.init(mContext);
        }
    }




    public static Context getContext() {
        return mContext;
    }
}
