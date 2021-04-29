package com.easefun.povplayer.core.video;

import java.util.HashMap;

/**
 * 子播放器接口，有标注的方法仅内部使用。
 */
public interface IPolyvSubVideoView extends IPolyvBaseVideoView {

    /**
     * 获取片头广告地址
     *
     * @return
     */
    String getHeadAdUrl();

    /**
     * 获取片尾广告地址
     *
     * @return 如果是直播模式，那么为null
     */
    String getTailAdUrl();

    /**
     * 获取暖场视频地址
     *
     * @return 如果是点播模式，那么为null
     */
    String getTeaserUrl();

    /**
     * 是否开启片头广告
     *
     * @return
     */
    boolean isOpenHeadAd();

    /**
     * 是否开启片尾广告
     *
     * @return 如果是直播模式，那么为false
     */
    boolean isOpenTailAd();

    /**
     * 是否开启暖场广告
     *
     * @return 如果是点播模式，那么为false.
     */
    boolean isOpenTeaser();

    /**
     * 开始播放片头广告，注：仅内部使用。
     */
    void startHeadAd();

    /**
     * 开始播放片尾广告，注：仅内部使用。
     */
    void startTailAd();

    /**
     * 开始播放暖场，注：仅内部使用。
     */
    void startTeaser();

    /**
     * 重置播放阶段，注：仅内部使用。
     */
    void resetPlayStage();

    /**
     * 获取播放阶段
     *
     * @return
     */
    int getPlayStage();

    /**
     * 显示，注：仅内部使用。
     */
    void show();

    /**
     * 隐藏，注：仅内部使用。
     */
    void hide();

    /**
     * 是否是显示状态
     *
     * @return 如果是显示状态，即是播放广告/暖场阶段
     */
    boolean isShow();

    /**
     * 初始化播放配置，注：仅内部使用。
     *
     * @param options 播放配置
     */
    void initOption(HashMap<String, Object> options);

    /**
     * 是否有下一个广告还没播放
     *
     * @return
     */
    boolean hasNextHeadAd();
}
