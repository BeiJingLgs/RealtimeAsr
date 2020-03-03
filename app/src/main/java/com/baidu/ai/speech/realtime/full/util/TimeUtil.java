package com.baidu.ai.speech.realtime.full.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by guhongbo on 2019/12/18.
 */

public class TimeUtil {

    public static String convertMillions2Time(long ms) {
        SimpleDateFormat formatter;
        if (ms > 1000 * 60 * 60) {
            formatter = new SimpleDateFormat("HH:mm:ss");
        } else {
            formatter = new SimpleDateFormat("mm:ss");
        }
        //这里想要只保留分秒可以写成"mm:ss"
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(ms);
        return hms;
    }

    public static String getDate(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = formatter.format(date);
        return dateString;

    }

    public static String getTime(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        String dateString = formatter.format(date);
        return dateString;

    }

    public static String getDateDetail(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String getCurrentTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return days + " days " + hours + " hours " + minutes + " minutes "
                + seconds + " seconds ";
    }

    /**
     * 返回时分秒
     * @param second
     * @return
     */
    public static String secondToTime(long second) {
        long hours = second / 3600;//转换小时数
        second = second % 3600;//剩余秒数
        long minutes = second / 60;//转换分钟
        second = second % 60;//剩余秒数
        if (0 < hours){
            if (minutes == 0) {
                return hours+"小时 ";
            } else {
                return hours+"小时 "+minutes+"分 ";
            }
        }else {
            if (second == 0) {
                return minutes+"分 ";
            } else {
                return minutes+"分 "+second+"秒";
            }
        }
    }

    /**
     * 返回时分秒
     * @param second
     * @return
     */
    public static String hourToTime(long second) {
        long hours = second / 24;//转换小时数
        second = second % 24;//剩余秒数
        long minutes = second / 60;//转换分钟
        second = second % 60;//剩余秒数
        if (0 < hours){
            if (minutes == 0) {
                return hours+"天 ";
            } else {
                return hours+"天 "+minutes+"小时";
            }

        }else {
            return minutes+"小时";
        }
    }

    public static String centToyuan(long second) {
        double yuan = second / 100.0;
        return yuan + "元";
    }
}
