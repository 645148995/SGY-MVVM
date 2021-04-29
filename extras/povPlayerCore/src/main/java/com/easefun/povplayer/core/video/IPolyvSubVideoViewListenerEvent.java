package com.easefun.povplayer.core.video;


import com.easefun.povplayer.core.video.listener.IPolyvOnSubVideoViewCompletionListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnSubVideoViewCountdownListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnSubVideoViewPlayStatusListener;

/**
 * 子播放器监听事件设置接口，有标注的方法仅内部使用。
 */
public interface IPolyvSubVideoViewListenerEvent {
    /**
     * 设置子播放器播放完成监听器
     *
     * @param l
     */
    public void setOnSubVideoViewPlayCompletionListener(IPolyvOnSubVideoViewCompletionListener l);

    /**
     * 设置子播放器播放状态监听器，注：仅内部使用。
     *
     * @param l
     */
    public void setOnSubVideoViewPlayStatusListener(IPolyvOnSubVideoViewPlayStatusListener l);

    /**
     * 设置子播放器倒计时监听器
     *
     * @param l
     */
    public void setOnSubVideoViewCountdownListener(IPolyvOnSubVideoViewCountdownListener l);
}
