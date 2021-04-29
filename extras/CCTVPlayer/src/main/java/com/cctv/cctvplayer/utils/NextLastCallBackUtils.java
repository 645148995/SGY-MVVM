package com.cctv.cctvplayer.utils;

import com.cctv.cctvplayer.listener.NextLastCallBack;


public class NextLastCallBackUtils {

    private static NextLastCallBack mCallBack;

    public static void setCallBack(NextLastCallBack callBack) {
        mCallBack = callBack;
    }

    public static NextLastCallBack getCallBack(){
        return mCallBack;
    }
    /**
     * 下一首
     */
    public static void doNextCallBackMethod(){
        mCallBack.nextOne();
    }
    public static void doDelNextCallBackMethod(){
        mCallBack.nextDelOne();
    }

    /**
     * 上一首
     */
    public static void doLastCallBackMethod(){
        mCallBack.LastOne();
    }

    /**
     * 展示播放列表
     */
    public static void doListCallBackMethod(){
        mCallBack.listShow();
    }

    /**
     * 切换歌曲
     */
    public static void doStartNewSongCallBackMethod(){
        mCallBack.newSong();
    }

    /**
     * 根据播放模式切换歌曲
     */
    public static void doNewSongTypeCallBackMethod(int i){
        mCallBack.playType(i);
    }

    /**
     * 播放列表没有歌曲时候请求推荐
     */
    public static void doClearData(){
        mCallBack.getClearData();
    }

    /**
     * 播放列表没有歌曲时候请求推荐
     */
    public static void doDelPlayIng(int pos){
        mCallBack.delPlayIng(pos);
    }

    /**
     * 打开简介
     */
    public static void doOpenBrief (){
        mCallBack.openBrief();
    }

    /**
     * 下载
     */
    public static void doDownload(){
        mCallBack.dowmLoad();
    }


    /**
     * 回收稿件
     */
    public static void doNoData(){
        mCallBack.noData();
    }
}
