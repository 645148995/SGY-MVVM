package com.easefun.povplayer.core.video;

import android.app.Activity;

import android.view.GestureDetector;

import androidx.annotation.NonNull;

import com.easefun.povplayer.core.config.PolyvPlayOption;

import www.viewscenestv.com.ijkvideoview.IjkVideoView;

/**
 * 主播放器接口
 */
public interface IPolyvVideoView extends IPolyvBaseVideoView {

    /**
     * 是否开启声音
     *
     * @return
     */
    boolean isOpenSound();

    /**
     * 开启声音
     */
    void openSound();

    /**
     * 关闭声音
     */
    void closeSound();

    /**
     * 设置音量
     *
     * @param volume - 音量，0 到 100 闭区间的数
     */
    void setVolume(int volume);

    /**
     * 取得音量
     *
     * @return 0 到 100 之间的值
     */
    int getVolume();

    /**
     * 设置当前窗口亮度
     *
     * @param brightness - 亮度，-1 到 100 闭区间的数，如果为-1，那么设置为系统亮度
     */
    void setBrightness(Activity activity, int brightness);

    /**
     * 取得当前窗口亮度
     *
     * @return 0 到 100 之间的值
     */
    int getBrightness(Activity activity);

    /**
     * 获取GestureDetector
     *
     * @return
     */
    GestureDetector getGestureDetector();

    /**
     * 取得是否需要手势
     *
     * @return
     */
    boolean getNeedGestureDetector();

    /**
     * 设置是否需要手势
     *
     * @param need
     */
    void setNeedGestureDetector(boolean need);

    /**
     * 设置子播放器，用于处理广告及暖场的播放逻辑。
     *
     * @param subVideoView
     */
    void setSubVideoView(@NonNull PolyvSubVideoView subVideoView);

    /**
     * 设置播放地址、播放配置，该方法的调用会先释放当前的播放器之后再创建
     *
     * @param playPath   播放地址
     * @param playOption 播放配置
     */
    void setOption(String playPath, PolyvPlayOption playOption);

    /**
     * 从片头广告开始播放，播放流程：<br>
     * 点播：片头广告-正片-片尾广告。如果没有片头广告，则使用{@link #play()}的播放流程。<br>
     * 直播：片头广告-暖场视频/正片。如果没有片头广告，有暖场视频，会播放暖场视频，否则播放正片。
     */
    void playFromHeadAd();

    /**
     * 播放正片，播放流程：<br>
     * 点播：正片-片尾广告<br>
     * 直播：正片
     */
    void play();

    /**
     * 播放片尾广告，如果没有片尾广告，那么会停止播放
     *
     * @return 是否有片尾广告播放 {@link IPolyvSubVideoView#isOpenTailAd()}
     */
    boolean playTailAd();

    /**
     * 播放暖场视频，如果没有暖场视频，那么会停止播放
     *
     * @return 是否有暖场视频播放 {@link IPolyvSubVideoView#isOpenTeaser()}
     */
    boolean playTeaser();

    /**
     * 跳过所有的片头广告播放，播放流程：<br>
     * 点播：正片-片尾广告<br>
     * 直播：有暖场视频，会播放暖场视频，否则播放正片。
     *
     * @return 当前是否在播放片头广告
     */
    boolean playSkipHeadAd();

    /**
     * 跳过片头广告播放，播放流程：<br>
     * isSkipAll：<br>
     * 点播：正片-片尾广告<br>
     * 直播：有暖场视频，会播放暖场视频，否则播放正片。<br>
     * !isSkipAll：<br>
     * 点播：下一个片头广告-正片-片尾广告<br>
     * 直播：下一个片头广告-有暖场视频，会播放暖场视频，否则播放正片。
     *
     * @param isSkipAll 是否跳过所有的片头广告
     * @return 当前是否在播放片头广告
     */
    boolean playSkipHeadAd(boolean isSkipAll);

    /**
     * 是否可以跳过片头广告播放
     *
     * @return 当前是否在播放片头广告
     */
    boolean canPlaySkipHeadAd();

    /**
     * 是否在播放状态，包括准备好，播放，缓冲，暂停，播放，完成，且子播放器不可见
     *
     * @return
     */
    boolean isInPlaybackStateEx();

    /**
     * 是否允许在后台播放
     *
     * @return
     */
    boolean isBackgroundPlayEnabled();

    /**
     * 设置是否允许在后台播放
     *
     * @param enable
     */
    void setEnableBackgroundPlay(boolean enable);

    /**
     * 获取播放配置
     *
     * @return
     */
    PolyvPlayOption getPlayOption();

    /**
     * 从新的直播流开始播放，仅直播模式有效
     */
    void startFromNew();

    /**
     * 获取子播放器
     *
     * @return
     */
    PolyvSubVideoView getSubVideoView();

    /**
     * 获取IjkVideoView，用于调用金石的接口
     * @return
     */
    IjkVideoView getIjkVideoView();

    /**
     * 音频数据是否为杜比音频编码
     * @return {@code true}:是<br/>{@code false}:否
     */
    boolean isDolbyAudio();

    /**
     * 是否开启VR
     * @return {@code true}:是 {@code false}:否
     */
    boolean vrOn();

    /**
     * 切换VR显示模式
     */
    void changeVRDisplayMode();

    /**
     * activity的onResume调用
     */
    void onResume();

    /**
     * activity的onPause调用
     */
    void onPause();

    /**
     * activity的onConfigurationChanged调用
     */
    void onConfigurationChanged();
}
