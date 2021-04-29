package com.easefun.povplayer.core.video.listener;


import androidx.annotation.MainThread;

/**
 * 手势左滑回调，主线程中回调
 */
public abstract class IPolyvOnGestureSwipeLeftListener {
    @MainThread
    @Deprecated
    public void callback(boolean start, boolean end) {
    }

    @MainThread
    public void callback(boolean start, int times, boolean end) {
    }
}
