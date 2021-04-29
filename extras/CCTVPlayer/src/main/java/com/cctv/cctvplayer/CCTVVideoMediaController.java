package com.cctv.cctvplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cctv.cctvplayer.entity.CCTVStreamEntity;
import com.cctv.cctvplayer.entity.CCTVSubPlayEntity;
import com.cctv.cctvplayer.listener.CCTVControllerListener;
import com.cctv.cctvplayer.listener.CCTVLockScreenListener;
import com.cctv.cctvplayer.listener.CCTVOrientationListener;
import com.cctv.cctvplayer.listener.CCTVPlayOrPauseListener;
import com.cctv.cctvplayer.player.Orientation;
import com.cctv.cctvplayer.player.PlayerOperate;
import com.cctv.cctvplayer.utils.LogUtils;
import com.cctv.cctvplayer.utils.ScreenUtils;
import com.cctv.cctvplayer.utils.SystemBarUtils;
import com.cctv.cctvplayer.utils.TimeUtils;
import com.cctv.cctvplayer.utils.ViewUtils;
import com.cctv.cctvplayer.widget.PauseTipsView;
import com.cctv.cctvplayer.widget.RateTipsView;
import com.cctv.cctvplayer.widget.ScrollableSeekBar;
import com.cctv.cctvplayer.widget.ThumbImageView;
import com.easefun.povplayer.core.video.PolyvBaseMediaController;
import com.easefun.povplayer.core.video.PolyvSubVideoView;
import com.easefun.povplayer.core.video.PolyvVideoView;

import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器控制栏
 */
public class CCTVVideoMediaController extends PolyvBaseMediaController implements View.OnClickListener {

    private static final int SHOW_TIME = 3000; //控制栏显示时间ms
    private static final int HIDE = 101; // 隐藏控制栏
    private static final int SHOW_PROGRESS = 102; //拖动进度条
    private static final int LIVE_PROGRESS_TIMER = 103; //拖动进度条
    private long mLiveProgressMs;//直播进度的位置，时间戳
    private long mLiveSystemTimeMs;//用与计算进度条平均时间的
    private CCTVControllerListener mControllerListener; //控制兰显示/隐藏的回调
    private CCTVLockScreenListener mLockScreenListener; //锁屏/解锁屏幕监听
    private CCTVPlayOrPauseListener mPlayOrPauseListener; //播放/暂停 监听

    private CCTVVideoView mCCTVVideoView;
    //子播放器，用于播放广告及暖场
    private PolyvSubVideoView mSubVideoView;
    //是否固定返回键
    private boolean mIsBackFixed = true;

    //主播放器
    private PolyvVideoView mVideoView;
    //主视频进入后台前是否是播放状态
    private boolean mIsPlayingOnStop;
    //横竖屏处理
    private Orientation mOrientation;

    //不跟随控制栏隐藏显示的返回
    private RelativeLayout mBackFixedLayout;
    //返回
    private ImageView mBackView;
    //标题
    private TextView mTitleView;
    //全屏
    private ImageView mFullScreenView;
    //子播放器全屏
    private ImageView mSubFullScreenView;
    //子播放器跳过
    private TextView mSubSkipView;
    //直播状态，绿色播放最新的直播，红色代表正在回看，点击后可观看最新直播
    private ImageView mLiveStatusView;
    //当前播放时间
    private TextView mCurTimeView;
    //总时间
    private TextView mEndTimeView;
    //进度条
    private ScrollableSeekBar mPlayProgressView;//进度条
    //进度条打点的View保持和ScrollableSeekBar的宽度一致
    private LinearLayout mPlayProgressNoteLayout;
    //进度条拖动的View
    private ThumbImageView mThumbView;
    //进度条的宽度，横竖屏会改变
    private int mSeekBarWidth;
    //码率
    private TextView mRateView;
    //锁屏
    private ImageView mLockView;
    //播放/暂停
    private ImageView mPlayPauseView;
    //顶部控制栏
    private RelativeLayout mTopLayout;
    //底部控制栏
    private RelativeLayout mBottomLayout;
    //固定的顶部控制栏
    private LinearLayout mFixedTopLayout;
    //暂停时显示的View
    private PauseTipsView mPauseTipsView;
    //暂停布局是否显示。默认不显示
    private boolean isShowPauseView;
    //强制隐藏显示顶部控制栏
    private int forceTopController = -100;
    //强制隐藏显示底部控制栏
    private int forceBottomController = -100;
    //顶部导航栏，右侧自定义布局
    private LinearLayout mRightTopLayout;
    //底部导航栏，右侧自定义布局
    private LinearLayout mRightBottomLayout;
    //左下角，显示的自定义布局
    private LinearLayout mLeftBottomLayoutLayout;
    // 是否正在拖拽中
    private boolean mIsDragging;
    // 控制栏是否正在显示
    private boolean mIsShowing;
    //当前控制栏显示的状态
    private int mCurShowStatus;
    //当前控制栏隐藏的状态
    private int mCurHiddenStatus;
    //是否禁止隐藏控制栏
    private boolean mIsDisableHiddenControler;
    //控制栏左侧布局
    private LinearLayout mControllerLeftLayout;
    //控制栏右侧布局
    private LinearLayout mControllerRightLayout;
    //控制栏中间布局
    private LinearLayout mControllerCenterLayout;
    //横竖屏的时候，是否控制系统状态栏
    private boolean mIsSystemStatusBar = false;

    public CCTVVideoMediaController(Context context) {
        this(context, null);
    }

    public CCTVVideoMediaController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CCTVVideoMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setCCTVVideoView(CCTVVideoView view) {
        this.mCCTVVideoView = view;
    }

    public CCTVVideoView getCCTVVideoView() {
        return this.mCCTVVideoView;
    }


    public void setSubVideoView(PolyvSubVideoView view) {
        this.mSubVideoView = view;
    }

    /**
     * 返回横竖屏操作控制类对象
     */
    public Orientation getOrientation() {
        return mOrientation;
    }

    public RelativeLayout getTopLayout() {
        return mTopLayout;
    }

    public RelativeLayout getBottomLayout() {
        return mBottomLayout;
    }

