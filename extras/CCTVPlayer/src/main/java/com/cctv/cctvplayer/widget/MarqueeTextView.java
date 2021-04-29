package com.cctv.cctvplayer.widget;

import android.content.Context;
import android.graphics.Rect;

import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 让所有要实现跑马灯效果的TextView都处于focused状态
 */
public class MarqueeTextView extends AppCompatTextView {
    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (focused)
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus)
            super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}

