package com.cctv.cctvplayer.listener;

import com.cctv.cctvplayer.player.PlayerOperate;

/**
 * 如果播放地址，是需要从接口请求，比如加防盗链，需要设置此监听
 */

public interface CCTVPlayListener {
    /**
     * 直播播放
     *
     * @param url     原始播放地址
     * @param operate 操作
     */
    void onLivePlay(String url, PlayerOperate operate);

    /**
     * 直播时移
     *
     * @param url      原始播放地址
     * @param progress 回看时间
     * @param operate  操作
     */
    void onLiveBackPlay(String url, long progress, PlayerOperate operate);

    /**
     * 点播播放
     *
     * @param url          原始播放地址
     * @param progressKeep 是否进度保持播放
     * @param operate      操作
     */
    void onVodPlay(String url, boolean progressKeep, PlayerOperate operate);
}
