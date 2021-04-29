package com.easefun.povplayer.core.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;

import java.io.FileReader;
import java.io.IOException;

public class PolyvDetection {

    /**
     * 设备是否支持dolby
     */
    public static boolean isDolbyDevice() {
        boolean dlbDevice = false;
        for (AudioEffect.Descriptor des : AudioEffect.queryEffects()) {
            if (des.implementor.contains("Dolby Laboratories")) {
                // There is an in-device Dolby technology.
                // Need to turn off application-level Dolby postprocessing.
                dlbDevice = true;
                break;
            }
        }
        return dlbDevice;
    }

    /**
     * 检查是否插入耳机
     *
     * @param context
     */
    @SuppressLint("MissingPermission")
    public static boolean isHeadsetOn(Context context) {
        AudioManager audoManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean isHeadsetOn = false;
        try {
            isHeadsetOn = audoManager.isWiredHeadsetOn();//MODIFY_AUDIO_SETTINGS permission
        } catch (Exception e) {
        }
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();//BLUETOOTH permission
        if (ba != null && ba.isEnabled()) {
            int a2dp = ba.getProfileConnectionState(BluetoothProfile.A2DP);              //可操控蓝牙设备，如带播放暂停功能的蓝牙耳机
            int headset = ba.getProfileConnectionState(BluetoothProfile.HEADSET);        //蓝牙头戴式耳机，支持语音输入输出
            int health = ba.getProfileConnectionState(BluetoothProfile.HEALTH);          //蓝牙穿戴式设备
            isHeadsetOn = isHeadsetOn || a2dp == BluetoothProfile.STATE_CONNECTED || headset == BluetoothProfile.STATE_CONNECTED || health == BluetoothProfile.STATE_CONNECTED;
        }
        return isHeadsetOn;
    }

    private static final String HEADSET_STATE_PATH = "/sys/class/switch/h2w/state";

    public static boolean isHeadsetExists() {
        char[] buffer = new char[1024];
        int newState = 0;
        FileReader fr = null;
        try {
            fr = new FileReader(HEADSET_STATE_PATH);
            int len = fr.read(buffer, 0, 1024);
            newState = Integer.valueOf(new String(buffer, 0, len).trim());
        } catch (Exception e) {
        } finally {
            try {
                if (fr != null)
                    fr.close();
            } catch (IOException e) {
            }
        }
        return newState != 0;
    }
}
