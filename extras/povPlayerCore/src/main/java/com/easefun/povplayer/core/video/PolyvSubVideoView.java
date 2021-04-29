package com.easefun.povplayer.core.video;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntDef;

import com.easefun.povplayer.core.config.PolyvPlayOption;
import com.easefun.povplayer.core.util.PolyvAudioFocusManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import www.viewscenestv.com.ijkvideoview.IjkVideoView;

/**
 * 子播放器，处理广告，暖场视频的播放逻辑
 */
public class PolyvSubVideoView extends PolyvSubVideoViewListenerEvent implements IPolyvSubVideoView {
    private static final String TAG = PolyvSubVideoView.class.getSimpleName();
    //播放阶段
    private int mPlayStage = PLAY_STAGE_NONE;
    //无播放阶段，默认阶段
    public static final int PLAY_STAGE_NONE = 0;
    //片头广告阶段
    public static final int PLAY_STAGE_HEADAD = 1;
    //暖场阶段
    public static final int PLAY_STAGE_TEASER = 2;
    //片尾广告阶段
    public static final int PLAY_STAGE_TAILAD = 3;
    //片尾广告已播放完成阶段
    public static final int PLAY_STAGE_TAILAD_FINISH = 33;

    @IntDef({
            PLAY_STAGE_HEADAD,
            PLAY_STAGE_TEASER,
            PLAY_STAGE_TAILAD
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayStage {
    }

    @IntDef({
            PLAY_STAGE_HEADAD,
            PLAY_STAGE_TAILAD
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface AdStage {
    }

    // 上下文
    private Context mContext;
    private boolean destroyFlag;
    //当前是否缓冲中
    private boolean isBuffering;
    //当前缓冲百分比
    private int mCurrentBufferPercentage;
    //播放器缓冲视图
    private View playerBufferingView;

    //广告，暖场视频相关配置
    private boolean isOpenHeadAd, isOpenTailAd, isOpenTeaser;
    private List<PolyvPlayOption.HeadAdOption> headAdOptions;
    private int headAdPlayIndex = -1;
    private Uri headAdUri, tailAdUri, teaserUri;
    private int headAdDuration, tailAdDuration;
    //倒计时，倒计时总时长
    private int countdown, totalTime;
    //是否第一次开始播放
    private boolean isFirstStart;
    private static final int WHAT_TIMEOUT = 12;
    //超时时间
    private int timeoutSecond;
    //播放配置集合
    private HashMap<String, Object> options;
    //播放模式
    private int playMode = PolyvVideoView.PLAY_MODE_VOD;
    //音频焦点管理
    private PolyvAudioFocusManager audioFocusManager;
    //加载时loadingview延迟显示时间
    private int loadingViewDelayTime;
    private static final int SHOW_LOADINGVIEW_DELAY = 13;

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PLAY_STAGE_HEADAD:
                case PLAY_STAGE_TAILAD:
                    callOnSubVideoViewCountdown(msg.arg1, countdown, msg.what);
                    if (!isInPlaybackState())
                        break;
                    if (--countdown >= 0) {
                        Message message = handler.obtainMessage();
                        message.copyFrom(msg);
                        sendMessageDelayed(message, 1000);
                    } else {
                        release(false);
                        setTargetState(getStatePlaybackCompletedCode());
                        if (msg.what == PLAY_STAGE_HEADAD) {
                            callOnSubVideoViewPlayStatusComplete((IMediaPlayer) msg.obj, PLAY_STAGE_HEADAD);
                            if (getTargetState() == getStatePlaybackCompletedCode() && hasNextHeadAd())
                                startHeadAd();
                            else if (getTargetState() == getStatePlaybackCompletedCode() && isOpenTeaser())
                                startTeaser();
                        } else if (msg.what == PLAY_STAGE_TAILAD) {
                            callOnSubVideoViewPlayStatusComplete((IMediaPlayer) msg.obj, PLAY_STAGE_TAILAD);
                            mPlayStage = PLAY_STAGE_TAILAD_FINISH;
                        }
                    }
                    break;
                case WHAT_TIMEOUT:
                    callOnDefineError(PolyvPlayError.ERROR_REQUEST_TIMEOUT);
                    break;
                case SHOW_LOADINGVIEW_DELAY:
                    setPlayerBufferingViewVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    public PolyvSubVideoView(Context context) {
        this(context, null);
    }

    public PolyvSubVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvSubVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (mContext == null)
            init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        PolyvIjkVideoView ijkVideoView = new PolyvIjkVideoView(mContext);
        initIPolyvIjkVideoView(ijkVideoView);
        addView(ijkVideoView);
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        setIjkLogLevel(IjkMediaPlayer.IJK_LOG_INFO);
        setLogTag(TAG);
    }

    void addAudioFocusManager(PolyvAudioFocusManager audioFocusManager) {
        this.audioFocusManager = audioFocusManager;
    }

    @Override
    public boolean isShow() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    public void show() {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
            callOnSubVideoViewVisibilityChange(true);
        }
    }

    @Override
    public void hide() {
        if (getVisibility() == View.VISIBLE) {
            setVisibility(View.GONE);
            callOnSubVideoViewVisibilityChange(false);
        }
        setPlayerBufferingViewVisibility(View.GONE);
    }

    @Override
    public void resetPlayStage() {
        mPlayStage = PLAY_STAGE_NONE;
        headAdPlayIndex = -1;
    }

    @Override
    public int getPlayStage() {
        return mPlayStage;
    }

    @Override
    public String getHeadAdUrl() {
        return headAdUri == null ? null : headAdUri.toString();
    }

    @Override
    public String getTailAdUrl() {
        return tailAdUri == null ? null : tailAdUri.toString();
    }

    @Override
    public String getTeaserUrl() {
        return teaserUri == null ? null : tailAdUri.toString();
    }

    @Override
    public boolean isOpenHeadAd() {
        return isOpenHeadAd;
    }

    @Override
    public boolean isOpenTailAd() {
        return isOpenTailAd;
    }

    @Override
    public boolean hasNextHeadAd() {
        return headAdOptions != null && headAdPlayIndex != (headAdOptions.size() - 1);
    }

    private void getNextHeadAd() {
        if (!hasNextHeadAd())
            return;
        PolyvPlayOption.HeadAdOption headAdOption = headAdOptions.get(++headAdPlayIndex);
        try {
            headAdUri = Uri.parse(headAdOption.headAdPath);
        } catch (NullPointerException e) {
            headAdUri = null;
        }
        headAdDuration = headAdOption.headAdDuration;
    }

    private void initOptionParameters() {
        int frameDrop = (int) options.get(PolyvPlayOption.KEY_FRAMEDROP);
        frameDrop = Math.min(Math.max(0, frameDrop), 10);
        int decodeMode = (int) options.get(PolyvPlayOption.KEY_DECODEMODE);
        Object[][] optionParameters = new Object[][]{
                {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", frameDrop},
                {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", decodeMode}
        };
        if (decodeMode == PolyvPlayOption.DECODEMODE_MEDIACODEC) {
            optionParameters = new Object[][]{
                    {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", frameDrop},
                    {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", decodeMode},
                    {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1},
                    {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1}
            };
        }
        setOptionParameters(optionParameters);
    }

    @Override
    public void initOption(HashMap<String, Object> optins) {
        this.options = optins;
        this.playMode = (int) options.get(PolyvPlayOption.KEY_PLAYMODE);
        this.timeoutSecond = Math.max(5, (int) options.get(PolyvPlayOption.KEY_TIMEOUT));
        this.loadingViewDelayTime = Math.max(0, (int) options.get(PolyvPlayOption.KEY_LOADINGVIEW_DELAY));
        Object headObj = options.get(PolyvPlayOption.KEY_HEADAD);
        if (headObj instanceof PolyvPlayOption.HeadAdOption) {
            PolyvPlayOption.HeadAdOption headAdOption = (PolyvPlayOption.HeadAdOption) headObj;
            try {
                headAdUri = Uri.parse(headAdOption.headAdPath);
            } catch (NullPointerException e) {
                headAdUri = null;
            }
            headAdDuration = headAdOption.headAdDuration;
            isOpenHeadAd = true;
        } else if (headObj instanceof List && ((List) headObj).size() > 0) {
            headAdOptions = (List<PolyvPlayOption.HeadAdOption>) headObj;
            isOpenHeadAd = true;
        }
        PolyvPlayOption.TailAdOption tailAdOption = (PolyvPlayOption.TailAdOption) options.get(PolyvPlayOption.KEY_TAILAD);
        if (tailAdOption != null && isVodPlayMode()) {//只有点播才有片尾广告
            try {
                tailAdUri = Uri.parse(tailAdOption.tailAdPath);
            } catch (NullPointerException e) {
                tailAdUri = null;
            }
            tailAdDuration = tailAdOption.tailAdDuration;
            isOpenTailAd = true;
        }
        if (options.containsKey(PolyvPlayOption.KEY_TEASER) && isLivePlayMode()) {//只有直播才有暖场视频
            try {
                teaserUri = Uri.parse((String) options.get(PolyvPlayOption.KEY_TEASER));
            } catch (NullPointerException e) {
                teaserUri = null;
            }
            isOpenTeaser = true;
        }
        initOptionParameters();
    }

    @Override
    public void stopPlay() {
        release(false);
    }

    @Override
    public boolean isVodPlayMode() {
        return playMode == PolyvVideoView.PLAY_MODE_VOD;
    }

    @Override
    public boolean isLivePlayMode() {
        return playMode == PolyvVideoView.PLAY_MODE_LIVE;
    }

    @Override
    public boolean isOpenTeaser() {
        return isOpenTeaser;
    }

    @Override
    public void startHeadAd() {
        getNextHeadAd();
        mPlayStage = PLAY_STAGE_HEADAD;
        setVideoURI(headAdUri);
    }

    @Override
    public void startTailAd() {
        mPlayStage = PLAY_STAGE_TAILAD;
        setVideoURI(tailAdUri);
    }

    @Override
    public void startTeaser() {
        mPlayStage = PLAY_STAGE_TEASER;
        setVideoURI(teaserUri);
    }

    @Override
    public boolean isPreparedState() {
        return getMediaPlayer() != null && getCurrentState() == getStatePreparedCode();
    }

    @Override
    public boolean isPreparingState() {
        return getMediaPlayer() != null && getCurrentState() == getStatePreparingCode();
    }

    @Override
    public boolean isPlayState() {
        return isPlayState(false);
    }

    @Override
    public boolean isPlayState(boolean isIngoreBuffer) {
        if (isIngoreBuffer) {
            return isInPlaybackState() && isPlaying();
        } else {
            return isInPlaybackState() && isPlaying() && !isBuffering;
        }
    }

    @Override
    public boolean isInPlaybackState() {
        return isInPlaybackStateForwarding();
    }

    @Override
    public boolean isPauseState() {
        return isInPlaybackState() && getCurrentState() == getStatePauseCode();
    }

    @Override
    public boolean isBufferState() {
        return isInPlaybackState() && isBuffering;
    }

    @Override
    public boolean isCompletedState() {
        return isInPlaybackState() && getCurrentState() == getStatePlaybackCompletedCode();
    }

    @Override
    public void setPlayerBufferingIndicator(View view) {
        this.playerBufferingView = view;
    }

    //设置缓存视图可见性
    private void setPlayerBufferingViewVisibility(final int visibility) {
        if (playerBufferingView != null)
            playerBufferingView.setVisibility(visibility);
        if (visibility == View.GONE)
            handler.removeMessages(SHOW_LOADINGVIEW_DELAY);
    }

    private void resetProperty() {
        isBuffering = false;
        mCurrentBufferPercentage = 0;
    }

    @Override
    public boolean setAspectRatio(@PolyvPlayerScreenRatio.RenderScreenRatio int screen) {
        if (getRenderView() == null) return false;
        setCurrentAspectRatio(screen);
        return true;
    }

    @Override
    public int getAspectRatio() {
        return getCurrentAspectRatio();
    }

    private void startTimeoutCountdown() {
        handler.removeMessages(WHAT_TIMEOUT);
        handler.sendEmptyMessageDelayed(WHAT_TIMEOUT, timeoutSecond * 1000);
    }

    private void stopTimeoutCountdown() {
        handler.removeMessages(WHAT_TIMEOUT);
    }

    private void stopCountdown() {
        handler.removeMessages(PLAY_STAGE_HEADAD);
        handler.removeMessages(PLAY_STAGE_TAILAD);
    }

    private void startCountdown() {
        stopCountdown();
        if (countdown >= 0 && mPlayStage != PLAY_STAGE_TEASER) {
            Message message = handler.obtainMessage();
            message.obj = getMediaPlayer();
            message.what = mPlayStage;
            message.arg1 = totalTime;
            handler.sendMessage(message);
        }
    }

    @Override
    public void destroy() {
        destroyFlag = true;
        release(true);
        clearAllListener();
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public String getCurrentPlayPath() {
        Uri playUri = mPlayStage == PLAY_STAGE_HEADAD ? headAdUri
                : mPlayStage == PLAY_STAGE_TAILAD ? tailAdUri
                : mPlayStage == PLAY_STAGE_TEASER ? teaserUri : null;
        return playUri == null ? null : playUri.toString();
    }

    private void callOnDefineError(@PolyvPlayError.PlayErrorCode int errorCode) {
        callOnError(PolyvPlayError.toErrorObj(getCurrentPlayPath(), errorCode, getPlayStage()));
        onErrorState();
    }

    private void callOnError(PolyvPlayError error) {
        stopCountdown();
        stopTimeoutCountdown();
        setPlayerBufferingViewVisibility(View.GONE);
        setTargetState(getStatePlaybackCompletedCode());
        switch (mPlayStage) {
            case PLAY_STAGE_HEADAD:
                callOnSubVideoViewPlayStatusError(error);
                if (getTargetState() == getStatePlaybackCompletedCode() && hasNextHeadAd())
                    startHeadAd();
                else if (getTargetState() == getStatePlaybackCompletedCode() && isOpenTeaser())
                    startTeaser();
                break;
            case PLAY_STAGE_TAILAD:
                callOnSubVideoViewPlayStatusError(error);
                break;
            case PLAY_STAGE_TEASER:
                callOnSubVideoViewPlayStatusError(error);
                break;
        }
    }

    private void showLoadingViewDelay() {
        handler.removeMessages(SHOW_LOADINGVIEW_DELAY);
        handler.sendEmptyMessageDelayed(SHOW_LOADINGVIEW_DELAY, loadingViewDelayTime);
    }

    private boolean prepare() {
        if (destroyFlag)
            return false;
        show();
        release(false);
        callOnPlayPauseListenerPreparing();
        showLoadingViewDelay();
        resetLoadCost();
        setOnCompletionListener(urlPlayCompletionListener);
        setOnPreparedListener(urlPlayPreparedListener);
        setOnErrorListener(urlPlayErrorListener);
        setOnInfoListener(urlPlayInfoListener);
        if (getCurrentPlayPath() == null) {
            callOnDefineError(PolyvPlayError.ERROR_PLAYPATH_IS_NULL);
            return false;
        }
        startTimeoutCountdown();
        return true;
    }

    private IMediaPlayer.OnCompletionListener urlPlayCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            setPlayerBufferingViewVisibility(View.GONE);
            if (mPlayStage == PLAY_STAGE_TEASER) {
                callOnPlayPauseListenerCompletion(mp);
                if (isCompletedState() && getTargetState() != getStatePauseCode())
                    start();
            }
        }
    };

    private IMediaPlayer.OnSeekCompleteListener urlPlaySeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            callOnSeekCompleteListener(mp);
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener urlPlayVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            callOnVideoSizeChangedListener(mp, width, height, sar_num, sar_den);
        }
    };

    private IMediaPlayer.OnPreparedListener urlPlayPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            setOnSeekCompleteListener(urlPlaySeekCompleteListener);
            setOnVideoSizeChangedListener(urlPlayVideoSizeChangedListener);
            getMediaPlayer().setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                    callOnBufferingUpdateListener(mp, percent);
                }
            });

