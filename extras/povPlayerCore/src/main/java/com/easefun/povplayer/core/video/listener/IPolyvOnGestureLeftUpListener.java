package com.easefun.povplayer.core.video.listener;


import androidx.annotation.MainThread;

/**
 * 手势左向上回调，主线程中回调
 */
public interface IPolyvOnGestureLeftUpListener {
    @MainThread
    void callback(boolean start, boolean end);
}
