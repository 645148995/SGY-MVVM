package com.cctv.cctvplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cctv.cctvplayer.entity.CCTVMainPlayEntity;
import com.cctv.cctvplayer.entity.CCTVSubPlayEntity;
import com.cctv.cctvplayer.listener.CCTVADClickListener;
import com.cctv.cctvplayer.listener.CCTVPlayListener;
import com.cctv.cctvplayer.player.DolbyHeadsetPlugReceiver;
import com.cctv.cctvplayer.utils.LogUtils;
import com.cctv.cctvplayer.utils.ScaleUtils;
import com.cctv.cctvplayer.utils.ScreenUtils;
import com.cctv.cctvplayer.utils.SystemBarUtils;
import com.cctv.cctvplayer.widget.LightTipsView;
import com.cctv.cctvplayer.widget.ProgressTipsView;
import com.cctv.cctvplayer.widget.VolumeTipsView;
import com.easefun.povplayer.core.config.PolyvPlayOption;
import com.easefun.povplayer.core.util.PolyvControlUtils;
import com.easefun.povplayer.core.video.PolyvMediaInfoType;
import com.easefun.povplayer.core.video.PolyvPlayError;
import com.easefun.povplayer.core.video.PolyvPlayerScreenRatio;
import com.easefun.povplayer.core.video.PolyvSubVideoView;
import com.easefun.povplayer.core.video.PolyvVideoView;
import com.easefun.povplayer.core.video.listener.IPolyvOnBufferingUpdateListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureClickListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureDoubleClickListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureLeftDownListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureLeftUpListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureRightDownListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureRightUpListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureSwipeLeftListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnGestureSwipeRightListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnInfoListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnPlayErrorListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnPlayPauseListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnPreparedListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnSeekCompleteListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnSubVideoViewCompletionListener;
import com.easefun.povplayer.core.video.listener.IPolyvOnSubVideoViewCountdownListener;

import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import www.viewscenestv.com.ijkvideoview.IjkVideoView;

/**
 * 视频播放器
 */

public class CCTVVideoView extends RelativeLayout {
    public Context mContext;
    private CCTVMainPlayEntity mPlayEntity = new CCTVMainPlayEntity();
    //子播放器是否显示中
    private boolean mIsSubVideoShow;
    //子播放器，用于播放广告及暖场
    private PolyvSubVideoView mSubVideoView;
    //子播放器载入状态指示器
    private ImageView mSubLoadingView;
    //主播放器
    private PolyvVideoView mVideoView;
    //主播放器载入状态指示器
    private ImageView mLoadingView;
    //主播放器在准备中，显示的视图
    private TextView mPreparingView;
    //视频播放器控类
    private CCTVVideoMediaController mController;
    //调整亮度的View
    private LightTipsView mLightTipsView;
    //调整音量的View
    private VolumeTipsView mVolumeTipsView;
    //右侧布局
    private LinearLayout mRightLayoutView;
    //浮在播放器上面，控制栏下面的的View
    private LinearLayout mFloatingLayerView;
    //手势滑动进度
    private int mFastForwardPos = 0;
    //手势滑动快进/快退提示的View
    private ProgressTipsView mProgressTipsView;
    //如果播放地址，是需要从接口请求，比如加防盗链，需要设置此监听
    private CCTVPlayListener mPlayListener;   //如果播放地址，是需要从接口请求，比如加防盗链，需要设置此监听
    //是否保持进度播放
    private boolean mIsProgressKeep;
    //点播保持进度的点
    private int mVodProgress;
    //视频左右边距，单位px
    private int mVideoLeftRightMarginPx;

    //是否禁用手势滑动快进快退功能
    private boolean mIsForbiddenGestureSeek;
    //是否禁用手势 除了双击
    private boolean mIsForbiddenGesture;
    //是否禁用所有手势
    private boolean mIsAllForbiddenGesture;
    //全屏播放器容器
    private ViewGroup mFullScreenPlayerContainer;
    //杜比功能，耳机检测
    private DolbyHeadsetPlugReceiver mDolbyHeadsetPlugReceiver;
    //视频View高度，默认16:9
    private int mVideoViewHeight;
    //是否VR播放
    private boolean mIsVR;

    public CCTVVideoView(Context context) {
        this(context, null);
    }

    public CCTVVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CCTVVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    /**
     * 返回播放器的对象。自己可以设置一些监听
     */
    public PolyvVideoView getPlayerView() {
        return mVideoView;
    }

