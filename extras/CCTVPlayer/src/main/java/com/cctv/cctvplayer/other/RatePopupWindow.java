package com.cctv.cctvplayer.other;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * 播放器码率弹窗
 * Created by PC on 2018/12/11.
 */

public class RatePopupWindow extends PopupWindow {

    public RatePopupWindow(Context context, View contentView) {
        super(context);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
    }
}
