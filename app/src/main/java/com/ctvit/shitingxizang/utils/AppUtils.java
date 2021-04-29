package com.ctvit.shitingxizang.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;


import androidx.core.content.FileProvider;

import com.ctvit.shitingxizang.app.MyApplication;

import java.io.File;

/**
 * Created by lenovo on 2016/12/22.
 * 获取应用本身的一些信息，或者APP本社你的操作
 */

public final class AppUtils {

    /**
     * 根据URL，用APP外的应用打开URL（比如说系统自带浏览器）
     */
    public static void openUrl(String url, Context context) {
        if (TextUtils.isEmpty(url))
            return;
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(it);
    }

    /**
     * 打开APK
     *
     * @param file APK在本地的路径
     */
    public static void openFile(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_INSTALL_PACKAGE);

        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // 下面这句话必须写在上面flag的下面，否则安装apk后，会没有安装完成页面
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri contentUri = FileProvider.getUriForFile(context, "com.ctvit.cctvchildren.fileProvider", file);//BuildConfig.APPLICATION_ID
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }

        context.startActivity(intent);
    }

    /**
     * 获取AndroidManifest.xml中的 应用版本包名
     */
    public static String getAppPackageName() {
        try {
            return getAppManifestTag().packageName;
        } catch (Exception e) {
            LogUtils.e(e);
            return null;
        }
    }

    /**
     * 获取AndroidManifest.xml中的 应用版本CODE
     */
    public static int getAppVersionCode() {
        try {
            return getAppManifestTag().versionCode;
        } catch (Exception e) {
            LogUtils.e(e);
            return -1;
        }
    }

    /**
     * AndroidManifest.xml中的 应用版本名称
     */
    public static String getAppVersionName() {
        try {
            return getAppManifestTag().versionName;
        } catch (Exception e) {
            LogUtils.e(e);
            return null;
        }
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName() {
        try {
            PackageInfo packageInfo = getAppManifestTag();
            int labelRes = packageInfo.applicationInfo.labelRes;
            return MyApplication.getInstance().getApplicationContext().getResources().getString(labelRes);
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return null;
    }

    /**
     * AndroidManifest.xml中的 manifest 标签
     */
    public static PackageInfo getAppManifestTag()
            throws Exception {
        Context context = MyApplication.getInstance().getApplicationContext();
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getPackageInfo(context.getPackageName(), 0);
    }

    /**
     * 获取ApplicationInfo对象，用于获得MetaData标签值
     *
     * @param context
     */
    public static ApplicationInfo getMetaDataValue(Context context)
            throws Exception {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getApplicationInfo(context.getPackageName(),
                PackageManager.GET_META_DATA);
    }

    /**
     * 判断Activity是否处于栈顶
     *
     * @param name    Activity的名称 Activity.class.getName() 获得即可
     * @param context 必须为Activity的上下文对象
     * @return true在栈顶false不在栈顶
     */
    public static boolean isActivityTop(String name, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String n = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return n.equals(name);
    }

    public static int getActivityNums(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return manager.getRunningTasks(1).get(0).numActivities;
    }

    /**
     * 发送邮件
     *
     * @param context 上下文对象
     * @param title   主题
     * @param content 正文
     */
    public static void sendEmail(Context context, String title, String content) {
        Uri uri = Uri.parse("mailto:");
        String[] email = {""};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
        intent.putExtra(Intent.EXTRA_SUBJECT, title); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, content); // 正文
        context.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
    }

    /**
     * 获取打包的渠道
     */
    public static String getChannel(Context context, String key) {
        try {
            return getMetaDataValue(context).metaData.getString(key);
        } catch (Exception e) {
            LogUtils.e(e);
            return "";
        }
    }

    /**
     * 获取当前的运营商
     *
     * @param context
     * @return 运营商名字
     */
    public static String getOperator(Context context) {


        String ProvidersName = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String IMSI = telephonyManager.getSubscriberId();
        LogUtils.i("运营商代码" + IMSI);
        if (IMSI != null) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
            return ProvidersName;
        } else {
            return "没有获取到sim卡信息";
        }
    }

    public static void webViewHttp2Https(WebSettings settings, String TAG) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.v(TAG, "版本大于21，启动setMixedContentMode(0)");
            // 解决http及https混合情况下页面加载问题
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        } else {
            Log.v(TAG, "版本小于21，无须启动setMixedContentMode(0)");
        }
    }

}