    private void init() {
        LogUtils.i("初始化播放器视图");
        mVideoViewHeight = ScaleUtils.countScale(getContext(), 16, 9, mVideoLeftRightMarginPx);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.cctv_videoview_layout, this);
        findViews(view);
        setListeners();
        initMainPlayer();
        initSubPlayer();
        showOrHiddenLoadingView(View.VISIBLE);
        initDolby();
    }

    /**
     * 初始化杜比功能
     */
    private void initDolby() {
        mDolbyHeadsetPlugReceiver = new DolbyHeadsetPlugReceiver(getContext(), mVideoView);
        mDolbyHeadsetPlugReceiver.registerHeadsetPlugReceiver();
    }

    public DolbyHeadsetPlugReceiver getDolbyHeadsetPlugReceiver() {
        return mDolbyHeadsetPlugReceiver;
    }

    private void findViews(View view) {
        mVideoView = view.findViewById(R.id.videoview);
        mFloatingLayerView = view.findViewById(R.id.floatingLayer);
        mLoadingView = view.findViewById(R.id.loadingview);
        final AnimationDrawable loadingAnimDrawable = (AnimationDrawable) mLoadingView.getBackground();
        mLoadingView.post(new Runnable() {
            @Override
            public void run() {
                loadingAnimDrawable.start();
            }
        });
        mPreparingView = view.findViewById(R.id.preparingview);
        mController = view.findViewById(R.id.controller);
        mLightTipsView = view.findViewById(R.id.tipsview_light);
        mVolumeTipsView = view.findViewById(R.id.tipsview_volume);
        mProgressTipsView = view.findViewById(R.id.tipsview_progress);
        mSubVideoView = view.findViewById(R.id.sub_videoview);
        mSubLoadingView = view.findViewById(R.id.sub_loadingview);
        final AnimationDrawable subLoadingAnimDrawable = (AnimationDrawable) mSubLoadingView.getBackground();
        mSubLoadingView.post(new Runnable() {
            @Override
            public void run() {
                subLoadingAnimDrawable.start();
            }
        });
        mRightLayoutView = view.findViewById(R.id.rightContentLayout);
        mController.setCCTVVideoView(this);
        mController.setSubVideoView(mSubVideoView);
        mProgressTipsView.setMediaController(mController);
    }

    private void setListeners() {
        setMainVideoViewListeners();
        setSubVideoViewListeners();
        setJSWSListener();
        mRightLayoutView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {//这个空事件必须有，否则焦点会被播放器抢去

            }
        });
    }

    /**
     * 添加浮在播放器上面，控制栏下面的自定义View
     */
    public void addVideoFloatingLayerCustomView(View view) {
        addVideoFloatingLayerCustomView(view, null);
    }

    /**
     * 添加浮在播放器上面，控制栏下面的自定义View
     */
    public void addVideoFloatingLayerCustomView(View view, ViewGroup.LayoutParams params) {
        mFloatingLayerView.removeAllViews();
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            mFloatingLayerView.setVisibility(View.VISIBLE);
            if (params != null)
                mFloatingLayerView.addView(view, params);
            else
                mFloatingLayerView.addView(view);
        } else {
            mFloatingLayerView.setVisibility(View.GONE);
        }
    }

    /**
     * 准备中
     */
    public void onPreparing_() {
        LogUtils.i("onPreparing");
        if (!isProgressKeep())
            mController.initPlayProgressView();
        mController.setBackground(true);
        mController.onUpdatePlayOrPause(true);
    }

    /**
     * 手动暂停
     */
    public void onPause_() {
        LogUtils.i("onPause");
        mController.onUpdatePlayOrPause(true);
        //mController.showOrHiddenPauseLayout(View.VISIBLE);
        mController.onShow();
        //mController.showOrHiddenLockView(View.GONE);
        mController.removeLiveProgressTimer();
    }

    /**
     * 手动播放
     *
     * @param isFirst 第一次播放时为true
     */
    public void onPlay_(boolean isFirst) {
        LogUtils.i("onPlay.isFirst=" + isFirst);
        mLoadingView.setVisibility(View.GONE);
        mController.onUpdatePlayOrPause(false);
        //mController.showOrHiddenPauseLayout(View.GONE);
        mController.onShow();
        //mController.showOrHiddenLockView(View.VISIBLE);
        mController.startLiveProgressTimer();
    }

    /**
     * 播放完成
     */
    public void onCompletion_() {
        LogUtils.i("onCompletion");
        mController.onUpdatePlayOrPause(true);
        //mController.showOrHiddenPauseLayout(View.GONE);
        mController.setBackground(true);
        mController.onShow(false);
        if (!mSubVideoView.isOpenTailAd()) {
            onPlayAllCompletion();
        }
    }

    /**
     * 播放完成，包含片尾广告
     */
    public void onPlayAllCompletion() {
        LogUtils.i("onPlayAllCompletion...");
        if (!mController.isLive())
            mController.setVodProgress();
        mController.onReset();
    }

    /**
     * 播放过程中的状态
     */
    public void onInfo_(IMediaPlayer mp, int what, int extra) {
        LogUtils.i("what=" + what + "&extra=" + extra);
        if (what == PolyvMediaInfoType.MEDIA_INFO_BUFFERING_START ||
                what == IMediaPlayer.MEDIA_INFO_OPEN_INPUT) {
            mController.setBackground(true);
            mController.onShow(false);
            mController.onUpdatePlayOrPause(true);
        } else if (what == PolyvMediaInfoType.MEDIA_INFO_BUFFERING_END) {
            //mController.showOrHiddenPauseLayout(View.GONE);
            mController.setBackground(true);
            mController.onHide();
            mController.onUpdatePlayOrPause(false);
        }
    }

    /**
     * 播放错误
     */
    public void onError_(PolyvPlayError error) {
        LogUtils.i("onError：" + error + "&PlayOption：" + mVideoView.getPlayOption());
        String tips = error.playStage == PolyvPlayError.PLAY_STAGE_HEADAD ? "片头广告"
                : error.playStage == PolyvPlayError.PLAY_STAGE_TAILAD ? "片尾广告"
                : error.playStage == PolyvPlayError.PLAY_STAGE_TEASER ? "暖场视频"
                : error.isMainStage() ? "主视频" : "";
        if (error.isMainStage()) {
            mController.onUpdatePlayOrPause(true);
            mController.onShow(false);
        }
        LogUtils.i(tips + "播放异常\n" + error.errorDescribe + "(" + error.errorCode + "-" + error.playStage + ")\n" + error.playPath);
    }

    /**
     * seekTo完成
     */
    public void onSeekComplete_(IMediaPlayer mp) {
        //mVideoView.setPlayerBufferingIndicator(mLoadingView);
        mp.start();
    }

    /**
     * 缓冲进度
     */
    public void onBufferingUpdate_(IMediaPlayer mp, int percent) {

    }

    /**
     * 马上进入播放
     */
    public void onPrepared_(IMediaPlayer mp) {
        LogUtils.i("onPrepared");
        mVideoView.setAspectRatio(PolyvPlayerScreenRatio.AR_ASPECT_FIT_PARENT);
        if (mVodProgress > 0) {
            seekTo(mVodProgress);
            mVodProgress = 0;
        }
    }

    /**
     * 点击了视频View
     */
    public void onClickVideoView() {
        if (mRightLayoutView.getVisibility() == View.VISIBLE) {
            showOrHiddenRightView(View.GONE);
            return;
        }

        if (mVideoView.getCurrentState() == mVideoView.getStatePauseCode() && !mVideoView.isPlaying()) //手动暂停一直显示控制栏
            return;

        if (mController.isShowing()) {
            mController.onHide();
        } else {
            mController.onShow();
        }
    }

    /**
     * 设置主播放器监听
     */
    private void setMainVideoViewListeners() {
        mVideoView.setOnPlayPauseListener(new IPolyvOnPlayPauseListener() {
            @Override
            public void onPreparing() {
                onPreparing_();
            }

            @Override
            public void onPause() {
                onPause_();
            }

            @Override
            public void onPlay(boolean isFirst) {
                onPlay_(isFirst);
            }

            @Override
            public void onCompletion(IMediaPlayer mp) {
                onCompletion_();
            }
        });

        mVideoView.setOnInfoListener(new IPolyvOnInfoListener() {
            @Override
            public void onInfo(IMediaPlayer mp, int what, int extra) {
                onInfo_(mp, what, extra);
            }
        });

        mVideoView.setOnPreparedListener(new IPolyvOnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                onPrepared_(mp);
            }
        });

        mVideoView.setOnPlayErrorListener(new IPolyvOnPlayErrorListener() {
            @Override
            public void onError(PolyvPlayError error) {
                onError_(error);
            }
        });

        mVideoView.setOnGestureClickListener(new IPolyvOnGestureClickListener() {
            @Override
            public void callback() {
                onClickVideoView();
            }
        });

        mVideoView.setOnGestureDoubleClickListener(new IPolyvOnGestureDoubleClickListener() {
            @Override
            public void callback() {
                if (isForbiddenGesture() || mIsAllForbiddenGesture)
                    return;
                if (mVideoView.isInPlaybackStateEx()) {
                    mController.onPlayOrPause();
                }
            }
        });

        mVideoView.setOnGestureLeftDownListener(new IPolyvOnGestureLeftDownListener() {
            @Override
            public void callback(boolean start, boolean end) {
                if (isForbiddenGesture() || mIsForbiddenGesture)
                    return;
                int brightness = mVideoView.getBrightness((Activity) mContext) - 8;
                if (brightness < 0) {
                    brightness = 0;
                }
                if (start)
                    mVideoView.setBrightness((Activity) mContext, brightness);
                mLightTipsView.setLightPercent(brightness, end);
            }
        });
        mVideoView.setOnGestureLeftUpListener(new IPolyvOnGestureLeftUpListener() {
            @Override
            public void callback(boolean start, boolean end) {
                if (isForbiddenGesture() || mIsForbiddenGesture)
                    return;
                int brightness = mVideoView.getBrightness((Activity) mContext) + 8;
                if (brightness > 100) {
                    brightness = 100;
                }
                if (start)
                    mVideoView.setBrightness((Activity) mContext, brightness);
                mLightTipsView.setLightPercent(brightness, end);
            }
        });

        mVideoView.setOnGestureRightDownListener(new IPolyvOnGestureRightDownListener() {
            @Override
            public void callback(boolean start, boolean end) {
                if (isForbiddenGesture() || mIsForbiddenGesture)
                    return;
                int volume = mVideoView.getVolume() - PolyvControlUtils.getVolumeValidProgress(mContext, 8);
                if (volume < 0) {
                    volume = 0;
                }
                if (start)
                    mVideoView.setVolume(volume);
                mVolumeTipsView.setVolumePercent(volume, end);
            }
        });
        mVideoView.setOnGestureRightUpListener(new IPolyvOnGestureRightUpListener() {
            @Override
            public void callback(boolean start, boolean end) {
                if (isForbiddenGesture() || mIsForbiddenGesture)
                    return;
                int volume = mVideoView.getVolume() + PolyvControlUtils.getVolumeValidProgress(mContext, 8);
                if (volume > 100) {
                    volume = 100;
                }
                if (start)
                    mVideoView.setVolume(volume);
                mVolumeTipsView.setVolumePercent(volume, end);
            }
        });

        mVideoView.setOnGestureSwipeLeftListener(new IPolyvOnGestureSwipeLeftListener() {
            @Override
            public void callback(boolean start, int times, boolean end) {
                if (isForbiddenGesture() || mIsForbiddenGesture || mIsForbiddenGestureSeek)
                    return;

                if (mVideoView.isLivePlayMode()) {
                    mProgressTipsView.setLiveProgressPercent(end, false, 0);
                    mProgressTipsView.delayHide();
                    return;
                }

                if (mVideoView.isInPlaybackStateEx() && mVideoView.isVodPlayMode()) {
                    if (mFastForwardPos == 0) {
                        mFastForwardPos = mVideoView.getCurrentPosition();
                    }
                    if (end) {
                        if (mFastForwardPos < 0)
                            mFastForwardPos = 0;
                        seekTo(mFastForwardPos);
                        if (mVideoView.isCompletedState()) {
                            mVideoView.start();
                        }
                        mFastForwardPos = 0;
                    } else {
                        mFastForwardPos -= 1000 * times;
                        if (mFastForwardPos <= 0)
                            mFastForwardPos = -1;
                    }
                    mProgressTipsView.setVodProgressPercent(mFastForwardPos, mVideoView.getDuration(), end, false);
                } else if (end) {
                    mFastForwardPos = 0;
                    mProgressTipsView.delayHide();
                }
            }
        });

        mVideoView.setOnGestureSwipeRightListener(new IPolyvOnGestureSwipeRightListener() {
            @Override
            public void callback(boolean start, int times, boolean end) {
                if (isForbiddenGesture() || mIsForbiddenGesture || mIsForbiddenGestureSeek)
                    return;

                if (mVideoView.isLivePlayMode()) {
                    mProgressTipsView.setLiveProgressPercent(end, true, 0);
                    if (end) {
                        mProgressTipsView.delayHide();
                    }
                    return;
                }

                if (mVideoView.isInPlaybackStateEx() && mVideoView.isVodPlayMode()) {
                    if (mFastForwardPos == 0) {
                        mFastForwardPos = mVideoView.getCurrentPosition();
                    }
                    if (end) {
                        if (mFastForwardPos > mVideoView.getDuration())
                            mFastForwardPos = mVideoView.getDuration();
                        if (!mVideoView.isCompletedState()) {
                            seekTo(mFastForwardPos);
                        } else if (mFastForwardPos < mVideoView.getDuration()) {
                            seekTo(mFastForwardPos);
                            mVideoView.start();
                        }
                        mFastForwardPos = 0;
                    } else {
                        mFastForwardPos += 1000 * times;
                        if (mFastForwardPos > mVideoView.getDuration())
                            mFastForwardPos = mVideoView.getDuration();
                    }
                    mProgressTipsView.setVodProgressPercent(mFastForwardPos, mVideoView.getDuration(), end, true);
                } else if (end) {
                    mFastForwardPos = 0;
                    mProgressTipsView.delayHide();
                }
            }
        });

        mVideoView.setOnSeekCompleteListener(new IPolyvOnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IMediaPlayer mp) {
                LogUtils.i("onSeekComplete");
                onSeekComplete_(mp);
            }
        });

        mVideoView.setOnBufferingUpdateListener(new IPolyvOnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                onBufferingUpdate_(mp, percent);
            }
        });
    }

    /**
     * 快进/快退
     */
    public void seekTo(int pos) {
        //mVideoView.setPlayerBufferingIndicator(null);
        mVideoView.seekTo(pos);
    }

    /**
     * 设置金石微视播放器监听
     */
    private void setJSWSListener() {
        IjkVideoView ijkVideoView = mVideoView.getIjkVideoView();
        ijkVideoView.addIjkVideoViewListener(new IjkVideoView.IjkVideoViewListener() {
            @Override
            public void onIAEvent(String eventContent) {
                LogUtils.i("onIAEvent");
            }

            @Override
            public void onBitRateChanged(String bitrate) {
                LogUtils.i("onBitRateChanged");
            }

            @Override
            public void onVolumeChanged(int onVolumeChanged) {
                LogUtils.i("onVolumeChanged");
            }

            @Override
            public void onIJKNeedRetry(int retryReason) {
                LogUtils.i("onIJKNeedRetry");
            }

            @Override
            public void onAudioRenderingStart() {
                LogUtils.i("onAudioRenderingStart");
            }

            @Override
            public void onViewChangeEnd(Integer result) {//只有0是正常的
                LogUtils.i("jsws onViewChangeEnd=" + result);
            }

            @Override
            public void onRenderingStart() {
                LogUtils.i("onRenderingStart");
            }

            @Override
            public void onIjkplayerCompleted() {
                LogUtils.i("onIjkplayerCompleted");
            }

            @Override
            public void onBufferingUpdate(int percent) {
                LogUtils.i("onBufferingUpdate");
            }

            @Override
            public void onBufferingStart() {
                LogUtils.i("onBufferingStart");
            }

            @Override
            public void onBufferingEnd() {
                LogUtils.i("onBufferingEnd");
            }

            @Override
            public void onClosed() {
                LogUtils.i("onClosed");
            }
        });
    }

    /**
     * 初始化子播放器，用于播放广告及暖场视频播放器
     */
    private void initSubPlayer() {
        mSubVideoView.setKeepScreenOn(true);
        mSubVideoView.setPlayerBufferingIndicator(mSubLoadingView);
    }

    /**
     * 设置子播放器，广告及暖场监听
     */
    private void setSubVideoViewListeners() {
        mSubVideoView.setOnPreparedListener(new IPolyvOnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                LogUtils.i("sub onPrepared");
                mSubVideoView.setAspectRatio(mVideoView.getAspectRatio());
            }
        });
        mSubVideoView.setOnInfoListener(new IPolyvOnInfoListener() {
            @Override
            public void onInfo(IMediaPlayer mp, int what, int extra) {
                if (what == PolyvMediaInfoType.MEDIA_INFO_BUFFERING_START ||
                        what == IMediaPlayer.MEDIA_INFO_OPEN_INPUT) {
                    LogUtils.i("sub 开始缓冲");
                    mSubLoadingView.setVisibility(View.VISIBLE);
                } else if (what == PolyvMediaInfoType.MEDIA_INFO_BUFFERING_END) {
                    LogUtils.i("sub 缓冲结束");
                    mSubLoadingView.setVisibility(View.GONE);
                }
            }
        });
        mSubVideoView.setOnPlayPauseListener(new IPolyvOnPlayPauseListener() {
            @Override
            public void onPreparing() {
                int playStage = mSubVideoView.getPlayStage();
                String tips = playStage == PolyvSubVideoView.PLAY_STAGE_HEADAD ? "片头广告"
                        : playStage == PolyvSubVideoView.PLAY_STAGE_TEASER ? "暖场视频"
                        : playStage == PolyvSubVideoView.PLAY_STAGE_TAILAD ? "片尾广告" : "";
                LogUtils.i("sub " + tips + " onPreparing");
                if (playStage != PolyvSubVideoView.PLAY_STAGE_HEADAD) {
                    mController.showOrHiddenSubSkipView(View.GONE);
                    mController.setHeadedCountdown(0, -1);
                } else
                    mController.showOrHiddenSubFullScreenView(View.VISIBLE);

                if (mADClickListener != null) {
                    String curUrl = mSubVideoView.getCurrentPlayPath();
                    mADClickListener.onADPlay(playStage, getPlayEntity().getSubPlayEntity().getHeadadADInfo(curUrl));
                }
            }

            @Override
            public void onPause() {
                LogUtils.i("sub onPause");
            }

            @Override
            public void onPlay(boolean isFirst) {
                LogUtils.i("sub onPlay：" + isFirst);
            }

            @Override
            public void onCompletion(IMediaPlayer mp) {
                LogUtils.i("sub teaser onCompletion");
            }
        });
        mSubVideoView.setOnSubVideoViewCountdownListener(new IPolyvOnSubVideoViewCountdownListener() {
            @Override
            public void onCountdown(int totalTime, int remainTime, @PolyvSubVideoView.AdStage int adStage) {
                if (adStage == PolyvSubVideoView.PLAY_STAGE_HEADAD) //片头广告
                    mController.setHeadedCountdown(totalTime, remainTime);
            }

            @Override
            public void onVisibilityChange(boolean isShow) {
                LogUtils.i("sub onVisibilityChange：" + isShow);
                mIsSubVideoShow = isShow;
                if (isShow) {
                    mController.onHide();
                    mController.showOrHiddenSubSkipView(View.VISIBLE);
                    mController.showOrHiddenSubFullScreenView(View.VISIBLE);
                    if (mController.isHorizontal()) {
                        mController.setTopLayoutTopMargin(0);
                        SystemBarUtils.showOrHiddenStatusBar((Activity) mContext, false);
                    }
                } else {
                    mController.showOrHiddenSubSkipView(View.GONE);
                    mController.showOrHiddenSubFullScreenView(View.GONE);
                    if (mController.isHorizontal()) {
                        mController.setTopLayoutTopMargin(ScreenUtils.getStatusBarHeight(mContext));
                        SystemBarUtils.showOrHiddenStatusBar((Activity) mContext, true);
                    }
                }
            }
        });
        mSubVideoView.setOnSubVideoViewPlayCompletionListener(new IPolyvOnSubVideoViewCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp, @PolyvSubVideoView.AdStage int adStage) {
                String tips = adStage == PolyvSubVideoView.PLAY_STAGE_HEADAD ? "片头广告"
                        : adStage == PolyvSubVideoView.PLAY_STAGE_TAILAD ? "片尾广告" : "";
                LogUtils.i("sub " + tips + " onCompletion");
                if (adStage == PolyvSubVideoView.PLAY_STAGE_TAILAD) //片尾广告播放完成
                    onPlayAllCompletion();
            }
        });
        mSubVideoView.setOnGestureClickListener(new IPolyvOnGestureClickListener() {
            @Override
            public void callback() {
                if (mADClickListener != null) {
                    CCTVSubPlayEntity subEntity = getPlayEntity().getSubPlayEntity();
                    CCTVSubPlayEntity.CCTVSubPlayHeadadAD data = subEntity.getHeadadADInfo(mSubVideoView.getCurrentPlayPath());
                    mADClickListener.onADClick(mSubVideoView.getPlayStage(), data);
                }
            }
        });
    }

    /**
     * 初始化主视频播放器
     */
    private void initMainPlayer() {
        mVideoView.setSubVideoView(mSubVideoView);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setPlayerBufferingIndicator(mLoadingView);
        mVideoView.setNeedGestureDetector(true);
        mVideoView.setMediaController(mController);
    }

    /**
     * 设置视频距离屏幕的左右边距
     */
    public void setVideoLeftRightMarginPx(int px) {
        this.mVideoLeftRightMarginPx = px;
        mVideoViewHeight = ScaleUtils.countScale(getContext(), 16, 9, mVideoLeftRightMarginPx);
    }

    /**
     * 获取视频View高度
     */
    public int getVideoViewHeight() {
        return this.mVideoViewHeight;
    }

    /**
     * 设置视频View高度，默认16:9。 可以为MATCH_PARENT/WRAP_CONTENT
     */
    public void setVideoViewHeight(int height) {
        this.mVideoViewHeight = height;
    }

    /**
     * 显示加载View
     */
    public void showOrHiddenLoadingView(int visibility) {
        mLoadingView.setVisibility(visibility);
    }

    /**
     * 播放
     *
     * @param subView      true 包含片头，片尾，暖场视频。 false 只是主视频
     * @param playURL      如果为空就用CCTVMainPlayEntity里设置的默认地址播放
     * @param cplaymode    金石的播放模式
     * @param progressKeep 是否进度保持播放
     */
    public void play(String playURL, boolean subView, String cplaymode, boolean progressKeep) {
        if (mPlayEntity == null || TextUtils.isEmpty(playURL))
            return;

        mIsProgressKeep = progressKeep;
        if (mIsProgressKeep && !mPlayEntity.isLive()) {//保持播放进度，且是点播
            mVodProgress = mVideoView.getCurrentPosition();
        }

        if (!playURL.contains("cplaymode")
                && !TextUtils.isEmpty(cplaymode)
                && mPlayEntity.isLive()) {
            if (playURL.contains("?"))
                playURL += "&cplaymode=" + cplaymode;
            else
                playURL += "?cplaymode=" + cplaymode;
        }

        showOrHiddenLoadingView(View.VISIBLE);
        mVideoView.setOption(playURL,
                generatePlayOption(mPlayEntity.isLive(),
                        mPlayEntity.getHeaders()));

        resetVideoLayout();
        if (subView)
            mVideoView.playFromHeadAd();
        else
            mVideoView.play();
    }

    /**
     * 金石威视多角度切换，目前只有播放直播时才用到，点播暂停时，有不播放的问题。
     *
     * @param playURL      播放地址
     * @param cplaymode    金石的播放模式 cplaymode=1 多角度切换，cplaymode=2 同步播放模式
     * @param progressKeep 是否进度保持播放
     * @return 成功返回0，失败返回-1（暂定）。不在播放状态返回-2， -3播放地址为空
     */
    public int playJSWS(String playURL, String cplaymode, boolean progressKeep) {
        if (TextUtils.isEmpty(playURL))
            return -3;

        if (playURL.contains("?"))
            playURL += "&cplaymode=" + cplaymode;
        else
            playURL += "?cplaymode=" + cplaymode;
        LogUtils.i("playJSWS url=" + playURL);

        mIsProgressKeep = progressKeep;
        if (mIsProgressKeep && !mPlayEntity.isLive()) {//保持播放进度，且是点播
            mVodProgress = mVideoView.getCurrentPosition();
        }
        resetVideoLayout();
        IjkVideoView ijkVideoView = mVideoView.getIjkVideoView();
        int status = ijkVideoView.setView2(playURL);
        LogUtils.i("playJSWS status=" + status);

        if (status == -2)
            play(playURL, false, cplaymode, mIsProgressKeep);
        else
            ijkVideoView.start();
        return 0;
    }

    /**
     * 自定义全屏的播放器容器
     */
    public void setFullScreenPlayerContainer(ViewGroup container) {
        this.mFullScreenPlayerContainer = container;
    }

    /**
     * 获取自定义全屏的播放器容器
     */
    public ViewGroup getFullScreenPlayerContainer() {
        return this.mFullScreenPlayerContainer;
    }

    /**
     * 是否保持进度播放
     */
    public boolean isProgressKeep() {
        return mIsProgressKeep;
    }

    /**
     * 每次播放前，重置布局
     */
    public void resetVideoLayout() {
        mFloatingLayerView.setVisibility(View.GONE);
        //mController.showOrHiddenPauseLayout(View.GONE);
    }

    /**
     * 设置横竖屏。
     */
    private void setOrientation() {
        if (mController.getOrientation().getCurOrientation() == 1 && !mController.isHorizontal())
            return;

        if (mController.getOrientation().getCurOrientation() == 2 && mController.isHorizontal())
            return;

        if (mController.isHorizontal())
            mController.getOrientation().changeToLandscape();
        else
            mController.getOrientation().changeToPortrait(false);
    }

    /**
     * 生成播放配置，记得在播放前生成
     */
    private PolyvPlayOption generatePlayOption(boolean isLive, Map<String, String> headers) {
        PolyvPlayOption playOption = PolyvPlayOption.getDefault();
        playOption.put(PolyvPlayOption.KEY_PLAYMODE, isLive ? PolyvPlayOption.PLAYMODE_LIVE : PolyvPlayOption.PLAYMODE_VOD)
                .put(PolyvPlayOption.KEY_DECODEMODE, PolyvPlayOption.DECODEMODE_AVCODEC)
                .put(PolyvPlayOption.KEY_TIMEOUT, 60)
                .put(PolyvPlayOption.KEY_PRELOADTIME, 3)
                .put(PolyvPlayOption.KEY_LOADINGVIEW_DELAY, 2)
                .put(PolyvPlayOption.KEY_VR_ON, mIsVR);

        if (mPlayEntity.getSubPlayEntity() != null &&
                mPlayEntity.getSubPlayEntity().getHeadadADList() != null &&
                !mPlayEntity.getSubPlayEntity().getHeadadADList().isEmpty()) { //片头广告
            playOption.put(PolyvPlayOption.KEY_HEADAD, mPlayEntity.getSubPlayEntity().getHeadadADList());
        }

        if (isLive) {//直播
            playOption.put(PolyvPlayOption.KEY_RECONNECTION_COUNT, 3);
            if (mPlayEntity.getSubPlayEntity() != null && !TextUtils.isEmpty(mPlayEntity.getSubPlayEntity().getTeaserADPlayURL()))//暖场视频
                playOption.put(PolyvPlayOption.KEY_TEASER, mPlayEntity.getSubPlayEntity().getTeaserADPlayURL());
        } else {//点播
            if (mPlayEntity.getSubPlayEntity() != null && !TextUtils.isEmpty(mPlayEntity.getSubPlayEntity().getTailadADPlayURL())) //片尾广告
                playOption.put(PolyvPlayOption.KEY_TAILAD, new PolyvPlayOption.TailAdOption(mPlayEntity.getSubPlayEntity().getTailadADPlayURL(), mPlayEntity.getSubPlayEntity().getTailadADDuration()));
        }

        if (headers != null) {
            playOption.put(PolyvPlayOption.KEY_HEADERS, headers);
        }
        return playOption;
    }

    /**
     * 获取播放器控制类
     */
    public CCTVVideoMediaController getMediaController() {
        return mController;
    }

    /**
     * 禁用手势操作
     */
    private boolean isForbiddenGesture() {
        if (mController.isLock())
            return true;

        if (mRightLayoutView.getVisibility() == View.VISIBLE)
            return true;

//        if (mVideoView.isPlaying())
//            return false;
//
//        return mVideoView.getCurrentState() != mVideoView.getStatePlayingCode();
//        return mController.isShowPauseView();
        return false;
    }

    /**
     * 是否禁用手势滑动来进行快退快进
     */

    public void setIsForbiddenGestureSeek(boolean forbidden) {
        this.mIsForbiddenGestureSeek = forbidden;
    }

    /**
     * 是否禁用手势，除了双击，默认false
     */
    public void setIsForbiddenGesture(boolean forbidden) {
        this.mIsForbiddenGesture = forbidden;
    }

    /**
     * 是否禁用所有手势默认false
     */
    public void setIsAllForbiddenGesture(boolean forbidden) {
        this.mIsAllForbiddenGesture = forbidden;
        this.mIsForbiddenGesture = forbidden;
    }

    /**
     * 设置播放前的一些信息。如果信息、设置有改动，需要在每次播放前调用此方法。
     */
    public void setPlay(CCTVMainPlayEntity entity) {
        if (entity == null)
            return;

        this.mPlayEntity = entity;
        mController.setTitle(entity.getTitle());
        mController.setPlayModeUI();
        setOrientation();
    }

    /**
     * 子播放器是否显示中
     */
    public boolean isSubVideoShow() {
        return mIsSubVideoShow;
    }

    //片头/暖场视频/片尾广告点击事件监听
    private CCTVADClickListener mADClickListener;

    /**
     * 片头/暖场视频/片尾广告点击事件监听
     */
    public void setADClickListener(CCTVADClickListener clickListener) {
        this.mADClickListener = clickListener;
    }

    /**
     * 获取播放信息
     */
    public CCTVMainPlayEntity getPlayEntity() {
        return mPlayEntity;
    }

    /**
     * 显示隐藏右边布局
     */
    public void showOrHiddenRightView(int visibility) {
        mController.setBackground(true);
        if (View.GONE == visibility) {
            if (mVideoView.getCurrentState() == mVideoView.getStatePauseCode() && !mVideoView.isPlaying()) {  //手动暂停
                //mController.showOrHiddenPauseLayout(View.VISIBLE);
                mController.onShow(false);
            }
            setRightLayoutViewWidth();
        } else {
            mController.showOrHiddenPauseLayout(View.GONE);
            mController.setBackground(false);
        }
        mRightLayoutView.setVisibility(visibility);
    }

    /**
     * 设置右侧布局宽度
     */
    public void setRightLayoutViewWidth() {
        if (getMediaController().isHorizontal())
            mRightLayoutView.getLayoutParams().width = ScreenUtils.getWidth(mContext) / 3;
        else
            mRightLayoutView.getLayoutParams().width = ScreenUtils.getWidth(mContext) / 2;
    }

    /**
     * 设置右侧布局宽度
     */
    public void setRightLayoutViewWidth(int width) {
        mRightLayoutView.getLayoutParams().width = width;
    }

    /**
     * 添加右侧自定义布局
     */
    public void addRightCustomView(View view) {
        setRightLayoutViewWidth();
        mRightLayoutView.removeAllViews();
        if (view != null)
            mRightLayoutView.addView(view);
    }


    /**
     * 如果播放地址，是需要从接口请求，比如加防盗链，需要设置此监听
     */
    public void setPlayListener(CCTVPlayListener listener) {
        mPlayListener = listener;
    }

    /**
     * 如果播放地址，是需要从接口请求，比如加防盗链，需要设置此监听
     */
    public CCTVPlayListener getPlayListener() {
        return mPlayListener;
    }

    /**
     * 只是停止当前播放，但不用创建实例
     */
    public void onStop() {
        //mVideoView.stopPlayback(); //播放器不是处于准备中状态，可以这样调用
        mVideoView.stopPlay();
        mVideoView.removeRenderView();
        mVideoView.getSubVideoView().removeRenderView();
        mController.onReset();
    }

    /**
     * 在Controller的onRestart里调用的
     */
    public void onActivityRestart() {

    }

    /**
     * 在Controller的onStop里调用的
     */
    public void onActivityStop() {

    }

    /**
     * 在Controller的onDestroy里调用的
     */
    public void onActivityDestroy() {

    }

    public boolean isAllForbiddenGesture() {
        return mIsAllForbiddenGesture;
    }

    /**
     * 否是VR播放
     */
    public void setVR(boolean vr) {
        this.mIsVR = vr;

        if (mIsVR && mController.isLive()) {
            mController.getLiveStatusView().setEnabled(false);
            mController.findViewById(R.id.thumbView).setEnabled(false);
            ((ViewGroup) mController.getPlayProgressView().getParent()).setOnTouchListener(null);
        } else {
            mController.findViewById(R.id.thumbView).setEnabled(true);
            mController.getLiveStatusView().setEnabled(true);
            mController.enlargeSeekBar();
        }
    }

    public void isSeekBarDrag (boolean flag){
        LogUtils.i("mController.isLive() = " + mController.isLive());
        if (mController.isLive()) {
            mController.getLiveStatusView().setEnabled(false);
            mController.findViewById(R.id.thumbView).setEnabled(false);
            ((ViewGroup) mController.getPlayProgressView().getParent()).setOnTouchListener(null);
        } else {
            mController.findViewById(R.id.thumbView).setEnabled(true);
            mController.getLiveStatusView().setEnabled(true);
            mController.enlargeSeekBar();
        }
    }
}
