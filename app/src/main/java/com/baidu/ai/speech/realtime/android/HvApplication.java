package com.baidu.ai.speech.realtime.android;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.hanvon.speech.realtime.util.SharedPreferencesUtils;

public class HvApplication extends Application {

    public static Context mContext;
    public static String TOKEN;
    public static String SESSION;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        TOKEN = SharedPreferencesUtils.getStringSharedprefer(mContext, SharedPreferencesUtils.TOKEN);
        String s =TOKEN;
        //if (TextUtils.isEmpty(SESSION))
         //   TOKEN = "CA419595F8C06E0DC21EE04C6EB65F6409AEF0630BBC8D986EA116493791FBADAC5E1E84AE8D528F771E8325811A315BA5FAADC77B63A276BD9B197399780A91D4FF218DD67C32536C8B71F7504E8AC06A50DEADC3424B3E94D92E85BE4BB62940826DDC0E765F8AA17AC8A499972E6762C0E7C9212F62B7E19D80D136FA2D3EDC339DA6D264554C94A8DFFC0EF15D851DEE0BB044C1FCF565EF9A9D72BBB168983C30D439FFE708820E1EDD8A9941AC";
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
