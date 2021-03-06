package com.hanvon.speech.realtime.util;

import android.view.Window;

public class EPDHelper {

    private static EPDHelper instance = null;
    public enum Mode {
        DU(0x02),
        GC16(0x4),
        GC16_LOCAL(0x84),
        DU_RECT(0x402),
        GC16_RECT(0x404),
        A2_RECT(0x410),
        GU16_RECT(0x484),
        GC16_LOCAL_RECT(0x484);

        private int val;
        Mode(int m) {
            val = m;
        }

        int value() {
            return val;
        }
    }

    static public EPDHelper getInstance() {
        if (instance == null)
            instance = new EPDHelper();
        return instance;
    }

    private EPDHelper() {
    }

    public void setWindowRefreshMode(Window window, Mode m) {
        System.out.println("Window mode" + m);
       // window.setRefreshMode(m.value());
    }
}
