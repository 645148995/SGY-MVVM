package com.cctv.cctvplayer;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

import com.viewscene.js_native.JSaLiveBaseJNI;
import com.viewscene.transcoder.JSJNILib;
import com.viewscene.transcoder.Transcoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 视频管理单例类
 * 静态内部类实现
 * 第一次调用getInstance()方法的时候，虚拟机才会加载静态内部类
 */

public class CCTVVideoManager {
    //保存每个页面的播放器容器
    private Map<String, ViewGroup> mContainerMap = new HashMap<>(6);
    //保存每个页面，最后一个视频播放器的浮层Vieww
    private Map<String, View> mLayerMap = new HashMap<>(6);
    //保存每个页面，播放过的视频播放器的位置
    private Map<String, Integer> mPositionMap = new HashMap<>(6);
    //保存每个页面的播放器
    private Map<String, Object> mPlayerMap = new HashMap<>(6);

    //将构造函数私有化，不让直接实例化
    private CCTVVideoManager() {
    }

    public static CCTVVideoManager getInstance() {
        return SingleVideoManager.singleInstance;
    }

    //静态内部类
    public static class SingleVideoManager {
        private static final CCTVVideoManager singleInstance = new CCTVVideoManager();
    }

    /**
     * 保证每个页面，只有一个实例。这里目前只有列表可以用到。
     *
     * @param context 实例化CCTVVideoView
     */
    public Object getPlayerInstance(Context context) {
        Object obj = mPlayerMap.get(context.toString());
        if (obj != null)
            return obj;
        return null;
    }

    /**
     * 保证每个页面，只有一个实例。这里目前只有列表可以用到。
     *
     * @param context  实例化CCTVVideoView
     * @param instance 继承CCTVVideoView的类，或者是CCTVVideoView
     */
    public void putPlayerInstance(Context context, CCTVVideoView instance) {
        if (context == null || instance == null)
            return;
        mPlayerMap.put(context.toString(), instance);
    }

    /**
     * 初始化金石威视功能配置
     * 初始化一次就可以
     */
    public void initJSWSConfig() {
        JSaLiveBaseJNI.globalInit(999, 80, 80);
        Transcoder.globalInit("IjkPlayer-demo", "IjkPlayerDemo", 999, "", JSJNILib.STATS_ENABLED);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        String log_path = Environment.getExternalStorageDirectory().getPath();
        IjkMediaPlayer.jsglobal_init(log_path);
    }

    /**
     * 保存每个页面的播放器容器
     */
    public void putPlayerContainer(Context context, ViewGroup container) {
        if (context == null || container == null)
            return;
        String key = context.toString();
        mContainerMap.put(key, container);
    }

    /**
     * 保存每个页面，最后一个视频播放器的浮层View
     * 在addPlayer后面调用
     */
    public void putPlayLayerView(Context context, View layerView) {
        if (context == null || layerView == null)
            return;
        String key = context.toString();
        mLayerMap.put(key, layerView);
    }

    /**
     * 显示/隐藏每个页面，最后一个视频播放器的浮层View
     */
    public void showOrHiddenPlayLayerView(Context context, int visibility) {
        showOrHiddenPlayLayerView(context, visibility, true);
    }

    /**
     * 显示/隐藏每个页面，最后一个视频播放器的浮层View
     */
    public void showOrHiddenPlayLayerView(Context context, int visibility, boolean removeVideo) {
        if (context == null)
            return;
        String key = context.toString();
        View layerView = mLayerMap.get(key);
        if (layerView != null) {
            layerView.setVisibility(visibility);
            if (layerView.getTag() != null && layerView.getTag() instanceof ArrayList) {
                List<View> viewList = (ArrayList) layerView.getTag();
                for (int i = 0, count = viewList.size(); i < count; i++) {
                    View view = viewList.get(i);
                    view.setVisibility(visibility);
                }
            }
        }
        if (removeVideo)
            remove(context);
    }

    /**
     * 添加播放器
     *
     * @param container 存放视频View的容器View
     * @param videoView 视频View
     */
    public void addPlayer(ViewGroup container, CCTVVideoView videoView) {
        putPlayerContainer(container.getContext(), container);

        container.removeView(videoView);
        ViewGroup viewGroup = (ViewGroup) videoView.getParent();
        if (viewGroup != null)
            viewGroup.removeView(videoView);
        container.addView(videoView);
    }

    /**
     * 移除所有视频View
     */
    public void removeAll() {
        for (ViewGroup value : mContainerMap.values()) {
            remove(value.getContext());
        }
        mContainerMap.clear();
    }

    /**
     * 移除视频View
     */
    public void remove(Context context) {
        if (context == null)
            return;
        ViewGroup container = getContainer(context);
        if (container == null)
            return;
        pause(context); //按理说这应该调用controller.onDestroy()，但是有的机型onDestroy后会出现黑屏，所以这里就暂停吧
        IjkMediaPlayer.native_profileEnd();
        CCTVVideoView videoView = getVideoView(context);
        if (videoView != null) {
            videoView.onStop();
            container.removeView(videoView);
        }
    }

