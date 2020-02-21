package com.baidu.ai.speech.realtime.android;

import android.app.Application;
import android.content.Context;

public class HvApplication extends Application {

    private static Context mContext;
    public static String TOKEN;
    public static String SESSION;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
