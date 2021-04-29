package com.ctvit.shitingxizang.utils;


import com.alibaba.fastjson.JSON;
import com.ctvit.shitingxizang.cardgroups.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2016/12/22.
 * 卡片组相关辅助类
 */

public final class CardGroupsUtils {

    /**
     * 搜索列表页的参数
     *
     * @param keyword    检索的关键词
     * @param channel    检索的频道号
     * @param searchType 搜索类型（图文、视频、图集、综合）...
     * @param pageNo     要查询的页号 不可为空
     * @param lastId     当前请求到数据最后一条的ID，如果不传有可能会丢数据。可为空
     */

    /**
     * 生成请求参数
     */
    public static Map<String, String> getCardGroupsParams(String cardgroups, int page_size, int page_no, String last_id) {
        Map<String, String> params = new HashMap<>();
        com.alibaba.fastjson.JSONObject root = new com.alibaba.fastjson.JSONObject();
//        root.put("version", "2.10.3");
        root.put("cardgroups", cardgroups);
        com.alibaba.fastjson.JSONObject paging = new com.alibaba.fastjson.JSONObject();
        paging.put("page_size", page_size);
        paging.put("page_no", page_no);
        paging.put("last_id", last_id);
        root.put("paging", paging);
        params.put("json", JSON.toJSONString(root));

        return params;
    }


    public static String getParamsBySearch(String keyword, String channel, String searchType, int pageNo, String lastId) {
        JSONObject root = new JSONObject();
        try {
            JSONObject paging = new JSONObject();
            paging.put("page_size", Constant.Paging.PAGE_SIZE);
            paging.put("page_no", pageNo);
            paging.put("last_id", lastId);
            root.put("paging", paging);
            root.put("keyword", keyword);
            root.put("channel", channel);
            root.put("deviceid", DeviceUtils.getDeviceId());
            root.put("searchtype", searchType);
        } catch (JSONException e) {
            LogUtils.e(e);
        }
        String data = root.toString();
        LogUtils.i("请求参数：" + data);
        return data;
    }


    /**
     * 生成请求参数。不带分页
     *
     * @param type  cardGroups / videolive
     * @param value link里的ID
     */
    public static String getParams(String type, String value) {
        JSONObject root = new JSONObject();
        try {
            root.put(type, value);
        } catch (JSONException e) {
            LogUtils.e(e);
        }
        String data = root.toString();
        LogUtils.i("请求参数：" + data);
        return data;
    }

    /**
     * 生成请求参数。带分页
     *
     * @param type     cardGroups / videolive
     * @param value    link里的ID
     * @param pageSize 每页条数 不可为空
     * @param pageNo   要查询的页号 不可为空
     * @param lastId   当前请求到数据最后一条的ID，如果不传有可能会丢数据。可为空
     */
    public static String getParams(String type, String value, int pageSize,
                                   int pageNo, String lastId) {
        JSONObject root = new JSONObject();
        try {
            JSONObject paging = new JSONObject();
            paging.put("page_size", pageSize);
            paging.put("page_no", pageNo);
            paging.put("last_id", lastId);
            root.put("paging", paging);
            root.put(type, value);
        } catch (JSONException e) {
            LogUtils.e(e);
        }
        String data = root.toString();
        LogUtils.i("请求参数：" + data);
        return data;
    }

    /**
     * link 转换 ID
     */
    public static String linkToId(String link) {
        if (link.isEmpty() || link.startsWith("http"))
            return "";
        if (link.contains("://")) {
            return link.substring(link.indexOf("://") + 3);
        } else {
            return link;
        }
    }

    /**
     * 从卡片组接口的请求参数里获取ID
     */
    public static String paramsToId(String key, String params) {
        String tempName = "_temp";
        if (params.isEmpty())
            return tempName;
        try {
            JSONObject json = new JSONObject(params);
            return json.optString(key, tempName);
        } catch (JSONException e) {
            LogUtils.e(e);
            return tempName;
        }
    }


}