    /**
     * 释放视频
     */
    public void release(Context context) {
        if (context == null)
            return;
        ViewGroup container = getContainer(context);
        CCTVVideoMediaController controller = getMediaController(context);
        if (container == null || controller == null)
            return;
        controller.onDestroy();
        mContainerMap.remove(context.toString());
        mLayerMap.remove(context.toString());
        mPositionMap.remove(context.toString());
        mPlayerMap.remove(context.toString());
    }

    /**
     * 释放所有视频
     */
    public void releaseAll() {
        for (ViewGroup value : mContainerMap.values()) {
            release(value.getContext());
        }
        mContainerMap.clear();
    }

    /**
     * 播放视频
     */
    public void start(Context context) {
        if (context == null)
            return;
        CCTVVideoView videoView = getVideoView(context);
        if (videoView == null)
            return;
        videoView.getPlayerView().start();
    }

    /**
     * 暂停视频
     */
    public void pause(Context context) {
        if (context == null)
            return;
        CCTVVideoView videoView = getVideoView(context);
        if (videoView == null)
            return;
        videoView.getPlayerView().pause();
    }

    /**
     * 暂停所有视频
     */
    public void pauseAll() {
        for (ViewGroup value : mContainerMap.values()) {
            pause(value.getContext());
        }
    }

    /**
     * 获取当前页，最后一个播放的，控制器对象
     */
    public CCTVVideoMediaController getMediaController(Context context) {
        if (context == null)
            return null;
        CCTVVideoView videoView = getVideoView(context);
        if (videoView == null)
            return null;
        return videoView.getMediaController();
    }

    /**
     * 获取当前页，最后一个播放的，VideoView对象
     */
    public CCTVVideoView getVideoView(Context context) {
        if (context == null)
            return null;
        ViewGroup container = getContainer(context);
        if (container == null || container.getChildCount() < 1)
            return null;
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof CCTVVideoView)
                return (CCTVVideoView) view;
        }
        return null;
    }

    /**
     * 获取当前页，最后一个播放的，播放器容器
     */
    public ViewGroup getContainer(Context context) {
        if (context == null)
            return null;
        String key = context.toString();
        return mContainerMap.get(key);
    }

    /**
     * 检测当页面是否有，有效的视频播放器
     */
    public boolean isPagePlayer(Context context) {
        if (context == null || getMediaController(context) == null)
            return false;
        return true;
    }

    /**
     * 竖屏,会释放播放器，finish页面。横屏会返回竖屏。和Activity的生命周期onBackPressed对应
     */
    public void onBackPressed(Context context) {
        if (context == null)
            return;
        CCTVVideoMediaController controller = getMediaController(context);
        if (controller == null)
            return;
        controller.onBackPressed(false);
    }

    /**
     * 和Activity的生命周期onRestart对应
     */
    public void onRestart(Context context) {
        if (context == null)
            return;
        CCTVVideoMediaController controller = getMediaController(context);
        if (controller == null)
            return;
        controller.onRestart();
    }

    /**
     * 和Activity的生命周期onStop对应
     */
    public void onStop(Context context) {
        if (context == null)
            return;
        CCTVVideoMediaController controller = getMediaController(context);
        if (controller == null)
            return;
        controller.onStop();
    }

    /**
     * 释放播放器资源。和Activity的生命周期onDestroy对应
     */
    public void onDestroy(Context context) {
        if (context == null)
            return;
        ViewGroup container = getContainer(context);
        CCTVVideoMediaController controller = getMediaController(context);
        if (container == null || controller == null) {
            CCTVVideoView videoView = (CCTVVideoView) CCTVVideoManager.getInstance().getPlayerInstance(context);
            if (videoView != null && videoView.getDolbyHeadsetPlugReceiver() != null)
                videoView.getDolbyHeadsetPlugReceiver().unregisterReceiver();
            return;
        }
        controller.onDestroy();
        CCTVVideoView videoView = getVideoView(context);
        if (videoView != null)
            container.removeView(videoView);
        mContainerMap.remove(context.toString());
        mLayerMap.remove(context.toString());
        mPositionMap.remove(context.toString());
        mPlayerMap.remove(context.toString());
    }

    /**
     * 获取当前播放视频，最后一个播放视频的位置
     *
     * @return 小于0则没找到，或者没设置
     */
    public int getPlayPosition(Context context) {
        String key = context.toString();
        Integer position = mPositionMap.get(key);
        if (position == null)
            return -1;
        return position;
    }

    /**
     * 保存每个页面，最后一个播放视频的位置
     * 在addPlayer后面调用
     */
    public void putPlayPosition(Context context, int position) {
        if (context == null)
            return;
        String key = context.toString();
        mPositionMap.put(key, position);
    }
}
