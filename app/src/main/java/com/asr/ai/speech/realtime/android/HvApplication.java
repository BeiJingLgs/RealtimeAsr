package com.asr.ai.speech.realtime.android;

import android.app.Application;
import android.content.Context;

import com.hanvon.speech.realtime.util.CrashHandler;

public class HvApplication extends Application {

    public static Context mContext;
    public static String TOKEN;
    public static String SESSION;

    public static int Recognition_Engine = 1;//1为百度，2为思必驰

    public static boolean HaveAuth = false;
    public static final boolean ISDEBUG = false;
    public static boolean IS_NEEDIALOG = true;
    public static boolean REFRESH = true;
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
