package com.cctv.cctvplayer.entity;

import java.util.List;
import java.util.Map;

/**
 * 视频播放信息实体类
 */

public class CCTVMainPlayEntity {
    private String link; //唯一标识
    private String title; //标题
    private boolean isLive;//是否是直播
    private boolean isHorizontal;//是否是横屏
    private Map<String, String> headers; //播放时的请求头
    private CCTVSubPlayEntity subPlayEntity;
    private List<CCTVStreamEntity> codeRateList;
    private long liveStartTime; //直播开始时间
    private long liveEndTime; //直播结束时间
    private String liveFormat;// CMS的Config接口来控制，最新直播，播放哪一种格式
    private String if4k;//是否是4K
    private Object mObjectCustom;//不可能所有的信息都写在这个实体类里，所以这里弄了个Object

    /**
     * 获得当前播放码率实体
     */
    public CCTVStreamEntity getCurPlayCodeRate() {
        if (getCodeRateList() == null || getCodeRateList().isEmpty())
            return null;

        for (CCTVStreamEntity rate : getCodeRateList()) {
            if (rate.isDefault())
                return rate;
        }
        getCodeRateList().get(0).setDefault(true);
        return getCodeRateList().get(0);
    }

    /**
     * 获得当前播放地址
     *
     * @param defaultUrl 点播、直播时移传true
     */
    public String getCurPlayURL(boolean defaultUrl) {
        if (getCurPlayCodeRate() == null)
            return null;

        if (defaultUrl || !isLive()) //时移和点播获取默认的地址
            return getCurPlayCodeRate().getPlayURL();

        if ("flv".equals(getLiveFormat()))
            return getCurPlayCodeRate().getFlvURL();
        else
            return getCurPlayCodeRate().getPlayURL();
    }

    /**
     * 获得当前播放码率名称
     */
    public String getCurPlayRateName() {
        if (getCurPlayCodeRate() == null)
            return null;

        return getCurPlayCodeRate().getName();
    }

    public CCTVSubPlayEntity getSubPlayEntity() {
        return subPlayEntity;
    }

    public void setSubPlayEntity(CCTVSubPlayEntity subPlayEntity) {
        this.subPlayEntity = subPlayEntity;
    }

    public List<CCTVStreamEntity> getCodeRateList() {
        return codeRateList;
    }

    public void setCodeRateList(List<CCTVStreamEntity> codeRateList) {
        this.codeRateList = codeRateList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 是直播，默认点播
     */
    public boolean isLive() {
        return isLive;
    }

    /**
     * 是直播，默认点播
     */
    public void setLive(boolean live) {
        isLive = live;
    }

    /**
     * 是否横屏，默认小屏
     */
    public boolean isHorizontal() {
        return isHorizontal;
    }

    /**
     * 是否横屏，默认小屏
     */
    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    /**
     * 播放时的请求头
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * 播放时的请求头
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public long getLiveStartTime() {
        return liveStartTime;
    }

    /**
     * 直播开始时间
     */
    public void setLiveStartTime(long liveStartTime) {
        this.liveStartTime = liveStartTime;
    }

    public long getLiveEndTime() {
        return liveEndTime;
    }

    /**
     * 直播结束时间
     */
    public void setLiveEndTime(long liveEndTime) {
        this.liveEndTime = liveEndTime;
    }

    public String getLiveFormat() {
        if (liveFormat == null)
            return "";
        return liveFormat.toLowerCase();
    }

    public void setLiveFormat(String liveFormat) {
        this.liveFormat = liveFormat;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Object getObjectCustom() {
        return mObjectCustom;
    }

    /**
     * 不可能所有的信息都写在这个实体类里，所以这里弄了个Object
     */
    public void setObjectCustom(Object object) {
        this.mObjectCustom = object;
    }

    public String getIf4k() {
        return if4k;
    }

    public void setIf4k(String if4k) {
        this.if4k = if4k;
    }

}
