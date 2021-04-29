package com.cctv.cctvplayer.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cctv.cctvplayer.CCTVVideoMediaController;
import com.cctv.cctvplayer.R;
import com.cctv.cctvplayer.player.PlayerOperate;
import com.cctv.cctvplayer.utils.TimeUtils;

/**
 * 手势滑动快进/快退提示的View
 */
public class ProgressTipsView extends FrameLayout {
    //progressView
    private View view;
    private TextView tv_currenttime, tv_totaltime, tv_sp;
    private ImageView iv_left, iv_right;
    private CCTVVideoMediaController mMediaController;

    public void setMediaController(CCTVVideoMediaController mediaController) {
        this.mMediaController = mediaController;
    }

    public ProgressTipsView(Context context) {
        this(context, null);
    }

    public ProgressTipsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.view = LayoutInflater.from(context).inflate(R.layout.cctv_progress_tips_view, this);
        initView();
    }

    private void initView() {
        hide();
        tv_currenttime = view.findViewById(R.id.tv_currenttime);
        tv_totaltime = view.findViewById(R.id.tv_totaltime);
        tv_sp = view.findViewById(R.id.tv_sp);
        iv_left = view.findViewById(R.id.iv_left);
        iv_right = view.findViewById(R.id.iv_right);
    }

    public void hide() {
        setVisibility(View.GONE);
    }

    public void delayHide() {
        handler.removeMessages(View.GONE);
        handler.sendEmptyMessageDelayed(View.GONE, 300);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == View.GONE)
                setVisibility(View.GONE);
        }
    };

    public void setLiveProgressPercent(boolean slideEnd, boolean isRightSwipe, long totaltime) {
        handler.removeMessages(View.GONE);
        tv_totaltime.setVisibility(View.GONE);
        tv_sp.setVisibility(View.GONE);
        if (slideEnd) {
            handler.sendEmptyMessageDelayed(View.GONE, 300);
            if (mMediaController != null) {
                mMediaController.startLiveProgressTimer();
                mMediaController.playLiveOrBack(PlayerOperate.OTHER);
            }
        } else {
            mMediaController.removeLiveProgressTimer();
            setVisibility(View.VISIBLE);
            if (isRightSwipe) {
                iv_left.setVisibility(View.GONE);
                iv_right.setVisibility(View.VISIBLE);
                mMediaController.setLiveProgress(1000);
            } else {
                iv_left.setVisibility(View.VISIBLE);
                iv_right.setVisibility(View.GONE);
                mMediaController.setLiveProgress(-1000);
            }
            if (mMediaController != null) {
                tv_currenttime.setText(TimeUtils.format(mMediaController.getLivePlayedMs(), "HH:mm:ss"));
            }
        }
    }

    public void setVodProgressPercent(int fastForwardPos, int totaltime, boolean slideEnd, boolean isRightSwipe) {
        handler.removeMessages(View.GONE);
        tv_totaltime.setVisibility(View.VISIBLE);
        tv_sp.setVisibility(View.VISIBLE);
        if (slideEnd) {
            handler.sendEmptyMessageDelayed(View.GONE, 300);
        } else {
            setVisibility(View.VISIBLE);
            if (isRightSwipe) {
                iv_left.setVisibility(View.GONE);
                iv_right.setVisibility(View.VISIBLE);
            } else {
                iv_left.setVisibility(View.VISIBLE);
                iv_right.setVisibility(View.GONE);
            }
            if (fastForwardPos < 0)
                fastForwardPos = 0;
            if (fastForwardPos > totaltime)
                fastForwardPos = totaltime;
            tv_currenttime.setText(TimeUtils.generateTime(fastForwardPos));
            tv_totaltime.setText(TimeUtils.generateTime(totaltime));
        }
    }
}
