package com.easefun.povplayer.core.video.listener;

import androidx.annotation.MainThread;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 视频播放出错回调，主线程中回调
 *
 * @see IPolyvOnPlayErrorListener
 * @deprecated
 */
public interface IPolyvOnErrorListener {
    /**
     * 仅播放器内部抛出错误，注意不要在该方法中操作任何有关播放的逻辑，或者使用{@link IPolyvOnPlayErrorListener}
     *
     * @param mp
     * @param what
     * @param extra
     * @deprecated
     */
    @MainThread
    void onError(IMediaPlayer mp, int what, int extra);
}
