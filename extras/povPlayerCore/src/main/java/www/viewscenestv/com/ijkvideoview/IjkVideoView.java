/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package www.viewscenestv.com.ijkvideoview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.easefun.povplayer.core.R;
import com.easefun.povplayer.core.gifmaker.GifMaker;
import com.easefun.povplayer.core.util.PolyvDetection;
import com.easefun.povplayer.core.util.PolyvLog;
import com.easefun.povplayer.core.video.PolyvRenderType;
import com.viewscene.transcoder.JSLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;
import tv.danmaku.ijk.media.player.TextureMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;
import tv.danmaku.ijk.media.player.misc.IMediaFormat;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkMediaFormat;

public class IjkVideoView extends FrameLayout implements MediaController.MediaPlayerControl, JSTPEventCallback.Callback {
    private String TAG = "IjkVideoView";
    // settable by the client
    private Uri mUri;
    private Map<String, String> mHeaders;

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;
    private int mSetView2Flag = 0;

    // All the stuff we need for playing and showing a video
    private IRenderView.ISurfaceHolder mSurfaceHolder = null;
    private IMediaPlayer mMediaPlayer = null;
    // private int         mAudioSession;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mVideoRotationDegree;
    private IMediaController mMediaController;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    private int mCurrentBufferPercentage;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    private int mSeekWhenPrepared;  // recording the seek position while preparing
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;

    // JS_MODIFY
    private boolean reconnetFlag = false;
    private boolean reconnetPerssionFlag = false;
    private boolean branchOriginalFlag = false;

    private SurfaceRenderView surfaceRenderView = null;
    private int _js_mode = 0;
    private String _js_data = "";
    private int _js_xhard = 0;
    private int _js_mute = 0;
    private int _stream_id = 0;
    private long _jsl_dmx = 0;
    private boolean renderingStarted = false;
    private boolean audioRenderingStarted = false;
    private int mHandle;

    private View mOldRenderUIView = null;

    private final SparseArray<IJKScheduledScreenshotTask> mScheduledScreenshotTasks
            = new SparseArray<>();

    @Override
    public void onJSTPEvent(final int cmd, final int data, final JSTPEventCallback.Params params) {
        JSLog.s(TAG, "onJSTPEvent: cmd=" + cmd + " data=" + data);
    }

    private HandlerThread mHandlerThread = new HandlerThread("open-video");

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Handler mOpenVideoHandler;


    @Keep
    public interface IjkVideoViewListener {
        //JS_MODIFY
        void onIAEvent(String eventContent);

        void onBitRateChanged(String bitrate);

        void onVolumeChanged(int onVolumeChanged);

        void onIJKNeedRetry(int retryReason);

        void onAudioRenderingStart();

        void onViewChangeEnd(Integer result);

        //JS_MODIFY_END

        void onRenderingStart();

        void onIjkplayerCompleted();

        void onBufferingUpdate(int percent);

        void onBufferingStart();

        void onBufferingEnd();

        void onClosed();
    }

    private final CompositeIjkVideoViewListener mIjkVideoViewListener
            = new CompositeIjkVideoViewListener();

    public void addIjkVideoViewListener(IjkVideoViewListener l) {
        mIjkVideoViewListener.register(l);
    }
    // JS_MODIFY END

    /** Subtitle rendering widget overlaid on top of the video. */
    // private RenderingWidget mSubtitleWidget;

    /**
     * Listener for changes to subtitle data, used to redraw when needed.
     */
    // private RenderingWidget.OnChangedListener mSubtitlesChangedListener;

    private Context mAppContext;
    private Settings mSettings;
    private IRenderView mRenderView;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private InfoHudViewHolder mHudViewHolder;

    private long mPrepareStartTime = 0;
    private long mPrepareEndTime = 0;

    private long mSeekStartTime = 0;
    private long mSeekEndTime = 0;

    private TextView subtitleDisplay;
    private PolyvGLSurfaceRenderView.OnInitCompletionListener glSurfaceInitCompletionListener;
    private PolyvGLTextureRenderView.OnInitCompletionListener glTextureInitCompletionListener;

