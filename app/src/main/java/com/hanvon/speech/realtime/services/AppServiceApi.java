package com.hanvon.speech.realtime.services;
import com.hanvon.speech.realtime.bean.DeviceLogin;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface AppServiceApi {
    /**
     * 上传账号密码
     * @param id
     * @return id
     */
    @GET("API/Account/devicelogin")
    Observable<DeviceLogin> DeviceLogin(@Query("deviceid") String id);
}
