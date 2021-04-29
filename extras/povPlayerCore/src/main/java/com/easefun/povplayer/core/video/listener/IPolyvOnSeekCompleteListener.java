package com.easefun.povplayer.core.video.listener;

import androidx.annotation.MainThread;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * seek完成回调，主线程中回调
 */
public interface IPolyvOnSeekCompleteListener {
    @MainThread
    void onSeekComplete(IMediaPlayer mp);
}
