package com.cctv.cctvplayer.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cctv.cctvplayer.R;

/**
 * 右边上下时提示的View
 */
public class VolumeTipsView extends FrameLayout {
    private View view;
    private TextView tv_percent;
    private ImageView mVolumeView;

    public VolumeTipsView(Context context) {
        this(context, null);
    }

    public VolumeTipsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VolumeTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.view = LayoutInflater.from(context).inflate(R.layout.cctv_videoview_tips_view_volume, this);
        initView();
    }

    private void initView() {
        hide();
        tv_percent = view.findViewById(R.id.tv_percent);
        mVolumeView = view.findViewById(R.id.volume);
    }

    public void hide() {
        setVisibility(View.GONE);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == View.GONE)
                setVisibility(View.GONE);
        }
    };

    public void setVolumePercent(int volume, boolean slideEnd) {
        handler.removeMessages(View.GONE);
        if (slideEnd) {
            handler.sendEmptyMessageDelayed(View.GONE, 300);
        } else {
            if (volume < 1)
                mVolumeView.setImageResource(R.drawable.mute);
            else
                mVolumeView.setImageResource(R.drawable.volume);
            setVisibility(View.VISIBLE);
            tv_percent.setText(volume + "%");
        }
    }
}
