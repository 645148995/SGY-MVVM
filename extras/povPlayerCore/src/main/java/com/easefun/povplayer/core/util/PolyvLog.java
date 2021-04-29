package com.easefun.povplayer.core.util;

import android.util.Log;

/**
 * Polyv日志输出控制类
 */
public class PolyvLog {
    public static boolean toggle = true;
    public static boolean useCusTag = false;
    public static String cusTag = "tag";

    public static void i(String tag, String msg) {
        if (toggle) {
            Log.i(useCusTag ? PolyvLog.cusTag : tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (toggle) {
            Log.i(useCusTag ? PolyvLog.cusTag : tag, msg, t);
        }
    }

    public static void e(String tag, String msg) {
        if (toggle) {
            Log.e(useCusTag ? PolyvLog.cusTag : tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (toggle) {
            Log.e(useCusTag ? PolyvLog.cusTag : tag, msg, t);
        }
    }
}
