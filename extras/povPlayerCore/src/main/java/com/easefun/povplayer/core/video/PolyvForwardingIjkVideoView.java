package com.easefun.povplayer.core.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import androidx.appcompat.app.AlertDialog;

import com.easefun.povplayer.core.gifmaker.GifMaker;

import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import www.viewscenestv.com.ijkvideoview.IMediaController;
import www.viewscenestv.com.ijkvideoview.IRenderView;
import www.viewscenestv.com.ijkvideoview.PolyvGLSurfaceRenderView;
import www.viewscenestv.com.ijkvideoview.PolyvGLTextureRenderView;

/**
 * IPolyvIjkVideoView 转发类
 */
public class PolyvForwardingIjkVideoView extends FrameLayout implements IPolyvIjkVideoView {
    private IPolyvIjkVideoView iIjkVideoView = null;

    public PolyvForwardingIjkVideoView(Context context) {
        super(context);
    }

    public PolyvForwardingIjkVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PolyvForwardingIjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void initIPolyvIjkVideoView(IPolyvIjkVideoView iIjkVideoView) {
        this.iIjkVideoView = iIjkVideoView;
    }

    @Override
    public void start() {
        iIjkVideoView.start();
    }

    @Override
    public void pause() {
        iIjkVideoView.pause();
    }

    @Override
    public int getDuration() {
        return iIjkVideoView.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return iIjkVideoView.getCurrentPosition();
    }

    @Override
    public void resetVideoURI() {
        iIjkVideoView.resetVideoURI();
    }

    @Override
    public void removeRenderView() {
        iIjkVideoView.removeRenderView();
    }

    @Override
    public void seekTo(int pos) {
        iIjkVideoView.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return iIjkVideoView.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return iIjkVideoView.getBufferPercentage();
    }

    @Override
    public boolean canPause() {
        return iIjkVideoView.canPause();
    }

    @Override
    public boolean canSeekBackward() {
        return iIjkVideoView.canSeekBackward();
    }

    @Override
    public boolean canSeekForward() {
        return iIjkVideoView.canSeekForward();
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void resetLoadCost() {
        iIjkVideoView.resetLoadCost();
    }

    @Override
    public void setMirror(boolean paramBoolean) {
        iIjkVideoView.setMirror(paramBoolean);
    }

    @Override
    public void setRenderView(IRenderView renderView) {
        iIjkVideoView.setRenderView(renderView);
    }

    @Override
    public void setRender(int render) {
        iIjkVideoView.setRender(render);
    }

    @Override
    public void setVideoPath(String path) {
        iIjkVideoView.setVideoPath(path);
    }

    @Override
    public void stopPlayback() {
        iIjkVideoView.stopPlayback();
    }

    @Override
    public void setVideoURI(Uri uri) {
        iIjkVideoView.setVideoURI(uri);
    }

    @Override
    public void setVideoURI(Uri uri, Map<String, String> headers) {
        iIjkVideoView.setVideoURI(uri, headers);
    }

    @Override
    public boolean startClip(int cancelSecond) {
        return iIjkVideoView.startClip(cancelSecond);
    }

    @Override
    public void stopClip(GifMaker.OnGifListener listener) {
        iIjkVideoView.stopClip(listener);
    }

    @Override
    public void cancelClip() {
        iIjkVideoView.cancelClip();
    }

    @Override
    public void setLogTag(String tag) {
        iIjkVideoView.setLogTag(tag);
    }

    @Override
    public boolean setDolbyEndpointParam(boolean isHeadsetOn) {
        return iIjkVideoView.setDolbyEndpointParam(isHeadsetOn);
    }

    @Override
    public void resetVRRender() {
        iIjkVideoView.resetVRRender();
    }

    @Override
    public void setVRViewInitCompletionListener(PolyvGLSurfaceRenderView.OnInitCompletionListener l) {
        iIjkVideoView.setVRViewInitCompletionListener(l);
    }

    @Override
    public void setVRViewInitCompletionListener(PolyvGLTextureRenderView.OnInitCompletionListener l) {
        iIjkVideoView.setVRViewInitCompletionListener(l);
    }

    @Override
    public void setMediaController(IMediaController controller) {
        iIjkVideoView.setMediaController(controller);
    }

    @Override
    public void releaseWithoutStop() {
        iIjkVideoView.releaseWithoutStop();
    }

    @Override
    public void release(boolean cleartargetstate) {
        iIjkVideoView.release(cleartargetstate);
    }

    @Override
    public void suspend() {
        iIjkVideoView.suspend();
    }

    @Override
    public void resume() {
        iIjkVideoView.resume();
    }

    @Override
    public int toggleAspectRatio() {
        return iIjkVideoView.toggleAspectRatio();
    }

    @Override
    public int toggleRender() {
        return iIjkVideoView.toggleRender();
    }

    @Override
    public int togglePlayer() {
        return iIjkVideoView.togglePlayer();
    }

    @Override
    public IMediaPlayer createPlayer(int playerType) {
        return iIjkVideoView.createPlayer(playerType);
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        return iIjkVideoView.getTrackInfo();
    }

    @Override
    public void selectTrack(int stream) {
        iIjkVideoView.selectTrack(stream);
    }

    @Override
    public void deselectTrack(int stream) {
        iIjkVideoView.deselectTrack(stream);
    }

    @Override
    public int getSelectedTrack(int trackType) {
        return iIjkVideoView.getSelectedTrack(trackType);
    }

    @Override
    public void setHudView(TableLayout tableLayout) {
        iIjkVideoView.setHudView(tableLayout);
    }

    @Override
    public AlertDialog showMediaInfo() {
        return iIjkVideoView.showMediaInfo();
    }

    @Override
    public Bitmap screenshot() {
        return iIjkVideoView.screenshot();
    }

    @Override
    public void enterBackground() {
        iIjkVideoView.enterBackground();
    }

    @Override
    public void stopBackgroundPlay() {
        iIjkVideoView.stopBackgroundPlay();
    }

    @Override
    public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener l) {
        iIjkVideoView.setOnSeekCompleteListener(l);
    }

