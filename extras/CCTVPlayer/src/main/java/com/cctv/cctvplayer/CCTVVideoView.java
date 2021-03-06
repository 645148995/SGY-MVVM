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
 * ???????????????
 */

public class CCTVVideoView extends RelativeLayout {
    public Context mContext;
    private CCTVMainPlayEntity mPlayEntity = new CCTVMainPlayEntity();
    //???????????????????????????
    private boolean mIsSubVideoShow;
    //??????????????????????????????????????????
    private PolyvSubVideoView mSubVideoView;
    //?????????????????????????????????
    private ImageView mSubLoadingView;
    //????????????
    private PolyvVideoView mVideoView;
    //?????????????????????????????????
    private ImageView mLoadingView;
    //??????????????????????????????????????????
    private TextView mPreparingView;
    //?????????????????????
    private CCTVVideoMediaController mController;
    //???????????????View
    private LightTipsView mLightTipsView;
    //???????????????View
    private VolumeTipsView mVolumeTipsView;
    //????????????
    private LinearLayout mRightLayoutView;
    //?????????????????????????????????????????????View
    private LinearLayout mFloatingLayerView;
    //??????????????????
    private int mFastForwardPos = 0;
    //??????????????????/???????????????View
    private ProgressTipsView mProgressTipsView;
    //??????????????????????????????????????????????????????????????????????????????????????????
    private CCTVPlayListener mPlayListener;   //??????????????????????????????????????????????????????????????????????????????????????????
    //????????????????????????
    private boolean mIsProgressKeep;
    //????????????????????????
    private int mVodProgress;
    //???????????????????????????px
    private int mVideoLeftRightMarginPx;

    //??????????????????????????????????????????
    private boolean mIsForbiddenGestureSeek;
    //?????????????????? ????????????
    private boolean mIsForbiddenGesture;
    //????????????????????????
    private boolean mIsAllForbiddenGesture;
    //?????????????????????
    private ViewGroup mFullScreenPlayerContainer;
    //???????????????????????????
    private DolbyHeadsetPlugReceiver mDolbyHeadsetPlugReceiver;
    //??????View???????????????16:9
    private int mVideoViewHeight;
    //??????VR??????
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
     * ?????????????????????????????????????????????????????????
     */
    public PolyvVideoView getPlayerView() {
        return mVideoView;
    }

