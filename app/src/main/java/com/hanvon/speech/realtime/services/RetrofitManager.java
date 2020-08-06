package com.hanvon.speech.realtime.services;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.asr.ai.speech.realtime.R;
import com.asr.ai.speech.realtime.android.HvApplication;
import com.google.gson.Gson;
import com.hanvon.speech.realtime.bean.Result.Constant;
import com.hanvon.speech.realtime.bean.Result.LoginResult;
import com.hanvon.speech.realtime.util.BasePath;
import com.hanvon.speech.realtime.util.DialogUtil;
import com.hanvon.speech.realtime.util.MethodUtils;
import com.hanvon.speech.realtime.util.ToastUtils;
import com.hanvon.speech.realtime.util.WifiUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.hanvon.speech.realtime.ui.BaseActivity.DEVICEID;
import static com.hanvon.speech.realtime.util.WifiUtils.isNetWorkConneted;


/*Time:2019/5/15
 *Author:guhongbo
 *Description:
 */
public class RetrofitManager {

    private AppServiceApi iApiService;
    private Retrofit mRetrofit;
    private static Handler mHandler;
    private static Context c;


    private RetrofitManager() {
        initRetrofit();
    }

    //静态内部类单例
    private static class oKHPMH {
        public static RetrofitManager instance = new RetrofitManager();
    }

    public static RetrofitManager getInstance(Context context) {
        c = context;
        if (!WifiUtils.isWifiOpened()) {
            new Handler(HvApplication.getContext().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.getInstance().showNetWorkDialog(context);
                    //ToastUtils.show(HvApplication.getContext(), HvApplication.getContext().getResources().getString(R.string.openwifi));
                }
            });

