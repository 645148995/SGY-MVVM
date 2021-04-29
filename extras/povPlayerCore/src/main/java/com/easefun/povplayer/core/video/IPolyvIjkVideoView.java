package com.easefun.povplayer.core.video;

import android.graphics.Bitmap;
import android.net.Uri;

import android.view.SurfaceHolder;
import android.widget.TableLayout;

import androidx.appcompat.app.AlertDialog;

import com.easefun.povplayer.core.gifmaker.GifMaker;

import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import www.viewscenestv.com.ijkvideoview.IMediaController;
import www.viewscenestv.com.ijkvideoview.IRenderView;
import www.viewscenestv.com.ijkvideoview.PolyvGLSurfaceRenderView;
import www.viewscenestv.com.ijkvideoview.PolyvGLTextureRenderView;

/**
 * IjkVideoView内部接口，有标注的方法仅内部使用。
 */
public interface IPolyvIjkVideoView extends IPolyvMediaPlayerControl {
    /**
     * 设置播放渲染视图，注：仅内部使用。
     *
     * @param renderView
     */
    void setRenderView(IRenderView renderView);

    /**
     * 设置播放渲染视图，注：仅内部使用。
     *
     * @param render
     */
    void setRender(int render);

    /**
     * 设置视频路径，注：仅内部使用。
     *
     * @param path 视频地址
     */
    void setVideoPath(String path);

    /**
     * 设置视频URI，注：仅内部使用。
     *
     * @param uri 视频地址
     */
    void setVideoURI(Uri uri);

    /**
     * 设置视频URI和Headers，注：仅内部使用。
     *
     * @param uri
     * @param headers
     */
    void setVideoURI(Uri uri, Map<String, String> headers);

    /**
     * 停止播放，并释放，注：仅内部使用。
     */
    void stopPlayback();

    /**
     * 设置控制栏
     *
     * @param controller
     */
    void setMediaController(IMediaController controller);

    /**
     * 设置准备完成监听，注：仅内部使用。
     *
     * @param l
     */
    void setOnPreparedListener(IMediaPlayer.OnPreparedListener l);

    /**
     * 设置播放完成监听，注：仅内部使用。
     *
     * @param l
     */
    void setOnCompletionListener(IMediaPlayer.OnCompletionListener l);

    /**
     * 设置播放失败监听，注：仅内部使用。
     *
     * @param l
     */
    void setOnErrorListener(IMediaPlayer.OnErrorListener l);

    /**
     * 设置播放缓冲监听，注：仅内部使用。
     *
     * @param l
     */
    void setOnInfoListener(IMediaPlayer.OnInfoListener l);

    /**
     * 释放渲染视图，注：仅内部使用。
     */
    void releaseWithoutStop();

    /**
     * 释放，注：仅内部使用。
     *
     * @param cleartargetstate
     */
    void release(boolean cleartargetstate);

    /**
     * 释放，但不清除状态{@link #release(boolean)}，注：仅内部使用。
     */
    void suspend();

    /**
     * 重新开始播放，注：仅内部使用。
     */
    void resume();

    /**
     * 切换画面填充模式，注：仅内部使用。
     *
     * @return
     */
    int toggleAspectRatio();

    /**
     * 切换播放渲染视图，注：仅内部使用。
     *
     * @return
     */
    int toggleRender();

    /**
     * 重新打开播放器，注：仅内部使用。
     *
     * @return
     */
    int togglePlayer();

    /**
     * 创建播放器，注：仅内部使用。
     *
     * @param playerType
     * @return
     */
    IMediaPlayer createPlayer(int playerType);

    /**
     * 获取追踪器信息
     *
     * @return
     */
    ITrackInfo[] getTrackInfo();

    /**
     * 选择追踪器
     *
     * @param stream {@link ITrackInfo}
     */
    void selectTrack(int stream);

    /**
     * 取消选择追踪器
     *
     * @param stream {@link ITrackInfo}
     */
    void deselectTrack(int stream);

    /**
     * 获取追追踪器是否是选择状态
     *
     * @param trackType {@link ITrackInfo}
     * @return
     */
    int getSelectedTrack(int trackType);

    /**
     * 设置hudView，用于显示播放信息
     *
     * @param tableLayout
     */
    void setHudView(TableLayout tableLayout);

    /**
     * 显示视频信息，内部会创建一个对话框并显示
     */
    AlertDialog showMediaInfo();

    // --------------------------- polyv ----------------------------------

    /**
     * 是否是准备中状态
     *
     * @return
     */
    boolean isInPlaybackStateForwarding();

    /**
     * 获取播放渲染视图控件
     *
     * @return
     */
    IRenderView getRenderView();

    /**
     * 获取当前填充模式
     *
     * @return {@link PolyvPlayerScreenRatio}
     */
    int getCurrentAspectRatio();

