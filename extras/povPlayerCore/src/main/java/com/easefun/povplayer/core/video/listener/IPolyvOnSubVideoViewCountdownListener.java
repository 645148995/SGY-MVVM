package com.easefun.povplayer.core.video.listener;


import com.easefun.povplayer.core.video.PolyvSubVideoView;

/**
 * 子播放器倒计时监听
 */
public interface IPolyvOnSubVideoViewCountdownListener {

    /**
     * 倒计时
     *
     * @param totalTime  总时间
     * @param remainTime 剩余时间
     * @param adStage    广告播放阶段
     */
    public void onCountdown(int totalTime, int remainTime, @PolyvSubVideoView.AdStage int adStage);

    /**
     * 子播放器可见性改变回调
     *
     * @param isShow
     */
    public void onVisibilityChange(boolean isShow);
}
