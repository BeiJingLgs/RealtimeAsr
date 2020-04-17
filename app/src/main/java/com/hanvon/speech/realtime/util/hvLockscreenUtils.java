package com.hanvon.speech.realtime.util;
import com.baidu.ai.speech.realtime.android.HvApplication;

import android.content.Context;
import android.provider.Settings;

public class hvLockscreenUtils {
	public static final int DISABLELOCKTIME = 604800000;
	public static final int DEFAULTLOCKTIME = 300*1000;
	public static int curlocktime = DEFAULTLOCKTIME;
	
	public static final String DEFLOCKPIC = "/system/media/standby/lockscreen_def.png";
	
	// 0: 没锁屏；1，锁屏；2，解锁画面，3，正在关机，不允许锁屏
	public static final String SETTINGS_SCREENLOCKSTATUS = "hanvon_screenlockstatus";
	
	// 解锁时是否正在显示解锁密码框
	public static final String SETTINGS_UNLOCKPSDSTATUS = "hanvon_isPsdActivityShow";
	
	// 是否有锁屏和开机密码:
	// 1. 如果有开机密码，插上USB线就不提示数据传输， SETTINGS_UNLOCKPSDSTATUS晚于usb消息，在开机时不可靠，开机结束了可靠
	public static final String SETTINGS_HASPASSWORD = "hanvon_haspassword";

	
	
	
	public static void setLockEnable(Context context, boolean bEnable){
		if(context == null)
			context = HvApplication.mContext;
		// dj Android8.1需要添加禁止和允许锁屏
		if (!bEnable){
			modifyLockTime(context, DISABLELOCKTIME);
		}else{
			modifyLockTime(context, curlocktime);
		}
	}
	
	public static void restoreDefLocktime(Context context){
		if(context == null)
			context = HvApplication.mContext;
		modifyLockTime(context,DEFAULTLOCKTIME);
	}
	
	public static void forceRestoreDefTimeIfNeed(Context context){
		if(context == null)
			context = HvApplication.mContext;
		int timeTmp = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, DEFAULTLOCKTIME);
		if (timeTmp == DISABLELOCKTIME){
			restoreDefLocktime(context);
		}
	}
	
	public static void modifyLockTime(Context context, int time){
		if(context == null)
			context = HvApplication.mContext;
		int timeTmp = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, DEFAULTLOCKTIME);
		if (time != timeTmp){
			Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
			curlocktime = time;
		}
	}
	
	
	public static boolean isLocked(Context context){
		if(context == null)
			context = HvApplication.mContext;
		int locked = Settings.Secure.getInt(context.getContentResolver(), SETTINGS_SCREENLOCKSTATUS, 0);
		return ((locked == 1) || (locked == 2));
	}
	
	
	public static int getLockStatus(Context context){
		if(context == null)
			context = HvApplication.mContext;
		int status = Settings.Secure.getInt(context.getContentResolver(), "hanvon_screenlockstatus", 0);
		return status;
	}
	
	public static int getPsdType(Context context){
		if(context == null)
			context = HvApplication.mContext;
		return Settings.Secure.getInt(context.getContentResolver(), SETTINGS_HASPASSWORD, 
 				0);
	}
}