            /*WifiOpenHelper wifi = new WifiOpenHelper(HvApplication.getContext());
            wifi.openWifi();
            HvApplication.getContext().startActivity(new Intent(
                    android.provider.Settings.ACTION_WIFI_SETTINGS));*/
            return oKHPMH.instance;
        }
        if (WifiUtils.getWifiConnectState(HvApplication.getContext()) == NetworkInfo.State.DISCONNECTED) {
            new Handler(HvApplication.getContext().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show(HvApplication.getContext(), HvApplication.getContext().getResources().getString(R.string.checkNeterror));
                }
            });
            return oKHPMH.instance;
        }
         else if (!isNetWorkConneted(HvApplication.getContext())) {
            new Handler(HvApplication.getContext().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show(HvApplication.getContext(), HvApplication.getContext().getResources().getString(R.string.checkNet));
                }
            });
        }
        return oKHPMH.instance;
    }



    //初始化fit
    private void initRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new ReceivedCookiesInterceptor()) //首次请求
                .addInterceptor(new AddCookiesInterceptor())
                //.addInterceptor()//首次请求
                .retryOnConnectionFailure(true);
        OkHttpClient client = builder.build();

        //Retrofit的创建
        mRetrofit = new Retrofit.Builder()
                //添加Rxjava工厂
                //.client(getOkHttpClient())//获取后的okhttp头部
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BasePath.BASE_TEST_URL)
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
                                //.addHeader("User-Agent", WebSettings.getDefaultUserAgent(HvApplication.mContext))//添加真正的头部
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
    public void sendByRegisteredUser(String phone, final ICallBack callback) {
        iApiService.sendByRegisteredUser(phone)
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
    public void loginBySMS(String phone, String sms, String deviceid, final ICallBack callback) {
        iApiService.loginBySMS(phone, sms, deviceid)
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
    public void changePasswordBySms(HashMap<String, String> params, ICallBack callBack) {
        //一定要判空，如果是空，创建一个实例就可以了
        if (params == null) {
            params = new HashMap<>();
        }
        iApiService.changePasswordBySms(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    //POst请求
    public void submitUsedTime(HashMap<String, String> params, ICallBack callBack) {
        //一定要判空，如果是空，创建一个实例就可以了
        if (params == null) {
            params = new HashMap<>();
        }
        iApiService.submitUsedTime(HvApplication.TOKEN, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    //POst请求
    public void createOrderByPack(HashMap<String, String> params, ICallBack callBack) {
        //一定要判空，如果是空，创建一个实例就可以了
        if (params == null) {
            params = new HashMap<>();
        }
        iApiService.createOrderByPack(HvApplication.TOKEN, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    //POst请求
    public void payOrder(HashMap<String, String> params, ICallBack callBack) {
        //一定要判空，如果是空，创建一个实例就可以了
        if (params == null) {
            params = new HashMap<>();
        }
        iApiService.payOrder(HvApplication.TOKEN, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    //POst请求
    public void PayOrderByWxNative(HashMap<String, String> params, ICallBack callBack) {
        //一定要判空，如果是空，创建一个实例就可以了
        if (params == null) {
            params = new HashMap<>();
        }
        iApiService.PayOrderByWxNative(HvApplication.TOKEN, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    //POst请求
    public void payOrder(String phone, String pass, ICallBack callBack) {
        //一定要判空，如果是空，创建一个实例就可以了

        iApiService.payOrder(HvApplication.TOKEN, phone, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }


    //POst请求
    public void getBindUser(String deviceSerialNo, ICallBack callBack) {
        //一定要判空，如果是空，创建一个实例就可以了

        iApiService.getBindUser(HvApplication.TOKEN, deviceSerialNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    //POst请求
    public void getBindDevices(ICallBack callBack) {
        //一定要判空，如果是空，创建一个实例就可以了

        iApiService.getBindDevices(HvApplication.TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }


    public void getPacks(ICallBack callBack) {
        iApiService.getPacks(HvApplication.TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    public void getDevicePacks(String token, ICallBack callBack) {
        iApiService.getDevicePacks(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    public void getUserPacks(ICallBack callBack) {
        iApiService.getUserPacks(HvApplication.TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    public void getAccountPacks( String curPage, String pageSize, String sort, ICallBack callBack) {
        iApiService.getAccountPacks(HvApplication.TOKEN, curPage, pageSize, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    public void getPayChannels(ICallBack callBack) {
        iApiService.getPayChannels(HvApplication.TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callBack));
    }

    //Get请求
    public void getUseRecord(String curPage, String pageSize, String sort, final ICallBack callback) {
        iApiService.getUseRecord(HvApplication.TOKEN, curPage, pageSize, sort)
                //被观察者执行在哪个线程，这里面执行在io线程，io线程是一个子线程
                .subscribeOn(Schedulers.io())
                //最终完成后结果返回到哪个线程，mainThread代表主线
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callback));
    }


    //Get请求
    public void getOrders(String curPage, String pageSize, String sort, final ICallBack callback) {
        iApiService.getOrders(HvApplication.TOKEN, curPage, pageSize, sort)
                //被观察者执行在哪个线程，这里面执行在io线程，io线程是一个子线程
                .subscribeOn(Schedulers.io())
                //最终完成后结果返回到哪个线程，mainThread代表主线
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callback));
    }

    //Get请求
    public void getOrder(String curPage, final ICallBack callback) {
        iApiService.getOrder(HvApplication.TOKEN, curPage)
                //被观察者执行在哪个线程，这里面执行在io线程，io线程是一个子线程
                .subscribeOn(Schedulers.io())
                //最终完成后结果返回到哪个线程，mainThread代表主线
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callback));
    }



    //Get请求
    public void upLoadFile(HashMap<String, RequestBody> params, final ICallBack callback) {
        iApiService.upLoadFile(HvApplication.TOKEN, params)
                //被观察者执行在哪个线程，这里面执行在io线程，io线程是一个子线程
                .subscribeOn(Schedulers.io())
                //最终完成后结果返回到哪个线程，mainThread代表主线
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsetver(callback));
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
                    Log.e("AAA", e.getMessage()  + "  e.getClass(): " + e.getClass());
                    if (e instanceof NullPointerException) {
                        String id = "";
                        if (HvApplication.ISDEBUG) {
                            id = DEVICEID;
                        } else {
                            id = MethodUtils.getDeviceId();
                        }
                        RetrofitManager.getInstance(c).loginByDeviceId(id, new RetrofitManager.ICallBack() {
                            @Override
                            public void successData(String result) {
                                Gson gson2 = new Gson();
                                LoginResult c = gson2.fromJson(result, LoginResult.class);
                                Log.e("A", "onResponse: " + result + "返回值");
                                if (TextUtils.equals(c.getCode(), Constant.SUCCESSCODE)) {
                                    HvApplication.TOKEN = c.getToken();
                                }
                            }
                            @Override
                            public void failureData(String error) {
                                Log.e("AA", "error: " + error + "错");
                            }
                        });
                    } else if(TextUtils.equals(e.getMessage(), "HTTP 403 Forbidden")) {
                        ToastUtils.showLong(HvApplication.getContext(), HvApplication.getContext().getString(R.string.loginByPass));
                    } else if(e instanceof UnknownHostException) {
                        //ToastUtils.showLong(HvApplication.getContext(), "服务器开小差了，请稍候再试");
                    } else if (e instanceof SocketTimeoutException) {
                        ToastUtils.showLong(HvApplication.getContext(), HvApplication.getContext().getString(R.string.timeOut));
                    }
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
