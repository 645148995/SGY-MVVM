package com.easefun.povplayer.core.video;


import com.easefun.povplayer.core.video.listener.IPolyvOnGestureDoubleClickListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureLeftDownListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureLeftUpListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureRightDownListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureRightUpListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureSwipeLeftListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureSwipeRightListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnPlayErrorListener;

/**
 * 主播放器监听事件设置接口
 */
public interface IPolyvVideoViewListenerEvent {

    /**
     * 设置播放错误监听
     *
     * @param l
     */
    public void setOnPlayErrorListener(IPolyvOnPlayErrorListener l);

    /**
     * 设置手势左向上回调
     *
     * @param l
     */
    public void setOnGestureLeftUpListener(IPolyvOnGestureLeftUpListener l);

    /**
     * 设置手势左向下回调
     *
     * @param l
     */
    public void setOnGestureLeftDownListener(IPolyvOnGestureLeftDownListener l);

    /**
     * 设置手势右向上回调
     *
     * @param l
     */
    public void setOnGestureRightUpListener(IPolyvOnGestureRightUpListener l);

    /**
     * 设置手势右向下回调
     *
     * @param l
     */
    public void setOnGestureRightDownListener(IPolyvOnGestureRightDownListener l);

    /**
     * 设置手势左滑回调
     *
     * @param l
     */
    public void setOnGestureSwipeLeftListener(IPolyvOnGestureSwipeLeftListener l);

    /**
     * 设置手势右滑回调
     *
     * @param l
     */
    public void setOnGestureSwipeRightListener(IPolyvOnGestureSwipeRightListener l);

    /**
     * 设置手势双击回调
     *
     * @param l
     */
    public void setOnGestureDoubleClickListener(IPolyvOnGestureDoubleClickListener l);
}
