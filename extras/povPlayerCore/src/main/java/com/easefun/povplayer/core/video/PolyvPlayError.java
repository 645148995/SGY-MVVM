package com.easefun.povplayer.core.video;

import android.media.MediaPlayer;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 视频播放失败信息封装类
 */
public class PolyvPlayError {
    /**
     * 播放器内部抛出的-10000错误
     */
    public static final int INNER_PLAY_ERROR = -10000;//一般是视频源有问题或者数据格式不支持
    //100~ 参数不合法/不支持
    /**
     * 播放地址为null
     */
    public static final int ERROR_PLAYPATH_IS_NULL = -1000;
    /**
     * host为null或""
     */
    public static final int ERROR_OPTION_KEY_HOST_EMPTY = -1002;
    /**
     * headers为null
     */
    public static final int ERROR_HEADERS_IS_NULL = -1006;
    //101~ 网络问题相关/请求失败
    /**
     * 当前网络不可用
     */
    public static final int ERROR_NETWORK_NOT_AVAILABLE = -1010;
    //-102~ 播放器问题/播放失败/解码失败等
    /**
     * 播放器请求超时
     */
    public static final int ERROR_REQUEST_TIMEOUT = -1020;

    /**
     * 片头广告阶段
     */
    public static final int PLAY_STAGE_HEADAD = PolyvSubVideoView.PLAY_STAGE_HEADAD;
    /**
     * 暖场视频阶段
     */
    public static final int PLAY_STAGE_TEASER = PolyvSubVideoView.PLAY_STAGE_TEASER;
    /**
     * 片尾广告阶段
     */
    public static final int PLAY_STAGE_TAILAD = PolyvSubVideoView.PLAY_STAGE_TAILAD;
    /**
     * 点播主视频阶段
     */
    public static final int PLAY_STAGE_MAIN_VOD = PolyvVideoView.PLAY_MODE_VOD;
    /**
     * 直播主视频阶段
     */
    public static final int PLAY_STAGE_MAIN_LIVE = PolyvVideoView.PLAY_MODE_LIVE;

    /**
     * 播放地址
     */
    public final String playPath;
    /**
     * 错误码
     */
    public final int errorCode;
    /**
     * 错误描述
     */
    public final String errorDescribe;
    /**
     * 播放阶段
     */
    public final int playStage;

    public PolyvPlayError(String playPath, @PlayErrorCode int errorCode, String errorDescribe, @PlayStage int playStage) {
        this.playPath = playPath;
        this.errorCode = errorCode;
        this.errorDescribe = errorDescribe;
        this.playStage = playStage;
    }

    public static PolyvPlayError toErrorObj(String playUrl, @PlayErrorCode int errorCode, @PlayStage int playStage) {
        return toErrorObj(playUrl, errorCode, getErrorDescribe(errorCode), playStage);
    }

    private static PolyvPlayError toErrorObj(String playUrl, @PlayErrorCode int errorCode, String errorDescribe, @PlayStage int playStage) {
        return new PolyvPlayError(playUrl, errorCode, errorDescribe, playStage);
    }

    private static String getErrorDescribe(@PlayErrorCode int errorCode) {
        String errorDescribe = "Unknown";
        switch (errorCode) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                errorDescribe = "Valid Play";//数据错误没有有效的回收
                break;
            case ERROR_PLAYPATH_IS_NULL:
                errorDescribe = "PlayPath Is Null";
                break;
            case ERROR_OPTION_KEY_HOST_EMPTY:
                errorDescribe = "Host Is Empty";
                break;
            case ERROR_HEADERS_IS_NULL:
                errorDescribe = "Headers Is Null";
                break;
            case ERROR_REQUEST_TIMEOUT:
                errorDescribe = "Request Timeout";
                break;
        }
        return errorDescribe;
    }

    /**
     * 是否是主视频阶段(包括点播、直播主视频阶段)
     *
     * @return
     */
    public boolean isMainStage() {
        return playStage == PLAY_STAGE_MAIN_VOD || playStage == PLAY_STAGE_MAIN_LIVE;
    }

    @Override
    public String toString() {
        return "PolyvPlayError{" +
                "playPath='" + playPath + '\'' +
                ", errorCode=" + errorCode +
                ", errorDescribe='" + errorDescribe + '\'' +
                ", playStage=" + playStage +
                '}';
    }


    @IntDef({
            ERROR_PLAYPATH_IS_NULL,
            ERROR_REQUEST_TIMEOUT,
            ERROR_HEADERS_IS_NULL,
            ERROR_OPTION_KEY_HOST_EMPTY
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayErrorCode {
    }

    @IntDef({
            PLAY_STAGE_HEADAD,
            PLAY_STAGE_TEASER,
            PLAY_STAGE_TAILAD,
            PLAY_STAGE_MAIN_VOD,
            PLAY_STAGE_MAIN_LIVE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayStage {
    }

}
