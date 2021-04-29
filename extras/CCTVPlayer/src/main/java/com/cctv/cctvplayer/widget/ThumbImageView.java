package com.cctv.cctvplayer.widget;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;

import com.cctv.cctvplayer.R;

public class ThumbImageView extends AppCompatImageView implements View.OnTouchListener {

    private ViewGroup mSeekBarParentView;
    private ViewGroup mNoteLayout;

    public ThumbImageView(Context context) {
        this(context, null);
    }

    public ThumbImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    public void setSeekBarParentView(ViewGroup viewGroup) {
        this.mSeekBarParentView = viewGroup;
        this.mNoteLayout = mSeekBarParentView.findViewById(R.id.playProgressNodeLayout);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mSeekBarParentView != null)
            return mSeekBarParentView.onTouchEvent(event);
        return true;
    }
}
