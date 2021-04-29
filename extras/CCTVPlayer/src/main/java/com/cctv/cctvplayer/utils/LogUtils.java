package com.cctv.cctvplayer.utils;

import android.text.TextUtils;
import android.util.Log;

import com.cctv.cctvplayer.BuildConfig;

/**
 * 日志工具类
 */

public final class LogUtils {
    static String className;//类名
    static String methodName;//方法名
    static int lineNumber;//行数

    private LogUtils() {

    }

    public static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(methodName);
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
        buffer.append(log);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

//    public static void e(String message) {
//        if (!isDebuggable())
//            return;
//
//        getMethodNames(new Throwable().getStackTrace());
//        Log.e(className, createLog(message));
//    }

    public static void e(String message, Throwable tr) {
        if (TextUtils.isEmpty(message))
            message = "";

        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.e(className, createLog(message), tr);
    }

    public static void e(Throwable tr) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.e(className, createLog(""), tr);
    }

    //规定每段显示的长度
    private static final int LOG_MAXLENGTH = 2000; //其实默认是4000，但是写3900打印还是不全

    /**
     * 用于超过长度的Log打印 处理是换行打印
     */
    public static void e(String msg) {
        if (TextUtils.isEmpty(msg))
            msg = "";

        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());

        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.e(className, "(" + methodName + ":" + lineNumber + ")[" + i + "]" + msg.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.e(className, createLog(msg.substring(start, strLength)));
                break;
            }
        }
    }

    /**
     * 用于超过长度的Log打印 处理是换行打印
     */
    public static void i(String msg) {
        if (TextUtils.isEmpty(msg))
            msg = "";

        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());

        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.i(className, "(" + methodName + ":" + lineNumber + ")[" + i + "]" + msg.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.i(className, createLog(msg.substring(start, strLength)));
                break;
            }
        }
    }
}
