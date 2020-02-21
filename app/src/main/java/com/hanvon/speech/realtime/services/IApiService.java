package com.hanvon.speech.realtime.services;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/*Time:2019/5/15
 *Author:zhaozhiwei
 *Description:
 */
public interface IApiService {
    /**
     * GET请求
     */
    @GET
    //ResponseBody是ok3提供的被观察者返回的对象
    Observable<ResponseBody> getBannerShow(@Url String url);

 /**
     * get请求Header入参
     */
    @GET
    Observable<ResponseBody> getHeader(@Url String url, @Header("userId") String userid, @Header("sessionId") String sessionId);
    
    /**
     * 登陆
     * @param phone
     * @param pwd
     * 观察者模式
     */
    @FormUrlEncoded //这个加上有啥用，为什么不加这个会崩溃  @Field parameters can only be used with form encoding.
    @POST
    Observable<ResponseBody> getPostShow(@Url String url, @FieldMap Map<String,String> map);
}