    /**
     * 强制显示隐藏顶部控制器
     */
    public void forceShowOrHiddenTopController(int visibility) {
        if (View.GONE == visibility)
            visibility = View.INVISIBLE;
        forceTopController = visibility;
        mHandler.removeMessages(HIDE);
        mTopLayout.setVisibility(visibility);
    }

    /**
     * 强制显示隐藏底部控制器
     */
    public void forceShowOrHiddenBottomController(int visibility) {
        if (View.GONE == visibility)
            visibility = View.INVISIBLE;
        forceBottomController = visibility;
        mHandler.removeMessages(HIDE);
        mBottomLayout.setVisibility(visibility);
    }

    private void initView(Context context) {
        if (!(context instanceof Activity) && !((Activity) context).isFinishing()) {
            LogUtils.e("must use activity inflate controller");
            return;
        }

        mOrientation = new Orientation(context, this);
        View view = LayoutInflater.from(context).inflate(R.layout.cctv_videoview_controller, this);
        findViews(view);
        setListeners();
        mHandler.sendEmptyMessageDelayed(HIDE, SHOW_TIME);
    }

    private void findViews(View view) {
        mTopLayout = view.findViewById(R.id.topLayout);
        mBottomLayout = view.findViewById(R.id.bottomLayout);
        mFixedTopLayout = view.findViewById(R.id.fixedTopLayout);
        mPauseTipsView = view.findViewById(R.id.pauseLayout);
        mPauseTipsView.setMediaController(this);
        mBackView = view.findViewById(R.id.back);
        mBackFixedLayout = view.findViewById(R.id.backFixedLayout);
        mTitleView = view.findViewById(R.id.title);
        mFullScreenView = view.findViewById(R.id.fullScreen);
        mSubFullScreenView = view.findViewById(R.id.subFullScreen);
        mSubSkipView = view.findViewById(R.id.subSkip);
        mSubSkipView.setEnabled(false);
        mLiveStatusView = view.findViewById(R.id.liveStatus);
        mCurTimeView = view.findViewById(R.id.currenTime);
        mEndTimeView = view.findViewById(R.id.endTime);
        mPlayProgressView = view.findViewById(R.id.playProgress);
        mPlayProgressNoteLayout = view.findViewById(R.id.playProgressNodeLayout);
        mThumbView = view.findViewById(R.id.thumbView);
        mThumbView.setSeekBarParentView(mPlayProgressNoteLayout);
        enlargeSeekBar();
        mRateView = view.findViewById(R.id.rate);
        mLockView = view.findViewById(R.id.lock);
        mPlayPauseView = view.findViewById(R.id.playPause);
        mPlayPauseView.setSelected(true);
        mLeftBottomLayoutLayout = view.findViewById(R.id.leftBottomLayout);
        mRightTopLayout = view.findViewById(R.id.rightTopLayout);
        mRightBottomLayout = view.findViewById(R.id.rightBottomLayout);
        mControllerLeftLayout = view.findViewById(R.id.controllerLeftLayout);
        mControllerRightLayout = view.findViewById(R.id.controllerRightLayout);
        mControllerCenterLayout = view.findViewById(R.id.controllerCenterLayout);
    }

    /**
     * 添加左下角，显示的自定义布局
     */
    public void addLeftBottomCustomView(View view) {
        this.addLeftBottomCustomView(view, null);
    }

