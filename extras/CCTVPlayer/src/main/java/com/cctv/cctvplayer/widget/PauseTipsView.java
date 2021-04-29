package com.cctv.cctvplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cctv.cctvplayer.CCTVVideoMediaController;
import com.cctv.cctvplayer.R;

/**
 * 暂停时提示的View
 */
public class PauseTipsView extends FrameLayout {

    private CCTVVideoMediaController mMediaController;
    //播放快退
    private RelativeLayout mDrawBackLayout;
    //播放快进
    private RelativeLayout mAdvanceLayout;
    //左侧自定义布局
    private LinearLayout mPauseLeftLayout;
    //右侧快进/快退布局
    private RelativeLayout mPauseRightLayout;

    public void setMediaController(CCTVVideoMediaController mediaController) {
        this.mMediaController = mediaController;
    }

    public PauseTipsView(Context context) {
        this(context, null);
    }

    public PauseTipsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PauseTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.cctv_videoview_pause, this);
        findViews(view);
        setVisibility(View.GONE);
    }

    private void findViews(View view) {
        mDrawBackLayout = view.findViewById(R.id.drawBackLayout);
        mAdvanceLayout = view.findViewById(R.id.advanceLayout);
        mPauseLeftLayout = view.findViewById(R.id.pauseLeft);
        mPauseRightLayout = view.findViewById(R.id.pauseRight);
    }

    /**
     * 设置快退事件监听
     */
    public void setDrawBackClickListener(OnClickListener clickListener) {
        mDrawBackLayout.setOnClickListener(clickListener);
    }

    /**
     * 设置快进事件监听
     */
    public void setAdvanceClickListener(OnClickListener clickListener) {
        mAdvanceLayout.setOnClickListener(clickListener);
    }

    /**
     * 添加手动暂停时，左侧自定义布局
     */
    public void addPauseLeftCustomView(View view) {
        if (view == null)
            return;
        mPauseLeftLayout.removeAllViews();
        mPauseLeftLayout.addView(view);
    }

    /**
     * 显示隐藏手动暂停时，左侧自定义布局
     */
    public void showOrHiddenPauseLeftCustomView(int visibility) {
        if (View.VISIBLE == visibility && mPauseLeftLayout.getChildCount() < 1)
            return;

//        if (View.VISIBLE == visibility && mMediaController != null && !mMediaController.isHorizontal())
//            return;

        mPauseLeftLayout.setVisibility(visibility);
    }

    /**
     * 是否显示了暂停布局
     */
    public boolean isShow() {
        if (getVisibility() == View.VISIBLE && mPauseLeftLayout.getVisibility() == View.VISIBLE ||
                mPauseRightLayout.getVisibility() == View.VISIBLE)
            return true;
        return false;
    }
}
