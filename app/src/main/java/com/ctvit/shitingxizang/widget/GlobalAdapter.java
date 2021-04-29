package com.ctvit.shitingxizang.widget;

import android.view.View;

import com.billy.android.loading.Gloading;

/*
   项目名：CtvitXiZang
   包名:   com.ctvit.ctvitxizang.widget
   创建者：孙光远
   创建时间：2020/7/23 14:49
 */
public class GlobalAdapter implements Gloading.Adapter  {
    @Override
    public View getView(Gloading.Holder holder, View convertView, int status) {
        GlobalLoadingStatusView loadingStatusView = null;
        //convertView为可重用的布局
        //Holder中缓存了各状态下对应的View
        //	如果status对应的View为null，则convertView为上一个状态的View
        //	如果上一个状态的View也为null，则convertView为null
        if (convertView != null && convertView instanceof GlobalLoadingStatusView) {
            loadingStatusView = (GlobalLoadingStatusView) convertView;
        }
        if (loadingStatusView == null) {
            loadingStatusView = new GlobalLoadingStatusView(holder.getContext(), holder.getRetryTask());
        }
        loadingStatusView.setStatus(status);
        return loadingStatusView;
    }


}