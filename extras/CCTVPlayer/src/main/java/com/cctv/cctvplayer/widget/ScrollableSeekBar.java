package com.cctv.cctvplayer.widget;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

/**
 * 只是个能控制是否禁用拖动进度条的SeekBar
 */
public class ScrollableSeekBar extends AppCompatSeekBar {

    private boolean isScrollable = true;

    public ScrollableSeekBar(Context context) {
        super(context);
    }

    public ScrollableSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public ScrollableSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }

    /**
     * onTouchEvent 是在 SeekBar 继承的抽象类 AbsSeekBar 里
     * 你可以看下他们的继承关系
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return isScrollable && super.onTouchEvent(event);
    }


}