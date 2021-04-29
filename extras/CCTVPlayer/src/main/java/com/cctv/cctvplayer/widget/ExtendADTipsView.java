package com.cctv.cctvplayer.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cctv.cctvplayer.R;

/**
 * 左下角，有推广广告时提示的View
 */
public class ExtendADTipsView extends FrameLayout {

    private boolean mIsShow; //是否可以显示
    private ImageView mImgView;
    private ImageView mCloseView;
    private TextView mTitleView;
    private View mView;

    public ExtendADTipsView(Context context) {
        this(context, null);
    }

    public ExtendADTipsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtendADTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mView = LayoutInflater.from(context).inflate(R.layout.cctv_videoview_extend_ad, this);
        findViews(mView);
        setListeners();
        setVisibility(View.GONE);
        mView.setVisibility(View.GONE);
    }

    private void findViews(View view) {
        mImgView = view.findViewById(R.id.extendImg);
        mCloseView = view.findViewById(R.id.closeExtend);
        mTitleView = view.findViewById(R.id.extendTitle);
    }

    private void setListeners() {
        mCloseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(View.GONE);
                mView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 显示推广广告的信息，只有横屏显示
     *
     * @param bitmap  请将网络或者本地图片转成bitmap类型传进来
     * @param subShow 子播放器是否正在显示
     */
    public void showExtendAD(Bitmap bitmap, String title, boolean subShow) {
        if (TextUtils.isEmpty(title) || subShow
                || getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setVisibility(View.GONE);
            mView.setVisibility(View.GONE);
            return;
        }

        mIsShow = true;
        setVisibility(View.VISIBLE);
        mView.setVisibility(View.VISIBLE);
        setTag("显示广告");

        mTitleView.setText(title);
        if (bitmap != null) {
            mImgView.setImageBitmap(bitmap);
            mImgView.setVisibility(View.VISIBLE);
        } else {
            mImgView.setVisibility(View.GONE);
        }

        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(1, 10000);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            mIsShow = false;
            setVisibility(View.GONE);
            mView.setVisibility(View.GONE);
        }
    };

    /**
     * 是否正在显示
     */
    public boolean isShow() {
        return this.mIsShow;
    }
}
