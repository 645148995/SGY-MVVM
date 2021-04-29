package com.cctv.cctvplayer.utils;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 比例计算工具类
 */

public final class ScaleUtils {

    /**
     * 根据屏幕宽度计算高度比例，用于宽比高长
     *
     * @param scaleW   宽比例
     * @param scaleH   高比例
     * @param marginPx 左右边距
     * @return 高度 单位 px
     */
    public static int countScale(Context context, int scaleW, int scaleH, int marginPx) {
        int sw = ScreenUtils.getWidth(context) - marginPx;
        float scale = 1;
        try {
            scale = Float.valueOf(percnet(Double.valueOf(scaleW + ""), Double.valueOf(scaleH + "")));
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return Math.round(sw / scale);
    }

    /**
     * 获取百分比
     */
    public static String percnet(double d, double e) {
        double p = d / e;
        DecimalFormat nf = (DecimalFormat) NumberFormat.getPercentInstance();
        nf.applyPattern("00"); // 00表示小数点2位
        nf.setMaximumFractionDigits(2); // 2表示精确到小数点后2位
        return nf.format(p);
    }
}
