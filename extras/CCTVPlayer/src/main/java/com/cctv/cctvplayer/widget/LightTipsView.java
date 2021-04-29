package com.cctv.cctvplayer.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cctv.cctvplayer.R;

/**
 * 左边上下时提示的View
 */
public class LightTipsView extends FrameLayout {
    //lightView
    private View view;
    private TextView tv_percent;

    public LightTipsView(Context context) {
        this(context, null);
    }

    public LightTipsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LightTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.view = LayoutInflater.from(context).inflate(R.layout.cctv_videoview_tips_view_light, this);
        initView();
    }

    private void initView() {
        hide();
        tv_percent = view.findViewById(R.id.tv_percent);
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

    public void setLightPercent(int brightness, boolean slideEnd) {
        handler.removeMessages(View.GONE);
        if (slideEnd) {
            handler.sendEmptyMessageDelayed(View.GONE, 300);
        } else {
            setVisibility(View.VISIBLE);
            tv_percent.setText(brightness + "%");
        }
    }
}
