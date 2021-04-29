package com.easefun.povplayer.core.video.listener;

import androidx.annotation.MainThread;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 视频信息回调，主线程中回调
 */
public interface IPolyvOnInfoListener {
    /**
     * @param what  类型见{@link tv.danmaku.ijk.media.player.MediaInfo}
     * @param extra 预留，未使用
     */
    @MainThread
    void onInfo(IMediaPlayer mp, int what, int extra);
}
