package com.ctvit.shitingxizang.base;

import android.content.Context;
import android.graphics.drawable.Drawable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * Created by Administrator on 2016/12/16.
 * 使用ViewHolder并且需要使用ViewType可以使用该类
 * 可以理解成一个view 比如Card类可以继承该类
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    protected Context mContext;
    protected View mItemView;
    public ImageView imageView;

    public BaseViewHolder(Context context, ViewGroup parent, int layoutId) {
        super(LayoutInflater.from(context).inflate(layoutId, parent, false));
        mContext = context;
        mItemView = itemView;
        findView(itemView);
    }

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void findView(View itemView);

    public void setData(T data) {

    }

    public void setData(T data, int position) {

    }

    public void setData(List<T> data, int position, BaseAdapter adapter) {

    }

    public void setData(T data, int position, boolean flag, boolean allFlag){

    }

    public void setData(T data, int position, List<Object> payloads){

    }

    public void getImg (){}

    public void setImageView (Drawable resource){}
}
