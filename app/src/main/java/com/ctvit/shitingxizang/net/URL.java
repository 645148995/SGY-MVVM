package com.ctvit.shitingxizang.net;


import com.ctvit.shitingxizang.utils.RetrofitClient;

/**
 * 接口地址在这里配置
 */
public final class URL {

    /**
     * 固定参数值
     */
    public static final class PARAM {

        public static final String HOME = "INDEX";  //首页要闻
        public static final String NAV = "NAV";     //下导航
        public static final String HOUR = "HOUR";   //7x24小时
        public static final String LIVE = "LIVE";   //直播导航

    }

    /**
     * APP更新
     */
    public static final String APP_UPDATE = "https://vod.cctv.cn/apk/cctvapp/updateVersion-caijing.json";

    /**
     * 防盗链
     */
    public static final String BASE_URL_ENCRYPTING = "https://vdn.cctv.cn/cctvmobileinf/rest/cctv/";

    /**
     * CMS系统
     */
    public static final class CMS {

        public static final String BASE = RetrofitClient.BASE_URL_CMS;

        /**
         * 闪屏
         */
        public static final String SPLASH = BASE + "financemobileinf/rest/cctv/splash/update";

        /**
         * 服务器时间
         */
        public static final String SERVER_TIME_URL = BASE + "financemobileinf/rest/cctv/epg/time";

        /**
         * 投屏 - 防盗链接口
         */
        public static final String ENCRYPTING_DLNA = BASE_URL_ENCRYPTING + "videoliveUrl/getscreenstream";

        /**
         * 防盗链接口
         */
        public static final String ENCRYPTING =  BASE_URL_ENCRYPTING +  "videoliveUrl/getstream";

        /**
         * 数据采集 - 正常数据
         */
        public static final String CTVIT_AGENT = "https://collect.cctv.cn/" + "cctvmobileinf/rest/cctv/receive/app";

        /**
         * 数据采集 - 错误数据
         */
        public static final String CTVIT_AGENT_ERROR = "https://collect.cctv.cn/" + "cctvmobileinf/rest/cctv/receive/error";

    }

    /**
     * 互动
     */
    public static final class Interaction {

        //线上地址
        public static final String BASE_USER = RetrofitClient.BASE_URL_CMS + "cctv2/";

        /**
         * 首页获取315投诉列表
         */
        public static final String  COMPLAINT = BASE_USER + "complaintApi.php/Complaint/getComplaintList";

        /**
         * 获取用户信息
         */
        public static final String GET_INFO = "userApi.php/User/userGetInfo";

        /**
         * 用户反馈
         */
        public static final String USER_REPORT = "commentApi.php/Feedback/addFeedback";

        /**
         * 点赞
         */
        public static final String CLICK_PRAISE = "commentApi.php/Feedback/addZan";

        /**
         * 获取点赞状态
         */
        public static final String GET_PRAISE_STATUS = "commentApi.php/Feedback/zanStatus";

        /**
         * 评论列表
         */
        public static final String COMMENT_LIST = "commentApi.php/Comment/getCommentList";

        /**
         * 获取点赞、评论、分享数量
         */
        public static final String VOTE_LIST = "commentApi.php/Feedback/getVoteList";

        /**
         * 添加评论
         */
        public static final String COMMENT_ADD = "commentApi.php/Comment/addComment";
        /**
         * 收藏列表
         */
        public static final String COLLECTION_DATA = "collectApi.php/Collect/getList";

        /**
         * 添加收藏
         */
        public static final String SHOUCANG_DATA = "collectApi.php/Collect/addCollect";

        /**
         * 获取收藏状态
         */
        public static final String SHOUCANG_STAUTE = "collectApi.php/Collect/getCollectStatus";

        /**
         * 删除收藏
         */
        public static final String DELETE_DATA = "collectApi.php/Collect/delCollect";

        /**
         * 积分分享
         */
        public static final String INTEGRAL_SHARE_URL = "commentApi.php/Feedback/addShare";

        /**
         * 退出登录
         */
        public static final String EDIT_USER_URL = "userApi.php/user/userExit";

