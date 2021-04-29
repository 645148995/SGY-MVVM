package com.ctvit.shitingxizang.cardgroups;

/**
 * 全局常量类（Android中不推荐使用枚举，说是相比常量占用内存，那就用常量吧，模仿R.java的C.java，用起来也不会太差）
 */
public final class Constant {


    /**
     * LiveDataBus的常量定义
     */
    public static final class LiveDataBus {
        public static final String TABAUTOREFRESH = "TAB_AUTOREFRESH"; // 直播

    }
    /**
     * 分页相关配置
     */
    public static final class Paging {
        /**
         * 每页20条数据
         */
        public static final int PAGE_SIZE = 20;
    }
    // intent extra
    public static final String SWITCH_NIGHT_MODE = "switch_night_mode";
    public static final String EXTRA_PAGE_ID = "extra_page_id";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_LINK = "extra_link";
    public static final String EXTRA_VIDEOID = "extra_videoid";
    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_SCODE = "extra_stock_code";
    public static final String EXTRA_ORDER_ID = "extra_order_id";
    public static final String EXTRA_ORDER_TITLE = "extra_order_title";
    public static final String EXTRA_GUIDE = "extra_guide";

    // sharedPreferences key
    public static final String NIGHT_MODE_ON = "night_mode_on";
    public static final String HOME_PAGE_ID = "home_page_id";
    public static final String NAV_FIRST = "nav_first";
    public static final String NAV_SECOND = "nav_second";
    public static final String STOCK_JSON = "stock_json";
    public static final String FONT_SIZE = "font_size";
    public static final String FORWARD_COLLECTION = "forward_collection";
    public static final String AUTOPLAY_SETTINGS = "autoplay_settings";
    public static final String APP_AGREE_PROTOCOL = "app_agree_protocol";
    public static final String APP_FIRST_INSTALL = "app_first_install";
    public static final String GUIDE_HOME = "guide_home";
    public static final String GUIDE_315 = "guide_315";
    public static final String GUIDE_VERTICAL_VIDEO = "guide_vertical_video";
    public static final String LOGIN_NICKNAMA = "login_nickname";
    public static final String LOGIN_USERLOGO = "login_logo";
    public static final String LIVE_SUBTITL_LINK = "live_subtitle_link";
    public static final String SERVER_TIME = "server_time";



    /**已选中频道的json*/
    public static final String SELECTED_CHANNEL_JSON = "selectedChannelJson";
    /**w未选频道的json*/
    public static final String UNSELECTED_CHANNEL_JSON = "unselectChannelJson";

    /**频道对应的请求参数*/
    public static final String CHANNEL_CODE = "channelCode";
    public static final String IS_VIDEO_LIST = "isVideoList";

    public static final String ARTICLE_GENRE_VIDEO = "video";
    public static final String  ARTICLE_GENRE_AD = "ad";

    public static final String TAG_MOVIE = "video_movie";

    public static final String URL_VIDEO = "/video/urls/v/1/toutiao/mp4/%s?r=%s";

    /**获取评论列表每页的数目*/
    public static final int COMMENT_PAGE_SIZE = 20;

    public static final String DATA_SELECTED = "dataSelected";
    public static final String DATA_UNSELECTED = "dataUnselected";




    /**
     *
     */
    public static final String DLNA_DEVICE_NAME = "DLNA_DEVICE_NAME";
    /**
     * 直播状态
     */
    public static final class LiveStatus {
        //直播中
        public static final String LIVE_START = "正在直播";

        //已结束
        public static final String LIVE_END = "已结束";

        //未开始
        public static final String LIVE_NOT = "未开始";
    }
    /**
     * 采集数据-页面类型定义
     */
    public static final class CtvitAgentType {
        public static final String LIVE = "live"; // 直播
        public static final String VOD = "vod"; // 点播
        public static final String VR_LIVE = "vrLive"; // VR 直播
        public static final String VR_VOD = "vrVod"; // VR 点播
        public static final String PHOTO_ALBUM = "photoAlbum"; // 图集
        public static final String PHOTO_ARTICLE = "photoArticle"; // 图文
        public static final String LIVE_ON_VOD = "liveOnVod";// 直播页面播放点播视频
        public static final String VOD_ON_LIVE = "vodOnLive";// 点播页面播放直播视频（目前没有...）
        public static final String H5 = "H5";// HTML页面
    }
    /**
     * 卡片序号（包含了自定义的和接口定义的）
     * 因为有的卡片需要特别判断，直接在程序里写数字不太好，所以在这里配置下
     */
    public static final class CardStyle {
        //轮播图
        public static final int N_256 = 256;
        //大图
        public static final int N_261 = 261;

