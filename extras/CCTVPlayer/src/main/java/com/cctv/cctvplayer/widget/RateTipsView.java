package com.cctv.cctvplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cctv.cctvplayer.CCTVVideoMediaController;
import com.cctv.cctvplayer.CCTVVideoView;
import com.cctv.cctvplayer.R;
import com.cctv.cctvplayer.entity.CCTVStreamEntity;
import com.cctv.cctvplayer.player.PlayerOperate;

import java.util.List;

/**
 * 码率View
 */
public class RateTipsView extends FrameLayout {

    private List<CCTVStreamEntity> mList;
    private LinearLayout mRateParentView;
    private View mChekView;
    private CCTVVideoView mCCTVVideoView;
    private CCTVVideoMediaController mController;

    public void addItem(List<CCTVStreamEntity> list, CCTVVideoView view) {
        this.mList = list;
        this.mCCTVVideoView = view;
        if (mCCTVVideoView == null)
            return;
        this.mController = mCCTVVideoView.getMediaController();
        createItemView();
    }

    public RateTipsView(Context context) {
        this(context, null);
    }

    public RateTipsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RateTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.cctv_videoview_rate, this);
        mRateParentView = view.findViewById(R.id.rateItemParent);
    }

    private void createItemView() {
        if (mList == null || mList.isEmpty())
            return;

        mRateParentView.removeAllViews();
        for (CCTVStreamEntity rate : mList) {
            View itemView = View.inflate(getContext(), R.layout.cctv_videoview_rate_item, null);
            TextView rateNameView = itemView.findViewById(R.id.rateName);
            ImageView rateCheckView = itemView.findViewById(R.id.rateCheck);
            rateNameView.setText(rate.getName());
            if (rate.isDefault()) {
                mChekView = rateCheckView;
                rateCheckView.setVisibility(View.VISIBLE);
            } else {
                rateCheckView.setVisibility(View.INVISIBLE);
            }
            mRateParentView.addView(itemView);
            setItemClick(itemView, rate.getName());
        }
    }

    private void setItemClick(final View itemView, final String rateName) {
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CCTVStreamEntity rate : mList) {
                    if (rate.getName().equals(rateName)) {
                        rate.setDefault(true);
                    } else
                        rate.setDefault(false);
                }

                if (mChekView != null)
                    mChekView.setVisibility(View.INVISIBLE);
                ImageView rateCheckView = itemView.findViewById(R.id.rateCheck);
                rateCheckView.setVisibility(View.VISIBLE);
                mChekView = rateCheckView;
                if (mCCTVVideoView.getPlayEntity() != null) {
                    mController.setBottomRateName();
                    mCCTVVideoView.showOrHiddenRightView(View.GONE);
                    if (mController.isLive()) {
                        mController.playLiveOrBack(PlayerOperate.RATE_CHANGE);
                    } else {
                        mController.playVod(true, PlayerOperate.RATE_CHANGE);
                    }
                }
            }
        });
    }
}
