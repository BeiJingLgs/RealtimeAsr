package com.hanvon.speech.realtime.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.storage.StorageManager;
import android.widget.EditText;


public class hvReflectUtils {
    public static void disableShowInput(EditText et) {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(et, false);
        } catch (Exception e) {//TODO: handle exception
        }
    }
    
    public static String getStoragePath(Context context,String keyword) {
		String targetpath = "MEDIA_UNKNOW";
    	StorageManager mStorageManager = (StorageManager) context
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
}

