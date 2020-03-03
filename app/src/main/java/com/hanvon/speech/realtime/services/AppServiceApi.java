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
    Observable<ResponseBody> getBindDevices();

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

    @POST("/API/Account/ChangePasswordBySms")
    Observable<ResponseBody> changePasswordBySms(@QueryMap Map<String, String> params);

    @POST("/API/Order/CreateOrderByPack")
    Observable<ResponseBody> createOrderByPack(@QueryMap Map<String, String> params);

    /**
     * 提交使用时间
     * @param params
     * @return
     */
    @POST("/API/Pack/SubmitUsedTime")
    Observable<ResponseBody> submitUsedTime(@QueryMap Map<String, String> params);

    /**
     * 获取使用记录
     */
    @GET("/API/Pack/GetUseRecord")
    Observable<ResponseBody> getUseRecord(@Query("curPage") String curPage, @Query("pageSize") String pageSize, @Query("sort") String sort);

    /**
     * 获取订单
     */
    @GET("/API/Order/GetOrders")
    Observable<ResponseBody> getOrders(@Query("curPage") String curPage, @Query("pageSize") String pageSize, @Query("sort") String sort);

    @GET("/API/Order/GetPayChannels")
    Observable<ResponseBody> getPayChannels();


    /**
     * 获取验证码
     * @return
     */
    @GET("/API/Account/logout")
    Observable<ResponseBody> logout();


    @POST("API/Device/BindDevice")
    Observable<ResponseBody> bindDevices();



    @GET("API/Pack/GetPacks")
    Observable<ResponseBody> getPacks();

    @GET("API/Pack/GetDevicePacks")
    Observable<ResponseBody> getDevicePacks();

    @GET("API/Pack/GetUserPacks")
    Observable<ResponseBody> getUserPacks();

    @POST("API/Order/PayOrder")
    Observable<ResponseBody> payOrder(@QueryMap Map<String, String> params);

    @POST("API/Order/PayOrderByWxNative")
    Observable<ResponseBody> PayOrderByWxNative(@QueryMap Map<String, String> params);

    @GET("API/Order/PayOrder")
    Observable<ResponseBody> payOrder(@Query("orderId") String curPage, @Query("channelId") String pageSize);

}