    private void init() {
        LogUtils.i("????????????????????????");
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
     * ?????????????????????
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
            public void onClick(View v) {//????????????????????????????????????????????????????????????

            }
        });
    }

    /**
     * ?????????????????????????????????????????????????????????View
     */
    public void addVideoFloatingLayerCustomView(View view) {
        addVideoFloatingLayerCustomView(view, null);
    }

    /**
     * ?????????????????????????????????????????????????????????View
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
     * ?????????
     */
    public void onPreparing_() {
        LogUtils.i("onPreparing");
        if (!isProgressKeep())
            mController.initPlayProgressView();
        mController.setBackground(true);
        mController.onUpdatePlayOrPause(true);
    }

    /**
     * ????????????
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
     * ????????????
     *
     * @param isFirst ?????????????????????true
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
     * ????????????
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
     * ?????????????????????????????????
     */
    public void onPlayAllCompletion() {
        LogUtils.i("onPlayAllCompletion...");
        if (!mController.isLive())
            mController.setVodProgress();
        mController.onReset();
    }

    /**
     * ????????????????????????
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
     * ????????????
     */
    public void onError_(PolyvPlayError error) {
        LogUtils.i("onError???" + error + "&PlayOption???" + mVideoView.getPlayOption());
        String tips = error.playStage == PolyvPlayError.PLAY_STAGE_HEADAD ? "????????????"
                : error.playStage == PolyvPlayError.PLAY_STAGE_TAILAD ? "????????????"
                : error.playStage == PolyvPlayError.PLAY_STAGE_TEASER ? "????????????"
                : error.isMainStage() ? "?????????" : "";
        if (error.isMainStage()) {
            mController.onUpdatePlayOrPause(true);
            mController.onShow(false);
        }
        LogUtils.i(tips + "????????????\n" + error.errorDescribe + "(" + error.errorCode + "-" + error.playStage + ")\n" + error.playPath);
    }

    /**
     * seekTo??????
     */
    public void onSeekComplete_(IMediaPlayer mp) {
        //mVideoView.setPlayerBufferingIndicator(mLoadingView);
        mp.start();
    }

    /**
     * ????????????
     */
    public void onBufferingUpdate_(IMediaPlayer mp, int percent) {

    }

    /**
     * ??????????????????
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
     * ???????????????View
     */
    public void onClickVideoView() {
        if (mRightLayoutView.getVisibility() == View.VISIBLE) {
            showOrHiddenRightView(View.GONE);
            return;
        }

        if (mVideoView.getCurrentState() == mVideoView.getStatePauseCode() && !mVideoView.isPlaying()) //?????????????????????????????????
            return;

        if (mController.isShowing()) {
            mController.onHide();
        } else {
            mController.onShow();
        }
    }

    /**
     * ????????????????????????
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
     * ??????/??????
     */
    public void seekTo(int pos) {
        //mVideoView.setPlayerBufferingIndicator(null);
        mVideoView.seekTo(pos);
    }

    /**
     * ?????????????????????????????????
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
            public void onViewChangeEnd(Integer result) {//??????0????????????
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
     * ??????????????????????????????????????????????????????????????????
     */
    private void initSubPlayer() {
        mSubVideoView.setKeepScreenOn(true);
        mSubVideoView.setPlayerBufferingIndicator(mSubLoadingView);
    }

    /**
     * ??????????????????????????????????????????
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
                    LogUtils.i("sub ????????????");
                    mSubLoadingView.setVisibility(View.VISIBLE);
                } else if (what == PolyvMediaInfoType.MEDIA_INFO_BUFFERING_END) {
                    LogUtils.i("sub ????????????");
                    mSubLoadingView.setVisibility(View.GONE);
                }
            }
        });
        mSubVideoView.setOnPlayPauseListener(new IPolyvOnPlayPauseListener() {
            @Override
            public void onPreparing() {
                int playStage = mSubVideoView.getPlayStage();
                String tips = playStage == PolyvSubVideoView.PLAY_STAGE_HEADAD ? "????????????"
                        : playStage == PolyvSubVideoView.PLAY_STAGE_TEASER ? "????????????"
                        : playStage == PolyvSubVideoView.PLAY_STAGE_TAILAD ? "????????????" : "";
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
                LogUtils.i("sub onPlay???" + isFirst);
            }

            @Override
            public void onCompletion(IMediaPlayer mp) {
                LogUtils.i("sub teaser onCompletion");
            }
        });
        mSubVideoView.setOnSubVideoViewCountdownListener(new IPolyvOnSubVideoViewCountdownListener() {
            @Override
            public void onCountdown(int totalTime, int remainTime, @PolyvSubVideoView.AdStage int adStage) {
                if (adStage == PolyvSubVideoView.PLAY_STAGE_HEADAD) //????????????
                    mController.setHeadedCountdown(totalTime, remainTime);
            }

            @Override
            public void onVisibilityChange(boolean isShow) {
                LogUtils.i("sub onVisibilityChange???" + isShow);
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
                String tips = adStage == PolyvSubVideoView.PLAY_STAGE_HEADAD ? "????????????"
                        : adStage == PolyvSubVideoView.PLAY_STAGE_TAILAD ? "????????????" : "";
                LogUtils.i("sub " + tips + " onCompletion");
                if (adStage == PolyvSubVideoView.PLAY_STAGE_TAILAD) //????????????????????????
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
     * ???????????????????????????
     */
    private void initMainPlayer() {
        mVideoView.setSubVideoView(mSubVideoView);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setPlayerBufferingIndicator(mLoadingView);
        mVideoView.setNeedGestureDetector(true);
        mVideoView.setMediaController(mController);
    }

    /**
     * ???????????????????????????????????????
     */
    public void setVideoLeftRightMarginPx(int px) {
        this.mVideoLeftRightMarginPx = px;
        mVideoViewHeight = ScaleUtils.countScale(getContext(), 16, 9, mVideoLeftRightMarginPx);
    }

    /**
     * ????????????View??????
     */
    public int getVideoViewHeight() {
        return this.mVideoViewHeight;
    }

    /**
     * ????????????View???????????????16:9??? ?????????MATCH_PARENT/WRAP_CONTENT
     */
    public void setVideoViewHeight(int height) {
        this.mVideoViewHeight = height;
    }

    /**
     * ????????????View
     */
    public void showOrHiddenLoadingView(int visibility) {
        mLoadingView.setVisibility(visibility);
    }

    /**
     * ??????
     *
     * @param subView      true ??????????????????????????????????????? false ???????????????
     * @param playURL      ??????????????????CCTVMainPlayEntity??????????????????????????????
     * @param cplaymode    ?????????????????????
     * @param progressKeep ????????????????????????
     */
    public void play(String playURL, boolean subView, String cplaymode, boolean progressKeep) {
        if (mPlayEntity == null || TextUtils.isEmpty(playURL))
            return;

        mIsProgressKeep = progressKeep;
        if (mIsProgressKeep && !mPlayEntity.isLive()) {//?????????????????????????????????
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
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param playURL      ????????????
     * @param cplaymode    ????????????????????? cplaymode=1 ??????????????????cplaymode=2 ??????????????????
     * @param progressKeep ????????????????????????
     * @return ????????????0???????????????-1???????????????????????????????????????-2??? -3??????????????????
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
        if (mIsProgressKeep && !mPlayEntity.isLive()) {//?????????????????????????????????
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
     * ?????????????????????????????????
     */
    public void setFullScreenPlayerContainer(ViewGroup container) {
        this.mFullScreenPlayerContainer = container;
    }

    /**
     * ???????????????????????????????????????
     */
    public ViewGroup getFullScreenPlayerContainer() {
        return this.mFullScreenPlayerContainer;
    }

    /**
     * ????????????????????????
     */
    public boolean isProgressKeep() {
        return mIsProgressKeep;
    }

    /**
     * ??????????????????????????????
     */
    public void resetVideoLayout() {
        mFloatingLayerView.setVisibility(View.GONE);
        //mController.showOrHiddenPauseLayout(View.GONE);
    }

    /**
     * ??????????????????
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
     * ?????????????????????????????????????????????
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
                !mPlayEntity.getSubPlayEntity().getHeadadADList().isEmpty()) { //????????????
            playOption.put(PolyvPlayOption.KEY_HEADAD, mPlayEntity.getSubPlayEntity().getHeadadADList());
        }

        if (isLive) {//??????
            playOption.put(PolyvPlayOption.KEY_RECONNECTION_COUNT, 3);
            if (mPlayEntity.getSubPlayEntity() != null && !TextUtils.isEmpty(mPlayEntity.getSubPlayEntity().getTeaserADPlayURL()))//????????????
                playOption.put(PolyvPlayOption.KEY_TEASER, mPlayEntity.getSubPlayEntity().getTeaserADPlayURL());
        } else {//??????
            if (mPlayEntity.getSubPlayEntity() != null && !TextUtils.isEmpty(mPlayEntity.getSubPlayEntity().getTailadADPlayURL())) //????????????
                playOption.put(PolyvPlayOption.KEY_TAILAD, new PolyvPlayOption.TailAdOption(mPlayEntity.getSubPlayEntity().getTailadADPlayURL(), mPlayEntity.getSubPlayEntity().getTailadADDuration()));
        }

        if (headers != null) {
            playOption.put(PolyvPlayOption.KEY_HEADERS, headers);
        }
        return playOption;
    }

    /**
     * ????????????????????????
     */
    public CCTVVideoMediaController getMediaController() {
        return mController;
    }

    /**
     * ??????????????????
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
     * ?????????????????????????????????????????????
     */

    public void setIsForbiddenGestureSeek(boolean forbidden) {
        this.mIsForbiddenGestureSeek = forbidden;
    }

    /**
     * ??????????????????????????????????????????false
     */
    public void setIsForbiddenGesture(boolean forbidden) {
        this.mIsForbiddenGesture = forbidden;
    }

    /**
     * ??????????????????????????????false
     */
    public void setIsAllForbiddenGesture(boolean forbidden) {
        this.mIsAllForbiddenGesture = forbidden;
        this.mIsForbiddenGesture = forbidden;
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
     * ???????????????????????????
     */
    public boolean isSubVideoShow() {
        return mIsSubVideoShow;
    }

    //??????/????????????/??????????????????????????????
    private CCTVADClickListener mADClickListener;

    /**
     * ??????/????????????/??????????????????????????????
     */
    public void setADClickListener(CCTVADClickListener clickListener) {
        this.mADClickListener = clickListener;
    }

    /**
     * ??????????????????
     */
    public CCTVMainPlayEntity getPlayEntity() {
        return mPlayEntity;
    }

    /**
     * ????????????????????????
     */
    public void showOrHiddenRightView(int visibility) {
        mController.setBackground(true);
        if (View.GONE == visibility) {
            if (mVideoView.getCurrentState() == mVideoView.getStatePauseCode() && !mVideoView.isPlaying()) {  //????????????
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
     * ????????????????????????
     */
    public void setRightLayoutViewWidth() {
        if (getMediaController().isHorizontal())
            mRightLayoutView.getLayoutParams().width = ScreenUtils.getWidth(mContext) / 3;
        else
            mRightLayoutView.getLayoutParams().width = ScreenUtils.getWidth(mContext) / 2;
    }

    /**
     * ????????????????????????
     */
    public void setRightLayoutViewWidth(int width) {
        mRightLayoutView.getLayoutParams().width = width;
    }

    /**
     * ???????????????????????????
     */
    public void addRightCustomView(View view) {
        setRightLayoutViewWidth();
        mRightLayoutView.removeAllViews();
        if (view != null)
            mRightLayoutView.addView(view);
    }


    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????
     */
    public void setPlayListener(CCTVPlayListener listener) {
        mPlayListener = listener;
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????
     */
    public CCTVPlayListener getPlayListener() {
        return mPlayListener;
    }

    /**
     * ????????????????????????????????????????????????
     */
    public void onStop() {
        //mVideoView.stopPlayback(); //?????????????????????????????????????????????????????????
        mVideoView.stopPlay();
        mVideoView.removeRenderView();
        mVideoView.getSubVideoView().removeRenderView();
        mController.onReset();
    }

    /**
     * ???Controller???onRestart????????????
     */
    public void onActivityRestart() {

    }

    /**
     * ???Controller???onStop????????????
     */
    public void onActivityStop() {

    }

    /**
     * ???Controller???onDestroy????????????
     */
    public void onActivityDestroy() {

    }

    public boolean isAllForbiddenGesture() {
        return mIsAllForbiddenGesture;
    }

    /**
     * ??????VR??????
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
