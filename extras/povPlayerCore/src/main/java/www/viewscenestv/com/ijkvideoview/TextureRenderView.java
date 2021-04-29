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
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.graphics.Rect;

import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.easefun.povplayer.core.gifmaker.GifMaker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
//import com.viewscene.transcoder.JSLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.ISurfaceTextureHolder;
import tv.danmaku.ijk.media.player.ISurfaceTextureHost;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TextureRenderView extends TextureView implements IRenderView {
    private static final String TAG = "TextureRenderView";
    private MeasureHelper mMeasureHelper;

    private int clipStage = STAGE_DEFAULT;
    private static final int STAGE_DEFAULT = 0;
    private static final int STAGE_START = 1;
    private static final int STAGE_STOP = 2;
    private static final int STAGE_CANCEL = 3;
    private IMediaPlayer mMediaPlayer;
    private Bitmap bitmap;
    private List<Bitmap> bitmaps = new ArrayList<>();
    private GifMaker gifMaker = new GifMaker();
    private static final int pw = 420;//不要设置过大，不然主线程会有卡顿。另外也会影响转换成gif的速度。
    private int vw, vh, sr;
    private int count;

    public TextureRenderView(Context context) {
        super(context);
        initView(context);
    }

    public TextureRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TextureRenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextureRenderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        mMeasureHelper = new MeasureHelper(this);
        mSurfaceCallback = new SurfaceCallback(this);
        setSurfaceTextureListener(mSurfaceCallback);

        /*
         * onSurfaceTextureAvailable does not get called if it is already available.
         */
        if (this.isAvailable()) {
            mSurfaceCallback.onSurfaceTextureAvailable(getSurfaceTexture(), getWidth(), getHeight());
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == STAGE_CANCEL) {
                cancelClip();
            }
        }
    };

    public void startClip(int cancelSecond, int vw, int vh, int sr, IMediaPlayer mediaPlayer) {
        cancelClip();
        this.vw = vw;
        this.vh = vh;
        this.sr = sr;
        this.mMediaPlayer = mediaPlayer;
        clipStage = STAGE_START;
        cancelSecond = Math.min(8, cancelSecond);
        handler.removeMessages(STAGE_CANCEL);
        handler.sendEmptyMessageDelayed(STAGE_CANCEL, cancelSecond * 1000);
    }

    public void stopClip(final GifMaker.OnGifListener listener) {
        clipStage = STAGE_STOP;
        handler.removeMessages(STAGE_CANCEL);
        makeGif(listener);
    }

    private void makeGif(final GifMaker.OnGifListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (clipStage != STAGE_CANCEL) {
                    gifMaker.setOnGifListener(new GifMaker.OnGifListener() {
                        @Override
                        public void onMake(int current, int total, int makeMs) {
                            if (listener != null)
                                listener.onMake(current, total, makeMs);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            if (listener != null)
                                listener.onError(throwable);
                            clear();
                        }

                        @Override
                        public void onFinish(byte[] data, int w, int h, int finishSecond) {
                            if (listener != null)
                                listener.onFinish(data, w, h, finishSecond);
                            clear();
                        }
                    });
                    if (bitmaps.size() == 0) {
                        if (listener != null)
                            listener.onError(new Exception("Convert image number is 0"));
                        clear();
                    } else {
                        gifMaker.makeGif(bitmaps, getScaleX(), getScaleY(), getRotation());
                    }
                } else {
                    if (listener != null)
                        listener.onError(new Exception("Has been cancelled"));
                    clear();
                }
            }
        }).start();
    }

    public void cancelClip() {
        clipStage = STAGE_CANCEL;
        clear();
    }

    private void clear() {
        count = 0;
        handler.removeMessages(STAGE_CANCEL);
        if (gifMaker != null)
            gifMaker.cancel();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        for (Bitmap bitmap : bitmaps) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        bitmaps.clear();
        mMediaPlayer = null;
        System.gc();
    }

    private float getVideoOutputFramesPerSecond() {
        IjkMediaPlayer ijkMediaPlayer = null;
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            ijkMediaPlayer = (IjkMediaPlayer) mMediaPlayer;
        } else if (mMediaPlayer instanceof MediaPlayerProxy) {
            MediaPlayerProxy proxy = (MediaPlayerProxy) mMediaPlayer;
            IMediaPlayer internal = proxy.getInternalMediaPlayer();
            if (internal instanceof IjkMediaPlayer)
                ijkMediaPlayer = (IjkMediaPlayer) internal;
        }
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.getVideoOutputFramesPerSecond();
        }
        return 0;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean shouldWaitForResize() {
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        mSurfaceCallback.willDetachFromWindow();
        super.onDetachedFromWindow();
        mSurfaceCallback.didDetachFromWindow();
    }

    //--------------------
    // Layout & Measure
    //--------------------
    @Override
    public Rect sourceAreaToScreen(Rect source){
        return mMeasureHelper.sourceAreaToScreen(source);
    }

    @Override
    public void setDisplayArea(Rect area) {
        mMeasureHelper.setDisplayArea(area);
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        if (videoSarNum > 0 && videoSarDen > 0) {
            mMeasureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
            requestLayout();
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        mMeasureHelper.setVideoRotation(degree);
        setRotation(degree);
    }

    @Override
    public void setAspectRatio(int aspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio);
        requestLayout();
    }

    public void setMirror(boolean paramBoolean) {
        if (getRotation() == 90 || getRotation() == 270) {
            setScaleY(paramBoolean ? -1.0F : 1.0F);
        } else {
            setScaleX(paramBoolean ? -1.0F : 1.0F);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }

    //--------------------
    // TextureViewHolder
    //--------------------

    public ISurfaceHolder getSurfaceHolder() {
        return mSurfaceCallback.mISurfaceHolder;

        //InternalSurfaceHolder(mWeakRenderView.get(), mSurfaceTexture, this);
        //return new InternalSurfaceHolder(this, mSurfaceCallback.mSurfaceTexture, mSurfaceCallback);
    }

    private static final class InternalSurfaceHolder implements ISurfaceHolder {
        private TextureRenderView mTextureView;
        private SurfaceTexture mSurfaceTexture;
        private ISurfaceTextureHost mSurfaceTextureHost;
        private Surface mSurface;

        public InternalSurfaceHolder(@NonNull TextureRenderView textureView,
                @Nullable SurfaceTexture surfaceTexture,
                @NonNull ISurfaceTextureHost surfaceTextureHost) {
            mTextureView = textureView;
            mSurfaceTexture = surfaceTexture;
            mSurfaceTextureHost = surfaceTextureHost;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void bindToMediaPlayer(IMediaPlayer mp) {
            if (mp == null)
                return;

            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) &&
                    (mp instanceof ISurfaceTextureHolder)) {
                ISurfaceTextureHolder textureHolder = (ISurfaceTextureHolder) mp;
                mTextureView.mSurfaceCallback.setOwnSurfaceTexture(false);

                // 修改这里会导致硬解码时 横竖屏切换黑屏
                SurfaceTexture surfaceTexture = textureHolder.getSurfaceTexture();
                if (surfaceTexture != null) {
                    mTextureView.setSurfaceTexture(surfaceTexture);
                } else {
                    textureHolder.setSurfaceTexture(mSurfaceTexture);
                    textureHolder.setSurfaceTextureHost(mTextureView.mSurfaceCallback);
                }

            } else {
                mp.setSurface(openSurface());
            }
        }

        @NonNull
        @Override
        public IRenderView getRenderView() {
            return mTextureView;
        }

        @Nullable
        @Override
        public SurfaceHolder getSurfaceHolder() {
            return null;
        }

        @Nullable
        @Override
        public SurfaceTexture getSurfaceTexture() {
            return mSurfaceTexture;
        }

        @Nullable
        @Override
        public Surface openSurface() {
            if (mSurfaceTexture == null)
                return null;
            if (mSurface == null)
                mSurface = new Surface(mSurfaceTexture);
            return mSurface;
        }

        @Override
        public Surface getSurface() {
            return mSurface == null ? mSurface = new Surface(mSurfaceTexture) : mSurface;
        }

        @Override
        public void release() {
            if (mSurface != null){
                mSurface.release();
            }
        }
    }

    //-------------------------
    // SurfaceHolder.Callback
    //-------------------------

    @Override
    public void addRenderCallback(IRenderCallback callback) {
        mSurfaceCallback.addRenderCallback(callback);
    }

    @Override
    public void removeRenderCallback(IRenderCallback callback) {
        mSurfaceCallback.removeRenderCallback(callback);
    }

    private SurfaceCallback mSurfaceCallback;

    private final class SurfaceCallback implements SurfaceTextureListener, ISurfaceTextureHost {
        private SurfaceTexture mSurfaceTexture;
        private ISurfaceHolder mISurfaceHolder;
        private boolean mIsFormatChanged;
        private int mWidth;
        private int mHeight;

        private boolean mOwnSurfaceTexture = true;
        private boolean mWillDetachFromWindow = false;
        private boolean mDidDetachFromWindow = false;

        private WeakReference<TextureRenderView> mWeakRenderView;
        private Map<IRenderCallback, Object> mRenderCallbackMap = new ConcurrentHashMap<IRenderCallback, Object>();

        public SurfaceCallback(@NonNull TextureRenderView renderView) {
            mWeakRenderView = new WeakReference<TextureRenderView>(renderView);
            mISurfaceHolder = new InternalSurfaceHolder(mWeakRenderView.get(), mSurfaceTexture, this);
        }

        public void setOwnSurfaceTexture(boolean ownSurfaceTexture) {
            mOwnSurfaceTexture = ownSurfaceTexture;
        }

        public void addRenderCallback(@NonNull IRenderCallback callback) {
            mRenderCallbackMap.put(callback, callback);

            if (mISurfaceHolder == null) {
                mISurfaceHolder = new InternalSurfaceHolder(mWeakRenderView.get(), mSurfaceTexture, this);
            }
            if (mSurfaceTexture != null) {
                callback.onSurfaceCreated(mISurfaceHolder, mWidth, mHeight);
            }
            if (mIsFormatChanged) {
                callback.onSurfaceChanged(mISurfaceHolder, 0, mWidth, mHeight);
            }
        }

        public void removeRenderCallback(@NonNull IRenderCallback callback) {
            mRenderCallbackMap.remove(callback);
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mSurfaceTexture = surface;
            mIsFormatChanged = false;
            mWidth = 0;
            mHeight = 0;
            //JSLog.s("Texture", "onSurfaceTextureAvailable " + String.valueOf(width) + " " + String.valueOf(height));


            if (mISurfaceHolder != null) {
                mISurfaceHolder.release();
                mISurfaceHolder = null;
            }
            mISurfaceHolder = new InternalSurfaceHolder(mWeakRenderView.get(), mSurfaceTexture, this);

            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceCreated(mISurfaceHolder, 0, 0);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            mSurfaceTexture = surface;
            mIsFormatChanged = true;
            mWidth = width;
            mHeight = height;

            if (mISurfaceHolder != null) {
                mISurfaceHolder.release();
                mISurfaceHolder = null;
            }
            mISurfaceHolder = new InternalSurfaceHolder(mWeakRenderView.get(), mSurfaceTexture, this);

            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceChanged(mISurfaceHolder, 0, width, height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mSurfaceTexture = surface;
            mIsFormatChanged = false;
            mWidth = 0;
            mHeight = 0;

            if (mISurfaceHolder != null) {
                mISurfaceHolder.release();
                mISurfaceHolder = null;
            }

            ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakRenderView.get(), surface, this);
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceDestroyed(surfaceHolder);
            }

            surfaceHolder.release();

            Log.d(TAG, "onSurfaceTextureDestroyed: destroy: " + mOwnSurfaceTexture);
            return mOwnSurfaceTexture;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            if (clipStage == STAGE_START) {
                int space = 4;
                float fpsOutput = getVideoOutputFramesPerSecond();
                if (fpsOutput > 0) {
                    space = Math.max(3, (int) (fpsOutput / 5.5));
                }
                if (count % space == 0) {
                    int w, h;
                    if (sr == IRenderView.AR_ASPECT_FILL_PARENT || sr == IRenderView.AR_MATCH_PARENT) {
                        int tw = pw;
                        int th = pw * vh / vw;
                        if (getRotation() == 90 || getRotation() == 270) {
                            w = th;
                            h = tw;
                        } else {
                            w = tw;
                            h = th;
                        }
                    } else {
                        int mw = Math.max(getWidth(), getHeight());
                        int mh = Math.min(getWidth(), getHeight());
                        int th = pw * mh / mw;
                        boolean flag = getWidth() > getHeight();
                        w = flag ? pw : th;
                        h = flag ? th : pw;
                    }
                    bitmap = getBitmap(w, h);
                    bitmaps.add(bitmap);
                }
                count++;
            }
        }

        //-------------------------
        // ISurfaceTextureHost
        //-------------------------

        @Override
        public void releaseSurfaceTexture(SurfaceTexture surfaceTexture) {
//            if (surfaceTexture == null) {
//                Log.d(TAG, "releaseSurfaceTexture: null");
//            } else if (mDidDetachFromWindow) {
//                if (surfaceTexture != mSurfaceTexture) {
//                    Log.d(TAG, "releaseSurfaceTexture: didDetachFromWindow(): release different SurfaceTexture");
//                    surfaceTexture.release();
//                } else if (!mOwnSurfaceTexture) {
//                    Log.d(TAG, "releaseSurfaceTexture: didDetachFromWindow(): release detached SurfaceTexture");
//                    surfaceTexture.release();
//                } else {
//                    Log.d(TAG, "releaseSurfaceTexture: didDetachFromWindow(): already released by TextureView");
//                }
//            } else if (mWillDetachFromWindow) {
//                if (surfaceTexture != mSurfaceTexture) {
//                    Log.d(TAG, "releaseSurfaceTexture: willDetachFromWindow(): release different SurfaceTexture");
//                    surfaceTexture.release();
//                } else if (!mOwnSurfaceTexture) {
//                    Log.d(TAG, "releaseSurfaceTexture: willDetachFromWindow(): re-attach SurfaceTexture to TextureView");
//                    setOwnSurfaceTexture(true);
//                } else {
//                    Log.d(TAG, "releaseSurfaceTexture: willDetachFromWindow(): will released by TextureView");
//                }
//            } else {
//                if (surfaceTexture != mSurfaceTexture) {
//                    Log.d(TAG, "releaseSurfaceTexture: alive: release different SurfaceTexture");
//                    surfaceTexture.release();
//                } else if (!mOwnSurfaceTexture) {
//                    Log.d(TAG, "releaseSurfaceTexture: alive: re-attach SurfaceTexture to TextureView");
//                    setOwnSurfaceTexture(true);
//                } else {
//                    Log.d(TAG, "releaseSurfaceTexture: alive: will released by TextureView");
//                }
//            }
        }

        public void willDetachFromWindow() {
            Log.d(TAG, "willDetachFromWindow()");
            mWillDetachFromWindow = true;
        }

        public void didDetachFromWindow() {
            Log.d(TAG, "didDetachFromWindow()");
            mDidDetachFromWindow = true;
        }
    }

    //--------------------
    // Accessibility
    //--------------------

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(TextureRenderView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(TextureRenderView.class.getName());
    }
}
