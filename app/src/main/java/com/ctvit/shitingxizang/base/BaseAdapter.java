package com.ctvit.shitingxizang.base;

import android.content.Context;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import com.ctvit.shitingxizang.cardgroups.model.CardGroupsDefault;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;


/**
 * Created by Administrator on 2016/12/16.
 * 适用于RecyclerView
 */

public class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    protected Context context;
    private List<BaseViewType> dataList;
    private boolean flag, allFlag;

    public BaseAdapter(Context context) {
        this.context = context;
    }

    public void addData(List<BaseViewType> list) {
        KLog.i("卡片序号4："+list.toString());
        getDataList().addAll(list);
    }

    public void addData(BaseViewType model) {
        List<BaseViewType> list = new ArrayList<>();
        list.add(model);
        addData(list);
    }

    //显示CheckBox
    public void setFlag(boolean mFlag){
        this.flag = mFlag;
    }

    //全选
    public void seleclectAll(boolean flag){
        this.allFlag = flag;
    }

    public void clean() {
        getDataList().clear();
    }

    public List<BaseViewType> getDataList() {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        return dataList;
    }

    @Override
    public int getItemViewType(int position) {
        KLog.i("卡片序号3："+getDataList().get(position).getViewType());
        return getDataList().get(position).getViewType();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        KLog.i("卡片序号0：" + viewType);
        return new CardGroupsDefault(context, parent);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        KLog.i("卡片序号1：" );
        holder.setData(getDataList(), position, this);
        holder.setData(getDataList().get(position));
        holder.setData(getDataList().get(position), position);
        holder.setData(getDataList().get(position), position, flag, allFlag);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        }else{
            //暂时用于刷新1001音频进度条
            holder.setData(getDataList().get(position), position, payloads);
        }
    }

    @Override
    public int getItemCount() {
        KLog.i("卡片序号2："+getDataList().size());
        return getDataList().size();
    }

}
