package com.cctv.cctvplayer.listener;

/**
 * 控制兰显示还是隐藏
 */

public interface CCTVControllerListener {
    /**
     * 显示
     * 1、只有上边显示
     * 2、只有底部显示
     * 3、上下都显示
     */
    void onShow(int type);

    /**
     * 隐藏
     * 1、只有上边隐藏
     * 2、只有底部隐藏
     * 3、上下都隐藏
     */
    void onHidden(int type);
}
