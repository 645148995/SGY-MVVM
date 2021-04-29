package com.cctv.cctvplayer.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.cctv.cctvplayer.CCTVVideoManager;
import com.cctv.cctvplayer.CCTVVideoMediaController;
import com.cctv.cctvplayer.CCTVVideoView;
import com.cctv.cctvplayer.listener.CCTVOrientationListener;
import com.cctv.cctvplayer.utils.SystemBarUtils;

/**
 * 横竖屏处理
 */
public class Orientation {

    private Context mContext;
    private CCTVVideoMediaController mMediaController;
    private CCTVOrientationListener mOrientationListener; // 横竖屏监听接口
    private ViewGroup mContentView, mPlayerContainer; //播放器容器
    private ViewGroup.LayoutParams mPortraitLP;//在竖屏下的LayoutParams
    private int mPortraitIndex;//竖屏下的父容器中的index
    private CCTVVideoView mCCTVVideoView; //播放器
    private int mCurOrientation; //当前屏幕方向，1 竖屏 2横屏，其它值没设置过

    public Orientation(Context context, CCTVVideoMediaController mediaController) {
        this.mContext = context;
        this.mMediaController = mediaController;
    }

    /**
     * 监听横竖屏
     */
    public void setCCTVOrientationListener(CCTVOrientationListener orientationListener) {
        this.mOrientationListener = orientationListener;
    }

    /**
     * 竖屏，点击返回
     */
    public void onPortraitBack() {
        ((Activity) mContext).finish();
    }

    /**
     * 横屏，点击返回
     */
    public void onLandscapeBack(boolean playComplete) {
        changeToPortrait(playComplete);
    }

    /**
     * 控制横屏旋转模式
     */
    public void setLandscapeRevolveModel(int model) {
        ((Activity) mContext).setRequestedOrientation(model);
    }

    /**
     * 切换到横屏
     */
    public void changeToLandscape() {
        mCurOrientation = 2;
        mMediaController.unLock();

        setLandscapeRevolveModel(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        ((ViewGroup) mMediaController.getParent()).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        mMediaController.setHorizontal(true);
        mMediaController.setPlayOrientationUI();

        if (mMediaController.isControlSystemStatusBar())
            SystemBarUtils.setLandscapeModel(((Activity) mContext));

        mCCTVVideoView = ((CCTVVideoView) mMediaController.getParent().getParent());
        if (mCCTVVideoView.isSubVideoShow()) {
            mMediaController.setTopLayoutTopMargin(0);
            SystemBarUtils.showOrHiddenStatusBar((Activity) mContext, false);
        } else {
            mMediaController.setTopLayoutTopMargin(0);
            SystemBarUtils.showOrHiddenStatusBar((Activity) mContext, true);
        }
        SystemBarUtils.showStatusView((Activity) mContext, false);


        mPlayerContainer = (ViewGroup) mCCTVVideoView.getParent();

        if (mCCTVVideoView.getFullScreenPlayerContainer() != null)
            mContentView = mCCTVVideoView.getFullScreenPlayerContainer();
        else
            mContentView = ((Activity) mContext).findViewById(Window.ID_ANDROID_CONTENT);

        if (mCCTVVideoView.getPlayerView().vrOn()) {
            mCCTVVideoView.getPlayerView().onPause();
        }

        mPortraitLP = mCCTVVideoView.getLayoutParams();
        mPortraitIndex = mPlayerContainer.indexOfChild(mCCTVVideoView);
        mPlayerContainer.removeView(mCCTVVideoView);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mCCTVVideoView.setLayoutParams(flp);
        ViewGroup viewGroup = (ViewGroup) mCCTVVideoView.getParent();
        if (viewGroup != null)
            viewGroup.removeView(mCCTVVideoView);
        mContentView.addView(mCCTVVideoView);
        CCTVVideoManager.getInstance().putPlayerContainer(mPlayerContainer.getContext(), mContentView);

        if (mCCTVVideoView.getPlayerView().vrOn()) {
            mCCTVVideoView.getPlayerView().resetVRRender();
            mCCTVVideoView.getPlayerView().onResume();
        }

        if (mOrientationListener != null)
            mOrientationListener.onLandscape();
    }

    /**
     * 切换到竖屏
     * @param playComplete 是否播放完成 true 完成  false未完成，横屏切为竖屏继续播放
     */
    public void changeToPortrait(boolean playComplete) {
        mCurOrientation = 1;
        mMediaController.unLock();

        ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        if (mCCTVVideoView != null)
//            ((ViewGroup) mMediaController.getParent()).getLayoutParams().height = mCCTVVideoView.getVideoViewHeight();
        mMediaController.setHorizontal(false);
        mMediaController.setPlayOrientationUI();

        if (mMediaController.isControlSystemStatusBar())
            SystemBarUtils.setPortraitModel(((Activity) mContext));
        SystemBarUtils.showStatusView((Activity) mContext, true); //财经

        mMediaController.setTopLayoutTopMargin(0);

        if (mCCTVVideoView != null) {
//            if (mCCTVVideoView.getPlayerView().vrOn()) {
//                mCCTVVideoView.getPlayerView().onPause();
//            }

            mContentView.removeView(mCCTVVideoView);

            if (!playComplete) {
                mCCTVVideoView.setLayoutParams(mPortraitLP);
                ViewGroup viewGroup = (ViewGroup) mCCTVVideoView.getParent();
                if (viewGroup != null)
                    viewGroup.removeView(mCCTVVideoView);
                mPlayerContainer.addView(mCCTVVideoView, mPortraitIndex);
                CCTVVideoManager.getInstance().putPlayerContainer(mPlayerContainer.getContext(), mPlayerContainer);

                if (mCCTVVideoView.getPlayerView().vrOn()) {
                    mCCTVVideoView.getPlayerView().resetVRRender();
                    mCCTVVideoView.getPlayerView().onResume();
                }
            }
        }

        if (mOrientationListener != null)
            mOrientationListener.onPortrait();
    }

    /**
     * 获得当前屏幕方向，1 竖屏 2横屏，其它值没设置过
     */
    public int getCurOrientation() {
        return mCurOrientation;
    }
}
