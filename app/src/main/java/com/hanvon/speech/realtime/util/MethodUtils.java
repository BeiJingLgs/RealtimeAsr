package com.hanvon.speech.realtime.util;

import android.content.Context;
import android.content.Intent;

public class MethodUtils {
    private  Context mContext;

    public MethodUtils(Context mContext) {
        this.mContext = mContext;
    }
    public  void  getHome(){
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory("android.intent.category.HOME");
        mContext.startActivity(home);
    }
}
