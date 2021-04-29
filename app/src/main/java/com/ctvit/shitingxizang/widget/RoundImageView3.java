package com.ctvit.shitingxizang.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import me.goldze.mvvmhabit.utils.KLog;


/**
 * from com.zhihu.matisse.internal.ui.widget.
 * 财经拷过来的图片类
 */
public class RoundImageView3 extends AppCompatImageView {

    private float mRadius; // dp
    private Path mRoundedRectPath;
    private RectF mRectF;

    public RoundImageView3(Context context) {
        super(context);
        init(context);
    }

    public RoundImageView3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundImageView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        mRadius = 2.0f * density;
        mRoundedRectPath = new Path();
        mRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
        mRoundedRectPath.addRoundRect(mRectF, mRadius, mRadius, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.clipPath(mRoundedRectPath);
//        super.onDraw(canvas);
        try {
            canvas.clipPath(mRoundedRectPath);
            super.onDraw(canvas);
        } catch (Exception e) {
            KLog.i("Catch Canvas: trying to use a recycled bitmap");
        }

    }

}
