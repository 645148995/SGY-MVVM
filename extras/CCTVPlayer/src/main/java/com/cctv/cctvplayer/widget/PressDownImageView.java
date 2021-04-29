package com.cctv.cctvplayer.widget;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 有按压效果的ImageView
 */
public class PressDownImageView extends AppCompatImageView {
    public PressDownImageView(Context context) {
        super(context);
        this.setOnTouchListener(VIEW_TOUCH_DARK);
    }

    public PressDownImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(VIEW_TOUCH_DARK);
    }

    public PressDownImageView(Context context, AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnTouchListener(VIEW_TOUCH_DARK);
    }

    private OnTouchListener VIEW_TOUCH_DARK = new OnTouchListener() {
        // 变暗(三个-50，值越大则效果越深)
        public final float[] BT_SELECTED_DARK = new float[]
                {1, 0, 0, 0, -60, 0, 1, 0, 0, -60, 0, 0, 1, 0, -60, 0, 0, 0, 1, 0};

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                AppCompatImageView iv = (AppCompatImageView) v;
                iv.setColorFilter(new ColorMatrixColorFilter(BT_SELECTED_DARK));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                AppCompatImageView iv = (AppCompatImageView) v;
                iv.clearColorFilter();
                mPerformClick();
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                AppCompatImageView iv = (AppCompatImageView) v;
                iv.clearColorFilter();
            }
            return true; // 如为false，执行ACTION_DOWN后不再往下执行
        }
    };

    private void mPerformClick() {
        PressDownImageView.this.performClick();
    }

}
