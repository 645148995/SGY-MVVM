package com.easefun.povplayer.core.video.listener;


import com.easefun.povplayer.core.video.PolyvPlayError;
import com.easefun.povplayer.core.video.PolyvSubVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 子播放器播放状态监听器
 */
public interface IPolyvOnSubVideoViewPlayStatusListener {

    /**
     * 倒计时
     *
     * @param totalTime
     * @param remainTime
     * @param adStage
     */
    public void onCountdown(int totalTime, int remainTime, @PolyvSubVideoView.AdStage int adStage);

    /**
     * 播放完成
     *
     * @param mp
     * @param adStage
     */
    public void onCompletion(IMediaPlayer mp, @PolyvSubVideoView.AdStage int adStage);

    /**
     * 播放失败
     *
     * @param error
     */
    public void onError(PolyvPlayError error);
}
