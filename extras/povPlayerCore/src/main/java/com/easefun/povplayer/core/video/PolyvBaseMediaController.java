package com.easefun.povplayer.core.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import www.viewscenestv.com.ijkvideoview.IMediaController;


/**
 * 媒体控制器基础抽象类，实现控制器需要继承该类
 */
public abstract class PolyvBaseMediaController extends FrameLayout implements IMediaController {

    public PolyvBaseMediaController(Context context) {
        super(context);
    }

    public PolyvBaseMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PolyvBaseMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 主播放器设置了控制栏时会回调
     *
     * @param videoView
     */
    public abstract void setVideoView(PolyvVideoView videoView);

    /**
     * 主视频准备完成时会回调
     *
     * @param mp
     */
    public abstract void onPrepared(IMediaPlayer mp);

    @Override
    public void setAnchorView(View view) {
    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
    }

    @Override
    public void showOnce(View view) {
    }

    @Override
    public void setOwnSeekBar() {
    }
}