    public IjkVideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }

    // REMOVED: onMeasure
    // REMOVED: onInitializeAccessibilityEvent
    // REMOVED: onInitializeAccessibilityNodeInfo
    // REMOVED: resolveAdjustedSize

    // JS_MODIFY

    /**
     * Call these method right after the Constructor.
     * <p>
     * If you don't care about the configuration, just call setParam(). It uses
     * IRenderView.AR_ASPECT_FIT_PARENT and SurfaceRenderView just as original
     * IjkVideoView does.
     * <p>
     * If you want to modify aspect ratio or use TextureRenderView instead of
     * SurfaceRenderView, call setParams(int), setParams(boolean), or setParams(int, boolean)
     * <p>
     * If you are an advanced user, call setParams(int, String, boolean, ...). It allows you to
     * modify all possible parameters.
     */
    public void setParams() {
        mSettings = new Settings(mAppContext);
        initRenders();
    }

    /**
     * @param aspectRatio Choose from Settings.FIT_PARENT, Settings.FILL_PARENT, etc.
     * @see Settings
     */
    public void setParams(int aspectRatio) {
        mSettings = new Settings(mAppContext);
        mSettings.setAspecRatio(aspectRatio);
        initRenders();
    }

    public void setParams(boolean enableTextureView) {
        mSettings = new Settings(mAppContext);
        mSettings.enableTextureView(enableTextureView);
        initRenders();
    }

    public void setParams(int aspectRatio, boolean enableTextureView) {
        mSettings = new Settings(mAppContext);
        mSettings.setAspecRatio(aspectRatio);
        mSettings.enableTextureView(enableTextureView);
        initRenders();
    }

    /**
     * @param aspectRatio                 Choose from Settings.FIT_PARENT, Settings.FILL_PARENT, etc.
     * @param pixelFormat                 Choose from Settings.AUTO_SELECT, Settings.RGB_565, etc.
     * @param isUsingMediaCodec
     * @param isUsingMediaCodecAutoRotate
     * @param isUsingOpenSLES
     * @param isEnableTextureView
     * @see Settings
     */
    public void setParams(int aspectRatio,
                          String pixelFormat,
                          boolean isUsingMediaCodec,
                          boolean isUsingMediaCodecAutoRotate,
                          boolean isUsingOpenSLES,
                          boolean isEnableTextureView) {
        mSettings = new Settings(mAppContext);
        mSettings.setParams(aspectRatio,
                pixelFormat,
                isUsingMediaCodec,
                isUsingMediaCodecAutoRotate,
                isUsingOpenSLES,
                isEnableTextureView);
        initRenders();
    }
    //JS_MODIFY_END

    private void initVideoView(Context context) {
        mHandlerThread.start();
        mOpenVideoHandler = new Handler(mHandlerThread.getLooper());
        mAppContext = context.getApplicationContext();
        mSettings = new Settings(mAppContext);
        //mSettings = new Settings(mAppContext);

        initBackground();
        //initRenders();

        mVideoWidth = 0;
        mVideoHeight = 0;
        // REMOVED: getHolder().addCallback(mSHCallback);
        // REMOVED: getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        requestFocus();
        // REMOVED: mPendingSubtitleTracks = new Vector<Pair<InputStream, MediaFormat>>();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;

        subtitleDisplay = new TextView(context);
        subtitleDisplay.setTextSize(24);
        subtitleDisplay.setGravity(Gravity.CENTER);
        LayoutParams layoutParams_txt = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM);
        addView(subtitleDisplay, layoutParams_txt);
    }

    // JS_MODIFY
    public void setJsdemux(int stream_id, long jsl_dmx) {
        _stream_id = stream_id;
        _jsl_dmx = jsl_dmx;
    }

    public synchronized int record(int enable, String fn, int max_size, long vstamp, int duration) {
        if (getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().record(enable, fn, max_size, vstamp, duration);
        }
        return -1;
    }

    public synchronized int flvtomp4cuttail(String infilename, String outfilename, double tail_cut_sec) {
        if (getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().flvtomp4cuttail(infilename, outfilename, tail_cut_sec);
        }
        return -1;
    }

    public synchronized int resetConnect() {
        suspend();
        if (branchOriginalFlag && !mUri.toString().contains("originalflag=1")){
            setVideoURI(Uri.parse(mUri.toString()+"&originalflag=1"),mHeaders);
        }else{
            setVideoURI(mUri,mHeaders);
        }
//        resume();//直播
        reconnetFlag = true;
        return 1;
    }


    public int getStreamId() {
        return _stream_id;
    }
    // JS_MODIFY END


    public void removeRenderView() {
        if (mRenderView != null) {
            if (mMediaPlayer != null)
                mMediaPlayer.setDisplay(null);

            if (mRenderView instanceof TextureRenderView)
                ((TextureRenderView) mRenderView).cancelClip();

            View renderUIView = mRenderView.getView();
            mRenderView.removeRenderCallback(mSHCallback);
            mRenderView = null;
            ViewGroup viewGroup = (ViewGroup) renderUIView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(renderUIView);
            }
        }
    }

    private synchronized void clearOldRenderView() {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //if IjkVideoView.this.
                if (mOldRenderUIView != null) {
                    ViewGroup viewGroup = (ViewGroup) mOldRenderUIView.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(mOldRenderUIView);
                    }
                }
                mOldRenderUIView = null;
            }
        });

    }

    public synchronized void setRenderView(final IRenderView renderView) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (mRenderView != null) {
                    if (mMediaPlayer != null)
                        mMediaPlayer.setDisplay(null);

                    if (mRenderView instanceof TextureRenderView)
                        ((TextureRenderView) mRenderView).cancelClip();

                    mRenderView.removeRenderCallback(mSHCallback);
                    if (mOldRenderUIView != null || mSetView2Flag == 0) {
                        ViewGroup viewGroup = (ViewGroup) mRenderView.getView().getParent();
                        if (viewGroup != null){
                            viewGroup.removeView(mRenderView.getView());
                        }
                    } else {
                        mOldRenderUIView = mRenderView.getView();
                    }
                }

                if (renderView == null)
                    return;

                mRenderView = renderView;
                renderView.setAspectRatio(mCurrentAspectRatio);//替换1
                if (mVideoWidth > 0 && mVideoHeight > 0)
                    renderView.setVideoSize(mVideoWidth, mVideoHeight);
                if (mVideoSarNum > 0 && mVideoSarDen > 0)
                    renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);

                View renderUIView = mRenderView.getView();
                LayoutParams lp = new LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER);
                renderUIView.setLayoutParams(lp);
                addView(renderUIView, 0);

                mRenderView.addRenderCallback(mSHCallback);
                mRenderView.setVideoRotation(mVideoRotationDegree);
            }
        });
    }

    public synchronized void setRender(int render) {
        if (mMediaPlayer == null)
            return;
        switch (render) {
            case RENDER_NONE:
                setRenderView(null);
                break;
            case RENDER_TEXTURE_VIEW: {
                TextureRenderView renderView = new TextureRenderView(getContext());
                if (mMediaPlayer != null) {
                    renderView.getSurfaceHolder().bindToMediaPlayer(mMediaPlayer);
                    renderView.setVideoSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
                    renderView.setVideoSampleAspectRatio(mMediaPlayer.getVideoSarNum(), mMediaPlayer.getVideoSarDen());
                    renderView.setAspectRatio(mSettings.getAspectRatio());
                }
                setRenderView(renderView);
                break;
            }
            case RENDER_SURFACE_VIEW: {
                SurfaceRenderView renderView = new SurfaceRenderView(getContext());
                setRenderView(renderView);

                // JS_MODIFY
                surfaceRenderView = renderView;
                // JS_MODIFY END

                break;
            }
            case PolyvRenderType.RENDER_GLSURFACE_VIEW: {
                PolyvLog.i(TAG, "setRender PolyvRenderType.RENDER_GLSURFACE_VIEW");
                break;
            }
            case PolyvRenderType.RENDER_GLTEXTURE_VIEW: {
                PolyvLog.i(TAG, "setRender PolyvRenderType.RENDER_GLTEXTURE_VIEW");
                break;
            }
            default:
                Log.e(TAG, String.format(Locale.getDefault(), "invalid render %d\n", render));
                break;
        }
    }

    // JS_MODIFY
    public SurfaceRenderView getSurfaceRenderView() {
        return surfaceRenderView;
    }

    /*
     *Currently only TextureRenderView supports screenshot.
     */
    public Bitmap getScreenShot() {
        if (mRenderView instanceof TextureRenderView) {
            return ((TextureRenderView) mRenderView).getBitmap();
        } else {
            Log.e(TAG, "Current RenderView does not support screenshot");
        }
        return null;
    }

    public synchronized long getTimeStamp() {
        if (getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().jsGetTimeStamp();
        } else {
            return -1;
        }
    }

    // JS_MODIFY END


    public void setHudView(TableLayout tableLayout) {
        mHudViewHolder = new InfoHudViewHolder(getContext(), tableLayout);
    }

    /**
     * Sets video path.
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    private void setHeaders(Map<String, String> headers) {
        if (headers == null) {
            mHeaders = new HashMap<>();
        } else {
            mHeaders = headers;
        }
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     *                Note that the cross domain redirection is allowed by default, but that can be
     *                changed with key/value pairs through the headers parameter with
     *                "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     *                to disallow or allow cross domain redirection.
     */
    public void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        setHeaders(headers);
        mSeekWhenPrepared = 0;
        mOpenVideoHandler.post(new Runnable() {
            @Override
            public void run() {
                JSLog.s(TAG, "call openVideo setVideoURI this=" + IjkVideoView.this);
                openVideo();
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        requestLayout();
//                        invalidate();
//                    }
//                });
            }
        });
    }

    public void resetVideoURI() {
        if (mMediaPlayer != null) {
            mSeekWhenPrepared = 0;
            mMediaPlayer.reset();
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            setRender(mSettings.getRenderViewType());
            setOption(mMediaPlayer);
            mCurrentBufferPercentage = 0;
            try {
                String scheme = mUri.getScheme();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        mSettings.getUsingMediaDataSource() &&
                        (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
                    IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
                    mMediaPlayer.setDataSource(dataSource);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
                } else {
                    mMediaPlayer.setDataSource(mUri.toString());
                }
//                bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
                if (getIjkMediaPlayer() != null)
                    getIjkMediaPlayer().setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "async,cache,crypto,file,http,https,ijkhttphook,ijkinject,ijklivehook,ijklongurl,ijksegment,ijktcphook,pipe,rtp,rtmp,tcp,tls,udp,ijkurlhook,data");
                mPrepareStartTime = System.currentTimeMillis();
                mMediaPlayer.prepareAsync();
                mCurrentState = STATE_PREPARING;
            } catch (IOException ex) {
                Log.w(TAG, "Unable to open content: " + mUri, ex);
                mCurrentState = STATE_ERROR;
                mTargetState = STATE_ERROR;
                mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            } catch (IllegalArgumentException ex) {
                Log.w(TAG, "Unable to open content: " + mUri, ex);
                mCurrentState = STATE_ERROR;
                mTargetState = STATE_ERROR;
                mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            } finally {
                // REMOVED: mPendingSubtitleTracks.clear();
            }
//            requestLayout();
//            invalidate();
        }
    }

    // REMOVED: addSubtitleSource
    // REMOVED: mPendingSubtitleTracks

    public synchronized void stopPlayback() {
        renderingStarted = false;
        audioRenderingStarted = false;
        if (mMediaPlayer != null) {
            JSTPEventCallback.unregister(mHandle);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            if (mHudViewHolder != null) {
                mHudViewHolder.setMediaPlayer(null);
            }
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
        mIjkVideoViewListener.onClosed();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private synchronized void openVideo() {
        JSLog.s(TAG, "openVideo this=" + this + "-" + Thread.currentThread().getName());
        if (mUri == null) {
            // not ready for playback just yet, will try again later
            JSLog.s(TAG, "not ready for playback just yet");
            return;
        }
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
//        release(false);
//        if (mMediaPlayer != null) {
//            JSLog.s(TAG, "playback already started");
//            return;
//        }

//        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
//        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        try {
            mMediaPlayer = createPlayer(mSettings.getPlayer());
            Log.e(TAG, "trackMeidaState openVideo: mMediapalyer"+this+" create" );
            setRender(mSettings.getRenderViewType());
            setOption(mMediaPlayer);

            // TODO: create SubtitleController in MediaPlayer, but we need
            // a context for the subtitle renderers
            final Context context = getContext();
            // REMOVED: SubtitleController

            // REMOVED: mAudioSession
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnTimedTextListener(mOnTimedTextListener);
            mCurrentBufferPercentage = 0;
            String scheme = mUri.getScheme();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    mSettings.getUsingMediaDataSource() &&
                    (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
                mMediaPlayer.setDataSource(dataSource);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
            } else {
                mMediaPlayer.setDataSource(mUri.toString());
            }
//            bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            if (getIjkMediaPlayer() != null)
                getIjkMediaPlayer().setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "async,cache,crypto,file,http,https,ijkhttphook,ijkinject,ijklivehook,ijklongurl,ijksegment,ijktcphook,pipe,rtp,rtmp,tcp,tls,udp,ijkurlhook,data");
            mPrepareStartTime = System.currentTimeMillis();
            mMediaPlayer.prepareAsync();
            if (mHudViewHolder != null) {
                mHudViewHolder.setMediaPlayer(mMediaPlayer);
            }

            // REMOVED: mPendingSubtitleTracks

            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    attachMediaController();
                }
            });

            // JS_MODIFY
            if (_js_mute != 0) {
                if (getIjkMediaPlayer() != null)
                    getIjkMediaPlayer().jsAudioMute(1);
            }
            // JS_MODIFY
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } finally {
            // REMOVED: mPendingSubtitleTracks.clear();
        }
    }

    public void setMediaController(IMediaController controller) {
//        if (mMediaController != null) {
//            mMediaController.hide();
//        }
        mMediaController = controller;
        attachMediaController();
    }

    private synchronized void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ?
                    (View) this.getParent() : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(isInPlaybackState());
        }
    }

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                    if (mOnVideoSizeChangedListener != null)
                        mOnVideoSizeChangedListener.onVideoSizeChanged(mp, width, height, sarNum, sarDen);
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();
                    mVideoSarNum = mp.getVideoSarNum();
                    mVideoSarDen = mp.getVideoSarDen();
                    if (mVideoWidth != 0 && mVideoHeight != 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mRenderView != null) {
                                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                                }
                                // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                                requestLayout();
                            }
                        });
                    }
                }
            };

    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            mPrepareEndTime = System.currentTimeMillis();
            if (mHudViewHolder != null) {
                mHudViewHolder.updateLoadCost(mPrepareEndTime - mPrepareStartTime);
            }
            mCurrentState = STATE_PREPARED;

            // Get the capabilities of the player for this stream
            // REMOVED: Metadata

            if (mOnPreparedListener != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mOnPreparedListener.onPrepared(mMediaPlayer);
                    }
                });
            }
            if (mMediaController != null) {
                mMediaController.setEnabled(true);

                // JS_MODIFY
                mMediaController.setOwnSeekBar();
                // JS_MODIFY END
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                //Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
                // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (mRenderView != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mRenderView != null) {
                                mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                                mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                            }
                        }
                    });
                    //if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                    // We didn't actually change the size (it was already at the size
                    // we need), so we won't get a "surface changed" callback, so
                    // start the video here instead of in the callback.
                    if (mTargetState == STATE_PLAYING) {
//                        start();
//                        if (mMediaController != null) {
//                            mMediaController.show();
//                        }
                    } else if (!isPlaying() &&
                            (seekToPosition != 0 || getCurrentPosition() > 0)) {
                        if (mMediaController != null) {
                            // Show the media controls when we're paused into a video and make 'em stick.
//                            mMediaController.show(0);
                        }
                    }
                    //}
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (mTargetState == STATE_PLAYING) {
//                    start();
                }
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mCompletionListener =
            new IMediaPlayer.OnCompletionListener() {
                public void onCompletion(IMediaPlayer mp) {
                    mIjkVideoViewListener.onIjkplayerCompleted();
                    mCurrentState = STATE_PLAYBACK_COMPLETED;
                    mTargetState = STATE_PLAYBACK_COMPLETED;
                    if (mMediaController != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mMediaController.hide();
                            }
                        });
                    }
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                }
            };

    private IMediaPlayer.OnInfoListener mInfoListener =
            new IMediaPlayer.OnInfoListener() {
                public boolean onInfo(IMediaPlayer mp, int arg1, Object arg2) {
                    Log.e(TAG, "onInfo:  setview2 case"+arg1 );
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mp, arg1, arg2);
                    }
                    switch (arg1) {
                        case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                            renderingStarted = true;
                            mIjkVideoViewListener.onRenderingStart();
                            reconnetPerssionFlag = true;
                            checkAndSendMessage_JSTP_MSG_VIEW_SWITCHED(0);
                            clearOldRenderView();
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                            mIjkVideoViewListener.onBufferingStart();
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                            mIjkVideoViewListener.onBufferingEnd();
                            break;
                        case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                            Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                            break;
                        case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                            Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                            Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                            Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                            Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                            break;
                        //JS_MODIFY
                        case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                            if (arg2 instanceof Integer) {
                                mVideoRotationDegree = (Integer) arg2;
                            }
                            Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
                            if (mRenderView != null)
                                mRenderView.setVideoRotation(mVideoRotationDegree);
                            break;
                        case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                            audioRenderingStarted = true;
                            mIjkVideoViewListener.onAudioRenderingStart();
                            break;
                        case IjkMediaPlayer.JSTP_MSG_INTERACT_ADV:
                            if (arg2 instanceof String) {
                                mIjkVideoViewListener.onIAEvent((String) arg2);
                            }
                            break;
                        case IjkMediaPlayer.FFP_MSG_BITRATE_CALLBACK:
                            if (arg2 instanceof String) {
                                mIjkVideoViewListener.onBitRateChanged((String) arg2);
                            }
                            break;
                        case IjkMediaPlayer.FFP_MSG_VOLUME_CHANGED:
                            mIjkVideoViewListener.onVolumeChanged((int) arg2);
                            break;

                        case IjkMediaPlayer.FFP_MSG_ERR_RETRY:
                            Log.e("FFP_MSG_ERR_RETRY", "FFP_MSG_ERR_RETRY"+"arg2("+arg2+")");
                            if ((Integer)arg2 == -300 && mMediaPlayer != null && getIjkMediaPlayer().getJstpRet() > 0)
                                branchOriginalFlag = true;
                            else
                                branchOriginalFlag = false;
                            resetConnect();
                            break;

                        case IjkMediaPlayer.FFP_MSG_IJK_NEED_RETRY:
                            mIjkVideoViewListener.onIJKNeedRetry((int) arg2);
                            Log.e("FFP_MSG_IJK_NEED_RETRY", "onInfo: FFP_MSG_IJK_NEED_RETRY" + (int) arg2);
//                            if ((int)arg2 >100 && (int)arg2 < 200){
//                                resetConnect();
//                            }
                            break;


                        case IjkMediaPlayer.JSTP_MSG_VIEW_SWITCHED:
                            // TODO: 21/10/2017 视角切换完成，需要时再定义外部接口
                            Log.e(TAG, "setview2 JSTP_MSG_VIEW_SWITCHED: "+ (Integer)arg2);
                            if (arg2 instanceof Integer) {
                                mIjkVideoViewListener.onViewChangeEnd((Integer) arg2);
                            }
                            if ((Integer)arg2 == -300 && mMediaPlayer != null && getIjkMediaPlayer().getJstpRet() > 0){
                                branchOriginalFlag = true;
                                resetConnect();
                            }else{
                                branchOriginalFlag = false;
                            }
                            break;
                        //JS_MODIFY END
                    }
                    return true;
                }
            };

    private void checkAndSendMessage_JSTP_MSG_VIEW_SWITCHED(int arg){
        if (mSetView2Flag == 1) {
            Log.e(TAG, "setview2 checkAndSendMessage_JSTP_MSG_VIEW_SWITCHED: "+ arg);
            mInfoListener.onInfo(mMediaPlayer, IjkMediaPlayer.JSTP_MSG_VIEW_SWITCHED, arg);
            mSetView2Flag = 0;
        }
    }

    private IMediaPlayer.OnErrorListener mErrorListener =
            new IMediaPlayer.OnErrorListener() {
                public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {

                    checkAndSendMessage_JSTP_MSG_VIEW_SWITCHED(-1);

                    Log.d(TAG, "Error: " + framework_err + "," + impl_err);
                    mCurrentState = STATE_ERROR;
                    mTargetState = STATE_ERROR;
                    if (mMediaController != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mMediaController.hide();
                            }
                        });
                    }

                    /* If an error handler has been supplied, use it and finish. */
                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }

                    return true;
                }
            };

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new IMediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                    mIjkVideoViewListener.onBufferingUpdate(percent);
                }
            };

    private IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            if (mOnSeekCompleteListener != null)
                mOnSeekCompleteListener.onSeekComplete(mp);
            mSeekEndTime = System.currentTimeMillis();
            if (mHudViewHolder != null) {
                mHudViewHolder.updateSeekCost(mSeekEndTime - mSeekStartTime);
            }
        }
    };

    private IMediaPlayer.OnTimedTextListener mOnTimedTextListener = new IMediaPlayer.OnTimedTextListener() {
        @Override
        public void onTimedText(IMediaPlayer mp, IjkTimedText text) {
            if (text != null) {
                subtitleDisplay.setText(text.getText());
            }
        }
    };

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    /**
     * Register a callback to be invoked when an informational event
     * occurs during playback or setup.
     *
     * @param l The callback that will be run
     */
    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }

    // REMOVED: mSHCallback
    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null)
            return;

        if (holder == null) {
            mp.setDisplay(null);
            return;
        }

        holder.bindToMediaPlayer(mp);
    }

    IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }

            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = !mRenderView.shouldWaitForResize()
                    || (mVideoWidth == w && mVideoHeight == h);
            synchronized (IjkVideoView.this) {
                if (mMediaPlayer != null && isValidState && hasValidSize) {
                    if (mSeekWhenPrepared != 0) {
                        seekTo(mSeekWhenPrepared);
                    }
                    start();
                }
            }
        }

        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }

            mSurfaceHolder = holder;
            synchronized (IjkVideoView.this) {
                if (mMediaPlayer != null) {
                    bindSurfaceHolder(mMediaPlayer, holder);
                } else {
//                    mOpenVideoHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            JSLog.s(TAG, "call openVideo onSurfaceCreated this="
//                                    + IjkVideoView.this);
//                            openVideo();
//                        }
//                    });
                }
            }
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
                return;
            }

            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            // REMOVED: if (mMediaController != null) mMediaController.hide();
            // REMOVED: release(true);
            releaseWithoutStop();
        }
    };

    public synchronized void releaseWithoutStop() {
        if (mMediaPlayer != null)
            mMediaPlayer.setDisplay(null);
    }

    /*
     * release the media player in any state
     */
    public synchronized void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            JSTPEventCallback.unregister(mHandle);
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            // REMOVED: mPendingSubtitleTracks.clear();
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = STATE_IDLE;
            }
