package com.hanvon.speech.realtime.services;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;

import com.alibaba.fastjson.JSONObject;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.hanvon.speech.realtime.ui.LoginActivity;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder()
                .removeHeader("User-Agent")//移除旧的
                .addHeader("User-Agent", WebSettings.getDefaultUserAgent(HvApplication.mContext))//添加真正的头部
                ;

       if (TextUtils.isEmpty(HvApplication.SESSION))
           return chain.proceed(builder.build());
        builder.addHeader("Cookie", HvApplication.SESSION);
        return chain.proceed(builder.build());
    }
}
   