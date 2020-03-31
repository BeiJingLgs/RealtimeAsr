package com.hanvon.speech.realtime.services;


import android.util.Log;
import android.webkit.WebSettings;

import com.alibaba.fastjson.JSONObject;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder()
                .removeHeader("User-Agent")//移除旧的
                .addHeader("User-Agent", WebSettings.getDefaultUserAgent(HvApplication.mContext))//添加真正的头部
                ;
        Response originalResponse = chain.proceed(builder.build());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            List<String> cookies = new ArrayList<>();
            Log.e("cookies", " 1cookies.size: " + cookies.size());
            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }
            HvApplication.SESSION = cookies.get(0);
            SharedPreferencesUtils.saveStringSharePrefer(HvApplication.mContext, SharedPreferencesUtils.SESSION, HvApplication.SESSION);
            Log.e("36AA", "onResponse: " + HvApplication.SESSION + ": " + HvApplication.SESSION);


        }

        return originalResponse;
    }
}