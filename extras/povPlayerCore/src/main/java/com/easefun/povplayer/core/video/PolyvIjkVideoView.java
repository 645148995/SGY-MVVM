package com.easefun.povplayer.core.video;

import android.content.Context;
import android.util.AttributeSet;

import www.viewscenestv.com.ijkvideoview.IjkVideoView;


/**
 * IjkVideoView子类
 */
public class PolyvIjkVideoView extends IjkVideoView implements IPolyvIjkVideoView {

    public PolyvIjkVideoView(Context context) {
        this(context, null);
    }

    public PolyvIjkVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvIjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
