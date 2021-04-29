package com.easefun.povplayer.core.video;

import android.content.Context;
import android.util.AttributeSet;

import com.easefun.povplayer.core.video.listener.IPolyvOnSubVideoViewCompletionListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnSubVideoViewCountdownListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnSubVideoViewPlayStatusListener;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 子播放器监听事件设置实现类
 */
public abstract class PolyvSubVideoViewListenerEvent extends PolyvBaseVideoViewListenerEvent implements IPolyvSubVideoViewListenerEvent {
    private IPolyvOnSubVideoViewCompletionListener onSubVideoViewCompletionListener = null;
    private IPolyvOnSubVideoViewPlayStatusListener onSubVideoViewPlayStatusListener = null;
    private IPolyvOnSubVideoViewCountdownListener onSubVideoViewCountdownListener = null;

    public PolyvSubVideoViewListenerEvent(Context context) {
        super(context);
    }

    public PolyvSubVideoViewListenerEvent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PolyvSubVideoViewListenerEvent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnSubVideoViewPlayCompletionListener(IPolyvOnSubVideoViewCompletionListener l) {
        onSubVideoViewCompletionListener = l;
    }

    @Override
    public void setOnSubVideoViewPlayStatusListener(IPolyvOnSubVideoViewPlayStatusListener l) {
        onSubVideoViewPlayStatusListener = l;
    }

    @Override
    public void setOnSubVideoViewCountdownListener(IPolyvOnSubVideoViewCountdownListener l) {
        onSubVideoViewCountdownListener = l;
    }

    protected void callOnSubVideoViewPlayStatusComplete(final IMediaPlayer mp, @PolyvSubVideoView.AdStage final int adStage) {
        if (onSubVideoViewCompletionListener != null) {
            onSubVideoViewCompletionListener.onCompletion(mp, adStage);
        }
        if (onSubVideoViewPlayStatusListener != null) {
            onSubVideoViewPlayStatusListener.onCompletion(mp, adStage);
        }
    }

    protected void callOnSubVideoViewPlayStatusError(final PolyvPlayError error) {
        if (onSubVideoViewPlayStatusListener != null) {
            onSubVideoViewPlayStatusListener.onError(error);
        }
    }

    protected void callOnSubVideoViewCountdown(final int totalTime, final int remainTime, @PolyvSubVideoView.PlayStage final int playStage) {
        if (onSubVideoViewCountdownListener != null) {
            onSubVideoViewCountdownListener.onCountdown(totalTime, remainTime, playStage);
        }
        if (onSubVideoViewPlayStatusListener != null) {
            onSubVideoViewPlayStatusListener.onCountdown(totalTime, remainTime, playStage);
        }
    }

    protected void callOnSubVideoViewVisibilityChange(final boolean isShow) {
        if (onSubVideoViewCountdownListener != null) {
            onSubVideoViewCountdownListener.onVisibilityChange(isShow);
        }
    }

    protected void clearAllListener() {
        super.clearListener();
        onSubVideoViewPlayStatusListener = null;
        onSubVideoViewCountdownListener = null;
        onSubVideoViewCompletionListener = null;
    }
}
