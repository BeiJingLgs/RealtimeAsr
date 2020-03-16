package com.baidu.ai.speech.realtime.android;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.hanvon.speech.realtime.util.CrashHandler;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;

public class HvApplication extends Application {

    public static Context mContext;
    public static String TOKEN;
    public static String SESSION;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        CrashHandler crashHandler = CrashHandler.getInstance();
        // 注册crashHandler
        crashHandler.init(mContext);
    }




    public static Context getContext() {
        return mContext;
    }
}
