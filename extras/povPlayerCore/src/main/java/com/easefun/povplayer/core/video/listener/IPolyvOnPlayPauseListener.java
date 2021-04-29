package com.easefun.povplayer.core.video.listener;



import androidx.annotation.MainThread;

import com.easefun.povplayer.core.video.PolyvVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 播放器播放、暂停、播放完成回调，主线程中回调
 */
public interface IPolyvOnPlayPauseListener {

    /**
     * 播放器准备中回调<br>
     * 注：该回调发生在{@link PolyvVideoView#isPreparingState()}之前。<br>
     * 另外请不要在该方法中调用{@link PolyvVideoView#stopPlay()}与PolyvVideoView.playXXX方法。
     */
    @MainThread
    void onPreparing();

    /**
     * 播放器暂停回调
     */
    @MainThread
    void onPause();

    /**
     * 播放器播放回调
     *
     * @param isFirst 视频准备完成之后是否是第一次开始播放
     */
    @MainThread
    void onPlay(boolean isFirst);

    /**
     * 主视频播放完成/暖场视频每次播放完成都会回调。
     * 注：如果是片头、片尾广告，该方法不会回调，需要使用{@link IPolyvOnSubVideoViewCompletionListener}监听。
     *
     * @param mp
     */
    @MainThread
    void onCompletion(IMediaPlayer mp);
}
