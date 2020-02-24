package com.hanvon.speech.realtime.services;


import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.hanvon.speech.realtime.util.SharedPreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            List<String> cookies = new ArrayList<>();
            Log.e("cookies", " 1cookies.size: " + cookies.size());
            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }
            HvApplication.SESSION = cookies.get(0);
            SharedPreferencesUtils.saveStringSharePrefer(HvApplication.mContext, SharedPreferencesUtils.SESSION, HvApplication.SESSION);
            Log.e("AA", "onResponse: " + HvApplication.SESSION + ": " + HvApplication.SESSION);

            Log.e("cookies", " 2cookies.: " + cookies.get(0));
            Log.e("cookies", " 2cookies.size: " + cookies.size());
            //String cookieStr = JSONObject.toJSONString(cookies);
            //SPUtil.putData(HealthKeys.Constant.SP.SP, Constant.SP.SESSION_ID, cookieStr);
        }

        return originalResponse;
    }
}