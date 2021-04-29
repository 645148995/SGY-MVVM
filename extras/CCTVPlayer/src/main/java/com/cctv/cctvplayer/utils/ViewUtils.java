package com.cctv.cctvplayer.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 *
 */

public final class ViewUtils {

    /**
     * View设置margin
     */
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