//            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
//            am.abandonAudioFocus(null);
        }
        if (cleartargetstate) {
            if (mHudViewHolder != null)
                mHudViewHolder.setMediaPlayer(null);
            if (mRenderView instanceof TextureRenderView)
                ((TextureRenderView) mRenderView).cancelClip();
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (isInPlaybackState() && mMediaController != null) {
//            toggleMediaControlsVisiblity();
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onTrackballEvent(MotionEvent ev) {
//        if (isInPlaybackState() && mMediaController != null) {
//            toggleMediaControlsVisiblity();
//        }
//        return false;
//    }
//
//    @Override
//    public synchronized boolean onKeyDown(int keyCode, KeyEvent event) {
//        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&
//                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
//                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
//                keyCode != KeyEvent.KEYCODE_VOLUME_MUTE &&
//                keyCode != KeyEvent.KEYCODE_MENU &&
//                keyCode != KeyEvent.KEYCODE_CALL &&
//                keyCode != KeyEvent.KEYCODE_ENDCALL;
//        if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
//            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
//                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
//                if (mMediaPlayer.isPlaying()) {
//                    pause();
//                    mMediaController.show();
//                } else {
//                    start();
//                    mMediaController.hide();
//                }
//                return true;
//            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
//                if (!mMediaPlayer.isPlaying()) {
//                    start();
//                    mMediaController.hide();
//                }
//                return true;
//            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
//                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
//                if (mMediaPlayer.isPlaying()) {
//                    pause();
//                    mMediaController.show();
//                }
//                return true;
//            } else {
//                toggleMediaControlsVisiblity();
//            }
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//    private void toggleMediaControlsVisiblity() {
//        if (mMediaController.isShowing()) {
//            mMediaController.hide();
//        } else {
//            mMediaController.show();
//        }
//    }

    @Override
    public synchronized void start() {
        if (mCurrentState == STATE_PLAYING)
            return;;

        if (isInPlaybackState()) {
            if (mCurrentState == STATE_PLAYBACK_COMPLETED)
                mSeekStartTime = System.currentTimeMillis();
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public synchronized void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void suspend() {
        renderingStarted = false;
        audioRenderingStarted = false;
        release(false);
    }

    public void resume() {
        mOpenVideoHandler.post(new Runnable() {
            @Override
            public void run() {
                JSLog.s(TAG, "call openVideo resume this=" + IjkVideoView.this);
                openVideo();
            }
        });
    }

    // JS_MODIFY
    public synchronized int getViewNumber() {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getViewNumber();
        }

        return 0;
    }

    public synchronized String getViewName(int view_no) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getViewName(view_no);
        }

        return null;
    }

    public synchronized String getCurrentViewName() {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getCurrentViewName();
        }

        return null;
    }

    public synchronized void setView(String view_name) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            getIjkMediaPlayer().setView(view_name);
        }
    }

    /**
     * @param view_name
     * @return setView2 成功返回0，失败返回-1（暂定）。不在播放状态返回-2
     */
    public synchronized int setView2(String view_name) {
        mSetView2Flag = 1;
//        Log.e(TAG, "setView2: currentstate " + mCurrentState + " currentMedia:" + getIjkMediaPlayer() + " isinplaybackState"+isInPlaybackState()+ " getIjkMediaPlayer"+getIjkMediaPlayer()+ " checkPlayable"+getIjkMediaPlayer().checkPlayable());
        if (isInPlaybackState() && getIjkMediaPlayer() != null && getIjkMediaPlayer().checkPlayable() == 0) {
//        if (getIjkMediaPlayer() != null) {
            mUri = Uri.parse(view_name);
            return getIjkMediaPlayer().setView2(view_name);
        }
        return -2;
    }

    public synchronized int getAudioNumber() {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getAudioNumber();
        }

        return 0;
    }

    public synchronized String getAudioName(int audio_no) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getAudioName(audio_no);
        }

        return null;
    }

    public synchronized String getCurrentAudioName() {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getCurrentAudioName();
        }

        return null;
    }

    public synchronized void setAudio(String audio_name) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            getIjkMediaPlayer().setAudio(audio_name);
        }
    }

    public synchronized int getBitrateNumber() {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getBitrateNumber();
        }

        return 0;
    }

    public synchronized String getBitrateName(int bitrate_no) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getBitrateName(bitrate_no);
        }

        return null;
    }

    public synchronized String getCurrentBitrateName() {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getCurrentBitrateName();
        }

        return null;
    }

    public synchronized void setBitrate(String bitrate_name) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            getIjkMediaPlayer().setBitrate(bitrate_name);
        }
    }

    public synchronized void jsSwitchBitrateCallback(boolean openFlag) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            getIjkMediaPlayer().jsSwitchBitrateCallback(openFlag);
        }
    }

    public synchronized void psSwitchVolumeChangeCallback(boolean openFlag, short threshold) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            getIjkMediaPlayer().psSwitchVolumeChangeCallback(openFlag, threshold);
        }
    }

    public synchronized int getAdaptive() {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getAdaptive();
        }

        return 0;
    }

    public synchronized long getJsdemux() {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getJsdemux();
        }

        return 0;
    }

    //多径
    public synchronized int jsGetMpathUsage(int[] mpathnArray, long[] mpathUsageArray, String[] mpathNameArray) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().jsGetMpathUsage(mpathnArray, mpathUsageArray, mpathNameArray);
        }
        return 0;
    }

    public synchronized int jsGetOpenTimeInfo(int[] timeInfoArray) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().jsGetOpenTimeInfo(timeInfoArray);
        }
        return 0;
    }

    public synchronized int jsGetOpenErrorCode(int[] errorCodeArray) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().jsGetOpenErrorCode(errorCodeArray);
        }
        return 0;
    }
    //多径end

    public synchronized int jsReconnect(int status) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().jsReconnect(status);
        }

        return 0;
    }

    public synchronized long getExtraTs() {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getExtraTS();
        }
        return -1;
    }

    public synchronized long jsGetIJKMeidaPlayer() {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().jsGetIJKMeidaPlayer();
        }
        return -1;
    }


    public synchronized String getNodeIp() {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().getNodeIp();
        }

        return null;
    }

    public synchronized void testLog(String log) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            getIjkMediaPlayer().jsLog(log);
        }
    }

    public synchronized void setBackground(int val) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            getIjkMediaPlayer().setBackground(val);
        }
    }

    public synchronized void showBitrates() {
        if (mMediaPlayer == null || getIjkMediaPlayer() == null)
            return;

        List<String> bitrates = new ArrayList<String>();
        int n = getIjkMediaPlayer().getBitrateNumber();
        for (int i = 0; i < n; i++) {
            bitrates.add(getIjkMediaPlayer().getBitrateName(i));
        }
        bitrates.add("auto");

        final CharSequence[] items = bitrates.toArray(new CharSequence[bitrates.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                synchronized (IjkVideoView.this) {
                    if (isInPlaybackState() && getIjkMediaPlayer() != null) {
                        getIjkMediaPlayer().setBitrate(items[item].toString());
                    }
                }
            }
        });
        builder.show();
    }

    public synchronized void showViews() {
        if (mMediaPlayer == null || getIjkMediaPlayer() == null)
            return;

        List<String> views = new ArrayList<String>();
        int n = getIjkMediaPlayer().getViewNumber();
        for (int i = 0; i < n; i++) {
            views.add(getIjkMediaPlayer().getViewName(i));
        }

        final CharSequence[] items = views.toArray(new CharSequence[views.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                synchronized (IjkVideoView.this) {
                    if (isInPlaybackState() && getIjkMediaPlayer() != null) {
                        getIjkMediaPlayer().setView(items[item].toString());
                    }
                }
            }
        });
        builder.show();
    }

    public synchronized void showAudios() {
        if (mMediaPlayer == null || getIjkMediaPlayer() == null)
            return;

        List<String> audios = new ArrayList<String>();
        int n = getIjkMediaPlayer().getAudioNumber();
        for (int i = 0; i < n; i++) {
            audios.add(getIjkMediaPlayer().getAudioName(i));
        }

        final CharSequence[] items = audios.toArray(new CharSequence[audios.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (isInPlaybackState() && getIjkMediaPlayer() != null) {
                    getIjkMediaPlayer().setAudio(items[item].toString());
                }
            }
        });
        builder.show();
    }

    public void jsInitMode(int mode, String data) {
        if (mode > 0) {
            _js_mode = mode;
            _js_data = data;
        }
    }

    public void jsInitXhard(int enable) {
        if (enable > 0) {
            _js_xhard = 1;
        } else {
            _js_xhard = 0;
        }
    }

    public synchronized int setPSMode(int mode, String url) {
        if (mode > 0 && !TextUtils.isEmpty(url) && getIjkMediaPlayer() != null) {
            return getIjkMediaPlayer().jsSetMode(mode, url);
        } else {
            return -1;
        }
    }

    public synchronized void jsAudioMute(int enable) {
        if (isInPlaybackState() && getIjkMediaPlayer() != null) {
            getIjkMediaPlayer().jsAudioMute(enable);
        } else {
            _js_mute = enable;
        }
    }

    // JS_MODIFY END

    @Override
    public synchronized int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }

        return -1;
    }

    @Override
    public synchronized int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public synchronized void seekTo(int msec) {
        if (isInPlaybackState()) {
            mSeekStartTime = System.currentTimeMillis();
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public synchronized boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public synchronized int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    private synchronized boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    @Override
    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    @Override
    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    // REMOVED: getAudioSessionId();
    // REMOVED: onAttachedToWindow();
    // REMOVED: onDetachedFromWindow();
    // REMOVED: onLayout();
    // REMOVED: draw();
    // REMOVED: measureAndLayoutSubtitleWidget();
    // REMOVED: setSubtitleWidget();
    // REMOVED: getSubtitleLooper();

    //-------------------------
    // Extend: Render
    //-------------------------
    public static final int RENDER_NONE = 0;
    public static final int RENDER_SURFACE_VIEW = 1;
    public static final int RENDER_TEXTURE_VIEW = 2;

    private List<Integer> mAllRenders = new ArrayList<Integer>();
    private int mCurrentRenderIndex = 0;
    private int mCurrentRender = RENDER_NONE;

    private void initRenders() {
        mAllRenders.clear();

        if (mSettings.getEnableSurfaceView())
            mAllRenders.add(RENDER_SURFACE_VIEW);
        if (mSettings.getEnableTextureView() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            mAllRenders.add(RENDER_TEXTURE_VIEW);
        if (mSettings.getEnableNoView())
            mAllRenders.add(RENDER_NONE);

        if (mAllRenders.isEmpty())
            mAllRenders.add(RENDER_SURFACE_VIEW);
        mCurrentRender = mAllRenders.get(mCurrentRenderIndex);
        setRender(mCurrentRender);
    }

    public int toggleRender() {
        mCurrentRenderIndex++;
        mCurrentRenderIndex %= mAllRenders.size();

        mCurrentRender = mAllRenders.get(mCurrentRenderIndex);
        setRender(mCurrentRender);
        return mCurrentRender;
    }

    public int toggleAspectRatio() {
        mCurrentAspectRatioIndex++;
        mCurrentAspectRatioIndex %= s_allAspectRatio.length;

        mCurrentAspectRatio = s_allAspectRatio[mCurrentAspectRatioIndex];
        if (mRenderView != null)
            mRenderView.setAspectRatio(mCurrentAspectRatio);
        return mCurrentAspectRatio;
    }

    @NonNull
    public static String getRenderText(Context context, int render) {
        String text;
        switch (render) {
            case RENDER_NONE:
                text = context.getString(R.string.VideoView_render_none);
                break;
            case RENDER_SURFACE_VIEW:
                text = context.getString(R.string.VideoView_render_surface_view);
                break;
            case RENDER_TEXTURE_VIEW:
                text = context.getString(R.string.VideoView_render_texture_view);
                break;
            default:
                text = context.getString(R.string.N_A);
                break;
        }
        return text;
    }

    //-------------------------
    // Extend: Player
    //-------------------------
    public synchronized int togglePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }

        if (mRenderView != null) {
            mRenderView.getView().invalidate();
        }
        mOpenVideoHandler.post(new Runnable() {
            @Override
            public void run() {
                JSLog.s(TAG, "call openVideo togglePlayer this=" + IjkVideoView.this);
                openVideo();
            }
        });
        return mSettings.getPlayer();
    }

    @NonNull
    public static String getPlayerText(Context context, int player) {
        String text;
        switch (player) {
            case Settings.PV_PLAYER__AndroidMediaPlayer:
                text = context.getString(R.string.VideoView_player_AndroidMediaPlayer);
                break;
            case Settings.PV_PLAYER__IjkMediaPlayer:
                text = context.getString(R.string.VideoView_player_IjkMediaPlayer);
                break;
            case Settings.PV_PLAYER__IjkExoMediaPlayer:
                text = context.getString(R.string.VideoView_player_IjkExoMediaPlayer);
                break;
            default:
                text = context.getString(R.string.N_A);
                break;
        }
        return text;
    }

    public IMediaPlayer createPlayer(int playerType) {
        IMediaPlayer mediaPlayer = null;

        switch (playerType) {
            //case Settings.PV_PLAYER__IjkExoMediaPlayer: {
            //    IjkExoMediaPlayer IjkExoMediaPlayer = new IjkExoMediaPlayer(mAppContext);
            //    mediaPlayer = IjkExoMediaPlayer;
            //}
            //break;
            case Settings.PV_PLAYER__AndroidMediaPlayer: {
                AndroidMediaPlayer androidMediaPlayer = new AndroidMediaPlayer();
                mediaPlayer = androidMediaPlayer;
            }
            break;
            case Settings.PV_PLAYER__IjkMediaPlayer:
            default: {
                IjkMediaPlayer ijkMediaPlayer = null;
                if (mUri != null) {
                    // JS_MODIFY
                    JSLog.s(TAG, "new IjkMediaPlayer " + _stream_id + " " + _jsl_dmx + " this=" + this);
                    ijkMediaPlayer = new IjkMediaPlayer(_stream_id, _jsl_dmx);
                    if (_js_mode > 0) {
                        ijkMediaPlayer.jsInitMode(_js_mode, _js_data);
                    }

                    ijkMediaPlayer.jsInitXhard(_js_xhard);
                    mHandle = JSTPEventCallback.register(this);
                    // JS_MODIFY END

//                    ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
//
//                    if (mSettings.getUsingMediaCodec()) {
//                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
//                        if (mSettings.getUsingMediaCodecAutoRotate()) {
//                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
//                        } else {
//                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
//                        }
//                        if (mSettings.getMediaCodecHandleResolutionChange()) {
//                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
//                        } else {
//                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
//                        }
//                    } else {
//                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
//                    }
//
//                    if (mSettings.getUsingOpenSLES()) {
//                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
//                    } else {
//                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
//                    }
//
//                    String pixelFormat = mSettings.getPixelFormat();
//                    if (TextUtils.isEmpty(pixelFormat)) {
//                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
//                    } else {
//                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
//                    }
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
//
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
//
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
                }
                mediaPlayer = ijkMediaPlayer;
            }
            break;
        }

        mediaPlayer = new TextureMediaPlayer(mediaPlayer);

        return mediaPlayer;
    }

    //-------------------------
    // Extend: Background
    //-------------------------

    private boolean mEnableBackgroundPlay = false;

    private void initBackground() {
        mEnableBackgroundPlay = mSettings.getEnableBackgroundPlay();
//        if (mEnableBackgroundPlay) {
        MediaPlayerService.intentToStart(getContext());
        mMediaPlayer = MediaPlayerService.getMediaPlayer();
        if (mHudViewHolder != null)
            mHudViewHolder.setMediaPlayer(mMediaPlayer);
//        }
    }

//    public boolean isBackgroundPlayEnabled() {
//        return mEnableBackgroundPlay;
//    }

    public void enterBackground() {
        MediaPlayerService.setMediaPlayer(mMediaPlayer);
    }

    public void stopBackgroundPlay() {
        MediaPlayerService.setMediaPlayer(null);
    }

    //-------------------------
    // Extend: Background
    //-------------------------
    public synchronized AlertDialog showMediaInfo() {
        if (mMediaPlayer == null)
            return null;

        int selectedVideoTrack = MediaPlayerCompat.getSelectedTrack(mMediaPlayer, ITrackInfo.MEDIA_TRACK_TYPE_VIDEO);
        int selectedAudioTrack = MediaPlayerCompat.getSelectedTrack(mMediaPlayer, ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
        int selectedSubtitleTrack = MediaPlayerCompat.getSelectedTrack(mMediaPlayer, ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);

        TableLayoutBinder builder = new TableLayoutBinder(getContext());
        builder.appendSection(R.string.mi_player);
        builder.appendRow2(R.string.mi_player, MediaPlayerCompat.getName(mMediaPlayer));
        builder.appendSection(R.string.mi_media);
        builder.appendRow2(R.string.mi_resolution, buildResolution(mVideoWidth, mVideoHeight, mVideoSarNum, mVideoSarDen));
        builder.appendRow2(R.string.mi_length, buildTimeMilli(mMediaPlayer.getDuration()));

        ITrackInfo trackInfos[] = mMediaPlayer.getTrackInfo();
        if (trackInfos != null) {
            int index = -1;
            for (ITrackInfo trackInfo : trackInfos) {
                index++;

                int trackType = trackInfo.getTrackType();
                if (index == selectedVideoTrack) {
                    builder.appendSection(getContext().getString(R.string.mi_stream_fmt1, index) + " " + getContext().getString(R.string.mi__selected_video_track));
                } else if (index == selectedAudioTrack) {
                    builder.appendSection(getContext().getString(R.string.mi_stream_fmt1, index) + " " + getContext().getString(R.string.mi__selected_audio_track));
                } else if (index == selectedSubtitleTrack) {
                    builder.appendSection(getContext().getString(R.string.mi_stream_fmt1, index) + " " + getContext().getString(R.string.mi__selected_subtitle_track));
                } else {
                    builder.appendSection(getContext().getString(R.string.mi_stream_fmt1, index));
                }
                builder.appendRow2(R.string.mi_type, buildTrackType(trackType));
                builder.appendRow2(R.string.mi_language, buildLanguage(trackInfo.getLanguage()));

                IMediaFormat mediaFormat = trackInfo.getFormat();
                if (mediaFormat == null) {
                } else if (mediaFormat instanceof IjkMediaFormat) {
                    switch (trackType) {
                        case ITrackInfo.MEDIA_TRACK_TYPE_VIDEO:
                            builder.appendRow2(R.string.mi_codec, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_LONG_NAME_UI));
                            builder.appendRow2(R.string.mi_profile_level, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PROFILE_LEVEL_UI));
                            builder.appendRow2(R.string.mi_pixel_format, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PIXEL_FORMAT_UI));
                            builder.appendRow2(R.string.mi_resolution, mediaFormat.getString(IjkMediaFormat.KEY_IJK_RESOLUTION_UI));
                            builder.appendRow2(R.string.mi_frame_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_FRAME_RATE_UI));
                            builder.appendRow2(R.string.mi_bit_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_BIT_RATE_UI));
                            break;
                        case ITrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                            builder.appendRow2(R.string.mi_codec, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_LONG_NAME_UI));
                            builder.appendRow2(R.string.mi_profile_level, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PROFILE_LEVEL_UI));
                            builder.appendRow2(R.string.mi_sample_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_SAMPLE_RATE_UI));
                            builder.appendRow2(R.string.mi_channels, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CHANNEL_UI));
                            builder.appendRow2(R.string.mi_bit_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_BIT_RATE_UI));
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        AlertDialog.Builder adBuilder = builder.buildAlertDialogBuilder();
        adBuilder.setTitle(R.string.media_information);
        adBuilder.setNegativeButton(R.string.close, null);
        return adBuilder.show();
    }

    private String buildResolution(int width, int height, int sarNum, int sarDen) {
        StringBuilder sb = new StringBuilder();
        sb.append(width);
        sb.append(" x ");
        sb.append(height);

        if (sarNum > 1 || sarDen > 1) {
            sb.append("[");
            sb.append(sarNum);
            sb.append(":");
            sb.append(sarDen);
            sb.append("]");
        }

        return sb.toString();
    }

    private String buildTimeMilli(long duration) {
        long total_seconds = duration / 1000;
        long hours = total_seconds / 3600;
        long minutes = (total_seconds % 3600) / 60;
        long seconds = total_seconds % 60;
        if (duration <= 0) {
            return "--:--";
        }
        if (hours >= 100) {
            return String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        }
    }

    private String buildTrackType(int type) {
        Context context = getContext();
        switch (type) {
            case ITrackInfo.MEDIA_TRACK_TYPE_VIDEO:
                return context.getString(R.string.TrackType_video);
            case ITrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                return context.getString(R.string.TrackType_audio);
            case ITrackInfo.MEDIA_TRACK_TYPE_SUBTITLE:
                return context.getString(R.string.TrackType_subtitle);
            case ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                return context.getString(R.string.TrackType_timedtext);
            case ITrackInfo.MEDIA_TRACK_TYPE_METADATA:
                return context.getString(R.string.TrackType_metadata);
            case ITrackInfo.MEDIA_TRACK_TYPE_UNKNOWN:
            default:
                return context.getString(R.string.TrackType_unknown);
        }
    }

    private String buildLanguage(String language) {
        if (TextUtils.isEmpty(language))
            return "und";
        return language;
    }

    public synchronized ITrackInfo[] getTrackInfo() {
        if (mMediaPlayer == null)
            return null;

        return mMediaPlayer.getTrackInfo();
    }

    public synchronized void selectTrack(int stream) {
        MediaPlayerCompat.selectTrack(mMediaPlayer, stream);
    }

    public synchronized void deselectTrack(int stream) {
        MediaPlayerCompat.deselectTrack(mMediaPlayer, stream);
    }

    public synchronized int getSelectedTrack(int trackType) {
        return MediaPlayerCompat.getSelectedTrack(mMediaPlayer, trackType);
    }


    //polyv

    private static final int[] s_allAspectRatio = {
            IRenderView.AR_ASPECT_FIT_PARENT,
            IRenderView.AR_ASPECT_FILL_PARENT,
            IRenderView.AR_ASPECT_WRAP_CONTENT,
            // IRenderView.AR_MATCH_PARENT,
            IRenderView.AR_16_9_FIT_PARENT,
            IRenderView.AR_4_3_FIT_PARENT};
    private int mCurrentAspectRatioIndex = 0;
    private int mCurrentAspectRatio = s_allAspectRatio[0];

    private IjkMediaPlayer getIjkMediaPlayer() {
        IjkMediaPlayer ijkMediaPlayer = null;
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            ijkMediaPlayer = (IjkMediaPlayer) mMediaPlayer;
        } else if (mMediaPlayer instanceof MediaPlayerProxy) {
            MediaPlayerProxy proxy = (MediaPlayerProxy) mMediaPlayer;
            IMediaPlayer internal = proxy.getInternalMediaPlayer();
            if (internal instanceof IjkMediaPlayer)
                ijkMediaPlayer = (IjkMediaPlayer) internal;
        }
        return ijkMediaPlayer;
    }

    public boolean isInPlaybackStateForwarding() {
        return isInPlaybackState();
    }

    public IRenderView getRenderView() {
        return this.mRenderView;
    }

    public int getCurrentAspectRatio() {
        return this.mCurrentAspectRatio;
    }

    public void setCurrentAspectRatio(int aspectRatio) {
        this.mCurrentAspectRatio = aspectRatio;
        if (mRenderView != null) {
            mRenderView.setAspectRatio(mCurrentAspectRatio);
        }
    }

    public int getVideoWidth() {
        return this.mVideoWidth;
    }

    public int getVideoHeight() {
        return this.mVideoHeight;
    }

    public IMediaPlayer getMediaPlayer() {
        return this.mMediaPlayer;
    }

    public SurfaceHolder getSurfaceHolder() {
        if (mSurfaceHolder == null) return null;
        return mSurfaceHolder.getSurfaceHolder();
    }

    public void setSpeed(float speed) {
        if (mMediaPlayer != null) {
            if (getIjkMediaPlayer() != null) {
                getIjkMediaPlayer().setSpeed(speed);
            }
        }
    }

    public float getSpeed() {
        if (mMediaPlayer != null) {
            if (getIjkMediaPlayer() != null) {
                return getIjkMediaPlayer().getSpeed(.0f);
            }
        }

        return .0f;
    }

    public int getStateIdleCode() {
        return STATE_IDLE;
    }

    public int getStateErrorCode() {
        return STATE_ERROR;
    }

    public int getStatePreparingCode() {
        return STATE_PREPARING;
    }

    public int getStatePreparedCode() {
        return STATE_PREPARED;
    }

    public int getStatePauseCode() {
        return STATE_PAUSED;
    }

    public int getStatePlayingCode() {
        return STATE_PLAYING;
    }

    public int getStatePlaybackCompletedCode() {
        return STATE_PLAYBACK_COMPLETED;
    }

    public int getCurrentState() {
        return mCurrentState;
    }

    public int getTargetState() {
        return mTargetState;
    }

    public void setTargetState(int state) {
        mTargetState = state;
    }

    private Object[][] mOptionParameters;

    public void setOptionParameters(Object[][] mOptionParameters) {
        this.mOptionParameters = mOptionParameters;
    }

    private void setOption(IMediaPlayer mediaPlayer) {
        if (getIjkMediaPlayer() != null) {
            IjkMediaPlayer ijkMediaPlayer = getIjkMediaPlayer();
            IjkMediaPlayer.native_setLogLevel(ijkLogLevel);

//            if (mSettings.getUsingMediaCodec()) {
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
//                if (mSettings.getUsingMediaCodecAutoRotate()) {
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
//                } else {
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
//                }
//                if (mSettings.getMediaCodecHandleResolutionChange()) {
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
//                } else {
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
//                }
//            } else {
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
//            }

            if (mSettings.getUsingOpenSLES()) {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
            } else {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
            }

            String pixelFormat = mSettings.getPixelFormat();
            if (TextUtils.isEmpty(pixelFormat)) {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
            } else {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
            }
//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);

//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);

            //polyv
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
            //dolby
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "endpoint", PolyvDetection.isHeadsetOn(mAppContext) ? 2 : 1);//speaker-1,headphone-2
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "dap_onoff", PolyvDetection.isDolbyDevice() ? 0 : 1);//后处理，1-on,0-off
//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "dialog_enhancement_gain", 0);//不用
        }
        if (mOptionParameters != null && mOptionParameters.length > 0 && getIjkMediaPlayer() != null) {
            IjkMediaPlayer ijkMediaPlayer = getIjkMediaPlayer();
            for (int i = 0; i < mOptionParameters.length; i++) {
                if (mOptionParameters[i][2] instanceof String) {
                    ijkMediaPlayer.setOption((int) mOptionParameters[i][0], (String) mOptionParameters[i][1], (String) mOptionParameters[i][2]);
                } else {
                    ijkMediaPlayer.setOption((int) mOptionParameters[i][0], (String) mOptionParameters[i][1], (int) mOptionParameters[i][2]);
                }
            }
        }
    }

    public void clearOptionParameters() {
        mOptionParameters = null;
    }

    public int ijkLogLevel = IjkMediaPlayer.IJK_LOG_DEBUG;

    public void setIjkLogLevel(int ijkLogLevel) {
        this.ijkLogLevel = ijkLogLevel;
    }

    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;
    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener l) {
        mOnSeekCompleteListener = l;
    }

    public void setOnVideoSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener l) {
        mOnVideoSizeChangedListener = l;
    }

    public void onErrorState() {
        release(false);
        mCurrentState = STATE_ERROR;
        mTargetState = STATE_ERROR;
        if (mMediaController != null) {
            mMediaController.hide();
        }
    }

    public void resetLoadCost() {
        if (mHudViewHolder != null) {
            mHudViewHolder.updateLoadCost(0);
            mHudViewHolder.updateSeekCost(0);
        }
    }

    public void setMirror(boolean paramBoolean) {
        if (mRenderView instanceof TextureRenderView)
            ((TextureRenderView) mRenderView).setMirror(paramBoolean);
    }

    public Bitmap screenshot() {
        if (mRenderView instanceof TextureRenderView) {
            Bitmap bitmap;
            if (mCurrentAspectRatio == IRenderView.AR_ASPECT_FILL_PARENT || mCurrentAspectRatio == IRenderView.AR_MATCH_PARENT) {
                bitmap = ((TextureRenderView) mRenderView).getBitmap(getWidth(), getHeight());
            } else {
                bitmap = ((TextureRenderView) mRenderView).getBitmap();
            }
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                matrix.postScale(((TextureRenderView) mRenderView).getScaleX(), ((TextureRenderView) mRenderView).getScaleY());
                matrix.postRotate(((TextureRenderView) mRenderView).getRotation());
                Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                if (!bitmap.equals(newBitmap) && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                return newBitmap;
            }
        }
        return null;
    }

    public boolean startClip(int cancelSecond) {
        if (mRenderView instanceof TextureRenderView && isInPlaybackState()) {
            ((TextureRenderView) mRenderView).startClip(cancelSecond, getWidth(), getHeight(), mCurrentAspectRatio, mMediaPlayer);
        }
        return false;
    }

    public void stopClip(GifMaker.OnGifListener listener) {
        if (mRenderView instanceof TextureRenderView) {
            ((TextureRenderView) mRenderView).stopClip(listener);
        }
    }

    public void cancelClip() {
        if (mRenderView instanceof TextureRenderView) {
            ((TextureRenderView) mRenderView).cancelClip();
        }
    }

    public void setLogTag(String tag) {
        this.TAG = tag;
    }

    public boolean setDolbyEndpointParam(boolean isHeadsetOn) {
        if (getIjkMediaPlayer() != null) {
            getIjkMediaPlayer().setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "endpoint", isHeadsetOn ? 2 : 1);//speaker-1,headphone-2
            return true;
        }
        return false;
    }

    public void resetVRRender() {
        int renderViewType = mSettings.getRenderViewType();
        if (renderViewType == PolyvRenderType.RENDER_GLSURFACE_VIEW) {
            PolyvGLSurfaceRenderView renderView = new PolyvGLSurfaceRenderView(getContext());
            if (glSurfaceInitCompletionListener != null) {
                glSurfaceInitCompletionListener.onCompletionListener(renderView);
            }

            Log.i(TAG, renderView.toString());
            setRenderView(renderView);
        } else if (renderViewType == PolyvRenderType.RENDER_GLTEXTURE_VIEW) {
            PolyvGLTextureRenderView renderView = new PolyvGLTextureRenderView(getContext());
            if (glTextureInitCompletionListener != null) {
                glTextureInitCompletionListener.onCompletionListener(renderView);
            }

            Log.i(TAG, renderView.toString());
            setRenderView(renderView);
        }
    }

    public void setVRViewInitCompletionListener(PolyvGLSurfaceRenderView.OnInitCompletionListener l) {
        glSurfaceInitCompletionListener = l;
    }

    public void setVRViewInitCompletionListener(PolyvGLTextureRenderView.OnInitCompletionListener l) {
        glTextureInitCompletionListener = l;
    }
}
