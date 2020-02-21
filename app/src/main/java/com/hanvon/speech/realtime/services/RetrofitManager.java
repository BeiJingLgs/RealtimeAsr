package com.hanvon.speech.realtime.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.webkit.WebSettings;

import com.baidu.ai.speech.realtime.android.HvApplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.security.KeyStore.getApplicationContext;

/*Time:2019/5/15
 *Author:zhaozhiwei
 *Description:
 */
public class RetrofitManager {

    private AppServiceApi iApiService;
    private Retrofit mRetrofit;

    private RetrofitManager() {
        initRetrofit();
    }

    //静态内部类单例
    private static class oKHPMH {
        public static RetrofitManager instance = new RetrofitManager();
    }

    public static RetrofitManager getInstance() {
        return oKHPMH.instance;
    }

    //网络判断
    public boolean isNetWorkConneted(Context context) {
        if (context != null) {
            ConnectivityManager systemService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = systemService.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    //初始化fit
    private void initRetrofit() {
        //拦截器
        //HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
         //loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new ReceivedCookiesInterceptor()) //首次请求
                .addInterceptor(new AddCookiesInterceptor()) //首次请求
                .retryOnConnectionFailure(true);
        OkHttpClient client = builder.build();

        //Retrofit的创建
        mRetrofit = new Retrofit.Builder()
                //添加Rxjava工厂
                .client(getOkHttpClient())//获取后的okhttp头部
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())

                .baseUrl("http://edu.hwebook.cn:8008/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        iApiService = mRetrofit.create(AppServiceApi.class);
    }
    private static OkHttpClient getOkHttpClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                //.removeHeader("User-Agent")//移除旧的
                                .header("Authorization", "auth-token")
                                .header("Accept", "application/json")
                               // .addHeader("User-Agent",
                               //         "Mozilla/5.0 ( Windows; U; Windows NT 5.1; en-US; rv:0.9.4 ")//添加真正的头部
                                .build();
                        return chain.proceed(request);
                    }
                }).build();
        return httpClient;
    }
    //Get请求
    public void loginByDeviceId(String deviceid, final ICallBack callback) {
        iApiService.loginByDeviceId(deviceid)
                //被观察者执行在哪个线程，这里面执行在io线程，io线程是一个子线程
                .subscribeOn(Schedulers.io())
                //最终完成后结果返回到哪个线程，mainThread代表主线
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callback));
    }

    //Get请求
    public void getVerificationCode(String phone, final ICallBack callback) {
        iApiService.getVerificationCode(phone)
                //被观察者执行在哪个线程，这里面执行在io线程，io线程是一个子线程
                .subscribeOn(Schedulers.io())
                //最终完成后结果返回到哪个线程，mainThread代表主线
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callback));
    }

    //Get请求
    public void loginByPassword(String phone, String pass, String deviceid, final ICallBack callback) {
        iApiService.loginByPassword(phone, pass, deviceid)
                //被观察者执行在哪个线程，这里面执行在io线程，io线程是一个子线程
                .subscribeOn(Schedulers.io())
                //最终完成后结果返回到哪个线程，mainThread代表主线
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callback));
    }


    //Get请求
    public void logout(final ICallBack callback) {
        iApiService.logout()
                //被观察者执行在哪个线程，这里面执行在io线程，io线程是一个子线程
                .subscribeOn(Schedulers.io())
                //最终完成后结果返回到哪个线程，mainThread代表主线
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callback));
    }

    //POst请求
    public void registerByPhone(HashMap<String, String> params, ICallBack callBack) {
        //一定要判空，如果是空，创建一个实例就可以了
        if (params == null) {
            params = new HashMap<>();
        }
        iApiService.registerByPhone(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    //POst请求
    public void bindDevices(String token, ICallBack callBack) {
        //一定要判空，如果是空，创建一个实例就可以了

        iApiService.bindDevices(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }


    public void getPacks(ICallBack callBack) {
        iApiService.getPacks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    public void getDevicePacks(ICallBack callBack) {
        iApiService.getDevicePacks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    public void getUserPacks(ICallBack callBack) {
        iApiService.getUserPacks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    //拿结果
    private Observer getObsetver(final ICallBack callBack) {
        //Rxjava
        Observer observer = new Observer<ResponseBody>() {
            @Override
            public void onCompleted() {
                //完成，所有事件全部完成才会执行
            }

            @Override
            public void onError(Throwable e) {
                //任何一个事件报错后执行
                if (callBack != null) {
                    callBack.failureData(e.getMessage());
                    Log.e("AAA", e.getMessage());
                }
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                //单一事件完成后执行
                //请求完成，会走onNext
                //这里面的请求完成，不代表服务器告诉我们请求成功
                //200 404 503这些都代表请求完成，有结果，中间没报错，但是结果不一定是200
               // responseBody.
                try {
                    String string = responseBody.string();
                    if (callBack != null) {
                        callBack.successData(string);
                        Log.e("AA", "onNext: 请求" + string);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callBack != null) {
                        callBack.failureData(e.getMessage());
                        Log.e("AA", "onNext: Exception请求" + e.getMessage());
                    }
                }
            }
        };
        return observer;
    }

    //回调
    public interface ICallBack {
        void successData(String result);

        void failureData(String error);
    }
}