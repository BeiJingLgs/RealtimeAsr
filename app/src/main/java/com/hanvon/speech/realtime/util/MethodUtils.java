package com.hanvon.speech.realtime.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hwebook.HANVONEBK;
import android.os.IBinder;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MethodUtils {
    /*默认数据*/
    private int mSampleRateInHZ = 8000; //采样率
    private  Context mContext;

    public MethodUtils(Context mContext) {
        this.mContext = mContext;
    }
    public  void  getHome(){
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory("android.intent.category.HOME");
        mContext.startActivity(home);
    }
    /**
     * 6.0获取外置sdcard和U盘路径，并区分
     * @param mContext
     * @param keyword SD = "内部存储"; EXT = "SD卡"; USB = "U盘"
     * @return
     */
    public static String getStoragePath(Context mContext, String keyword) {
        List list=new ArrayList<String>();
        String targetpath = "";
        StorageManager mStorageManager = (StorageManager) mContext
                .getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");

            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");

            Method getPath = storageVolumeClazz.getMethod("getPath");

            Object result = getVolumeList.invoke(mStorageManager);

            final int length = Array.getLength(result);

            Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");


            for (int i = 0; i < length; i++) {

                Object storageVolumeElement = Array.get(result, i);

                String userLabel = (String) getUserLabel.invoke(storageVolumeElement);
                list.add(userLabel);
                String path = (String) getPath.invoke(storageVolumeElement);

                if(userLabel.contains(keyword)){
                    targetpath = path;
                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return targetpath ;
    }


    public static void hideSoftInput(Activity activity){
        InputMethodManager im = (InputMethodManager)activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (im != null){
            if (activity.getCurrentFocus() != null)
                im.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static String getDeviceId(){
        HANVONEBK hv_ebk = new HANVONEBK();
        String strId = hv_ebk.GetDeviceID();
        if (strId != null && strId.length() > 0){
            return strId;
        }
        return "";
    }

    public static long getCurrentTimebyNetWork() {
        try {
            URL url = new URL("http://open.baidu.com/special/time/");//取得资源对象
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            long currentTime = uc.getDate(); //取得网站日期时间
            System.out.println("当前时间：" + currentTime);
            return currentTime;
        } catch (Exception e){
            System.out.println("当前时间：error");
        }
        return 0;
    }

    public static RequestBody parseRequestBody(File file) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }

    public static String parseMapKey(String key, String fileName) {
        return key + "\"; filename=\"" + fileName;
    }

    public static int calculatorProgress(int r, int t) {
        if (t == 0)
            return 0;

        if ((t - r) > t)
            return  100;
        return ((t - r) * 100) / t;
    }
}
