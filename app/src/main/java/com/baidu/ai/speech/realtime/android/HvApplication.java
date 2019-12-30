package com.baidu.ai.speech.realtime.android;

import android.app.Application;
import android.content.Context;

public class HvApplication extends Application {

    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
