package com.cctv.cctvplayer.entity;

/**
 * 码率信息实体
 */

public class CCTVRateEntity {
    private String playURL; //播放地址
    private String rateName; //码率名称
    private boolean isDefaultRate; //是否默认

    public String getPlayURL() {
        return playURL;
    }

    public void setPlayURL(String playURL) {
        this.playURL = playURL;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public boolean isDefaultRate() {
        return isDefaultRate;
    }

    public void setDefaultRate(boolean defaultRate) {
        isDefaultRate = defaultRate;
    }
}
