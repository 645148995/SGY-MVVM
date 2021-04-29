package com.easefun.povplayer.core.video;

import android.content.Context;
import android.util.AttributeSet;

import com.easefun.povplayer.core.video.listener.IPolyvOnGestureDoubleClickListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureLeftDownListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureLeftUpListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureRightDownListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureRightUpListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureSwipeLeftListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureSwipeRightListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnPlayErrorListener;


/**
 * 主播放器监听事件设置实现类
 */
public abstract class PolyvVideoViewListenerEvent extends PolyvBaseVideoViewListenerEvent implements IPolyvVideoViewListenerEvent {
    private IPolyvOnPlayErrorListener onPlayErrorListener = null;
    private IPolyvOnGestureLeftUpListener onGestureLeftUpListener = null;
    private IPolyvOnGestureLeftDownListener onGestureLeftDownListener = null;
    private IPolyvOnGestureRightUpListener onGestureRightUpListener = null;
    private IPolyvOnGestureRightDownListener onGestureRightDownListener = null;
    private IPolyvOnGestureSwipeLeftListener onGestureSwipeLeftListener = null;
    private IPolyvOnGestureSwipeRightListener onGestureSwipeRightListener = null;
    private IPolyvOnGestureDoubleClickListener onGestureDoubleClickListener = null;

    public PolyvVideoViewListenerEvent(Context context) {
        super(context);
    }

    public PolyvVideoViewListenerEvent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PolyvVideoViewListenerEvent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnPlayErrorListener(IPolyvOnPlayErrorListener l) {
        onPlayErrorListener = l;
    }

    @Override
    public void setOnGestureLeftUpListener(IPolyvOnGestureLeftUpListener l) {
        this.onGestureLeftUpListener = l;
    }

    @Override
    public void setOnGestureLeftDownListener(IPolyvOnGestureLeftDownListener l) {
        this.onGestureLeftDownListener = l;
    }

    @Override
    public void setOnGestureRightUpListener(IPolyvOnGestureRightUpListener l) {
        this.onGestureRightUpListener = l;
    }

    @Override
    public void setOnGestureRightDownListener(IPolyvOnGestureRightDownListener l) {
        this.onGestureRightDownListener = l;
    }

    @Override
    public void setOnGestureSwipeLeftListener(IPolyvOnGestureSwipeLeftListener l) {
        this.onGestureSwipeLeftListener = l;
    }

    @Override
    public void setOnGestureSwipeRightListener(IPolyvOnGestureSwipeRightListener l) {
        this.onGestureSwipeRightListener = l;
    }

    @Override
    public void setOnGestureDoubleClickListener(IPolyvOnGestureDoubleClickListener l) {
        this.onGestureDoubleClickListener = l;
    }

    protected void callOnPlayErrorListener(final PolyvPlayError error) {
        if (onPlayErrorListener != null) {
            onPlayErrorListener.onError(error);
        }
    }

    protected void callOnGestureLeftUpListener(final boolean start, final boolean end) {
        if (onGestureLeftUpListener != null) {
            onGestureLeftUpListener.callback(start, end);
        }
    }

    protected void callOnGestureLeftDownListener(final boolean start, final boolean end) {
        if (onGestureLeftDownListener != null) {
            onGestureLeftDownListener.callback(start, end);
        }
    }

    protected void callOnGestureRightUpListener(final boolean start, final boolean end) {
        if (onGestureRightUpListener != null) {
            onGestureRightUpListener.callback(start, end);
        }
    }

    protected void callOnGestureRightDownListener(final boolean start, final boolean end) {
        if (onGestureRightDownListener != null) {
            onGestureRightDownListener.callback(start, end);
        }
    }

    protected void callOnGestureSwipeLeftListener(final boolean start, int times, final boolean end) {
        if (onGestureSwipeLeftListener != null) {
            onGestureSwipeLeftListener.callback(start, end);
            onGestureSwipeLeftListener.callback(start, times, end);
        }
    }

    protected void callOnGestureSwipeRightListener(final boolean start, int times, final boolean end) {
        if (onGestureSwipeRightListener != null) {
            onGestureSwipeRightListener.callback(start, end);
            onGestureSwipeRightListener.callback(start, times, end);
        }
    }

    protected void callOnGestureDoubleClickListener() {
        if (onGestureDoubleClickListener != null) {
            onGestureDoubleClickListener.callback();
        }
    }

    protected void clearAllListener() {
        super.clearListener();
        onPlayErrorListener = null;

        onGestureLeftUpListener = null;
        onGestureLeftDownListener = null;
        onGestureRightUpListener = null;
        onGestureRightDownListener = null;
        onGestureSwipeLeftListener = null;
        onGestureSwipeRightListener = null;
        onGestureDoubleClickListener = null;
    }
}
