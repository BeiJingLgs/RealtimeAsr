package com.hanvon.speech.realtime.services;

import android.text.TextUtils;
import android.util.Log;

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
        Request.Builder builder = chain.request().newBuilder();
        //String cookieStr = SPUtil.getData(Constant.SP.SP, Constant.SP.SESSION_ID, String.class, null);
        //List<String> cookies = JSONObject.parseArray(cookieStr, String.class);
       /* if (cookies != null) {
            for (String cookie : cookies) {
                builder.addHeader("Cookie", cookie);
                Log.v("OkHttp", "Adding Header: " + cookie); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
            }
        }*/
       if (TextUtils.isEmpty(HvApplication.SESSION))
           return chain.proceed(builder.build());
        builder.addHeader("Cookie", HvApplication.SESSION);
        return chain.proceed(builder.build());
    }
}
   