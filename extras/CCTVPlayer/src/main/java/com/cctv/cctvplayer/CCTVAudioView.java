package com.cctv.cctvplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.easefun.povplayer.core.config.PolyvPlayOption;
import com.easefun.povplayer.core.video.PolyvSubVideoView;
import com.easefun.povplayer.core.video.PolyvVideoView;

import java.util.Map;

/**
 * 音频播放器
 */

public class CCTVAudioView extends PolyvVideoView {
    public CCTVAudioView(Context context) {
        this(context, null);
    }

    public CCTVAudioView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CCTVAudioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setVisibility(View.GONE);
        setSubVideoView(new PolyvSubVideoView(getContext()));
    }

    /**
     * 播放
     *
     * @param isLive  是否为直播
     * @param playURL 播放地址
     * @param headers 请求头
     */
    public void mPlay(boolean isLive, String playURL, Map<String, String> headers) {
        setOption(playURL, generatePlayOption(isLive, headers));
        play();
    }

    /**
     * 生成播放器配置，记得在播放器生成
     */
    private PolyvPlayOption generatePlayOption(boolean isLive, Map<String, String> headers) {
        PolyvPlayOption playOption = PolyvPlayOption.getDefault();
        playOption.put(PolyvPlayOption.KEY_PLAYMODE, isLive ? PolyvPlayOption.PLAYMODE_LIVE : PolyvPlayOption.PLAYMODE_VOD)
                .put(PolyvPlayOption.KEY_DECODEMODE, PolyvPlayOption.DECODEMODE_AVCODEC)
                .put(PolyvPlayOption.KEY_FRAMEDROP, 0)
                .put(PolyvPlayOption.KEY_TIMEOUT, 20);

        if (isLive) {//直播
            playOption.put(PolyvPlayOption.KEY_RECONNECTION_COUNT, 3);
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    headers.put(entry.getKey(), " " + entry.getValue());//播放器要求value前面加一个空格
                }
                playOption.put(PolyvPlayOption.KEY_HEADERS, headers);
            }
        } else {//点播
        }
        return playOption;
    }
}
