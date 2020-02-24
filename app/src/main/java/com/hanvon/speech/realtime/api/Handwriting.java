package com.hanvon.speech.realtime.api;
import com.hanvon.speech.realtime.services.AppServiceApi;
import com.hanvon.speech.realtime.util.BasePath;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Handwriting {
    private static AppServiceApi appServiceApi=null;
    public static AppServiceApi getAppServiceApi(){
        if (appServiceApi==null){
            synchronized (AppServiceApi.class){
                if (appServiceApi==null){
                    Retrofit builder = new Retrofit.Builder()
                            .baseUrl(BasePath.BASE_LOCALTEST_URL)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    appServiceApi= builder.create(AppServiceApi.class);
                }
            }
        }
      return  appServiceApi;
    }
}
