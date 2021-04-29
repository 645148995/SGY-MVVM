package com.ctvit.shitingxizang.cardgroups.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import com.ctvit.shitingxizang.base.BaseViewHolder;
import com.ctvit.shitingxizang.entity.CardGroupsEntity;


/**
 * 默认布局。如果itemType没找到，就用这个布局，必须有，否则recyclerivew报错找不到itemType
 */

public class CardGroupsXiZangDefault extends BaseViewHolder<CardGroupsEntity.CardgroupsBean> {
    public CardGroupsXiZangDefault(Context context, ViewGroup parent, int layoutId) {
        super(context, parent, layoutId);
    }

    @Override
    public void findView(View itemView) {

    }

//    LinearLayout ll;
//    private List<CardGroupsEntity.CardgroupsBean> mCardgroupsBeans;
//    private AudioViewUtils mAudioViewUtils;
//    private BaseAdapter mBaseAdapter;
//
//    public CardGroupsXiZangDefault(Context context, ViewGroup parent) {
//        super(context, parent, R.layout.cardgroups_default);
//        this.mAudioViewUtils = mAudioViewUtils;
//    }
//
//    @Override
//    public void findView(View itemView) {
//        ll = itemView.findViewById(R.id.ll_default);
//    }
//
//    @Override
//    public void setData(List<CardGroupsEntity.CardgroupsBean> data, int position, BaseAdapter adapter) {
//        super.setData(data, position, adapter);
//        mBaseAdapter = adapter;
//        mCardgroupsBeans = data;
//    }
//
//    @Override
//    public void setData(CardGroupsEntity.CardgroupsBean data, int position) {
//        if (ll.getChildCount() > 0) ll.removeAllViews();
//        if (data == null || data.getCards() == null || data.getCards().size() < 1) {
//            return;
//        }
//        CardGroupsEntity.CardgroupsBean.CardsBean cardsBean = data.getCards().get(0);
//
//        if (TextUtils.isEmpty(cardsBean.getPhoto().getThumb())) {
//            ll.addView(new CardGroups3007x(mContext, data, position, mAudioViewUtils, mCardgroupsBeans, mBaseAdapter));
//        } else {
//            ll.addView(new CardGroups3004x(mContext, data, position, mAudioViewUtils, mCardgroupsBeans, mBaseAdapter));
//        }
//    }

}
