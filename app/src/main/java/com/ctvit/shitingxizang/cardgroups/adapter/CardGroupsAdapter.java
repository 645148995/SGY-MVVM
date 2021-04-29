package com.ctvit.shitingxizang.cardgroups.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.ctvit.shitingxizang.base.BaseAdapter;
import com.ctvit.shitingxizang.base.BaseViewHolder;
import com.ctvit.shitingxizang.cardgroups.Constant;
import com.ctvit.shitingxizang.cardgroups.model.CardGroups3004;

import me.goldze.mvvmhabit.utils.KLog;


/**
 * Created by Administrator on 2016/12/19.
 *
 * @see BaseAdapter
 */

public class CardGroupsAdapter extends BaseAdapter {
    //来源，根据不同的来源来处理数据
    private String isfullScreenWidth;  //用来卡片组宽度是否全屏显示


    public CardGroupsAdapter(Context context, String isfullScreenWidth) {
        super(context);
    }

    /**
     * @param viewType 页面类型 这里对应card的类型 比如card1那就是case1
     *                 此处可以实例化card 比如Card1 card1 = new Card1(Context context,ViewGroup parent);
     * @see BaseViewHolder 因为card需要继承该类 所以本质上card是一个ViewHolder
     */
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        KLog.i("卡片序号：" + viewType);
        switch (viewType) {

            case Constant.CardStyle.N_3044:
//                return new CardGroups3044(context, parent);
            default:
                return new CardGroups3004(context, parent);

        }
//        return super.onCreateViewHolder(parent, viewType);
//        return new CardGroupsXiZangDefault(context, parent,1);
    }


}
