package com.easefun.povplayer.core.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.asha.vrlib.MDVRLibrary;
import com.easefun.povplayer.core.config.PolyvPlayOption;
import com.easefun.povplayer.core.util.PolyvAudioFocusManager;
import com.easefun.povplayer.core.util.PolyvControlUtils;
import com.easefun.povplayer.core.util.PolyvLog;
import com.easefun.povplayer.core.util.PolyvVR;
import com.easefun.povplayer.core.video.listener.IPolyvOnSubVideoViewPlayStatusListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkMediaFormat;
import www.viewscenestv.com.ijkvideoview.IMediaController;
import www.viewscenestv.com.ijkvideoview.PolyvGLSurfaceRenderView;
import www.viewscenestv.com.ijkvideoview.PolyvGLTextureRenderView;
import www.viewscenestv.com.ijkvideoview.Settings;

/**
 * 主播放器
 */
public class PolyvVideoView extends PolyvVideoViewListenerEvent implements IPolyvVideoView {
    private static final String TAG = PolyvVideoView.class.getSimpleName();
    //------------------------------手势------------------------------//
    private float lastX;
    private float lastY;
    //是否需要手势
    private boolean mNeedGesture;
    //手势检测器
    private GestureDetector mGestureDetector;
    private int eventType = NONE;
    private static final int NONE = 0;
    private static final int RIGHT_DOWN = 1;
    private static final int RIGHT_UP = 2;
    private static final int LEFT_DOWN = 3;
    private static final int LEFT_UP = 4;
    private static final int SWIPE_LEFT = 5;
    private static final int SWIPE_RIGHT = 6;
    private static final double RADIUS_SLOP = Math.PI * 1 / 4;
    //------------------------------手势------------------------------//
    //上下文
    private Context mContext;
    private boolean destroyFlag;
    //播放地址
    private Uri playUri;
    //当前是否缓冲中
    private boolean isBuffering;
    //当前缓冲百分比
    private int mCurrentBufferPercentage;
    //播放器缓冲视图
    private View playerBufferingView;
    //播放器控制栏
    private IMediaController mMediaController;
    //子播放器
    private PolyvSubVideoView subVideoView;
    //是否第一次开始播放
    private boolean isFirstStart;
    private static final int WHAT_TIMEOUT = 12;
    //超时时间
    private int timeoutSecond;
    //重连次数，重连计数
    private int reconnectCount, reconnectCountdown;
    private PolyvPlayOption playOption;
    //播放配置集合
    private HashMap<String, Object> options;
    //点播模式
    public static final int PLAY_MODE_VOD = 4;
    //直播模式
    public static final int PLAY_MODE_LIVE = 5;
    //播放模式
    private int playMode = PLAY_MODE_VOD;
    //是否允许后台播放
    private boolean isEnableBackground;
    private Map<String, String> mHeaders;
    //音频焦点管理
    private PolyvAudioFocusManager audioFocusManager;
    private PolyvIjkVideoView ijkVideoView;
    //预加载时间
    private int inLastHeadAdPlayTime;
    //是否开启VR
    private boolean vrOn = false;
    private MDVRLibrary mdvrLibrary = null;
    private int vrDisplayMode = MDVRLibrary.DISPLAY_MODE_NORMAL;

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_TIMEOUT) {
                callOnDefineError(PolyvPlayError.ERROR_REQUEST_TIMEOUT);
            }
        }
    };

    public PolyvVideoView(Context context) {
        this(context, null);
    }

    public PolyvVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (mContext == null)
            init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        ijkVideoView = new PolyvIjkVideoView(mContext);
        initIPolyvIjkVideoView(ijkVideoView);
        addView(ijkVideoView);
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        setIjkLogLevel(IjkMediaPlayer.IJK_LOG_INFO);
        setLogTag(TAG);
        initGestureDetector();
        audioFocusManager = new PolyvAudioFocusManager(mContext);
        audioFocusManager.addPlayer(this);
    }

    @Override
    public PolyvIjkVideoView getIjkVideoView() {
        return ijkVideoView;
    }

    @Override
    public void setEnableBackgroundPlay(boolean enable) {
        isEnableBackground = enable;
    }

    @Override
    public boolean isBackgroundPlayEnabled() {
        return isEnableBackground;
    }

    private void initGestureDetector() {
        //----------------- 手势 -----------------------------------------------------//
        mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (subVideoView.isShow()) {
                    subVideoView.callOnGestureClickListener();
                }
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                callOnGestureClickListener();
                toggleMediaControlsVisiblity();
                eventType = NONE;
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                callOnGestureDoubleClickListener();
                eventType = NONE;
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (eventType == NONE) {
                    if (e1.getRawY() <= PolyvControlUtils.getStatusBarHeight(mContext))
                        return false;
                    if (PolyvControlUtils.hasVirtualNavigationBar(mContext)
                            && mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                            && mContext instanceof Activity) {
                        if (e1.getX() + PolyvControlUtils.getNavigationBarHeight(mContext) >= PolyvControlUtils.getDisplayWH((Activity) mContext)[0])
                            return false;
                    }
                }

                if (lastX == 0 || lastY == 0) {
                    lastX = e1.getX();
                    lastY = e1.getY();
                }

                int selfWidth = ((ViewGroup) getParent()).getMeasuredWidth();
                int selfHeight = ((ViewGroup) getParent()).getMeasuredHeight();
                int halfWidth = selfWidth / 2;
                float distancePPX = Math.abs(lastX - e2.getX());
                double percentWidth = selfWidth * 0.01;
                boolean UDCond = Math.abs(lastY - e2.getY()) > selfHeight * 0.05;
                boolean LRCond = distancePPX > percentWidth;
                int times = Math.max(1, (int) (distancePPX / percentWidth));
                final double distance = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
                final double radius = distanceY / distance;
                // 当角度值大于设置值时,当做垂直方向处理,反之当做水平方向处理
                if (Math.abs(radius) > RADIUS_SLOP && UDCond) {
                    // 右上下滑动
                    if ((lastX > halfWidth && (eventType == NONE || eventType == RIGHT_UP || eventType == RIGHT_DOWN))
                            || (lastX <= halfWidth && (eventType == RIGHT_UP || eventType == RIGHT_DOWN))) {
                        if (lastY > e2.getY()) {
                            // 上滑动
                            eventType = RIGHT_UP;
                            callOnGestureRightUpListener(true, false);
                        } else {
                            // 下滑动
                            eventType = RIGHT_DOWN;
                            callOnGestureRightDownListener(true, false);
                        }
                    } else if ((lastX <= halfWidth && (eventType == NONE || eventType == LEFT_UP || eventType == LEFT_DOWN))
                            || (lastX > halfWidth && (eventType == LEFT_UP || eventType == LEFT_DOWN))) {
                        // 左上下滑动
                        if (lastY > e2.getY()) {
                            // 上滑动
                            eventType = LEFT_UP;
                            callOnGestureLeftUpListener(true, false);
                        } else {
                            // 下滑动
                            eventType = LEFT_DOWN;
                            callOnGestureLeftDownListener(true, false);
                        }
                    }
                    lastX = e2.getX();
                    lastY = e2.getY();
                } else if (Math.abs(radius) <= RADIUS_SLOP && LRCond) {
                    if (eventType == NONE || eventType == SWIPE_LEFT || eventType == SWIPE_RIGHT) {
                        // 左右滑动
                        if (lastX > e2.getX()) {
                            // 左滑动
                            eventType = SWIPE_LEFT;
                            callOnGestureSwipeLeftListener(true, times, false);
                        } else {
                            // 右滑动
                            eventType = SWIPE_RIGHT;
                            callOnGestureSwipeRightListener(true, times, false);
                        }
                    }
                    lastX = e2.getX();
                    lastY = e2.getY();
                }

                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });
    }

    @Override
    public boolean canPlaySkipHeadAd() {
        return subVideoView.getPlayStage() == PolyvSubVideoView.PLAY_STAGE_HEADAD && subVideoView.isShow();
    }

    @Override
    public boolean playSkipHeadAd() {
        return playSkipHeadAd(true);
    }

    @Override
    public boolean playSkipHeadAd(boolean isSkipAll) {
        if (!canPlaySkipHeadAd()) {
            clear();
            return false;
        }
        if (isSkipAll || !subVideoView.hasNextHeadAd()) {
            subVideoView.stopPlay();
            subVideoView.hide();
            afterHeadAdPlay();
        } else {
            subVideoView.startHeadAd();
        }
        return true;
    }

    private void afterHeadAdPlay() {
        if (canPreload()) {
            if (isInPlaybackState()) {
                setOnPreparedListener(urlPlayPreparedListener);
                if (mdvrLibrary != null) {
                    mdvrLibrary.notifyPlayerChanged();
                    mdvrLibrary.onResume(mContext);
                }

                start();
            } else if (isPreparingState()) {
                setOnPreparedListener(urlPlayPreparedListener);
            } else if (getCurrentState() != getStateErrorCode()) {
                setVideoURI(playUri);
            }
        } else {
            subVideoView.startTeaser();
        }
    }

    private boolean canPreload() {
        return isVodPlayMode() || !subVideoView.isOpenTeaser();
    }

    @Override
    public void setSubVideoView(@NonNull final PolyvSubVideoView subVideoView) {
        this.subVideoView = subVideoView;
        subVideoView.addAudioFocusManager(audioFocusManager);
        subVideoView.setOnSubVideoViewPlayStatusListener(new IPolyvOnSubVideoViewPlayStatusListener() {
            @Override
            public void onCountdown(int totalTime, int remainTime, @PolyvSubVideoView.AdStage int adStage) {
                //在这里实现预加载
                if (totalTime - remainTime == inLastHeadAdPlayTime && adStage == PolyvSubVideoView.PLAY_STAGE_HEADAD
                        && subVideoView.isInPlaybackState() && !subVideoView.hasNextHeadAd()) {
                    if (canPreload()) {
                        preloadVideoUri(playUri);
                    }
                }
            }

            @Override
            public void onCompletion(@Nullable IMediaPlayer mp, @PolyvSubVideoView.AdStage int adStage) {
                if (adStage == PolyvSubVideoView.PLAY_STAGE_HEADAD && !subVideoView.hasNextHeadAd()
                        && subVideoView.getTargetState() == subVideoView.getStatePlaybackCompletedCode()) {//片头广告播放完成
                    if (canPreload()) {
                        subVideoView.hide();
                        afterHeadAdPlay();
                    }
                } else if (adStage == PolyvSubVideoView.PLAY_STAGE_TAILAD) {//片尾广告播放完成
                    subVideoView.hide();
                }
            }

            @Override
            public void onError(PolyvPlayError error) {
                callOnPlayErrorListener(error);
                if (error.playStage == PolyvPlayError.PLAY_STAGE_HEADAD && !subVideoView.hasNextHeadAd()
                        && subVideoView.getTargetState() == subVideoView.getStatePlaybackCompletedCode()) {//片头广告播放失败
                    if (canPreload()) {
                        subVideoView.hide();
                        afterHeadAdPlay();
                    }
                } else if (error.playStage == PolyvPlayError.PLAY_STAGE_TEASER) {//暖场视频放失败
                    subVideoView.hide();
                } else if (error.playStage == PolyvPlayError.PLAY_STAGE_TAILAD) {//片尾广告放失败
                    subVideoView.hide();
                }
            }
        });
    }

    @Override
    public PolyvSubVideoView getSubVideoView() {
        return subVideoView;
    }

    @Override
    public boolean isDolbyAudio() {
        int track = getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
        if (track == -1) {
            return false;
        }

        ITrackInfo[] trackInfos = getTrackInfo();
        if (trackInfos == null) {
            return false;
        }

        if (trackInfos.length <= track) {
            return false;
        }

        ArrayList<String> dolbyCodeNameList = new ArrayList<>();
        dolbyCodeNameList.add("ac3");
        dolbyCodeNameList.add("eac3");
        dolbyCodeNameList.add("dolbypolyv");

        ITrackInfo trackInfo = trackInfos[track];
        String codeName = trackInfo.getFormat().getString(IjkMediaFormat.KEY_IJK_CODEC_NAME_UI);
        return dolbyCodeNameList.contains(codeName);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 是否需要执行手势操作
        if (mNeedGesture) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                lastX = 0;
                lastY = 0;
                eventType = NONE;
            }
            if (mGestureDetector != null)
                mGestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                switch (eventType) {
                    case RIGHT_DOWN:
                        callOnGestureRightDownListener(false, true);
                        break;
                    case RIGHT_UP:
                        callOnGestureRightUpListener(false, true);
                        break;
                    case LEFT_DOWN:
                        callOnGestureLeftDownListener(false, true);
                        break;
                    case LEFT_UP:
                        callOnGestureLeftUpListener(false, true);
                        break;
                    case SWIPE_LEFT:
                        callOnGestureSwipeLeftListener(false, 1, true);
                        break;
                    case SWIPE_RIGHT:
                        callOnGestureSwipeRightListener(false, 1, true);
                        break;
                }
                lastX = 0;
                lastY = 0;
                eventType = NONE;
            }
            return true;
        } else {
            toggleMediaControlsVisiblity();
            return false;
        }
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        toggleMediaControlsVisiblity();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&
                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                keyCode != KeyEvent.KEYCODE_VOLUME_MUTE &&
                keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_CALL &&
                keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackStateEx() && isKeyCodeSupported && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (!isPlaying()) {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (isPlaying()) {
                    pause();
                    mMediaController.show();
                }
                return true;
            } else {
                toggleMediaControlsVisiblity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (isInPlaybackStateEx() && mMediaController != null) {
            if (mMediaController.isShowing()) {
                mMediaController.hide();
            } else {
                mMediaController.show();
            }
        }
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
    public boolean isInPlaybackStateEx() {
        return isInPlaybackState() && !subVideoView.isShow();
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
    public boolean setAspectRatio(@PolyvPlayerScreenRatio.RenderScreenRatio int screen) {
        if (getRenderView() == null) return false;
        setCurrentAspectRatio(screen);
        return true;
    }

    @Override
    public int getAspectRatio() {
        return getCurrentAspectRatio();
    }

    @Override
    public void setPlayerBufferingIndicator(View view) {
        this.playerBufferingView = view;
    }

    private void resetProperty() {
        isBuffering = false;
        mCurrentBufferPercentage = 0;
        mHeaders = null;
        reconnectCountdown = 0;
        isFirstStart = false;
    }

    @Override
    public boolean isOpenSound() {
        return PolyvControlUtils.isOpenSound(mContext);
    }

    @Override
    public void openSound() {
        PolyvControlUtils.openSound(mContext);
    }

    @Override
    public void closeSound() {
        PolyvControlUtils.closeSound(mContext);
    }

    @Override
    public void setVolume(int volume) {
        PolyvControlUtils.setVolume(mContext, volume);
    }

    @Override
    public int getVolume() {
        return PolyvControlUtils.getVolume(mContext);
    }

    @Override
    public void setBrightness(Activity activity, int brightness) {
        PolyvControlUtils.setBrightness(activity, brightness);
    }

    @Override
    public int getBrightness(Activity activity) {
        return PolyvControlUtils.getBrightness(activity);
    }

    @Override
    public void destroy() {
        PolyvLog.i(TAG, "destroy");
        destroyFlag = true;
        audioFocusManager.abandonAudioFocus();
        if (mdvrLibrary != null) {
            mdvrLibrary.onPause(mContext);
            mdvrLibrary.onDestroy();
        }

        subVideoView.destroy();
        stopBackgroundPlay();
        release(true);
        clearAllListener();
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public GestureDetector getGestureDetector() {
        return mGestureDetector;
    }

    @Override
    public boolean getNeedGestureDetector() {
        return mNeedGesture;
    }

    @Override
    public void setNeedGestureDetector(boolean need) {
        this.mNeedGesture = need;
    }

    //设置缓存视图可见性
    private void setPlayerBufferingViewVisibility(final int visibility) {
        if (playerBufferingView != null)
            playerBufferingView.setVisibility(visibility);
    }

    private void startTimeoutCountdown() {
        handler.removeMessages(WHAT_TIMEOUT);
        handler.sendEmptyMessageDelayed(WHAT_TIMEOUT, timeoutSecond * 1000);
    }

    private void stopTimeoutCountdown() {
        handler.removeMessages(WHAT_TIMEOUT);
    }

    private void hideController() {
        if (mMediaController != null)
            mMediaController.hide();
    }

    @Override
    public boolean isVodPlayMode() {
        return playMode == PLAY_MODE_VOD;
    }

    @Override
    public boolean isLivePlayMode() {
        return playMode == PLAY_MODE_LIVE;
    }

    private void initOptionParameters() {
        int frameDrop = (int) options.get(PolyvPlayOption.KEY_FRAMEDROP);
        frameDrop = Math.min(Math.max(0, frameDrop), 10);
        int decodeMode = (int) options.get(PolyvPlayOption.KEY_DECODEMODE);
        Object[][] optionParameters;
        if (playMode == PLAY_MODE_LIVE) {
            optionParameters = new Object[][]{
                    {IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 8},
                    {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", frameDrop},
                    {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", decodeMode}
            };
        } else {
            optionParameters = new Object[][]{
                    {IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 8},
                    {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", decodeMode}
            };
        }

        if (decodeMode == PolyvPlayOption.DECODEMODE_MEDIACODEC) {
            optionParameters = new Object[][]{
                    {IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 8},
                    {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", frameDrop},
                    {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 60},
                    {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", decodeMode},
                    {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1},
                    {IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1}
            };
        }

        Settings settings = new Settings(mContext);
        settings.setPlayer(Settings.PV_PLAYER__IjkMediaPlayer);
        if (vrOn) { //VR
            setVRViewInitCompletionListener(new PolyvGLSurfaceRenderView.OnInitCompletionListener() {
                @Override
                public void onCompletionListener(PolyvGLSurfaceRenderView view) {
                    if (mdvrLibrary != null) {
                        mdvrLibrary.onDestroy();
                    }

                    removeRenderView();
                    mdvrLibrary = PolyvVR.vrParamInit(getContext(), vrDisplayMode, view,
                            new MDVRLibrary.IOnSurfaceReadyCallback() {
                                @Override
                                public void onSurfaceReady(Surface surface) {
                                    if (getMediaPlayer() != null) {
                                        getMediaPlayer().setSurface(surface);
                                    }
                                }
                            }, new MDVRLibrary.INotSupportCallback() {
                                @Override
                                public void onNotSupport(int mode) {
                                    String tip = mode == MDVRLibrary.INTERACTIVE_MODE_MOTION
                                            ? "onNotSupport:MOTION" : "onNotSupport:" + String.valueOf(mode);
                                    PolyvLog.e(TAG, tip);
                                    onErrorState();
                                }
                            }, new MDVRLibrary.IGestureListener() {
                                @Override
                                public void onClick(MotionEvent e) {
                                    callOnGestureClickListener();
                                    toggleMediaControlsVisiblity();
                                }

                                @Override
                                public void onDoubleClick(MotionEvent e) {
                                    callOnGestureDoubleClickListener();
                                }
                            });
                }
            });

            setVRViewInitCompletionListener(new PolyvGLTextureRenderView.OnInitCompletionListener() {
                @Override
                public void onCompletionListener(PolyvGLTextureRenderView view) {
                    if (mdvrLibrary != null) {
                        mdvrLibrary.onDestroy();
                    }

                    removeRenderView();
                    mdvrLibrary = PolyvVR.vrParamInit(getContext(), vrDisplayMode, view,
                            new MDVRLibrary.IOnSurfaceReadyCallback() {
                                @Override
                                public void onSurfaceReady(Surface surface) {
                                    if (getMediaPlayer() != null) {
                                        getMediaPlayer().setSurface(surface);
                                    }
                                }
                            }, new MDVRLibrary.INotSupportCallback() {
                                @Override
                                public void onNotSupport(int mode) {
                                    String tip = mode == MDVRLibrary.INTERACTIVE_MODE_MOTION
                                            ? "onNotSupport:MOTION" : "onNotSupport:" + String.valueOf(mode);
                                    PolyvLog.e(TAG, tip);
                                    onErrorState();
                                }
                            }, new MDVRLibrary.IGestureListener() {
                                @Override
                                public void onClick(MotionEvent e) {
                                    callOnGestureClickListener();
                                    toggleMediaControlsVisiblity();
                                }

                                @Override
                                public void onDoubleClick(MotionEvent e) {
                                    callOnGestureDoubleClickListener();
                                }
                            });
                }
            });

            settings.setRenderViewType(PolyvRenderType.RENDER_GLSURFACE_VIEW);
            resetVRRender();
        } else {
            settings.setRenderViewType(PolyvRenderType.RENDER_TEXTURE_VIEW);
        }

        setOptionParameters(optionParameters);
    }

    @Override
    public PolyvPlayOption getPlayOption() {
        return playOption == null ? PolyvPlayOption.getDefault() : playOption;
    }

    @Override
    public void setOption(String playPath, PolyvPlayOption playOption) {
        clear(true);
        try {
            playUri = Uri.parse(playPath);
        } catch (NullPointerException e) {
            playUri = null;
        }
        if (playOption == null)
            playOption = PolyvPlayOption.getDefault();
        this.playOption = playOption;
        this.options = new HashMap<>(playOption.getOptions());
        this.playMode = (int) options.get(PolyvPlayOption.KEY_PLAYMODE);
        this.timeoutSecond = Math.max(5, (int) options.get(PolyvPlayOption.KEY_TIMEOUT));
        this.reconnectCount = Math.max(0, (int) options.get(PolyvPlayOption.KEY_RECONNECTION_COUNT));
        this.inLastHeadAdPlayTime = Math.max(0, (int) options.get(PolyvPlayOption.KEY_PRELOADTIME));
        this.vrOn = (boolean) options.get(PolyvPlayOption.KEY_VR_ON);
        subVideoView.initOption(options);
        initOptionParameters();
    }

    @Override
    public void startFromNew() {
        if (!isLivePlayMode())
            return;
        if (subVideoView.isShow()) {
            subVideoView.start();
            return;
        }
        if (isInPlaybackState()) {
            reloadLiveUri();
        }
        setTargetState(getStatePlayingCode());
    }

    private void reloadLiveUri() {
        audioFocusManager.requestAudioFocus();
        setPlayerBufferingViewVisibility(View.VISIBLE);
        resetLoadCost();
        setOnPreparedListener(urlPlayPreparedLiveListener);
        startTimeoutCountdown();
        hideController();
        super.resetVideoURI();
    }

    @Override
    public void stopPlay() {
        subVideoView.stopPlay();
        release(false);
        audioFocusManager.abandonAudioFocus();
    }

    private void clear() {
        clear(false);
    }

    private void clear(boolean isSetOption) {
        stopPlay();
        subVideoView.hide();
        subVideoView.resetPlayStage();
        subVideoView.resetLoadCost();
        subVideoView.removeRenderView();
        resetLoadCost();

        if (isSetOption || !vrOn()) {
            removeRenderView();
        }
    }

    @Override
    public void playFromHeadAd() {
        clear();
        if (subVideoView.isOpenHeadAd()) {
            subVideoView.startHeadAd();
        } else if (subVideoView.isOpenTeaser()) {
            subVideoView.startTeaser();
        } else {
            setVideoURI(playUri);
        }
    }

    @Override
    public void play() {
        clear();
        setVideoURI(playUri);
    }

    @Override
    public boolean playTailAd() {
        clear();
        if (subVideoView.isOpenTailAd()) {
            subVideoView.startTailAd();
            return true;
        }
        return false;
    }

    @Override
    public boolean playTeaser() {
        clear();
        if (subVideoView.isOpenTeaser()) {
            subVideoView.startTeaser();
            return true;
        }
        return false;
    }

    private void preloadVideoUri(Uri uri) {
        if (prepare(true)) {
            super.setVideoURI(uri, mHeaders);
        }
    }

    private boolean prepare(boolean isPreload) {
        if (destroyFlag)
            return false;
        release(false);
        callOnPlayPauseListenerPreparing();
        setPlayerBufferingViewVisibility(View.VISIBLE);
        resetLoadCost();
        setOnCompletionListener(urlPlayCompletionListener);
        setOnPreparedListener(isPreload ? urlPlayPreparedPreloadListener : urlPlayPreparedListener);
        setOnErrorListener(urlPlayErrorListener);
        setOnInfoListener(urlPlayInfoListener);
        if (getCurrentPlayPath() == null) {
            callOnDefineError(PolyvPlayError.ERROR_PLAYPATH_IS_NULL);
            return false;
        }

        if (options.containsKey(PolyvPlayOption.KEY_HEADERS)) {
            mHeaders = (Map<String, String>) options.get(PolyvPlayOption.KEY_HEADERS);
            if (mHeaders == null) {
                callOnDefineError(PolyvPlayError.ERROR_HEADERS_IS_NULL);
                return false;
            }
        }

        if (options.containsKey(PolyvPlayOption.KEY_HOST)) {
            String host = (String) options.get(PolyvPlayOption.KEY_HOST);
            if (TextUtils.isEmpty(host)) {
                callOnDefineError(PolyvPlayError.ERROR_OPTION_KEY_HOST_EMPTY);
                return false;
            } else {
                mHeaders = mHeaders == null ? new HashMap<String, String>() : mHeaders;
                mHeaders.put("host", " " + host);
            }
        }

        startTimeoutCountdown();
        return true;
    }

    @Override
    public String getCurrentPlayPath() {
        return playUri == null ? null : playUri.toString();
    }

    @SuppressLint("WrongConstant")
    private void callOnDefineError(@PolyvPlayError.PlayErrorCode int errorCode) {
        callOnError(PolyvPlayError.toErrorObj(getCurrentPlayPath(), errorCode, playMode));
        onErrorState();
    }

    private void callOnError(PolyvPlayError error) {
        stopTimeoutCountdown();
        setPlayerBufferingViewVisibility(View.GONE);
        callOnPlayErrorListener(error);
//        subVideoView.stopPlay();//主播放器播放(预加载)失败时，停止子播放器的播放
//        subVideoView.hide();
    }

    private void callOnPrepared(IMediaPlayer mp) {
        printVdec(mp);
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
        if (mMediaController instanceof PolyvBaseMediaController)
            ((PolyvBaseMediaController) mMediaController).onPrepared(mp);
    }

    private IMediaPlayer.OnCompletionListener urlPlayCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            setPlayerBufferingViewVisibility(View.GONE);
            callOnPlayPauseListenerCompletion(mp);
            if (isCompletedState() && subVideoView.isOpenTailAd() && subVideoView.getPlayStage() != PolyvSubVideoView.PLAY_STAGE_TAILAD_FINISH)
                subVideoView.startTailAd();
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
            callOnPrepared(mp);
            isFirstStart = false;
            if (getTargetState() != getStatePauseCode()) {
                if (mdvrLibrary != null) {
                    mdvrLibrary.notifyPlayerChanged();
                    mdvrLibrary.onResume(mContext);
                }

                start(isFirstStart = true);
            }
        }
    };

    private IMediaPlayer.OnPreparedListener urlPlayPreparedPreloadListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            callOnPrepared(mp);
        }
    };

    private IMediaPlayer.OnPreparedListener urlPlayPreparedLiveListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            stopTimeoutCountdown();
            setPlayerBufferingViewVisibility(View.GONE);
            if (reconnectCountdown != 0)
                PolyvLog.i(TAG, "直播重连成功");
            reconnectCountdown = 0;

            if (getTargetState() != getStatePauseCode()) {
                if (mdvrLibrary != null) {
                    mdvrLibrary.notifyPlayerChanged();
                    mdvrLibrary.onResume(mContext);
                }

                start(false);
            }
        }
    };

    private IMediaPlayer.OnErrorListener urlPlayErrorListener = new IMediaPlayer.OnErrorListener() {
        @SuppressLint("WrongConstant")
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            if (isVodPlayMode() || reconnectCount == reconnectCountdown) {
                callOnErrorListener(mp, what, extra);
                callOnError(PolyvPlayError.toErrorObj(mp != null ? mp.getDataSource() : getCurrentPlayPath(), what, playMode));
            } else {
                reconnectCountdown++;
                PolyvLog.i(TAG, "直播重连：" + reconnectCountdown);
                reloadLiveUri();
                setTargetState(getStatePlayingCode());
            }
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

    /**
     * 打印解码方式
     */
    private void printVdec(IMediaPlayer mMediaPlayer) {
        IjkMediaPlayer mp = null;
        if (mMediaPlayer == null)
            return;
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            mp = (IjkMediaPlayer) mMediaPlayer;
        } else if (mMediaPlayer instanceof MediaPlayerProxy) {
            MediaPlayerProxy proxy = (MediaPlayerProxy) mMediaPlayer;
            IMediaPlayer internal = proxy.getInternalMediaPlayer();
            if (internal != null && internal instanceof IjkMediaPlayer)
                mp = (IjkMediaPlayer) internal;
        }
        if (mp == null)
            return;

        int vdec = mp.getVideoDecoder();
        switch (vdec) {
            case IjkMediaPlayer.FFP_PROPV_DECODER_AVCODEC:
                Log.i(TAG, "vdec:avcodec");
                break;
            case IjkMediaPlayer.FFP_PROPV_DECODER_MEDIACODEC:
                Log.i(TAG, "vdec:MediaCodec");
                break;
            default:
                Log.i(TAG, "vdec is " + vdec);
                break;
        }
    }

    //--------------------override ijk---------------------------------------//

    @Override
    public int getBufferPercentage() {
        if (getMediaPlayer() != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    @Override
    public void setMediaController(IMediaController controller) {
        this.mMediaController = controller;
        if (controller instanceof PolyvBaseMediaController)
            ((PolyvBaseMediaController) mMediaController).setVideoView(this);
        super.setMediaController(controller);
    }

    @Override
    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    @Override
    public void setVideoURI(Uri uri) {
        if (prepare(false)) {
            super.setVideoURI(uri, mHeaders);
        }
    }

    @Override
    public void setVideoURI(Uri uri, Map<String, String> headers) {
        if (prepare(false)) {
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
        if (subVideoView.isShow()) {
            subVideoView.start();
            return false;
        } else {
            setTargetState(getStatePlayingCode());
            if (isInPlaybackState()) {
                audioFocusManager.requestAudioFocus();
                super.start();
                callOnPlayPauseListenerPlay(isFirst);
                return true;
            }
            return false;
        }
    }

    @Override
    public void pause() {
        pause(true);
    }

    @Override
    public void pause(boolean isAbandonAudioFocus) {
        if (subVideoView.isShow()) {
            subVideoView.pause(isAbandonAudioFocus);
        } else {
            setTargetState(getStatePauseCode());
            if (isInPlaybackState()) {
                if (isAbandonAudioFocus) {
                    audioFocusManager.abandonAudioFocus();
                }
                super.pause();
                callOnPlayPauseListenerPause();
            }
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
        stopTimeoutCountdown();
        hideController();
        resetProperty();
    }

    //--------------------override ijk---------------------------------------//

    @Override
    public boolean vrOn() {
        return vrOn;
    }

    @Override
    public void changeVRDisplayMode() {
        if (mdvrLibrary != null) {
            mdvrLibrary.switchDisplayMode(mContext);
            vrDisplayMode = mdvrLibrary.getDisplayMode();
        }
    }

    @Override
    public void onResume() {
        if (mdvrLibrary != null) {
            mdvrLibrary.onResume(mContext);
        }

        start();
    }

    @Override
    public void onPause() {
        if (mdvrLibrary != null) {
            mdvrLibrary.onPause(mContext);
        }

        pause();
    }

    @Override
    public void onConfigurationChanged() {
        if (mdvrLibrary != null) {
            mdvrLibrary.onOrientationChanged(mContext);
        }
    }
}
