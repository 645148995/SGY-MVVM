package com.easefun.povplayer.core.video;

import android.view.View;

/**
 * 播放器接口基类
 */
public interface IPolyvBaseVideoView {
    /**
     * 当前是否在准备中
     *
     * @return
     */
    boolean isPreparingState();

    /**
     * 当前是否已准备好
     *
     * @return
     */
    boolean isPreparedState();

    /**
     * 当前是否在播放中，缓冲时为非播放状态，且不包括目标播放状态
     *
     * @return
     */
    boolean isPlayState();

    /**
     * 当前是否在播放中，不包括目标播放状态
     *
     * @param isIngoreBuffer false：缓冲时为非播放状态，true：忽略缓冲
     * @return
     */
    boolean isPlayState(boolean isIngoreBuffer);

    /**
     * 是否在播放状态，包括准备好，播放，缓冲，暂停，播放完成
     *
     * @return
     */
    boolean isInPlaybackState();

    /**
     * 当前是否在暂停中
     *
     * @return
     */
    boolean isPauseState();

    /**
     * 当前是否在缓冲中
     *
     * @return
     */
    boolean isBufferState();

    /**
     * 当前是否播放完成
     *
     * @return
     */
    boolean isCompletedState();

    /**
     * 设置播放器缓冲视图
     *
     * @param view 缓冲视图
     */
    void setPlayerBufferingIndicator(View view);

    /**
     * 设置播放填充模式
     *
     * @param screen 填充模式
     * @return 设置成功返回true，失败返回false
     */
    boolean setAspectRatio(@PolyvPlayerScreenRatio.RenderScreenRatio int screen);

    /**
     * 取得播放填充模式
     *
     * @return {@link PolyvPlayerScreenRatio}
     */
    int getAspectRatio();

    /**
     * 销毁，在最后不使用播放器时调用
     */
    void destroy();

    /**
     * 是否是点播播放模式
     *
     * @return
     */
    boolean isVodPlayMode();

    /**
     * 是否是直播播放模式
     *
     * @return
     */
    boolean isLivePlayMode();

    /**
     * 停止播放
     */
    void stopPlay();

    /**
     * 获取当前的播放地址
     *
     * @return
     */
    String getCurrentPlayPath();

    /**
     * 暂停
     *
     * @param isAbandonAudioFocus 是否放弃音频焦点
     */
    void pause(boolean isAbandonAudioFocus);
}
