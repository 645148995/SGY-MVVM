package com.ctvit.shitingxizang.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.goldze.mvvmhabit.utils.KLog;

/**
 * Created by 1 on 2018/4/14.
 * 时间转换工具
 */

public class DateUtil {

    /**
     * 格式化日期的标准字符串
     */
    public final static String My_Format = "yyyy-MM-dd HH:mm:ss";
    public final static String Format8 = "yyyy-MM-dd";
    //  private final static String Simple_Format="yyyy-MM-dd";
    public final static String Date_Str_Format = "yyyyMMdd";//20180122
    public final static String Sys_Hms_Format = "HH:mm:ss";


    /**
     * 将日期字符串转换为Date对象
     *
     * @param dateStr 日期字符串
     * @param format  格式化字符串，例如"yyyy-MM-dd HH:mm:ss"
     * @return 日期字符串的Date对象表达形式
     */
    public static Date parseToDate(String dateStr, String format) {
        Date dt = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        try {
            dt = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dt;
    }

    /**
     * 将date----->String
     * 将Date对象转换为指定格式的字符串
     *
     * @param date     Date对象
     * @param //String format 格式化字符串
     * @return Date对象的字符串表达形式
     */
    public static String formatDateToStr(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(date);
    }

    /**
     * 掉此方法输入所要转换的时间输入例如（"yyyy-MM-dd HH:mm:ss"）返回时间戳
     */
    public static long getTimeStampLong(String timeStr) {
        long l = 0;
        try {
            Date date = parseToDate(timeStr, My_Format);
            l = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }


    //把毫秒转换为 hh:mm:ss的形式
    public static String millsToHMS(long milliseconds) {

        int toSeconds = (int) (milliseconds / 1000);

        int hour = (toSeconds / 3600);
        int minute = ((toSeconds - hour * 3600) / 60);
        int seconds = (toSeconds - hour * 3600 - minute * 60);

        String hourStr = hour + "";
        String minuteStr = minute + "";
        String secondsStr = seconds + "";
        if (hour < 10) {
            if (hour == 0) {
                hourStr = "00";
            } else {
                hourStr = "0" + hour;
            }
        }

        if (minute < 10) {
            if (minute == 0) {
                minuteStr = "00";
            } else {
                minuteStr = "0" + minute;
            }
        }

        if (seconds < 10) {
            if (seconds == 0) {
                secondsStr = "00";
            } else {
                secondsStr = "0" + seconds;
            }
        }

        return hourStr + ":" + minuteStr + ":" + secondsStr;
    }

    /**
     * 计算两个日期相差几天
     */
    public static String dateDiff(String publishTime) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(My_Format, Locale.getDefault());
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        try {
            // 获得两个时间的毫秒时间差异
            diff = new Date().getTime() - sd.parse(publishTime).getTime();
            long day = diff / nd;               // 计算差多少天
            long hour = diff % nd / nh;         // 计算差多少小时
            long min = diff % nd % nh / nm;     // 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒
            long month = day / 30;              // 计算差多少月
            long year = month / 12;              // 计算差多少年
            // 输出结果
            System.out.println("时间相差：" + year + "年" + month + "月" + day + "天" + hour + "小时" + min
                    + "分钟" + sec + "秒。");
            if (year >= 1) {
                return year + "年前";
            } else if (month >= 1) {
                return month + "个月前";
            } else if (day >= 1) {
                return day + "天前";
            } else if (hour >= 1) {
                return hour + "小时前";
            } else if (min >= 1) {
                return min + "分钟前";
            } else {
                return "刚刚";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 计算日期相差是否超过一天，（历史记录 今天，更早）
     */
    public static boolean dateDiffOneDay(String publishTime) {
        KLog.i("publishTime" + publishTime);
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(My_Format, Locale.getDefault());
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        try {
            // 获得两个时间的毫秒时间差异
            diff = new Date().getTime() - sd.parse(publishTime).getTime();
            long day = diff / nd;               // 计算差多少天
            long hour = diff % nd / nh;         // 计算差多少小时
            long min = diff % nd % nh / nm;     // 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒
            long month = day / 30;              // 计算差多少月
            long year = month / 12;              // 计算差多少年
            // 输出结果
            System.out.println("时间相差：" + year + "年" + month + "月" + day + "天" + hour + "小时" + min
                    + "分钟" + sec + "秒。");
            KLog.i("publishTime" + day);
            if (day >= 1) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 计算两个日期相差，传来的日期小于当前的日期，直播已经开始
     * 如果正在直播，返回true
     */
    public static boolean isLiveing(String publishTime) {
        if (TextUtils.isEmpty(publishTime))
            return false;
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(My_Format, Locale.getDefault());
        try {
            if (sd.parse(publishTime).getTime() - new Date().getTime() < 0)
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断直播状态 0 已结束  1 正在直播  2 未开始
     */
    public static int liveState(String startTime, String endTime, long serverTime) {
        if (serverTime == -1) {
            return -1;    //表示未与服务器时间比较
        }
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(My_Format, Locale.getDefault());
        try {
            long start = sd.parse(startTime).getTime();
            long end = sd.parse(endTime).getTime();
            if (serverTime - end > 0) {
                return 0;   //已结束
            } else if (serverTime - start > 0) {
                return 1;   //正在直播
            } else {
                return 2;   //未开始
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * "2010-10-01 12:00:00"
     */
    public static String get7x24TimeFormat(String timeStr) {
        long stamp = getTimeStampLong(timeStr);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long zeroToday = calendar.getTimeInMillis();

        int minDay = calendar.getActualMinimum(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, minDay);
        long lastMonth = calendar.getTimeInMillis();

        KLog.i("get7x24TimeFormat : timeStr = " + timeStr +
                ", zeroToday = " + formatDateToStr(new Date(zeroToday), My_Format) +
                ", lastMonth = " + formatDateToStr(new Date(lastMonth), My_Format));

        if (stamp >= zeroToday) {
            return formatDateToStr(new Date(stamp), "HH:mm");
        } else if (stamp >= lastMonth) {
            return formatDateToStr(new Date(stamp), "MM-dd HH:mm");
        } else {
            return formatDateToStr(new Date(stamp), "yyyy-MM-dd HH:mm");
        }
    }


    /**
     * "2010-10-01 12:00:00"
     */
    public static String getShareImgTimeFormat(String timeStr) {
        long stamp = getTimeStampLong(timeStr);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long zeroToday = calendar.getTimeInMillis();

        int minDay = calendar.getActualMinimum(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, minDay);
        long lastMonth = calendar.getTimeInMillis();

        KLog.i("get7x24TimeFormat : timeStr = " + timeStr +
                ", zeroToday = " + formatDateToStr(new Date(zeroToday), My_Format) +
                ", lastMonth = " + formatDateToStr(new Date(lastMonth), My_Format));

        return formatDateToStr(new Date(stamp), "MM月dd日 HH:mm");
    }

    /**
     * 日期转星期
     *
     * @param datetime
     * @return
     */
    public static String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 日期转星期
     *
     * @param datetime
     * @return
     */
    public static String dateToWeek2(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

}