    /**
     * 添加左下角，显示的自定义布局
     */
    public void addLeftBottomCustomView(View view, ViewGroup.LayoutParams params) {
        mLeftBottomLayoutLayout.removeAllViews();
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            mLeftBottomLayoutLayout.setVisibility(View.VISIBLE);
            if (params != null)
                mLeftBottomLayoutLayout.addView(view, params);
            else
                mLeftBottomLayoutLayout.addView(view);
        } else {
            mLeftBottomLayoutLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 添加控制栏中间自定义布局
     */
    public void addControllerCenterCustomView(View view) {
        this.addControllerCenterCustomView(view, null);
    }

    /**
     * 添加控制栏中间自定义布局
     */
    public void addControllerCenterCustomView(View view, ViewGroup.LayoutParams params) {
        mControllerCenterLayout.removeAllViews();
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            mControllerCenterLayout.setVisibility(View.VISIBLE);
            if (params != null)
                mControllerCenterLayout.addView(view, params);
            else
                mControllerCenterLayout.addView(view);
        } else {
            mControllerCenterLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 添加控制栏左侧自定义布局
     */
    public void addControllerLeftCustomView(View view, int index) {
        this.addControllerLeftCustomView(view, index, null);
    }

    /**
     * 添加控制栏左侧自定义布局
     */
    public void addControllerLeftCustomView(View view, int index, ViewGroup.LayoutParams params) {
        for (int i = 0; i < mControllerLeftLayout.getChildCount(); i++) {
            View childView = mControllerLeftLayout.getChildAt(i);
            if (childView.getId() != mLockView.getId())
                mControllerLeftLayout.removeViewAt(i);
        }

        if (view != null) {
            view.setVisibility(View.VISIBLE);
            mControllerLeftLayout.setVisibility(View.VISIBLE);
            if (params != null)
                mControllerLeftLayout.addView(view, index, params);
            else
                mControllerLeftLayout.addView(view, index);
        } else {
            mControllerLeftLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 添加控制栏右侧自定义布局
     */
    public void addControllerRightCustomView(View view, int index) {
        this.addControllerRightCustomView(view, index);
    }

    /**
     * 添加控制栏右侧自定义布局
     */
    public void addControllerRightCustomView(View view, int index, ViewGroup.LayoutParams params) {
        mControllerRightLayout.removeAllViews();

        if (view != null) {
            view.setVisibility(View.VISIBLE);
            mControllerRightLayout.setVisibility(View.VISIBLE);
            if (params != null)
                mControllerRightLayout.addView(view, index, params);
            else
                mControllerRightLayout.addView(view, index);
        } else {
            mControllerRightLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 解决SeekBar太细了，触摸不到
     */
    public void enlargeSeekBar() {
        ((ViewGroup) mPlayProgressView.getParent()).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Rect seekRect = new Rect();
                mPlayProgressView.getHitRect(seekRect);
                if ((event.getY() >= (seekRect.top - 500)) && (event.getY() <= (seekRect.bottom + 500))) {
                    float y = seekRect.top + seekRect.height() / 2;
                    float x = event.getX() - seekRect.left;
                    if (x < 0) {
                        x = 0;
                    } else if (x > seekRect.width()) {
                        x = seekRect.width();
                    }
                    MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
                            event.getAction(), x, y, event.getMetaState());
                    return mPlayProgressView.onTouchEvent(me);
                }
                return false;
            }
        });
    }

    /**
     * 初始化点播和直播播放进度条的View
     */
    public void initPlayProgressView() {
        LogUtils.i("initPlayProgressView...");
        if (isLive()) {
            mPlayProgressView.setProgress(mPlayProgressView.getMax());
            mPlayProgressView.setSecondaryProgress(mPlayProgressView.getMax());
            mPlayProgressView.setScrollable(false);
            mLiveProgressMs = System.currentTimeMillis();
            mLiveSystemTimeMs = System.currentTimeMillis();
            mLiveStatusView.setSelected(false);
            mLiveStatusView.setVisibility(View.VISIBLE);
            mCurTimeView.setText(TimeUtils.format(getLivePlayedMs(), "HH:mm:ss"));
            mHandler.removeMessages(LIVE_PROGRESS_TIMER);
            mHandler.sendEmptyMessageDelayed(LIVE_PROGRESS_TIMER, 1000);
        } else {
            mLiveStatusView.setVisibility(View.GONE);
            mPlayProgressView.setProgress(0);
            mPlayProgressView.setSecondaryProgress(0);
            mPlayProgressView.setScrollable(true);
            mCurTimeView.setText("00:00");
        }
    }

    /**
     * 顶部导航栏，右侧自定义布局
     */
    public void addRightTopCustomView(View view) {
        mRightTopLayout.removeAllViews();
        if (view != null)
            mRightTopLayout.addView(view);
    }

    /**
     * 顶部导航栏，右侧自定义布局
     */
    public void addRightTopCustomView(View view, int index) {
        if (view != null)
            mRightTopLayout.addView(view, index);
    }

    /**
     * 底部导航栏，右侧自定义布局
     */
    public void addRightBottomCustomView(View view, int index) {
        for (int i = 0; i < mRightBottomLayout.getChildCount(); i++) {
            View childView = mRightBottomLayout.getChildAt(i);
            if (childView.getId() != mRateView.getId())
                mRightBottomLayout.removeViewAt(i);
        }

        if (view != null)
            mRightBottomLayout.addView(view, index);
    }

    /**
     * 进度条打点的View
     */
    public void addPlayProgressNoteCustomView(View view) {
        mPlayProgressNoteLayout.removeAllViews();
        if (view != null)
            mPlayProgressNoteLayout.addView(view);
    }

    /**
     * 显示隐藏顶部导航栏，右侧自定义布局。可
     */
    public void showOrHiddenRightTopCustomView(int visibility) {
        mRightTopLayout.setVisibility(visibility);
    }

    /**
     * 设置底部导航码率名称
     */
    public void setBottomRateName() {
        if (mCCTVVideoView.getPlayEntity() != null) {
            String rateName = mCCTVVideoView.getPlayEntity().getCurPlayRateName();
            if (!TextUtils.isEmpty(rateName))
                mRateView.setText(rateName);
//            else
//                mRateView.setVisibility(View.GONE);
        }
    }

    /**
     * 获取底部导航码率名称
     */
    public String getBottomRateName() {
        if (mRateView == null) {
            return null;
        }
        return mRateView.getText().toString();
    }

    /**
     * 如果没有设置默认码率，且只有一种码率。就隐藏码率切换按钮
     */
    private boolean isShowRateView() {
        if (mCCTVVideoView.getPlayEntity() != null) {
            List<CCTVStreamEntity> rateList = mCCTVVideoView.getPlayEntity().getCodeRateList();
            if (rateList == null || rateList.isEmpty())
                return false;
            if (rateList.size() > 1)
                return true;
            return rateList.get(0).isDefault();
        }
        return false;
    }

    private void setListeners() {
        mBackView.setOnClickListener(this);
        mBackFixedLayout.setOnClickListener(this);
        mFullScreenView.setOnClickListener(this);
        mSubFullScreenView.setOnClickListener(this);
        mSubSkipView.setOnClickListener(this);
        mPlayPauseView.setOnClickListener(this);
        mLockView.setOnClickListener(this);
        mRateView.setOnClickListener(this);
        mPauseTipsView.setDrawBackClickListener(this);
        mPauseTipsView.setAdvanceClickListener(this);
        mLiveStatusView.setOnClickListener(this);
        mPlayProgressView.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mPlayProgressView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mSeekBarWidth != v.getWidth()) {
                    mSeekBarWidth = v.getWidth();
                    LogUtils.i("进度条View改变了： " + mSeekBarWidth);
                    setSeekBarThumbPosition(mPlayProgressView.getProgress());
                }
            }
        });
    }

    @Override
    public void hide() {
    }

    public boolean isLock() {
        return mLockView.getTag() != null;
    }

    /**
     * 强制解锁
     */
    public void unLock() {
        if (mLockView.getTag() != null) {
            mLockView.setTag(null);
            mLockView.setImageResource(R.drawable.unlock);
            if (mLockScreenListener != null)
                mLockScreenListener.onUnLockScreen();
        }
    }

    /**
     * 是否禁用隐藏控制栏
     */
    public void setDisableHiddenControler(boolean disable) {
        mIsDisableHiddenControler = disable;
    }

    public void onHide() {
        onHide(true);
    }

    public void onHide(boolean hideLockView) {
        if (mIsDragging || mIsDisableHiddenControler || mCCTVVideoView.isAllForbiddenGesture())
            return;
        LogUtils.i("onHide：" + hideLockView);
        mHandler.removeMessages(HIDE);
        mHandler.removeMessages(SHOW_PROGRESS);
        mIsShowing = false;
        mTopLayout.setVisibility(View.INVISIBLE);
        mBottomLayout.setVisibility(View.INVISIBLE);
        if (hideLockView) {
            showOrHiddenLockView(View.GONE);
            if ((!isHorizontal() && mIsBackFixed) || mCCTVVideoView.isSubVideoShow())
                mBackFixedLayout.setVisibility(View.VISIBLE);
        } else {
            mHandler.sendEmptyMessageDelayed(HIDE, SHOW_TIME);
        }
        if (isHorizontal())
            SystemBarUtils.showOrHiddenStatusBar((Activity) getContext(), false);
        forceShowOrHiddenController();
        onCallbackControllerListener(2);
    }

    public void onShow() {
        onShow(true);
    }

    public void onShow(boolean hide) {
        if (mCCTVVideoView.isSubVideoShow() || mCCTVVideoView.isAllForbiddenGesture()) {  //显示子播放器时，不显示主播放器控制栏
            return;
        }

        LogUtils.i("onShow：" + hide);
        mHandler.removeMessages(HIDE);
        if (mLockView.getTag() == null) {
            mIsShowing = true;
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
            mTopLayout.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.VISIBLE);
        }

        showOrHiddenLockView(View.VISIBLE);
        mBackFixedLayout.setVisibility(View.GONE);
        if (hide)
            mHandler.sendEmptyMessageDelayed(HIDE, SHOW_TIME);
        if (isHorizontal() && !isLock())
            SystemBarUtils.showOrHiddenStatusBar((Activity) getContext(), true);
        forceShowOrHiddenController();
        onCallbackControllerListener(1);
    }

    /**
     * 强制显示隐藏上下控制栏
     */
    private void forceShowOrHiddenController() {
        if (forceTopController != -100) { //强制显示隐藏
            if (forceTopController == View.VISIBLE)
                mTopLayout.setVisibility(View.VISIBLE);
            else if (forceTopController == View.INVISIBLE)
                mTopLayout.setVisibility(View.INVISIBLE);
        }

        if (forceBottomController != -100) { //强制显示隐藏
            if (forceBottomController == View.VISIBLE)
                mBottomLayout.setVisibility(View.VISIBLE);
            else if (forceBottomController == View.INVISIBLE)
                mBottomLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示或隐藏子播放器跳过View
     */
    public void showOrHiddenSubSkipView(int visibility) {
        mSubSkipView.setVisibility(visibility);
    }

    /**
     * 显示或隐藏子播放器全屏View
     */
    public void showOrHiddenSubFullScreenView(int visibility) {
        if (View.VISIBLE == visibility && isHorizontal())
            return;
        mSubFullScreenView.setVisibility(visibility);
    }

    @Override
    public boolean isShowing() {
        if (isLock())
            return mLockView.getVisibility() == View.VISIBLE;
        else
            return mTopLayout.getVisibility() == View.VISIBLE || mBottomLayout.getVisibility() == View.VISIBLE;
    }

    @Override
    public void show(int timeout) {
    }

    @Override
    public void show() {
    }

    /**
     * 主播放器设置了控制栏时会回调
     *
     * @param videoView
     */
    @Override
    public void setVideoView(PolyvVideoView videoView) {
        this.mVideoView = videoView;
    }

    /**
     * 主视频准备完成时会回调
     *
     * @param mp
     */
    @Override
    public void onPrepared(IMediaPlayer mp) {
        if (!isLive())
            mEndTimeView.setText(TimeUtils.generateTime(mp.getDuration(), true));
    }

    /**
     * 竖屏,会释放播放器，finish页面。横屏会返回竖屏。和Activity的生命周期onBackPressed对应
     */
    public void onBackPressed(boolean playComplete) {
        if (isLock())
            return;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mOrientation.onLandscapeBack(playComplete);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mOrientation.onPortraitBack();
        }
    }

    /**
     * 和Activity的oonResume对应
     */
    public void onResume() {
        mVideoView.onResume();
    }

    /**
     * 和Activity的onPause对应
     */
    public void onPause() {
        mVideoView.onPause();
    }

    /**
     * 和Activity的onConfigurationChanged对应
     */
    public void onConfigurationChanged() {
        mVideoView.onConfigurationChanged();
    }

    /**
     * 和Activity的生命周期onRestart对应
     */
    public void onRestart() {
        LogUtils.i("onRestart...");
        if (isHorizontal())
            SystemBarUtils.setLandscapeModel((Activity) mVideoView.getContext());

        if (!mVideoView.isBackgroundPlayEnabled()) {
            if (mIsPlayingOnStop || mSubVideoView.isShow()) {
                mVideoView.start();
//                if (isLive())
//                    //mVideoView.startFromNew(); 每次都重新播放
//                    mVideoView.start();
//                else if (mVideoView.isVodPlayMode())
//                    mVideoView.start();
            }
        }
        mCCTVVideoView.onActivityRestart();
    }

    /**
     * 和Activity的生命周期onStop对应
     */
    public void onStop() {
        LogUtils.i("onStop...");
        mIsPlayingOnStop = mVideoView.isPlaying() || mSubVideoView.isShow();
        if (mVideoView.isBackgroundPlayEnabled())
            mVideoView.enterBackground();
        else
            mVideoView.pause();
        IjkMediaPlayer.native_profileEnd();
        mCCTVVideoView.onActivityStop();
    }

    /**
     * 释放播放器资源。和Activity的生命周期onDestroy对应
     */
    public void onDestroy() {
        LogUtils.i("onDestroy...");
        mHandler.removeCallbacksAndMessages(null);
        mVideoView.destroy();
        mCCTVVideoView.onActivityDestroy();
        mCCTVVideoView.getDolbyHeadsetPlugReceiver().unregisterReceiver();
    }

    /**
     * 重置资源
     */
    public void onReset() {
        LogUtils.i("onReset...");
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 是否是直播
     */
    public boolean isLive() {
        if (mCCTVVideoView.getPlayEntity() != null)
            return mCCTVVideoView.getPlayEntity().isLive();
        return mVideoView.isLivePlayMode();
    }

    /**
     * 屏幕方向的UI
     */
    public void setPlayOrientationUI() {
        mCCTVVideoView.setRightLayoutViewWidth();
        if (isHorizontal())
            showLandscapeUI();
        else
            showPortraitUI();

        if (isShowPauseView())
            setBackground(false);
        else
            setBackground(true);
    }

    /**
     * 直播点播UI设置
     */
    public void setPlayModeUI() {
        if (isLive())
            showLiveUI();
        else
            showVodUI();
    }

    /**
     * 显示竖屏UI
     */
    public void showPortraitUI() {
        LogUtils.i("显示竖屏UI");
        mFullScreenView.setVisibility(View.VISIBLE);
        showOrHiddenLockView(View.GONE);
        mRateView.setVisibility(View.GONE);
        mCCTVVideoView.showOrHiddenRightView(View.GONE);
        mPauseTipsView.showOrHiddenPauseLeftCustomView(View.GONE);
        if (mCCTVVideoView.isSubVideoShow())
            mSubFullScreenView.setVisibility(View.VISIBLE);
    }

    /**
     * 显示横屏UI
     */
    public void showLandscapeUI() {
        LogUtils.i("显示横屏UI");
        mFullScreenView.setVisibility(View.GONE);
        showOrHiddenLockView(View.VISIBLE);
        mCCTVVideoView.showOrHiddenRightView(View.GONE);
        if (isShowRateView()) {
            mRateView.setVisibility(View.VISIBLE);
            setBottomRateName();
        } else
            mRateView.setVisibility(View.GONE);

        mPauseTipsView.showOrHiddenPauseLeftCustomView(View.VISIBLE);
        mSubFullScreenView.setVisibility(View.GONE);
    }

    /**
     * 显示直播UI
     */
    public void showLiveUI() {
        LogUtils.i("显示直播UI");
        mLiveStatusView.setVisibility(View.VISIBLE);
        mEndTimeView.setVisibility(View.GONE);
    }

    /**
     * 显示点播UI
     */
    public void showVodUI() {
        LogUtils.i("显示点播UI");
        mLiveStatusView.setVisibility(View.GONE);
        mEndTimeView.setVisibility(View.VISIBLE);
    }

    /**
     * 根据视频的播放状态去暂停或播放
     */
    public void onPlayOrPause() {
        if (this.mPlayOrPauseListener != null) {
            this.mPlayOrPauseListener.onPlayOrPause();
            return;
        }

        if (mVideoView.isPlaying()) {
            mVideoView.pause();
            mPlayPauseView.setSelected(true);
        } else {
            if (isLive()) {
                if (isReLoadPlay() && mCCTVVideoView != null) {
                    initPlayProgressView();
                    playLiveOrBack(PlayerOperate.RELOAD);
                } else
                    mVideoView.start();
            } else {
                if (isReLoadPlay() && mCCTVVideoView != null) {
                    playVod(false, PlayerOperate.RELOAD);
                } else
                    mVideoView.start();
            }
            mPlayPauseView.setSelected(false);
        }
    }

    /**
     * 更新播放、暂停状态
     *
     * @param pause 播放false，暂停true
     */
    public void onUpdatePlayOrPause(boolean pause) {
        if (this.mPlayOrPauseListener != null) {
            return;
        }

        mPlayPauseView.setSelected(pause);

        mHandler.removeMessages(SHOW_PROGRESS);
        if (!pause)
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
    }

    /**
     * 更新播放、暂停状态、且控制进度条定时
     *
     * @param pause 播放false，暂停true
     */
    public void onUpdatePlayOrPauseAndProgressTimer(boolean pause, boolean addTimer) {
        mPlayPauseView.setSelected(pause);
        mHandler.removeMessages(SHOW_PROGRESS);
        if (addTimer && !pause) {
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    }

    /**
     * 显示、隐藏暂停时的布局
     */
    public void showOrHiddenPauseLayout(int visibility) {
        if (View.VISIBLE == visibility && mCCTVVideoView.isSubVideoShow())
            return;

        boolean childShow = false;
        for (int i = 0; i < mPauseTipsView.getChildCount(); i++) {
            if (View.VISIBLE == mPauseTipsView.getChildAt(i).getVisibility()) {
                childShow = true;
                break;
            }
        }
        if (View.VISIBLE == visibility && childShow)
            setBackground(false);
        else
            setBackground(true);
        mPauseTipsView.setVisibility(visibility);
        mPauseTipsView.showOrHiddenPauseLeftCustomView(visibility);
    }

    /**
     * 显示、隐藏固定返回键
     */
    public void showOrHiddenFixedBackView(int visibility) {
        mBackFixedLayout.setVisibility(visibility);
    }

    /**
     * 显示、隐藏锁屏图标。只有播放主视频，且横屏时有效。
     */
    public void showOrHiddenLockView(int visibility) {
        if (View.GONE == visibility)
            mLockView.setVisibility(visibility);
        else if (isHorizontal() && !mCCTVVideoView.isSubVideoShow()) // && (mVideoView.isPlaying() || mVideoView.isCompletedState())
            mLockView.setVisibility(visibility);
    }

    /**
     * 设置顶部控制栏，距离顶部的边距
     */
    public void setTopLayoutTopMargin(int top) {
        ViewUtils.setMargins(mTopLayout, 0, top, 0, 0);
        ViewUtils.setMargins(mFixedTopLayout, 0, top, 0, 0);
    }

    /**
     * 锁屏
     */
    public void onLock() {
        if (mLockView.getTag() == null) {
            mLockView.setTag("锁屏了");
            mLockView.setSelected(true);
            onHide(false);
            mOrientation.setLandscapeRevolveModel(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            if (mLockScreenListener != null)
                mLockScreenListener.onLockScreen();
        } else {
            mLockView.setTag(null);
            mLockView.setSelected(false);
            onShow();
            mOrientation.setLandscapeRevolveModel(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            if (mLockScreenListener != null)
                mLockScreenListener.onUnLockScreen();
        }
    }

    /**
     * 码率切换
     */
    public void onRateChange() {
        if (mCCTVVideoView.getPlayEntity() == null)
            return;

        onHide();
        RateTipsView rateTipsView = new RateTipsView(getContext());
        if (isHorizontal())
            rateTipsView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.cctv_videoview_rate_horizontal_padding), 0, 0);
        else
            rateTipsView.setPadding(0, ScreenUtils.getStatusBarHeight(getContext()), 0, 0);
        rateTipsView.addItem(mCCTVVideoView.getPlayEntity().getCodeRateList(), mCCTVVideoView);
        mCCTVVideoView.addRightCustomView(rateTipsView);
        mCCTVVideoView.showOrHiddenRightView(View.VISIBLE);
    }

    /**
     * 快退30秒
     */
    public void onPlayErwindDown() {
        if (isLive()) {
            setLiveProgress(-30000);
            playLiveOrBack(PlayerOperate.FAST);
        } else {
            mCCTVVideoView.seekTo(mVideoView.getCurrentPosition() - 30000);
            //showOrHiddenPauseLayout(View.GONE);
        }
    }

    /**
     * 快进30秒
     */
    public void onPlaySpeed() {
        if (isLive()) {
            setLiveProgress(30000);
            playLiveOrBack(PlayerOperate.FAST);
        } else {
            mCCTVVideoView.seekTo(mVideoView.getCurrentPosition() + 30000);
            //showOrHiddenPauseLayout(View.GONE);
        }
    }

    /**
     * 自定义快进/快退
     *
     * @param progress 点播就传秒，直播传13位时间戳
     * @param position 点播要快进的位置
     */
    public void onSeekTo(long progress, int position) {
        if (isLive()) {
            mLiveProgressMs = progress;
            setLiveProgress(0);
            playLiveOrBack(PlayerOperate.FAST);
        } else {
            mVideoView.seekTo(position);
            mPlayProgressView.setProgress((int) progress);
        }
    }

    /**
     * 设置前贴片倒计时
     */
    public void setHeadedCountdown(int totalTime, int remainTime) {
        if (remainTime > -1) {
            boolean skip = true;
            CCTVSubPlayEntity subPlayEntity = mCCTVVideoView.getPlayEntity().getSubPlayEntity();
            if (subPlayEntity != null) {
                String curUrl = mVideoView.getSubVideoView().getCurrentPlayPath();
                CCTVSubPlayEntity.CCTVSubPlayHeadadAD headadAD = subPlayEntity.getHeadadADInfo(curUrl);
                if (headadAD != null)
                    skip = headadAD.isSkip();
            }

            if (skip) {
                if (totalTime - remainTime >= 5) {
                    mSubSkipView.setEnabled(true);
                    mSubSkipView.setText(remainTime + " 跳过");
                } else {
                    mSubSkipView.setEnabled(false);
                    mSubSkipView.setText(remainTime + " 广告");
                }
                mSubSkipView.setVisibility(View.VISIBLE);
            } else {
//                mSubSkipView.setVisibility(View.GONE);
                mSubSkipView.setText(remainTime + " 广告");
            }
        } else
            mSubSkipView.setVisibility(View.GONE);
    }

    /**
     * 前贴片广告跳过
     */
    public void onPlaySkipHeadAd() {
        if (mVideoView.canPlaySkipHeadAd()) {
            showOrHiddenSubSkipView(View.GONE);
            showOrHiddenSubFullScreenView(View.GONE);
            mVideoView.playSkipHeadAd(false);
        } else {
            LogUtils.i("跳过广告播放失败，当前没有播放片头广告" + "&PlayOption：" + mVideoView.getPlayOption());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back || id == R.id.backFixedLayout) { //返回
            onBackPressed(false);
        } else if (id == R.id.fullScreen || id == R.id.subFullScreen) { //全屏
            mOrientation.changeToLandscape();
        } else if (id == R.id.playPause) { //播放/暂停
            onPlayOrPause();
        } else if (id == R.id.rate) { //码率切换
            onRateChange();
        } else if (id == R.id.lock) { //锁屏
            onLock();
        } else if (id == R.id.drawBackLayout) { //快退
            onPlayErwindDown();
        } else if (id == R.id.advanceLayout) { //快进
            onPlaySpeed();
        } else if (id == R.id.subSkip) { //前贴片广告跳过
            onPlaySkipHeadAd();
        } else if (id == R.id.liveStatus) { //直播状态
            initPlayProgressView();
            playLiveOrBack(PlayerOperate.RELOAD);
        }
    }

    /**
     * 根据播放进度，来控制直播是播放时移，还是最新直播
     *
     * @param operate 操作
     */
    public void playLiveOrBack(PlayerOperate operate) {
        if (mCCTVVideoView.getPlayListener() == null || mCCTVVideoView.getPlayEntity() == null)
            return;

        if (mPlayProgressView.getProgress() >= mPlayProgressView.getMax()) { //拖动到了最后，设置最新的时间
            LogUtils.i("直播拖动 - 最新...");
            setLiveStatus(true);
            mCCTVVideoView.getPlayListener().onLivePlay(mCCTVVideoView.getPlayEntity().getCurPlayURL(false), operate);
        } else {
            LogUtils.i("直播拖动 - 时移...");
            setLiveStatus(false);
            mCCTVVideoView.getPlayListener().onLiveBackPlay(mCCTVVideoView.getPlayEntity().getCurPlayURL(true), getLivePlayedMs(), operate);
        }
    }

    /**
     * 点播
     *
     * @param progressKeep 是否进度保持播放
     * @param operate      操作
     */
    public void playVod(boolean progressKeep, PlayerOperate operate) {
        if (mCCTVVideoView.getPlayListener() == null || mCCTVVideoView.getPlayEntity() == null)
            return;
        mCCTVVideoView.getPlayListener().onVodPlay(mCCTVVideoView.getPlayEntity().getCurPlayURL(false), progressKeep, operate);
    }

    /**
     * 返回键会一直显示，不会跟随控制栏隐藏。默认true，不会跟随控制栏隐藏。
     */
    public void showBackFixed(boolean show) {
        mIsBackFixed = show;
    }

    /**
     * 删除直播进度定时
     */
    public void removeLiveProgressTimer() {
        if (!isLive())
            return;
        mHandler.removeMessages(LIVE_PROGRESS_TIMER);
    }

    /**
     * 开启直播进度定时
     */
    public void startLiveProgressTimer() {
        if (!isLive())
            return;
        mHandler.sendEmptyMessageDelayed(LIVE_PROGRESS_TIMER, 1000);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE:
                    if (mVideoView.isPlaying()) //只有播放中才自动隐藏
                        onHide();
                    break;

                case LIVE_PROGRESS_TIMER:
                    if (!isLive()) {
                        mHandler.removeMessages(LIVE_PROGRESS_TIMER);
                        return;
                    }
                    LogUtils.i("LIVE_PROGRESS_TIMER...");
                    if (mVideoView.getCurrentState() == mVideoView.getStatePlayingCode()) {
                        LogUtils.i("播放中...");
                        mLiveProgressMs += 1000;
                        mLiveSystemTimeMs += 1000;
                    }
                    mHandler.removeMessages(LIVE_PROGRESS_TIMER);
                    mHandler.sendEmptyMessageDelayed(LIVE_PROGRESS_TIMER, 1000);
                    break;

                case SHOW_PROGRESS:
                    LogUtils.i("SHOW_PROGRESS...");
                    if (isLive()) {
                        setLiveProgress(0);
                    } else {
                        setVodProgress();
                    }

                    if (!mIsDragging && mIsShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                        onUpdatePlayOrPause(!mVideoView.isPlaying());
                    }
                    break;
            }
        }
    };

    /**
     * 设置控制栏背景色
     *
     * @param fullTransparency 是否100%透明
     */
    public void setBackground(boolean fullTransparency) {
        if (fullTransparency)
            setBackgroundColor(Color.parseColor("#00000000"));
        else
            setBackgroundColor(Color.parseColor("#80000000"));
    }

    /**
     * 重新加载播放
     */
    public boolean isReLoadPlay() {
        return mVideoView.getCurrentState() == mVideoView.getStateErrorCode() ||
                mVideoView.getCurrentState() == mVideoView.getStatePlaybackCompletedCode();
    }

    /**
     * 是否显示了暂停布局
     */
    public boolean isShowPauseView() {
        return mPauseTipsView.isShow();
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    /**
     * 当前视频是否为横屏
     */
    public boolean isHorizontal() {
        return mCCTVVideoView.getPlayEntity().isHorizontal();
    }

    /**
     * 设置横竖屏
     */
    public void setHorizontal(boolean orientation) {
        mCCTVVideoView.getPlayEntity().setHorizontal(orientation);
    }

    /**
     * 监听横竖屏
     */
    public void setOrientationListener(CCTVOrientationListener orientationListener) {
        mOrientation.setCCTVOrientationListener(orientationListener);
    }

    /**
     * 添加手动暂停时，左侧自定义布局
     */
    public void addPauseLeftCustomView(View view) {
        mPauseTipsView.addPauseLeftCustomView(view);
    }

    /**
     * 暂停布局是否显示。默认不显示
     */
    public void setShowPauseView(boolean show) {
        this.isShowPauseView = show;
    }

    /**
     * 设置直播进度条
     */
    public void setLiveProgress(int ms) {
        if (mVideoView == null
                || mCCTVVideoView.getPlayEntity() == null
                || mIsDragging)
            return;
        mLiveProgressMs += ms;
        mCurTimeView.setText(TimeUtils.format(getLivePlayedMs(), "HH:mm:ss"));
        mPlayProgressView.setProgress(getLiveProgress(mLiveProgressMs, ms, mLiveStatusView.isSelected()));
    }

    /**
     * 根据时间戳，计算进度条
     *
     * @param time     时间戳
     * @param ms       快进/快退的毫秒数， 正常传0
     * @param liveBack true 直播回看， false 直播
     */
    public int getLiveProgress(long time, int ms, boolean liveBack) {
        long avgDuration = (mLiveSystemTimeMs - getLiveStartTime()) / 1000 / mPlayProgressView.getMax();
        long curPlayerDuration = (time - getLiveStartTime()) / 1000;
        int pos = 0;
        try {
            pos = (int) (curPlayerDuration / avgDuration);
        } catch (Exception e) {
        }
        if (pos >= 100 && ms < 0)
            pos = 99;

        if (pos >= 100 && ms == 0 && liveBack) //时移
            pos = 99;

        if (mLiveProgressMs >= getLiveEndTime())
            pos = mPlayProgressView.getMax();

        LogUtils.i("ms..." + ms);
        LogUtils.i("curPlayerDuration..." + curPlayerDuration);
        LogUtils.i("newAvgDuration..." + avgDuration);
        LogUtils.i("Progress..." + pos);
        return pos;
    }

    /**
     * 获取进度条View
     */
    public ScrollableSeekBar getPlayProgressView() {
        return mPlayProgressView;
    }

    /**
     * 显示/隐藏进度条View
     */
    public void showOrHiddenPlayProgressView(int visibility) {
        mPlayProgressView.setVisibility(visibility);
        mPlayProgressNoteLayout.setVisibility(visibility);
        mThumbView.setVisibility(visibility);
        if (View.GONE == visibility) {
            ((ViewGroup) mPlayProgressView.getParent()).setOnTouchListener(null);
        } else {
            enlargeSeekBar();
        }
    }

    /**
     * 开始时间View
     */
    public TextView getCurTimeView() {
        return mCurTimeView;
    }

    /**
     * 开始总时长View
     */
    public TextView getEndTimeView() {
        return mEndTimeView;
    }

    /**
     * 获取直播时，当前播放进度
     *
     * @return 已播的毫秒数
     */
    public long getLivePlayedMs() {
        validLiveProgressMs();
        return mLiveProgressMs;
    }

    /**
     * 设置点播进度条
     */
    public void setVodProgress() {
        if (mVideoView == null || mIsDragging || !mIsShowing)
            return;

        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        if (mVideoView.isCompletedState() || position > duration)
            position = duration;

        if (duration > 0) {
            double pos = getVodProgress(position, duration);
            mPlayProgressView.setProgress((int) pos);
        }
        int percent = mVideoView.getBufferPercentage();
        mPlayProgressView.setSecondaryProgress(percent * 10);

        if (mEndTimeView != null)
            mEndTimeView.setText(TimeUtils.generateTime(duration, true));

        if (mCurTimeView != null)
            mCurTimeView.setText(TimeUtils.generateTime(position, true));
    }

    /**
     * 根据毫秒数，计算进度条
     *
     * @param position 当前播放时长 ms
     * @param duration 总时长 ms
     */
    public int getVodProgress(long position, long duration) {
        if (duration < 1)
            return 0;

        if (mVideoView.isCompletedState() || position > duration)
            position = duration;
        double pos = mPlayProgressView.getMax() * position / duration;
        return (int) pos;
    }

    /**
     * 获取直播开始时间
     */
    private long getLiveStartTime() {
        if (mCCTVVideoView.getPlayEntity() != null)
            return mCCTVVideoView.getPlayEntity().getLiveStartTime();
        return 0l;
    }

    /**
     * 获取直播结束时间
     */
    private long getLiveEndTime() {
        if (mCCTVVideoView.getPlayEntity() != null)
            return mCCTVVideoView.getPlayEntity().getLiveEndTime();
        return 0l;
    }

    /**
     * 根据播放进度，设置进度条滑块的位置
     */
    private void setSeekBarThumbPosition(int progress) {
        LogUtils.i("当前进度信息：progress=" + progress + " | SeekBarWidth=" + mSeekBarWidth);
        double seekBarWidht = mSeekBarWidth - mThumbView.getWidth();
        double curPosition = progress * (seekBarWidht / mPlayProgressView.getMax());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mThumbView.getLayoutParams();
        params.leftMargin = (int) curPosition;
    }

    /**
     * 播放进度条监听
     */
    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
            setSeekBarThumbPosition(progress);
            if (!fromuser)
                return;

            if (isLive()) {
                mLiveProgressMs = getLiveStartTime() + (long) ((mLiveSystemTimeMs - getLiveStartTime()) * (progress / (float) seekBar.getMax()));
                mCurTimeView.setText(TimeUtils.format(getLivePlayedMs(), "HH:mm:ss"));
            } else {
                long newPosition = mVideoView.getDuration() * progress / seekBar.getMax();
                String time = TimeUtils.generateTime(newPosition, true);
                mCurTimeView.setText(time);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mIsDragging = true;
            //showOrHiddenPauseLayout(View.GONE);
            mHandler.removeMessages(SHOW_PROGRESS);
            mHandler.removeMessages(LIVE_PROGRESS_TIMER);
            mHandler.removeMessages(HIDE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (isLive()) {
                playLiveOrBack(PlayerOperate.FAST);
            } else {
                int seekToPosition = mVideoView.getDuration() * seekBar.getProgress() / seekBar.getMax();
                if (!mVideoView.isCompletedState()) {
                    mCCTVVideoView.seekTo(seekToPosition);
                } else {
                    mCCTVVideoView.seekTo(seekToPosition);
                    mVideoView.start();
                }
            }
            mHandler.removeMessages(LIVE_PROGRESS_TIMER);
            mHandler.removeMessages(SHOW_PROGRESS);
            mHandler.sendEmptyMessageDelayed(LIVE_PROGRESS_TIMER, 1000);
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
            mIsDragging = false;
            mHandler.sendEmptyMessageDelayed(HIDE, SHOW_TIME);
        }
    };

    /**
     * 更改最新直播或者回看状态
     *
     * @param nowLive 是否为最新直播
     */
    private void setLiveStatus(boolean nowLive) {
        mLiveStatusView.setSelected(!nowLive);
    }

    /**
     * 验证直播时间的正确性
     */
    private void validLiveProgressMs() {
        if (mLiveProgressMs > System.currentTimeMillis() || String.valueOf(mLiveProgressMs).length() != 13) {
            mLiveProgressMs = System.currentTimeMillis();
        }
    }

    /**
     * 取消强制显示隐藏顶部控制栏
     */
    public void cancelForceTopController() {
        forceTopController = -100;
    }

    /**
     * 取消强制显示隐藏底部控制栏
     */
    public void cancelForceBottomController() {
        forceBottomController = -100;
    }

    /**
     * 监听控制栏显示/隐藏
     */
    public void setCCTVControllerListener(CCTVControllerListener listener) {
        this.mControllerListener = listener;
    }

    /**
     * 监听锁屏/解锁屏幕
     */
    public void setCCTVLockScreenListener(CCTVLockScreenListener listener) {
        this.mLockScreenListener = listener;
    }

    /**
     * 设置播放/暂停 监听
     */
    public void setCCTVPlayOrPauseListener(CCTVPlayOrPauseListener listener) {
        this.mPlayOrPauseListener = listener;
    }

    /**
     * 回调控制栏的监听
     *
     * @param type 1显示 2隐藏
     */
    public void onCallbackControllerListener(int type) {
        if (mControllerListener == null)
            return;

        if (type == 1) { //显示
            int status = 0;
            if (mTopLayout.getVisibility() == View.VISIBLE && mBottomLayout.getVisibility() != View.VISIBLE)
                status = 1;
            else if (mTopLayout.getVisibility() != View.VISIBLE && mBottomLayout.getVisibility() == View.VISIBLE)
                status = 2;
            else if (mTopLayout.getVisibility() == View.VISIBLE && mBottomLayout.getVisibility() == View.VISIBLE)
                status = 3;
            if (status > 0 && mCurShowStatus != status) {
                mControllerListener.onShow(status);
                mCurHiddenStatus = 0;
                mCurShowStatus = status;
            }
        } else if (type == 2) {//隐藏
            int status = 0;
            if (mTopLayout.getVisibility() == View.VISIBLE && mBottomLayout.getVisibility() != View.VISIBLE)
                status = 2;
            else if (mTopLayout.getVisibility() != View.VISIBLE && mBottomLayout.getVisibility() == View.VISIBLE)
                status = 1;
            else if (mTopLayout.getVisibility() != View.VISIBLE && mBottomLayout.getVisibility() != View.VISIBLE)
                status = 3;
            if (status > 0 && mCurHiddenStatus != status) {
                mControllerListener.onHidden(status);
                mCurShowStatus = 0;
                mCurHiddenStatus = status;
            }
        }
    }

    /**
     * 获取返回键View
     */
    public ImageView getBackView() {
        return mBackView;
    }


    /**
     * 获取titleView
     */
    public TextView getTitleView() {
        return mTitleView;
    }
    /**
     * 获取直播/回看状态View
     */
    public ImageView getLiveStatusView() {
        return mLiveStatusView;
    }

    /**
     * 横竖屏的时候，是否控制系统状态栏
     */
    public boolean isControlSystemStatusBar() {
        return mIsSystemStatusBar;
    }

    /**
     * 横竖屏的时候，是否控制系统状态栏
     */
    public void setControlSystemStatusBar(boolean control) {
        this.mIsSystemStatusBar = control;
    }
}