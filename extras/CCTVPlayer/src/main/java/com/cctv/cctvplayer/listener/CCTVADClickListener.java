package com.cctv.cctvplayer.listener;

/**
 * 片头/暖场视频/片尾广告点击事件监听
 */

public interface CCTVADClickListener {
    /**
     * 点击了广告
     *
     * @param playStage 播放阶段（PolyvSubVideoView.PLAY_STAGE_HEADAD片头广告、PolyvSubVideoView.PLAY_STAGE_TEASER暖场视频、PolyvSubVideoView.PLAY_STAGE_TAILAD片尾广告）
     * @param data      自定义
     */
    void onADClick(int playStage, Object data);

    /**
     * 播放了广告
     *
     * @param playStage 播放阶段（PolyvSubVideoView.PLAY_STAGE_HEADAD片头广告、PolyvSubVideoView.PLAY_STAGE_TEASER暖场视频、PolyvSubVideoView.PLAY_STAGE_TAILAD片尾广告）
     * @param data      自定义
     */
    void onADPlay(int playStage, Object data);
}
