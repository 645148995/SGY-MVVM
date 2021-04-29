package com.easefun.povplayer.core.video.listener;


import androidx.annotation.MainThread;

/**
 * 手势右向下回调，主线程中回调
 */
public interface IPolyvOnGestureRightDownListener {
    @MainThread
    void callback(boolean start, boolean end);
}
