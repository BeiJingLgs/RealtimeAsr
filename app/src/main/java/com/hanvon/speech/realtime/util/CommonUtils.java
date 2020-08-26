package com.hanvon.speech.realtime.util;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.asr.ai.speech.realtime.R;
import com.asr.ai.speech.realtime.android.HvApplication;
import com.asr.ai.speech.realtime.full.util.TimeUtil;

import static android.security.KeyStore.getApplicationContext;

public class CommonUtils {
	
	private static final String TAG = "CommonUtils";
	// 键盘隐藏

	public static void hideIME(Context ctx, View v) {
		if (ctx != null && v != null) {
			v.clearFocus();
			v.setActivated(false);
			InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
			    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		}
	}

	// 键盘显示
	public static void showIME(Context ctx, View v) {
		if (ctx != null && v != null) {
			v.setActivated(true);
			v.requestFocus();
			InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
			    imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
			}
		}
	}

	public static void showIME() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 1000);
	}

	public static void hideIME() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(1, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 1000);
	}



	/**
	 * dp转成px
	 * 
	 * @param context
	 *            上下文对象
	 * @param dp
	 *            dp数值
	 * @return px数值
	 * 
	 */
	public static int dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		//LogUtils.printLog("scale", "scale: " + scale);
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * px转成dp
	 * 
	 * @param context
	 *            上下文对象
	 * @param px
	 *            像素数值
	 * @return dp数值
	 * 
	 */
	public static int px2Dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	private static long lastClickTime = 0;

	public static boolean isFastDoubleClick() {
		
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		
		LogUtils.printLog("", "isFastDoubleClick:  lastClickTime:" + lastClickTime + "  now: " + time + "  cha: " + timeD);
		if (0 < timeD && timeD < 1000) {
			LogUtils.printLog("", "isFastDoubleClick2");
			return true;
		}
		LogUtils.printLog("", "isFastDoubleClick3");
		lastClickTime = time;
		return false;
	}

	/** 
     * 将px值转换为sp值，保证文字大小不变 
     * 
     * @param pxValue 
     * @return 
     */  
    public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  
  
    /** 
     * 将sp值转换为px值，保证文字大小不变 
     * 
     * @param spValue 
     * @return 
     */  
    public static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }  
    
	public static String toString(Cursor cursor) {
		StringBuilder sb = new StringBuilder();
		sb.append("cusor:");
		sb.append(cursor.getPosition());
		sb.append(":{");
		if (cursor.getColumnCount() > 0) {
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				sb.append(cursor.getColumnName(i));
				sb.append("=");
				String value = null;
				try {
					value = cursor.getString(i);
				} catch (Exception e) {
					e.printStackTrace();
				}
				sb.append(value);
				sb.append(",");
			}
		}
		sb.append("}");
		return sb.toString();
	}


    public static int getScreenWidth(Context context) {
		return getScreenSize(context)[0];
	}
 
	private int getScreenHeight(Context context) {
		return getScreenSize(context)[1];
	}
 
	private static int[] getScreenSize(Context context) {
		WindowManager windowManager;
		try {
			windowManager = (WindowManager)context.getSystemService("window");
		} catch (Throwable var6) {
			Log.w(TAG, var6);
			windowManager = null;
		}
 
		if(windowManager == null) {
			return new int[]{0, 0};
		} else {
			Display display = windowManager.getDefaultDisplay();
			if(Build.VERSION.SDK_INT < 13) {
				DisplayMetrics t1 = new DisplayMetrics();
				display.getMetrics(t1);
				return new int[]{t1.widthPixels, t1.heightPixels};
			} else {
				try {
					Point t = new Point();
					Method method = display.getClass().getMethod("getRealSize", new Class[]{Point.class});
					method.setAccessible(true);
					method.invoke(display, new Object[]{t});
					return new int[]{t.x, t.y};
				} catch (Throwable var5) {
					Log.w(TAG, var5);
					return new int[]{0, 0};
				}
			}
		}
	}
	
	 public static String getVersionName(Context context) {
			PackageInfo packInfo = null;
			try {
				// 获取packagemanager的实例
				PackageManager packageManager = context.getPackageManager();
				// getPackageName()是你当前类的包名，0代表是获取版本信息
				packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
				return packInfo.versionName;
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}
	    
	    public static String getVersionCode2Name(String string) {
	    	int code = Integer.valueOf(string) + 100;
			if (code <= 100)
				return "";
			else {
				return String.valueOf(code / 100.0);
			}
		}

    
    public static int getVersionCode(Context context) {
		PackageInfo packInfo = null;
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packInfo.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	public static void saveAsFileWriter(String filePath, String content, boolean isappend, boolean isToast) {
		FileWriter fwriter = null;
		StringBuffer buffer = new StringBuffer(TimeUtil.getCurrentTime());
		buffer.append("\n").append(content).append("\n");
		try {
			// true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
			fwriter = new FileWriter(filePath, isappend);
			fwriter.write(buffer.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fwriter.flush();
				fwriter.close();
                filePath = filePath.substring(20, filePath.length());
				String path = getApplicationContext().getString(R.string.saveTo) + filePath;
				if (isToast)
					ToastUtils.show(getApplicationContext(), path);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void setOnValidate(boolean isValidate, Context context) {
    	setScreenOffValidation(isValidate, context);
    	setSleepValidation(context, isValidate);
	}

	/**
	 * 设置是否允许锁屏
	 */
	public static void setScreenOffValidation(boolean isValidate, Context context) {
		if (context == null) {
			context = HvApplication.mContext;
		}

		if (isValidate) {
			reLock(context);
		} else {
			unLock(context);
		}
	}

	private static void unLock(Context context) {
		hvLockscreenUtils.setLockEnable(context, false);
	}

	private static void reLock(Context context) {
		hvLockscreenUtils.setLockEnable(context, true);
	}

	// forbidden to sleep, add by cs 20180125
	private static void unsleep(Context mContext) {
		// forbidden to sleep,add by cs 20180125
		Intent intentUnsleep = new Intent();
		intentUnsleep.setAction("hanvon.intent.action.sysunsleep");
		mContext.sendBroadcast(intentUnsleep);
	}

	// allow to sleep, add by cs 20180125
	private static void resleep(Context mContext) {
		// allow to sleep,add by cs 20180125
		Intent intentUnsleep = new Intent();
		intentUnsleep.setAction("hanvon.intent.action.sysresleep");
		mContext.sendBroadcast(intentUnsleep);
	}

	/**
	 * 设置是否允许休眠
	 */
	public static void setSleepValidation(Context mContext, boolean isValidate) {
		if (mContext == null) {
			mContext = HvApplication.mContext;
		}

		if (isValidate) {
			resleep(mContext);
		} else {
			unsleep(mContext);
		}
	}
}
