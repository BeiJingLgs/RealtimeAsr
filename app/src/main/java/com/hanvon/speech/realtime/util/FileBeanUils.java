package com.hanvon.speech.realtime.util;

import com.hanvon.speech.realtime.bean.FileBean;

public class FileBeanUils {
    private static FileBean mFileBean;
    private static boolean isRecoding;

    public static long getmStartRecordTime() {
        return mStartRecordTime;
    }

    public static void setmStartRecordTime(long mStartRecordTime) {
        FileBeanUils.mStartRecordTime = mStartRecordTime;
    }

    private static long mStartRecordTime;


    public static FileBean getmFileBean() {
        return mFileBean;
    }

    public static void setmFileBean(FileBean mFileBean2) {
        mFileBean = mFileBean2;
    }

    public static boolean isRecoding() {
        return isRecoding;
    }

    public static void setRecoding(boolean recoding) {
        isRecoding = recoding;
    }

    public static long getCurrrentRecordTime() {
        return mFileBean.getTime() + System.currentTimeMillis() - mStartRecordTime;
    }

}
