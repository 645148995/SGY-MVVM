package com.easefun.povplayer.core.video.listener;

import androidx.annotation.MainThread;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 视频尺寸改变回调，主线程中回调
 */
public interface IPolyvOnVideoSizeChangedListener {
    @MainThread
    void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen);
}
