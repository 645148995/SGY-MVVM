package com.ctvit.shitingxizang.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;


import androidx.core.app.ActivityCompat;

import com.ctvit.shitingxizang.app.MyApplication;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public final class DeviceUtils {
    /**
     * 设备ID唯一ID（不一定100%唯一）
     */
    public static String getDeviceId() {
        Context context = MyApplication.getInstance().getApplicationContext();
        String deviceId = "";
        try {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                deviceId = ((TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            }
            if (TextUtils.isEmpty(deviceId) || "000000000000000".equals(deviceId)) {
                deviceId = Secure.getString(context.getContentResolver(),
                        Secure.ANDROID_ID);
            }
        } catch (Exception e) {
            deviceId = Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID);
            LogUtils.e(e);
        }

        if (TextUtils.isEmpty(deviceId) || "9774d56d682e549c".equals(deviceId)) {
            deviceId = android.os.Build.SERIAL;
        }

        //其实也可以自己生成UUID
        return deviceId;
    }

    /**
     * 获取mac地址
     * 有可为null，也有可能是 00:00:00:00:00:00
     */
    public final static String getMac() {
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null) {
                macSerial += line.trim();
            }
            input.close();
        } catch (IOException e) {
            //这里可以设置个默认的...
            LogUtils.e(e);
        }
        return macSerial;
    }

    /**
     * 当前是否使用的是 WIFI网络
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI
                && activeNetInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 屏幕分辨率
     */
    public static Integer[] getScreenWH(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);

        return new Integer[]{dm.widthPixels, dm.heightPixels};
    }

    /**
     * 获取手机厂商
     * @return
     */
    public static String getDeviceBrand(){
        return android.os.Build.BRAND;
    }

    /**
     * 获取设备型号
     * @return
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取当前系统版本
     * @return
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取运营商的名字
     * @param context
     * @return
     */
    public static String getOperatorName(Context context) {
        /*
         * getSimOperatorName()就可以直接获取到运营商的名字
         * 也可以使用IMSI获取，getSimOperator()，然后根据返回值判断，例如"46000"为移动
         * IMSI相关链接：http://baike.baidu.com/item/imsi
         */
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // getSimOperatorName就可以直接获取到运营商的名字
        return telephonyManager.getSimOperatorName();
    }

    /**
     * 获取联网方式
     * @param context
     * @return
     */
    public static String getNetworkState(Context context) {
        String strNetworkType = "";
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // 获取网络服务
        //NetworkInfo networkInfo = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE).getActiveNetworkInfo();
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable())
        {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                strNetworkType = "WIFI";
            }
            else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                String _strSubTypeName = networkInfo.getSubtypeName();

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000"))
                        {
                            strNetworkType = "3G";
                        }
                        else
                        {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }
            }
        }
        return strNetworkType;
    }
}