        //-----------------------------以上是示例，以下是财经--------------------------------
        //轮播图
        public static final int N_3001 = 3001;
        //文 - 更多
        public static final int N_3002 = 3002;
        //横向列表 - 文字（股票）
        public static final int N_3003 = 3003;
        //左文右图
        public static final int N_3004 = 3004;
        //广告，后改成轮播形式
        public static final int N_3005 = 3005;
        //标题 + 横向三图
        public static final int N_3006 = 3006;
        //标题
        public static final int N_3007 = 3007;
        //上标题 + 下大图
        public static final int N_3008 = 3008;
        //上标题 + 下大图 （广告）
        public static final int N_3009 = 3009;
        //同3002（直播频道 - 更多）
        public static final int N_3010 = 3010;
        //横向列表 - 视频
        public static final int N_3011 = 3011;
        //小视频
        public static final int N_3012 = 3012;
        //VR
        public static final int N_3015 = 3015;
        //横向列表 - 二级导航跳转
        public static final int N_3016 = 3016;
        //横向列表 - 权威发布
        public static final int N_3027 = 3027;
        //分割线
        public static final int N_3017 = 3017;
        //直播预告
        public static final int N_3019 = 3019;
        //直播
        public static final int N_3020 = 3020;

        //        CCTV2
        public static final int N_3018 = 3018; //CCTV2两行两列card
        public static final int N_3021 = 3021; //CCTV2列表标题
        public static final int N_3023 = 3023;// CCTV2横向滑动布局
        public static final int N_3245 = 3245;//首页播放块
        public static final int N_3246 = 3246; //首页界面单
        //文 - 315首页入口
        public static final int N_3022 = 3022;
        //7x24 - 文
        public static final int N_3024 = 3024;
        //7x24 - 图文
        public static final int N_3025 = 3025;
        //横图
        public static final int N_3026 = 3026;

        //cctv2搜索视频集
        public static final int N_3028 = 3028;
        //cctv2搜索节目条
        public static final int N_3029 = 3029;
        //cctv2搜索查看更多
        public static final int N_3030 = 3030;
        //左图右文
        public static final int N_3031 = 3031;
        //专题
        public static final int N_3032 = 3032;

        //财经频道左图右文
        public static final int N_3033 = 3033;
        //上图下文
        public static final int N_3040 = 3040;
        //轮播 - 标题 + 大图
        public static final int N_3041 = 3041;
        //轮播 - 广告
        public static final int N_3042 = 3042;
        //大图 - 广告
        public static final int N_3043 = 3043;
        //大视频 - 广告
        public static final int N_3044 = 3044;

        //----------本地自定义类型---------------------------

        //经济之声交通广播
        public static final int N_1001 = 1001;
        //竖屏播放页
        public static final int N_1002 = 1002;
        //竖屏播放页评论列表
        public static final int N_1003 = 1003;
        //历史记录 今天 更早
        public static final int N_03030 = 03030;

    }



    /**
     * 卡片组的link 以app开头的
     */
    public static final class LinkApp {
        //专题列表
//        public static final String PAGE = "app://PAGE";

        //图文底层（包含了专题列表app://PAGE）
        public static final String BASE = "app://";

        //图文底层
        public static final String ARTI = "app://ARTI";

        //视频底层
        public static final String VIDE = "app://VIDE";

        //视频集（样式1-播放单显示标题）
        public static final String VIDA = "app://VIDA";

        //视频集（样式2-播放单显示集数）
        public static final String VIDEOALBUM1 = "videoalbum1://";

        //图集
        public static final String PHOA = "app://PHOA";

        //视频直播底层
        public static final String VIDEOLIVE = "videolive://";

        //微信
        public static final String WEIXIN = "weixin://";

        //VR视频
        public static final String VR = "app://VR";

        //VR点播
        public static final String VRDE = "app://VRDE";

        //音频底层
        public static final String MUSIC = "app://AUDI";

        //音频集底层
        public static final String MUSIC_COllECTIONS = "app://AUDA";

        //竖屏直播
        public static final String VERTICAL_LIVE = "verticalvideolive://";

        //竖屏点播
        public static final String VERTICAL_VOD = "verticalapp://";
    }

    /**
     * 从哪个页面跳转进来的
     */
    public static final String FORWARD_SOURCE = "FORWARD_SOURCE";

    /**
     * app来源
     */
    public static final String APP_SOURCE = "14";
    /**
     * 我的设置常量
     */
    public static final class SetC {
        //对应"消息推送"，true为打开，false为关闭
        public static final String NEWS_PUSH = "NEWS_PUSH";
        //对应"仅wifi下自动播放"，true为打开，false为关闭
        public static final String AUTO_PLAY = "AUTO_PLAY";
        //对应“弹幕”，true为打开，false为关闭
        public static final String DANMU_ON = "AUTO_PLAY";
        //对应"下载功能-音频"，true为打开，false为关闭
        public static final String AUTO_DOWNLOAD_AUDIO = "AUTO_DOWNLOAD_AUDIO";
        //对应"下载功能-视频"，true为打开，false为关闭
        public static final String AUTO_DOWNLOAD_VIDEO = "AUTO_DOWNLOAD_VIDEO";
    }
}
