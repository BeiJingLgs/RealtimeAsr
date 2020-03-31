package com.hanvon.speech.realtime.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
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

		StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		try {
			Class storeManagerClazz = Class.forName("android.os.storage.StorageManager");

			Method getVolumesMethod = storeManagerClazz.getMethod("getVolumes");

			List<?> volumeInfos  = (List<?>)getVolumesMethod.invoke(storageManager);

			Class volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");

			Method getTypeMethod = volumeInfoClazz.getMethod("getType");
			Method getFsUuidMethod = volumeInfoClazz.getMethod("getFsUuid");

			Field fsTypeField = volumeInfoClazz.getDeclaredField("fsType");
			Field fsLabelField = volumeInfoClazz.getDeclaredField("fsLabel");
			Field pathField = volumeInfoClazz.getDeclaredField("path");
			Field internalPath = volumeInfoClazz.getDeclaredField("internalPath");
			Field diskIdField = volumeInfoClazz.getDeclaredField("disk");

			if(volumeInfos != null){
				for(Object volumeInfo:volumeInfos){
					String uuid = (String)getFsUuidMethod.invoke(volumeInfo);
					if(uuid != null){
						if (volumeInfo.toString().contains(keyword)){
							targetpath = (String)pathField.get(volumeInfo);
							break;
						}

					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return targetpath;


	}
}

