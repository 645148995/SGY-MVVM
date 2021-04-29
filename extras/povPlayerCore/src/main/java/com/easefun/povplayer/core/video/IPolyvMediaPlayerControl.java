package com.easefun.povplayer.core.video;

import android.widget.MediaController;

/**
 * 媒体播放器控制接口
 */
public interface IPolyvMediaPlayerControl extends MediaController.MediaPlayerControl {
    /**
     * 开始
     */
    @Override
    void start();

    /**
     * 暂停
     */
    @Override
    void pause();

    /**
     * 取得总时长
     *
     * @return 毫秒
     */
    @Override
    int getDuration();

    /**
     * 取得视频播放进度当前位置
     *
     * @return 毫秒
     */
    @Override
    int getCurrentPosition();

    /**
     * 跳转到，请于视频准备完成后调用
     *
     * @param pos 毫秒
     */
    @Override
    void seekTo(int pos);

    /**
     * 是否播放中，包括缓冲和目标播放状态
     *
     * @return
     */
    @Override
    boolean isPlaying();

    /**
     * 取得视频缓存百分比
     *
     * @return 0-100
     */
    @Override
    int getBufferPercentage();

    /**
     * 返回true
     *
     * @return true
     */
    @Override
    boolean canPause();

    /**
     * 返回true
     *
     * @return true
     */
    @Override
    boolean canSeekBackward();

    /**
     * 返回true
     *
     * @return true
     */
    @Override
    boolean canSeekForward();
}
