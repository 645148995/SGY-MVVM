package com.cctv.cctvplayer.entity;

import com.easefun.povplayer.core.config.PolyvPlayOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 子播放器信息实体。片头广告、暖场视频（仅直播有效）、片尾广告（仅点播有效）的设置
 */

public class CCTVSubPlayEntity {
    private List<PolyvPlayOption.HeadAdOption> headadADList = new ArrayList<>(5); //播放器的实体片头广告
    private Map<String, CCTVSubPlayHeadadAD> headadADInfoMap = new HashMap(5); //片头广告
    private String teaserADPlayURL; //暖场视频（仅直播有效）
    private String tailadADPlayURL; //片尾广告（仅点播有效）
    private int tailadADDuration = -1;//片尾广告时长，小于等于0或者大于视频时长时用视频的时长

    /**
     * 获取片头广告集合
     */
    public List<PolyvPlayOption.HeadAdOption> getHeadadADList() {
        return headadADList;
    }

    /**
     * 添加片头广告
     */
    public void addHeadadAD(CCTVSubPlayHeadadAD headadAD) {
        if (headadAD == null)
            return;
        headadADList.add(new PolyvPlayOption.HeadAdOption(headadAD.getHeadadADPlayURL(), headadAD.getHeadadADDuration()));
        headadADInfoMap.put(headadAD.getHeadadADPlayURL(), headadAD);
    }

    /**
     * @param curUrl 当前播放的地址
     * @return 广告信息
     */
    public CCTVSubPlayHeadadAD getHeadadADInfo(String curUrl) {
        return headadADInfoMap.get(curUrl);
    }

    /**
     * 暖场视频（仅直播有效）
     */
    public String getTeaserADPlayURL() {
        return teaserADPlayURL;
    }

    /**
     * 暖场视频（仅直播有效）
     */
    public void setTeaserADPlayURL(String teaserADPlayURL) {
        this.teaserADPlayURL = teaserADPlayURL;
    }

    /**
     * 片尾广告（仅点播有效）
     */
    public String getTailadADPlayURL() {
        return tailadADPlayURL;
    }

    /**
     * 片尾广告（仅点播有效）
     */
    public void setTailadADPlayURL(String tailadADPlayURL) {
        this.tailadADPlayURL = tailadADPlayURL;
    }

    /**
     * 片尾广告时长，小于等于0或者大于视频时长时用视频的时长，默认-1
     */
    public int getTailadADDuration() {
        return tailadADDuration;
    }

    /**
     * 片尾广告时长，小于等于0或者大于视频时长时用视频的时长，默认-1
     */
    public void setTailadADDuration(int tailadADDuration) {
        this.tailadADDuration = tailadADDuration;
    }

    /**
     * 前贴片广告
     */
    public static class CCTVSubPlayHeadadAD {
        private String headadADPlayURL; //片头广告
        private int headadADDuration = -1;//片头广告时长，小于等于0或者大于视频时长时用视频的时长
        private boolean skip = true; //是否可以跳过， true可以
        private Object data; //自定义保存的信息

        public boolean isSkip() {
            return skip;
        }

        public void setSkip(boolean skip) {
            this.skip = skip;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        /**
         * 片头广告时长，小于等于0或者大于视频时长时用视频的时长，默认-1
         */
        public int getHeadadADDuration() {
            return headadADDuration;
        }

        /**
         * 片头广告时长，小于等于0或者大于视频时长时用视频的时长，默认-1
         */
        public void setHeadadADDuration(int headadADDuration) {
            this.headadADDuration = headadADDuration;
        }

        /**
         * 片头广告
         */
        public String getHeadadADPlayURL() {
            return headadADPlayURL;
        }

        /**
         * 片头广告
         */
        public void setHeadadADPlayURL(String headadADPlayURL) {
            this.headadADPlayURL = headadADPlayURL;
        }
    }
}