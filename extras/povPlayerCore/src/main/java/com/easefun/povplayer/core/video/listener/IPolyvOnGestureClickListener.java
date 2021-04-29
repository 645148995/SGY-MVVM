package com.easefun.povplayer.core.video.listener;

import androidx.annotation.MainThread;

/**
 * 手势单击回调，主线程中回调
 */
public interface IPolyvOnGestureClickListener {
    @MainThread
    void callback();
}