    /**
     * 设置填充模式
     *
     * @param aspectRatio
     */
    void setCurrentAspectRatio(@PolyvPlayerScreenRatio.RenderScreenRatio int aspectRatio);

    /**
     * 获取视频的宽
     *
     * @return
     */
    int getVideoWidth();

    /**
     * 获取视频的高
     *
     * @return
     */
    int getVideoHeight();

    /**
     * 获取播放器
     *
     * @return
     */
    IMediaPlayer getMediaPlayer();

    /**
     * 获取播放渲染视图holder
     *
     * @return
     */
    SurfaceHolder getSurfaceHolder();

    /**
     * 设置播放速度
     *
     * @param speed 播放速度，最好不要超过2
     */
    void setSpeed(float speed);

    /**
     * 获取播放速度
     *
     * @return 播放速度
     */
    float getSpeed();

    /**
     * 获取默认播放状态码
     *
     * @return
     */
    int getStateIdleCode();

    /**
     * 获取播放异常状态码
     *
     * @return
     */
    int getStateErrorCode();

    /**
     * 获取准备中状态码
     *
     * @return
     */
    int getStatePreparingCode();

    /**
     * 获取准备完成状态码
     *
     * @return
     */
    int getStatePreparedCode();

    /**
     * 获取暂停状态码
     *
     * @return
     */
    int getStatePauseCode();

    /**
     * 获取播放中状态码
     *
     * @return
     */
    int getStatePlayingCode();

    /**
     * 获取播放完成状态码
     *
     * @return
     */
    int getStatePlaybackCompletedCode();

    /**
     * 获取当前的播放状态
     *
     * @return
     */
    int getCurrentState();

    /**
     * 获取目标的播放状态，注：仅内部使用。
     *
     * @return
     */
    int getTargetState();

    /**
     * 设置目标的播放状态，注：仅内部使用。
     *
     * @param state
     */
    void setTargetState(int state);

    /**
     * 设置播放器option参数，注：仅内部使用。
     *
     * @param mOptionParameters
     */
    void setOptionParameters(Object[][] mOptionParameters);

    /**
     * 清除option参数，注：仅内部使用。
     */
    void clearOptionParameters();

    /**
     * 设置播放器内部的日志等级
     *
     * @param ijkLogLevel {@link IjkMediaPlayer}
     */
    void setIjkLogLevel(int ijkLogLevel);

    /**
     * 进入后台播放
     */
    void enterBackground();

    /**
     * 停止后台播放，一般在{@link IPolyvBaseVideoView#destroy()}时调用，注：仅内部使用。
     */
    void stopBackgroundPlay();

    /**
     * 进入播放错误状态，当外部处理发生错误时，需要调用该方法，注：仅内部使用。
     */
    void onErrorState();

    /**
     * 设置seek完成监听器，注：仅内部使用。
     *
     * @param l
     */
    void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener l);

    /**
     * 设置播放器大小改变监听器，注：仅内部使用。
     *
     * @param l
     */
    void setOnVideoSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener l);

    /**
     * 重置加载所耗时间，注：仅内部使用。
     */
    void resetLoadCost();

    /**
     * 重新加载，注：仅内部使用。
     */
    void resetVideoURI();

    /**
     * 移除渲染视图，注：仅内部使用。
     */
    void removeRenderView();

    /**
     * 镜面翻转
     *
     * @param paramBoolean
     */
    void setMirror(boolean paramBoolean);

    /**
     * 截图
     *
     * @return
     */
    Bitmap screenshot();

    /**
     * 开始截取视频片段
     *
     * @param cancelSecond 自动取消的时间，如果小于8，那么为8.
     * @return
     */
    boolean startClip(int cancelSecond);

    /**
     * 停止截取视频片段
     *
     * @param listener
     */
    void stopClip(GifMaker.OnGifListener listener);

    /**
     * 取消截取视频片段
     */
    void cancelClip();

    /**
     * 设置tag
     *
     * @param tag
     */
    void setLogTag(String tag);

    /**
     * 动态设置杜比的endpoint参数。另外该参数在播放器每次初始化的时候都会重新获取当前耳机的接入状态并设置。
     *
     * @param isHeadsetOn 是否有耳机接入
     * @return {@link #getMediaPlayer()}不为null时返回true，未准备或者播放器已经释放会返回false。
     */
    boolean setDolbyEndpointParam(boolean isHeadsetOn);

    /**
     * 重设VR渲染组件，切换横竖屏时需要调用这个方法
     */
    void resetVRRender();

    /**
     * 设置VR渲染组件初始化完成监听回调
     * @param l 监听回调
     */
    void setVRViewInitCompletionListener(PolyvGLSurfaceRenderView.OnInitCompletionListener l);

    /**
     * 设置VR渲染组件初始化完成监听回调
     * @param l 监听回调
     */
    void setVRViewInitCompletionListener(PolyvGLTextureRenderView.OnInitCompletionListener l);
}
