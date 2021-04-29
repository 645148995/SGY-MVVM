package com.cctv.cctvplayer.entity;

import android.text.TextUtils;

/**
 * 播放地址实体
 */

public class CCTVStreamEntity {
    private String flvURL; //Flv播放地址
    private String playURL; //常规播放地址
    private String name; //流名称
    private boolean isDefault; //是否默认

    public String getFlvURL() {
        if (TextUtils.isEmpty(flvURL))
            return getPlayURL();
        return flvURL;
    }

    public void setFlvURL(String flvURL) {
        this.flvURL = flvURL;
    }

    public String getPlayURL() {
        return playURL;
    }

    public void setPlayURL(String playURL) {
        this.playURL = playURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