    @Override
    public void setOnVideoSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener l) {
        iIjkVideoView.setOnVideoSizeChangedListener(l);
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener l) {
        iIjkVideoView.setOnPreparedListener(l);
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener l) {
        iIjkVideoView.setOnCompletionListener(l);
    }

    @Override
    public void setOnErrorListener(OnErrorListener l) {
        iIjkVideoView.setOnErrorListener(l);
    }

    @Override
    public void setOnInfoListener(OnInfoListener l) {
        iIjkVideoView.setOnInfoListener(l);
    }

    @Override
    public boolean isInPlaybackStateForwarding() {
        return iIjkVideoView.isInPlaybackStateForwarding();
    }

    @Override
    public IRenderView getRenderView() {
        return iIjkVideoView.getRenderView();
    }

    @Override
    public int getCurrentAspectRatio() {
        return iIjkVideoView.getCurrentAspectRatio();
    }

    @Override
    public void setCurrentAspectRatio(int aspectRatio) {
        iIjkVideoView.setCurrentAspectRatio(aspectRatio);
    }

    @Override
    public int getVideoWidth() {
        return iIjkVideoView.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return iIjkVideoView.getVideoHeight();
    }

    @Override
    public IMediaPlayer getMediaPlayer() {
        return iIjkVideoView.getMediaPlayer();
    }

    @Override
    public SurfaceHolder getSurfaceHolder() {
        return iIjkVideoView.getSurfaceHolder();
    }

    @Override
    public void setSpeed(float speed) {
        iIjkVideoView.setSpeed(speed);
    }

    @Override
    public float getSpeed() {
        return iIjkVideoView.getSpeed();
    }

    @Override
    public void onErrorState() {
        iIjkVideoView.onErrorState();
    }

    @Override
    public int getStateIdleCode() {
        return iIjkVideoView.getStateIdleCode();
    }

    @Override
    public int getStateErrorCode() {
        return iIjkVideoView.getStateErrorCode();
    }

    @Override
    public int getStatePreparingCode() {
        return iIjkVideoView.getStatePreparingCode();
    }

    @Override
    public int getStatePreparedCode() {
        return iIjkVideoView.getStatePreparedCode();
    }

    @Override
    public int getStatePauseCode() {
        return iIjkVideoView.getStatePauseCode();
    }

    @Override
    public int getStatePlayingCode() {
        return iIjkVideoView.getStatePlayingCode();
    }

    @Override
    public int getStatePlaybackCompletedCode() {
        return iIjkVideoView.getStatePlaybackCompletedCode();
    }

    @Override
    public int getCurrentState() {
        return iIjkVideoView.getCurrentState();
    }

    @Override
    public int getTargetState() {
        return iIjkVideoView.getTargetState();
    }

    @Override
    public void setOptionParameters(Object[][] mOptionParameters) {
        iIjkVideoView.setOptionParameters(mOptionParameters);
    }

    @Override
    public void setTargetState(int state) {
        iIjkVideoView.setTargetState(state);
    }

    @Override
    public void clearOptionParameters() {
        iIjkVideoView.clearOptionParameters();
    }

    @Override
    public void setIjkLogLevel(int ijkLogLevel) {
        iIjkVideoView.setIjkLogLevel(ijkLogLevel);
    }
}
