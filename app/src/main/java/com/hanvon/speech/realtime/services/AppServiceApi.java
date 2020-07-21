package com.hanvon.speech.realtime.services;
import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.bean.Result.VerificationResult;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
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
    Observable<ResponseBody> loginByPassword(@Query("phone") String username, @Query("password") String password, @Query("deviceSerialNo") String deviceid);


    /**
     * 上传账号密码
     * @param username
     * @param password
     * @return
     */
    @GET("/API/Account/PhoneLoginBySms")
    Observable<ResponseBody> loginBySMS(@Query("phone") String username, @Query("smscode") String password, @Query("deviceSerialNo") String deviceid);


    /**
     * 设备id登陆
     * @param deviceid
     * @return
     */
    @GET("API/Account/devicelogin")
    Observable<ResponseBody> loginByDeviceId(@Query("deviceSerialNo") String deviceid);



    @GET("API/Device/GetBindDevices")
    Observable<ResponseBody> getBindDevices(@Header("token") String token);

    /**
     * 获取验证码
     * @param phone
     * @return
     */
    @GET("/API/Sms/send")
    Observable<ResponseBody> getVerificationCode(@Query("phone") String phone);

    /**
     * 获取验证码
     * @param phone
     * @return
     */
    @GET("/API/Sms/SendByRegisteredUser")
    Observable<ResponseBody> sendByRegisteredUser(@Query("phone") String phone);




    // @FormUrlEncoded //这个加上有啥用，为什么不加这个会崩溃  @Field parameters can only be used with form encoding.
    @POST("/API/Account/Register")
    Observable<ResponseBody> registerByPhone(@QueryMap Map<String, String> params);

    @POST("/API/Account/ChangePasswordBySms")
    Observable<ResponseBody> changePasswordBySms(@QueryMap Map<String, String> params);

    @POST("/API/Order/CreateOrderByPack")
    Observable<ResponseBody> createOrderByPack(@Header("token") String token, @QueryMap Map<String, String> params);

    /**
     * 提交使用时间
     * @param params
     * @return
     */
    @POST("/API/Pack/SubmitUsedTime")
    Observable<ResponseBody> submitUsedTime(@Header("token") String token, @QueryMap Map<String, String> params);

    /**
     * 获取使用记录
     */
    @GET("/API/Pack/GetUseRecord")
    Observable<ResponseBody> getUseRecord(@Header("token") String token, @Query("curPage") String curPage, @Query("pageSize") String pageSize, @Query("sort") String sort);

    /**
     * 获取订单
     */
    @GET("/API/Order/GetOrders")
    Observable<ResponseBody> getOrders(@Header("token") String token, @Query("curPage") String curPage, @Query("pageSize") String pageSize, @Query("sort") String sort);

    /**
     * 获取订单
     */
    @GET("/API/Order/GetOrder")
    Observable<ResponseBody> getOrder(@Header("token") String token, @Query("orderId") String orderId);


    @GET("/API/Order/GetPayChannels")
    Observable<ResponseBody> getPayChannels(@Header("token") String token);


    /**
     * 获取验证码
     * @return
     */
    @GET("/API/Account/logout")
    Observable<ResponseBody> logout();


    @GET("API/Device/GetBindUser")
    Observable<ResponseBody> getBindUser(@Header("token") String token, @Query("deviceSerialNo")String deviceSerialNo);



    @GET("API/Pack/GetPacks")
    Observable<ResponseBody> getPacks(@Header("token") String token);

    @GET("API/Pack/GetDevicePacks")
    Observable<ResponseBody> getDevicePacks(@Header("token") String token);

    @GET("API/Pack/GetUserPacks")
    Observable<ResponseBody> getUserPacks(@Header("token") String token);

    @GET("API/Pack/GetAccountPacks")
    Observable<ResponseBody> getAccountPacks(@Header("token") String token, @Query("voiceEngineTypeID") String VoiceEngineTypeID, @Query("curPage") String curPage, @Query("pageSize") String pageSize, @Query("sort") String sort);

    @POST("API/Order/PayOrder")
    Observable<ResponseBody> payOrder(@Header("token") String token, @QueryMap Map<String, String> params);

    @POST("API/Order/PayOrderByWxNative")
    Observable<ResponseBody> PayOrderByWxNative(@Header("token") String token, @QueryMap Map<String, String> params);

    @GET("API/Order/PayOrder")
    Observable<ResponseBody> payOrder(@Header("token") String token, @Query("orderId") String curPage, @Query("channelId") String pageSize);


    /**
     * 上传日志
     *
     * @return
     */
    @Multipart
    @POST("API/Share/ShareHtml")
    Observable<ResponseBody> upLoadFile(@Header("token") String token, @PartMap Map<String, RequestBody> map);
}
