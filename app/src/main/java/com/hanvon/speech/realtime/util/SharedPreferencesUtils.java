package com.hanvon.speech.realtime.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.R.string;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

public class SharedPreferencesUtils {

    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "share_date";
    public static final String SEARCH_HISTORY = "search_history";
    public static final String LOCAL_SEARCH_HISTORY = "local_search_history";
    public static final String PERMANENT_ID = "permanent_id";
    public static final String ISFIRST = "isFirst";
	public static final String SESSION = "session";
	public static final String TOKEN = "token";
	public static final String LOGIN = "login";
	public static final String ENGINE = "engine";

	public static final String USAGETIME = "usage_time";
	public static final String PLAYTIME = "play_time";
    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param context
     * @param key
     * @param object
     */
    public static void saveStringSharePrefer(Context context , String key, String object){

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, object);
        editor.commit();
    }

	public static void saveUsageTimeSharePrefer(Context context , String key, long object){

		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putLong(key, object);
		editor.commit();
	}

	public static long getUsageTimeSharedprefer(Context context , String key){
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		return sp.getLong(key,  0);

	}


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param context editor.putLong("height", 175L);
     * @param key
     * @param defaultObject
     * @return
     */
    public static String getStringSharedprefer(Context context , String key, String defaultObject){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, (String)defaultObject);
        
    }
    public static String getStringSharedprefer(Context context , String key){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }
    
    public static boolean getIsFirstRun(Context context){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ISFIRST, true);
    }
    
    public static void setFirstRun(Context context , Boolean bool){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(ISFIRST, bool);
        editor.commit();
    }
    
    
    /**
     * 清除所有数据
     * @param context
     */
    public static void clearAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
    }

    /**
     * 清除指定数据
     * @param context
     */
    public static void clear(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

	/**
	 * 清除指定数据
	 * @param context
	 */
	public static void clearAll(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}
    
    public static void saveHistory(Context context, String key, String searchStr) { 
    	String history = getStringSharedprefer(context, key);
		if (history == null) {
			saveStringSharePrefer(context, key, searchStr);
			return ;
		}
		String[] tmpHistory = history.split("h&v");

		Log.i("tmpHistory", "tmpHistory : " + tmpHistory);
		List<String> historyList = new ArrayList<String>(Arrays.asList(tmpHistory));
		if (historyList.size() > 0) {
			for (int i = 0; i < historyList.size(); i++) {  
                if (searchStr.equals(historyList.get(i))) {  
                	historyList.remove(i);  
                    break;  
                }  
            }
			historyList.add(0, searchStr);
		}
		StringBuilder sb = new StringBuilder();  
		if (historyList.size() > 0) {
			
			for (int i = 0; i < historyList.size() && i < 9; i++) {
				sb.append(historyList.get(i)).append("h&v");
			}
			sb.substring(0, sb.length() - 3);
		}
		Log.i("saveHistory", sb.toString());
		saveStringSharePrefer(context, key, sb.toString());
	}
    
    public static List<String> getHistory(Context context, String key) {
    	String history = getStringSharedprefer(context, key);
    	if (history == null)
    			return null;
    	String[] tmpHistory = history.split("h&v");
    	Log.i("tmpHistory", "tmpHistory[] : " + tmpHistory);
    	List<String>historyList = new ArrayList<String>(Arrays.asList(tmpHistory));
		return historyList;
	}
    
    public static void saveLocalSearchHistory(Context context, String key, String searchStr) { 
    	String history = getStringSharedprefer(context, key);
		if (history == null) {
			saveStringSharePrefer(context, key, searchStr);
			return ;
		}
		String[] tmpHistory = history.split("h#v");

		Log.i("tmpHistory", "tmpHistory : " + tmpHistory);
		List<String> historyList = new ArrayList<String>(Arrays.asList(tmpHistory));
		if (historyList.size() > 0) {
			for (int i = 0; i < historyList.size(); i++) {  
                if (searchStr.equals(historyList.get(i))) {  
                	historyList.remove(i);  
                    break;  
                }  
            }
			historyList.add(0, searchStr);
		}
		StringBuilder sb = new StringBuilder();  
		if (historyList.size() > 0) {
			
			for (int i = 0; i < historyList.size() && i < 9; i++) {
				sb.append(historyList.get(i)).append("h#v");
			}
			sb.substring(0, sb.length() - 3);
		}
		Log.i("saveHistory", sb.toString());
		saveStringSharePrefer(context, key, sb.toString());
	}
    
    public static List<String> getLocalSearchHistory(Context context, String key) {
    	String history = getStringSharedprefer(context, key);
    	if (TextUtils.isEmpty(history))
    			return null;
    	String[] tmpHistory = history.split("h#v");
    	Log.i("tmpHistory", "tmpHistory[] : " + tmpHistory);
    	List<String>historyList = new ArrayList<String>(Arrays.asList(tmpHistory));
		return historyList;
	}


	public static void saveLoginStatesSharePrefer(Context context, String state){
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(LOGIN, state);
		editor.commit();
	}



	public static String getLoginStatesprefer(Context context , String key){
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		return sp.getString(key, "login");
	}

	public static int getRecogEngineSharePrefer(Context context){
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		return sp.getInt(ENGINE, 1);
	}
	public static void setRecogEngineSharePrefer(Context context, int state){
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(ENGINE, state);
		editor.commit();
	}


}