            stopTimeoutCountdown();
            setPlayerBufferingViewVisibility(View.GONE);
            callOnPreparedListener(mp);

            int secondDuration = Math.max(getDuration() / 1000, 1);
            switch (mPlayStage) {
                case PLAY_STAGE_HEADAD:
                    if (headAdDuration <= 0 || headAdDuration > secondDuration)
                        headAdDuration = secondDuration;
                    countdown = totalTime = headAdDuration;
                    break;
                case PLAY_STAGE_TAILAD:
                    if (tailAdDuration <= 0 || tailAdDuration > secondDuration)
                        tailAdDuration = secondDuration;
                    countdown = totalTime = tailAdDuration;
                    break;
                default:
                    countdown = 0;
                    break;
            }
            isFirstStart = false;
            if (getTargetState() != getStatePauseCode())
                start(isFirstStart = true);
        }
    };

    private IMediaPlayer.OnErrorListener urlPlayErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            callOnErrorListener(mp, what, extra);
            callOnError(PolyvPlayError.toErrorObj(mp != null ? mp.getDataSource() : getCurrentPlayPath(), what, getPlayStage()));
            return true;
        }
    };

    private IMediaPlayer.OnInfoListener urlPlayInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, Object extra) {
            callOnInfoListener(mp, what, extra);
            if (getMediaPlayer() != null) {
                if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    isBuffering = true;
                    setPlayerBufferingViewVisibility(View.VISIBLE);
                } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    isBuffering = false;
                    setPlayerBufferingViewVisibility(View.GONE);
                }
            }
            return true;
        }
    };

    //--------------------override ijk---------------------------------------//

    @Override
    public int getBufferPercentage() {
        if (getMediaPlayer() != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    @Override
    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    @Override
    public void setVideoURI(Uri uri) {
        if (prepare()) {
            super.setVideoURI(uri);
        }
    }

    @Override
    public void setVideoURI(Uri uri, Map<String, String> headers) {
        if (prepare()) {
            super.setVideoURI(uri, headers);
        }
    }

    @Override
    public void start() {
        if (isFirstStart)
            start(false);
        else if (start(true))
            isFirstStart = true;
    }

    private boolean start(boolean isFirst) {
        setTargetState(getStatePlayingCode());
        if (isInPlaybackState() && countdown >= 0) {
            startCountdown();
            audioFocusManager.requestAudioFocus();
            super.start();
            callOnPlayPauseListenerPlay(isFirst);
            return true;
        }
        return false;
    }

    @Override
    public void pause() {
        pause(true);
    }

    @Override
    public void pause(boolean isAbandonAudioFocus) {
        setTargetState(getStatePauseCode());
        if (isInPlaybackState() && countdown >= 0) {
            stopCountdown();
            if (isAbandonAudioFocus) {
                audioFocusManager.abandonAudioFocus();
            }
            super.pause();
            callOnPlayPauseListenerPause();
        }
    }

    @Override
    public void seekTo(int pos) {
        if (pos >= getDuration())
            pos = getDuration() - 100;
        else if (pos < 0)
            pos = 0;
        super.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return super.isPlaying() || getTargetState() == getStatePlayingCode();
    }

    @Override
    public void release(boolean cleartargetstate) {
        super.release(cleartargetstate);
        setTargetState(getStateIdleCode());
        setPlayerBufferingViewVisibility(View.GONE);
        resetProperty();
        stopCountdown();
        stopTimeoutCountdown();
    }
}
