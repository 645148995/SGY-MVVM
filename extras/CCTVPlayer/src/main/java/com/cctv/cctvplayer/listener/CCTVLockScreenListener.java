package com.cctv.cctvplayer.listener;

/**
 * 锁屏/解锁屏幕监听
 */

public interface CCTVLockScreenListener {
    /**
     * 锁屏
     */
    void onLockScreen();

    /**
     * 解锁屏幕
     */
    void onUnLockScreen();
}
