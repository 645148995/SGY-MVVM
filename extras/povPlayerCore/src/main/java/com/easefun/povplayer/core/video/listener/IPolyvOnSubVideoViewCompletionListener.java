package com.easefun.povplayer.core.video.listener;


import com.easefun.povplayer.core.video.PolyvSubVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 子播放器倒计时完成监听器
 */
public interface IPolyvOnSubVideoViewCompletionListener {
    /**
     * 倒计时完成，倒计时完成后会停止播放，所以{@link PolyvSubVideoView#isCompletedState()}为false。
     *
     * @param mp
     * @param adStage 倒计时完成的广告阶段类型
     */
    public void onCompletion(IMediaPlayer mp, @PolyvSubVideoView.AdStage int adStage);
}