        /**
         * 用户更新access_token接口
         */
        public static final String  CHANGE_ACCESS_TOKEN_URL= "userApi.php/User/userRefreshToken";

    }


    /**
     * Html页面
     */
    public static final class H5 {

        private static final String BASE = RetrofitClient.BASE_URL_CMS;

        public static final String TU_WEN = BASE + "detail/msgdetail.html";   //图文

        public static final String DIAN_BO = BASE + "detail/playdetail.html";   //点播

        public static final String ZHI_BO = BASE + "detail/livemsg.html";   //直播

        public static final String HUI_KAN = BASE + "detail/videoPlay.html";   //回看

        public static final String _315_PLAT = BASE + "complaint/inforList.html";   //315平台 - 列表

        public static final String _315_TOU_SU = BASE + "complaint/inforComplaint.html?isnative=1";   //315平台 - 投诉

        public static final String STOCK_LIST = BASE + "stock/stocklist.html";   //股票 - 列表

        public static final String STOCK_DETAIL = BASE + "stock/stockdetail.html?codeId=";   //股票 - 详情

        public static final String HELP = BASE + "detail/help.html";   //使用帮助

        public static final String PROTOCOL_CLIENT = BASE + "detail/appAgree.html";   //客户端协议
//        public static final String PROTOCOL_CLIENT = "file:///android_asset/appAgree.html";   //客户端协议

        public static final String PROTOCOL_USER = BASE + "detail/agreement.html";   //用户协议

        public static final String PROTOCOL_PRIVACY = BASE + "detail/privacy.html";   //隐私条款

        /**
         * 分享默认logo图
         */
        public static final String SHARE_DEFAULT_IMG = BASE + "detail/img/defaulticon.png";

        /**
         * 分享前缀url
         */
        public static final String SHARE_PREFIX_BASE_URL = BASE + "finance/index.html?pageId=";
    }

    /**
     * 广告返回字段
     */
    public static class AD {
        /**
         * 特殊分隔符-拼接url上
         */
        public static final String SPLIT_STR1 = "$original$";

        /**
         * 特殊分隔符-split分割
         */
        public static final String SPLIT_STR2 = "\\$original\\$";

        /**
         * 广告id
         */
        public static final String AD_ID = "ad_id";

        /**
         * click_url 就是跳转链接（同时也是监控链接）
         */
        public static final String CLICK_URL = "click_url";

        /**
         * 创意类型值
         */
        public static final String CREATIVETYPE = "creativeType";

        /**
         * 创意类型id
         */
        public static final String CREATIVEID = "creativeId";

        /**
         * fifa  前贴片广告是否跳过标识
         * fifa值是true的 不能跳过 页面上也不需要保留跳过按钮
         * 没有这个字段或者值不是true  就是正常的前贴片 5秒后可以跳过
         * <p>
         * 字符串类型的true或者false
         */
        public static final String FIFA = "fifa";

        /**
         * adviseid
         */
        public static final String ADVISEID = "adviseId";

        /**
         * 曝光url
         */
        public static final String IMP_URL = "imp_url";

        /**
         * 通过广告位id，获取的json
         */
        public static final String ADJSON = "ad_json";

        /**
         * 三方曝光地址
         */
        public static final String THIRD_PV_URL = "thirdparty_pv_monitor_urls";

        /**
         * 三方点击地址
         */
        public static final String THIRD_CK_URL = "thirdparty_ck_monitor_urls";

        /**
         * 时长
         */
        public static final String DURATION = "duration";

        /**
         * 图片或者视频url
         */
        public static final String AD_URL = "ad_url";

        /**
         * is_dest为1时 click_url 就是跳转链接
         */
        public static final String IS_DEST = "is_dest";

        /**
         * ifjump 能否跳过 0 可跳过 1 不可跳过
         */
        public static final String IF_JUMP = "ifjump";

        /**
         * 广告操作类型-加载
         */
        public static final String LOAD = "0";

        /**
         * 广告操作类型-跳转
         */
        public static final String CLICK = "1";
    }

}
