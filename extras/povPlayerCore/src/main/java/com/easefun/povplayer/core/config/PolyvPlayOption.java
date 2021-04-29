package com.easefun.povplayer.core.config;



import androidx.annotation.IntDef;

import com.easefun.povplayer.core.video.PolyvVideoView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * 播放配置类
 */
public class PolyvPlayOption {
    private Map<String, Object> options = new HashMap<>();
    /**
     * 点播模式
     */
    public static final int PLAYMODE_VOD = PolyvVideoView.PLAY_MODE_VOD;
    /**
     * 直播模式
     */
    public static final int PLAYMODE_LIVE = PolyvVideoView.PLAY_MODE_LIVE;
    /**
     * 软解码方式
     */
    public static final int DECODEMODE_AVCODEC = 0;
    /**
     * 硬解码方式
     */
    public static final int DECODEMODE_MEDIACODEC = 1;

    @IntDef({
            PLAYMODE_VOD,
            PLAYMODE_LIVE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayMode {
    }

    @IntDef({
            DECODEMODE_AVCODEC,
            DECODEMODE_MEDIACODEC
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DecodeMode {
    }

    //配置参数的key
    /**
     * 片头广告key，value类型为{@link HeadAdOption}或者{@link java.util.List<HeadAdOption>}
     */
    public static final String KEY_HEADAD = "KEY_HEADAD";
    /**
     * 片尾广告key，value：{@link TailAdOption}，仅点播有效
     */
    public static final String KEY_TAILAD = "KEY_TAILAD";
    /**
     * 暖场视频key，value：{@link String}，仅直播有效
     */
    public static final String KEY_TEASER = "KEY_TEASER";
    /**
     * 播放模式key，value：{@link PlayMode}
     */
    public static final String KEY_PLAYMODE = "KEY_PLAYMODE";
    /**
     * 解码方式key，value：{@link DecodeMode}
     */
    public static final String KEY_DECODEMODE = "KEY_DECODEMODE";
    /**
     * 播放器请求超时时间key，value：{@link Integer}，单位：秒。<br>
     * 小于5时为5
     */
    public static final String KEY_TIMEOUT = "KEY_TIMEOUT";
    /**
     * 播放器重连次数key，value：{@link Integer}，仅直播有效
     */
    public static final String KEY_RECONNECTION_COUNT = "KEY_RECONNECTION_COUNT";
    /**
     * 动态丢帧(跳帧)key，value：{@link Integer}，仅直播有效。<br>
     * 注：取值范围：[0, 10]，参数为0时视频解码不及时时不会丢帧，会增加缓冲区和延时
     */
    public static final String KEY_FRAMEDROP = "KEY_FRAMEDROP";
    /**
     * 主视频请求头的Host key，value类型为：{@link String}，指定ip对应的host。<br>
     * 针对带IP地址的播放URL，如果是m3u8地址，需要m3u8地址的host与ts的host一致
     */
    public static final String KEY_HOST = "KEY_HOST";
    /**
     * 设置请求头。value类型为：{@link Map<String,String>}，注意value的字符串前面需要加上" "。
     */
    public static final String KEY_HEADERS = "KEY_HEADERS";
    /**
     * 主视频(点播和直播)预加载的时间，即在最后一个前贴片广告开始播放第n秒，开始预加载正片。value类型为：{@link Integer}，单位：秒。
     */
    public static final String KEY_PRELOADTIME = "KEY_PRELOADTIME";
    /**
     * 设置子播放器加载视频时缓冲视图的延迟显示时间。value类型为：{@link Integer}，单位：毫秒。
     */
    public static final String KEY_LOADINGVIEW_DELAY = "KEY_LOADINGVIEW_DELAY";
    /**
     * 是否VR模式
     */
    public static final String KEY_VR_ON = "KEY_VR_ON";

    /**
     * 片头广告配置类
     */
    public static class HeadAdOption {
        /**
         * 片头广告地址
         */
        public final String headAdPath;
        /**
         * 时长，小于等于0或者大于视频时长时用视频的时长
         */
        public final int headAdDuration;

        public HeadAdOption(String headAdPath, int headAdDuration) {
            this.headAdPath = headAdPath;
            this.headAdDuration = headAdDuration;
        }

        @Override
        public String toString() {
            return "HeadAdOption{" +
                    "headAdPath='" + headAdPath + '\'' +
                    ", headAdDuration=" + headAdDuration +
                    '}';
        }
    }

    /**
     * 片尾广告配置类
     */
    public static class TailAdOption {
        /**
         * 片尾广告地址
         */
        public final String tailAdPath;
        /**
         * 时长，小于等于0或者大于视频时长时用视频的时长
         */
        public final int tailAdDuration;

        public TailAdOption(String tailAdPath, int tailAdDuration) {
            this.tailAdPath = tailAdPath;
            this.tailAdDuration = tailAdDuration;
        }

        @Override
        public String toString() {
            return "TailAdOption{" +
                    "tailAdPath='" + tailAdPath + '\'' +
                    ", tailAdDuration=" + tailAdDuration +
                    '}';
        }
    }

    private PolyvPlayOption() {
    }

    private void addDefaultOption() {
        put(KEY_DECODEMODE, DECODEMODE_AVCODEC);
        put(KEY_PLAYMODE, PLAYMODE_VOD);
        put(KEY_FRAMEDROP, 1);
        put(KEY_TIMEOUT, 20);
        put(KEY_RECONNECTION_COUNT, 3);
        put(KEY_PRELOADTIME, 3);
        put(KEY_LOADINGVIEW_DELAY, 0);
        put(KEY_VR_ON, false);
    }

    /**
     * 获取默认的配置
     *
     * @return
     */
    public static PolyvPlayOption getDefault() {
        PolyvPlayOption playOption = new PolyvPlayOption();
        playOption.addDefaultOption();
        return playOption;
    }

    /**
     * 添加配置
     *
     * @param key
     * @param value
     * @return
     */
    public PolyvPlayOption put(String key, Object value) {
        options.put(key, value);
        return this;
    }

    /**
     * 获取某个key的配置值
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return options.get(key);
    }

    /**
     * 获取播放配置集合
     *
     * @return
     */
    public Map<String, Object> getOptions() {
        return options;
    }

    /**
     * 重置配置
     *
     * @return
     */
    public PolyvPlayOption reset() {
        options.clear();
        addDefaultOption();
        return this;
    }

    @Override
    public String toString() {
        return "PolyvPlayOption{" +
                "options=" + options +
                '}';
    }
}
