package com.hanvon.speech.realtime.services;
import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.bean.Result.VerificationResult;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface AppServiceApi {
    /**
     * 上传账号密码
     * @param username
     * @param password
     * @return
     */
    @GET("/API/Account/PhoneLoginByPassword")
    Observable<ResponseBody> loginByPassword(@Query("phone") String username, @Query("password") String password, @Query("deviceid") String deviceid);

    /**
     * 设备id登陆
     * @param deviceid
     * @return
     */
    @GET("API/Account/devicelogin")
    Observable<ResponseBody> loginByDeviceId(@Query("deviceid") String deviceid);

    /**
     *
     * @param phone
     * @param smscode
     * @param deviceid
     * @return
     */
    @GET("........")
    Observable<ResponseBody> loginBySms(@Path("phone") String phone ,@Path("smscode") String smscode, @Path("deviceid ") String deviceid);

    /**
     * 获取验证码
     * @param phone
     * @return
     */
    @GET("/API/Sms/send")
    Observable<ResponseBody> getVerificationCode(@Query("phone") String phone);


   // @FormUrlEncoded //这个加上有啥用，为什么不加这个会崩溃  @Field parameters can only be used with form encoding.
    @POST("/API/Account/Register")
    Observable<ResponseBody> registerByPhone(@QueryMap Map<String, String> params);


    /**
     * 获取验证码
     * @return
     */
    @GET("/API/Account/logout")
    Observable<ResponseBody> logout();

    @POST("/API/Device/GetBindDevices")
    Observable<ResponseBody> getBindDevices(@Header("token") String token);

    @POST("API/Device/BindDevice")
    Observable<ResponseBody> bindDevices(@Header("token") String token);

    @POST("/API/Device/GetBindUser")
    Observable<ResponseBody> getBindUser(@Header("token") String token, @QueryMap Map<String, String> params);

    @POST("/API/Device/SetCommonlyUsedDevice")
    Observable<ResponseBody> setCommonlyUsedDevice(@Header("token") String token, @QueryMap Map<String, String> params);

    @POST("/API/Device/CancelCommonlyUsedDevice")
    Observable<ResponseBody> cancelCommonlyUsedDevice(@Header("token") String token, @QueryMap Map<String, String> params);

    @POST("/API/Device/UnBindDevice")
    Observable<ResponseBody> unBindDevice(@Header("token") String token, @QueryMap Map<String, String> params);


    @GET("API/Pack/GetPacks")
    Observable<ResponseBody> getPacks();

    @GET("........")
    Observable<VerificationResult> getDevicePacks(@Path("token") String token );

    @GET("........")
    Observable<VerificationResult> getUserPacks(@Path("token") String token );

    @GET("........")
    Observable<VerificationResult> getUseRecord(@Path("token") String token, @Path("curpage") int curpage,
                                                @Path("pagesize") int pagesize, @Path("sort") String sort);

    @GET("........")
    Observable<VerificationResult> buy(@Path("token") String token );

    @GET("........")
    Observable<VerificationResult> getOrder(@Path("token") String token, @Path("curpage") int curpage,
                                                @Path("pagesize") int pagesize, @Path("sort") String sort);
}
