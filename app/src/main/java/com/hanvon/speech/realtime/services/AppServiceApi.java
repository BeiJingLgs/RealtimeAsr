package com.hanvon.speech.realtime.services;
import com.hanvon.speech.realtime.bean.FileBean;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface AppServiceApi {
    /**
     * 上传账号密码
     * @param username
     * @param password
     * @return
     */
    @GET("........")
    Observable<FileBean> login(@Path("username") String username, @Path("password") String password);
}
