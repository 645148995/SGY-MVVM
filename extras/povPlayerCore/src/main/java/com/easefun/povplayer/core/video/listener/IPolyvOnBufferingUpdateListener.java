package com.easefun.povplayer.core.video.listener;



import androidx.annotation.MainThread;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 缓冲更新回调，主线程中回调
 */
public interface IPolyvOnBufferingUpdateListener {
    @MainThread
    void onBufferingUpdate(IMediaPlayer mp, int percent);
}
