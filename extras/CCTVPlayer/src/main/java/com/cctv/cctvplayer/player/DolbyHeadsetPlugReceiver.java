package com.cctv.cctvplayer.player;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.cctv.cctvplayer.utils.LogUtils;
import com.easefun.povplayer.core.util.PolyvDetection;
import com.easefun.povplayer.core.video.PolyvVideoView;

/**
 * 杜比功能，耳机检测
 */
public class DolbyHeadsetPlugReceiver extends BroadcastReceiver {

    private Context mContext;
    private PolyvVideoView mVideoView;
    private boolean mRegister;// 是否注册过 ，保证注册和取消成对出现

    public DolbyHeadsetPlugReceiver(Context context, PolyvVideoView videoView) {
        this.mContext = context;
        this.mVideoView = videoView;
    }

    public void registerHeadsetPlugReceiver() {
        LogUtils.i("注册...");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        mContext.registerReceiver(this, intentFilter);
        mRegister = true;
    }

    public void unregisterReceiver() {
        if (!mRegister)
            return;
        mRegister = false;
        LogUtils.i("取消注册...");
        mContext.unregisterReceiver(this);
    }

    private void setDolbyEndpointParam(boolean isHeadsetOn) {
        mVideoView.setDolbyEndpointParam(isHeadsetOn);
        mVideoView.getSubVideoView().setDolbyEndpointParam(isHeadsetOn);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null)
                return;
            int a2dp = adapter.getProfileConnectionState(BluetoothProfile.A2DP);
            int headset = adapter.getProfileConnectionState(BluetoothProfile.HEADSET);
            int health = adapter.getProfileConnectionState(BluetoothProfile.HEALTH);
            if (BluetoothProfile.STATE_CONNECTED == a2dp
                    || BluetoothProfile.STATE_CONNECTED == headset
                    || BluetoothProfile.STATE_CONNECTED == health) {
                //Toast.makeText(context, "bluetooth headset connected", Toast.LENGTH_LONG).show();
                LogUtils.i("蓝牙耳机连接");
                setDolbyEndpointParam(true);
            } else if (BluetoothProfile.STATE_CONNECTING == a2dp
                    || BluetoothProfile.STATE_CONNECTING == headset
                    || BluetoothProfile.STATE_CONNECTING == health) {
            } else if (BluetoothProfile.STATE_DISCONNECTING == a2dp
                    || BluetoothProfile.STATE_DISCONNECTING == headset
                    || BluetoothProfile.STATE_DISCONNECTING == health) {
            } else if (BluetoothProfile.STATE_DISCONNECTED == a2dp
                    || BluetoothProfile.STATE_DISCONNECTED == headset
                    || BluetoothProfile.STATE_DISCONNECTED == health) {
                if (!PolyvDetection.isHeadsetOn(mContext)) {
                    //Toast.makeText(context, "bluetooth headset not connected", Toast.LENGTH_LONG).show();
                    LogUtils.i("蓝牙耳机未连接");
                    setDolbyEndpointParam(false);
                }
            }
        } else if (AudioManager.ACTION_HEADSET_PLUG.equals(action) && intent.hasExtra("state")) {
            if (intent.getIntExtra("state", 0) == 0) {
                if (!PolyvDetection.isHeadsetOn(mContext)) {
                    //Toast.makeText(context, "headset not connected", Toast.LENGTH_LONG).show();
                    LogUtils.i("耳机未连接");
                    setDolbyEndpointParam(false);
                }
            } else if (intent.getIntExtra("state", 0) == 1) {
                //Toast.makeText(context, "headset connected", Toast.LENGTH_LONG).show();
                LogUtils.i("耳机连接");
                setDolbyEndpointParam(true);
            }
        }
    }
}
