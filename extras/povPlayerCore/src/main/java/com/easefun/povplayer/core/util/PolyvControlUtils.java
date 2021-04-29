package com.easefun.povplayer.core.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * 声音，亮度等控制
 */
public class PolyvControlUtils {
    private static int streamMusicVolume = 0;

    //是否打开声音
    public static boolean isOpenSound(Context context) {
        AudioManager am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int sv = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        return sv != 0;
    }

    //打开声音
    public static void openSound(Context context) {
        AudioManager am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (streamMusicVolume == 0) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 3, 0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, streamMusicVolume, 0);
        }
    }

    //关闭声音
    public static void closeSound(Context context) {
        AudioManager am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        streamMusicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }

    /**
     * 获取音量改变的有效值
     *
     * @param context
     * @return
     */
    public static int getVolumeValidProgress(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return (int) Math.ceil(100.0d / maxVolume);
    }

    /**
     * 获取音量改变的有效值
     *
     * @param context
     * @param value   预设值
     * @return 有效值
     */
    public static int getVolumeValidProgress(Context context, int value) {
        return Math.max(getVolumeValidProgress(context), value);
    }

    //设置媒体音量
    public static void setVolume(Context context, int volume) {
        if (volume < 0) {
            volume = 0;
        } else if (volume > 100) {
            volume = 100;
        }
        double volumeD = (double) volume / 100;
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int newVolume = (int) (maxVolume * volumeD);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
    }

    //获取媒体音量
    public static int getVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        double volumeD = (double) currVolume / maxVolume;
        return (int) Math.round(volumeD * 100);
    }

    //请求音频焦点
    public static int requestTheAudioFocus(Context mContext) {
        int requestFocusResult = 0;
        if (Build.VERSION.SDK_INT < 8) //Android 2.2开始(API8)才有音频焦点机制
            return requestFocusResult;
        AudioManager am = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        requestFocusResult = am.requestAudioFocus(null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        return requestFocusResult;
    }

    //清除音频焦点
    public static void releaseTheAudioFocus(Context mContext) {
        AudioManager am = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);
    }

    //设置当前窗口亮度
    public static void setBrightness(Activity activity, int brightness) {
        if (brightness <= 0 && brightness != -1) {
            brightness = Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN ? 0 : 1;
        } else if (brightness > 100) {
            brightness = 100;
        }
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = brightness / 100.0f;
        }
        window.setAttributes(lp);
    }

    //获取当前窗口亮度
    public static int getBrightness(Activity activity) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int brightness = 0;
        try {
            if (lp.screenBrightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE)
                brightness = Math.round(Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) * 100 / 255.0f);
            else
                brightness = Math.round(lp.screenBrightness * 100);//lp.screenBrightness的值可能与设置的值接近而不相等
        } catch (Exception e) {
        }
        return brightness;
    }

    //网络是否可用
    public static boolean isOpenNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo netinfo = connManager.getActiveNetworkInfo();
            if (netinfo != null && netinfo.isConnected()) {
                if (netinfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    //获取状态栏的高度
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int height = resources.getDimensionPixelSize(resourceId);
            return height;
        } else {
            return 0;
        }
    }

    //获取导航栏的高度
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int height = resources.getDimensionPixelSize(resourceId);
            return height;
        } else {
            return 0;
        }
    }

    //获取显示的宽高
    public static int[] getDisplayWH(Activity activity) {
        Display mDisplay = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT < 17)
            mDisplay.getMetrics(mDisplayMetrics);
        else
            mDisplay.getRealMetrics(mDisplayMetrics);
        return new int[]{mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels};
    }

    //是否有导航栏
    public static boolean hasVirtualNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }
}
