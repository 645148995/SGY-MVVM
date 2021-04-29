package com.easefun.povplayer.core.video;


import com.easefun.povplayer.core.video.listener.IPolyvOnBufferingUpdateListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnErrorListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureClickListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnInfoListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnPlayPauseListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnPreparedListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnSeekCompleteListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnVideoSizeChangedListener;

/**
 * 播放监听事件设置接口基类
 */
public interface IPolyvBaseVideoViewListenerEvent {
    /**
     * 设置视频缓存更新回调
     *
     * @param l
     */
    void setOnBufferingUpdateListener(IPolyvOnBufferingUpdateListener l);

    /**
     * 设置视频播放/暂停/播放完成回调
     *
     * @param l
     */
    void setOnPlayPauseListener(IPolyvOnPlayPauseListener l);

    /**
     * 设置视频已准备好马上进入播放回调
     *
     * @param l
     */
    public void setOnPreparedListener(IPolyvOnPreparedListener l);

    /**
     * 设置视频播放器内部错误回调
     *
     * @param l
     */
    public void setOnErrorListener(IPolyvOnErrorListener l);

    /**
     * 设置视频播放器信息有变更回调
     *
     * @param l
     */
    public void setOnInfoListener(IPolyvOnInfoListener l);

    /**
     * 设置seek完成回调
     *
     * @param l
     */
    public void setOnSeekCompleteListener(IPolyvOnSeekCompleteListener l);

    /**
     * 设置视频尺寸改变回调
     *
     * @param l
     */
    public void setOnVideoSizeChangedListener(IPolyvOnVideoSizeChangedListener l);

    /**
     * 设置手势单击回调
     *
     * @param l
     */
    public void setOnGestureClickListener(IPolyvOnGestureClickListener l);
}
