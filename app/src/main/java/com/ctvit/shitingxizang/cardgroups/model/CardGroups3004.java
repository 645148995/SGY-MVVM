package com.ctvit.shitingxizang.cardgroups.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctvit.shitingxizang.R;
import com.ctvit.shitingxizang.base.BaseAdapter;
import com.ctvit.shitingxizang.base.BaseViewHolder;
import com.ctvit.shitingxizang.cardgroups.Constant;
import com.ctvit.shitingxizang.entity.CardGroupsEntity;
import com.ctvit.shitingxizang.ui.fragment.AgentWebFragment;
import com.ctvit.shitingxizang.utils.DateUtil;
import com.ctvit.shitingxizang.utils.ImageLoader;
import com.ctvit.shitingxizang.utils.LogUtils;


import java.util.List;

import com.ctvit.shitingxizang.base.ContainerActivity;

/**
 * 左文右图样式
 */
public class CardGroups3004 extends BaseViewHolder<CardGroupsEntity.CardgroupsBean> {

    private ImageView img, img_play, img_over_play;
    private TextView titleView, sourceView;
    private LinearLayout liner_listen_news;
    private String imgUrl;


    public CardGroups3004(Context context, ViewGroup parent) {
        super(context, parent, R.layout.cardgroups_3004);

    }

    @Override
    public void findView(View itemView) {
        img = itemView.findViewById(R.id.img);
        titleView = itemView.findViewById(R.id.title);
        liner_listen_news = itemView.findViewById(R.id.liner_listen_news);
        sourceView = itemView.findViewById(R.id.source);
        img_play = itemView.findViewById(R.id.img_play);
        img_over_play = itemView.findViewById(R.id.img_over_play);
    }

    @Override
    public void setData(List<CardGroupsEntity.CardgroupsBean> data, int position, BaseAdapter adapter) {
        super.setData(data, position, adapter);

        LogUtils.i("3004  setData");
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void setData(CardGroupsEntity.CardgroupsBean data, int position) {
        if (data == null || data.getCards() == null || data.getCards().size() < 1) {
            return;
        }
        CardGroupsEntity.CardgroupsBean.CardsBean cardsBean = data.getCards().get(0);
        imgUrl = cardsBean.getPhoto().getThumb();

        ImageLoader.load(mContext, imgUrl, img);
        String link = !TextUtils.isEmpty(cardsBean.getLink()) ? cardsBean.getLink() : "";

        LogUtils.i("CardGroups3004 link = " + link);

        itemView.setOnClickListener(v -> {

//            LinkUtils.resolveLink(mContext, cardsBean);
            Bundle bundle = new Bundle();
            bundle.putString(Constant.EXTRA_PAGE_ID, "11111111");
            ContainerActivity.start(mContext, AgentWebFragment.class.getCanonicalName(), bundle);

        });

        titleView.setText(cardsBean.getTitle());
        LogUtils.i("SourceTime = " + cardsBean.getSource() + ", time = " + DateUtil.dateDiff(cardsBean.getDate()));
        sourceView.setText(String.format("%s  %s", TextUtils.isEmpty(cardsBean.getSource()) || cardsBean.getSource().equals("null") ? "" : cardsBean.getSource(), DateUtil.dateDiff(cardsBean.getDate())));
        String url = cardsBean.getVideo().getAudio_url();
        if (TextUtils.isEmpty(url)) {
            liner_listen_news.setVisibility(View.GONE);
        } else {
            liner_listen_news.setVisibility(View.VISIBLE);
        }


    }


}
