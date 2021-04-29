package com.easefun.povplayer.core.video.listener;


import androidx.annotation.MainThread;

/**
 * 手势双击回调，主线程中回调
 */
public interface IPolyvOnGestureDoubleClickListener {
    @MainThread
    void callback();
}
