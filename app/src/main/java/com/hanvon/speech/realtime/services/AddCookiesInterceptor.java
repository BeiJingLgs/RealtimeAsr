package com.hanvon.speech.realtime.services;

import android.text.TextUtils;
import android.webkit.WebSettings;

import com.asr.ai.speech.realtime.android.HvApplication;

import java.io.IOException;

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
   