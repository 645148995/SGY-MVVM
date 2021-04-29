package com.cctv.cctvplayer.listener;

/**
 * 视频横竖屏接口监听
 */

public interface CCTVOrientationListener {
    /**
     * 横屏
     */
    void onLandscape();

    /**
     * 竖屏
     */
    void onPortrait();
}
