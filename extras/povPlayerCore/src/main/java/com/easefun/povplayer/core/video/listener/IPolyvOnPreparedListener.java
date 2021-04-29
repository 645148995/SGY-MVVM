package com.easefun.povplayer.core.video.listener;

import androidx.annotation.MainThread;

import com.easefun.povplayer.core.video.PolyvVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 视频准备好回调，主线程中回调
 */
public interface IPolyvOnPreparedListener {
    /**
     * 准备完成回调<br>
     * 注：不要在该方法中调用{@link PolyvVideoView#stopPlay()}与PolyvVideoView.playXXX方法。
     *
     * @param mp
     */
    @MainThread
    void onPrepared(IMediaPlayer mp);
}
