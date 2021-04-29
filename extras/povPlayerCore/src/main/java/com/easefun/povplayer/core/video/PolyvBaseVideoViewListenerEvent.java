package com.easefun.povplayer.core.video;

import android.content.Context;
import android.util.AttributeSet;

import com.easefun.povplayer.core.video.listener.IPolyvOnBufferingUpdateListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnErrorListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureClickListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnInfoListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnPlayPauseListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnPreparedListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnSeekCompleteListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnVideoSizeChangedListener;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 播放器监听事件设置接口抽象类，并集成了播放转发类的功能。主/子播放器继承该类即可。
 */
public abstract class PolyvBaseVideoViewListenerEvent extends PolyvForwardingIjkVideoView implements IPolyvBaseVideoViewListenerEvent {
    private IPolyvOnBufferingUpdateListener onBufferingUpdateListener = null;
    private IPolyvOnPlayPauseListener onPlayPauseListener = null;
    private IPolyvOnPreparedListener onPreparedListener = null;
    private IPolyvOnErrorListener onErrorListener = null;
    private IPolyvOnInfoListener onInfoListener = null;
    private IPolyvOnSeekCompleteListener onSeekCompleteListener = null;
    private IPolyvOnVideoSizeChangedListener onVideoSizeChangedListener = null;
    private IPolyvOnGestureClickListener onGestureClickListener = null;

    public PolyvBaseVideoViewListenerEvent(Context context) {
        super(context);
    }

    public PolyvBaseVideoViewListenerEvent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PolyvBaseVideoViewListenerEvent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnBufferingUpdateListener(IPolyvOnBufferingUpdateListener l) {
        this.onBufferingUpdateListener = l;
    }

    @Override
    public void setOnPlayPauseListener(IPolyvOnPlayPauseListener l) {
        this.onPlayPauseListener = l;
    }

    @Override
    public void setOnPreparedListener(IPolyvOnPreparedListener l) {
        this.onPreparedListener = l;
    }

    @Override
    public void setOnErrorListener(IPolyvOnErrorListener l) {
        this.onErrorListener = l;
    }

    @Override
    public void setOnInfoListener(IPolyvOnInfoListener l) {
        this.onInfoListener = l;
    }

    @Override
    public void setOnSeekCompleteListener(IPolyvOnSeekCompleteListener l) {
        this.onSeekCompleteListener = l;
    }

    @Override
    public void setOnVideoSizeChangedListener(IPolyvOnVideoSizeChangedListener l) {
        this.onVideoSizeChangedListener = l;
    }

    @Override
    public void setOnGestureClickListener(IPolyvOnGestureClickListener l) {
        this.onGestureClickListener = l;
    }

    protected void callOnBufferingUpdateListener(final IMediaPlayer mp, final int percent) {
        if (onBufferingUpdateListener != null) {
            onBufferingUpdateListener.onBufferingUpdate(mp, percent);
        }
    }

    protected void callOnPlayPauseListenerPreparing() {
        if (onPlayPauseListener != null) {
            onPlayPauseListener.onPreparing();
        }
    }

    protected void callOnPlayPauseListenerPlay(final boolean isFirst) {
        if (onPlayPauseListener != null) {
            onPlayPauseListener.onPlay(isFirst);
        }
    }

    protected void callOnPlayPauseListenerPause() {
        if (onPlayPauseListener != null) {
            onPlayPauseListener.onPause();
        }
    }

    protected void callOnPlayPauseListenerCompletion(final IMediaPlayer mp) {
        if (onPlayPauseListener != null) {
            onPlayPauseListener.onCompletion(mp);
        }
    }

    protected void callOnPreparedListener(final IMediaPlayer mp) {
        if (onPreparedListener != null) {
            onPreparedListener.onPrepared(mp);
        }
    }

    protected void callOnErrorListener(final IMediaPlayer mp, final int what, final int extra) {
        if (onErrorListener != null) {
            onErrorListener.onError(mp, what, extra);
        }
    }

    protected void callOnInfoListener(final IMediaPlayer mp, final int what, final Object extra) {
        if (onInfoListener != null && extra instanceof Integer) {
            onInfoListener.onInfo(mp, what, (Integer) extra);
        }
    }

    protected void callOnSeekCompleteListener(final IMediaPlayer mp) {
        if (onSeekCompleteListener != null) {
            onSeekCompleteListener.onSeekComplete(mp);
        }
    }

    protected void callOnVideoSizeChangedListener(final IMediaPlayer mp, final int width, final int height, final int sarNum, final int sarDen) {
        if (onVideoSizeChangedListener != null) {
            onVideoSizeChangedListener.onVideoSizeChanged(mp, width, height, sarNum, sarDen);
        }
    }

    protected void callOnGestureClickListener() {
        if (onGestureClickListener != null) {
            onGestureClickListener.callback();
        }
    }

    protected void clearListener() {
        onBufferingUpdateListener = null;
        onPlayPauseListener = null;
        onPreparedListener = null;
        onErrorListener = null;
        onInfoListener = null;
        onSeekCompleteListener = null;
        onVideoSizeChangedListener = null;
        onGestureClickListener = null;
    }
}
