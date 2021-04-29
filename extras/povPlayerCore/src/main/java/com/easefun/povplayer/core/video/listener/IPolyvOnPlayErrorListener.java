package com.easefun.povplayer.core.video.listener;



import androidx.annotation.MainThread;

import com.easefun.povplayer.core.video.PolyvPlayError;


/**
 * 播放出错回调，主线程中回调
 */
public interface IPolyvOnPlayErrorListener {

    /**
     * 所有播放错误的回调
     *
     * @param error
     */
    @MainThread
    public void onError(PolyvPlayError error);
}
