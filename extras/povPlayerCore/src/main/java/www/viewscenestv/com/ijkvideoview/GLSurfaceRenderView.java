package www.viewscenestv.com.ijkvideoview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import com.asha.vrlib.MDVRLibrary;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author Lionel 2018-11-27
 */
public class GLSurfaceRenderView extends GLSurfaceView implements IRenderView {
    private MDVRLibrary mdvrLibrary;

    public GLSurfaceRenderView(Context context) {
        super(context);
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
    public void setDisplayArea(Rect source) {

    }

    @Override
    public Rect sourceAreaToScreen(Rect source) {
        return null;
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (mdvrLibrary != null) {
            mdvrLibrary.onTextureResize(videoWidth, videoHeight);
        }
    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {

    }

    @Override
    public void setVideoRotation(int degree) {

    }

    @Override
    public void setAspectRatio(int aspectRatio) {

    }

    @Override
    public void addRenderCallback(@NonNull IRenderCallback callback) {

    }

    @Override
    public void removeRenderCallback(@NonNull IRenderCallback callback) {
        mdvrLibrary = null;
    }

    public void setMdvrLibrary(MDVRLibrary mdvrLibrary) {
        this.mdvrLibrary = mdvrLibrary;
    }

    public ISurfaceHolder getSurfaceHolder() {
        return new InternalSurfaceHolder();
    }

    private static final class InternalSurfaceHolder implements ISurfaceHolder {

        @Override
        public void bindToMediaPlayer(IMediaPlayer mp) {

        }

        @NonNull
        @Override
        public IRenderView getRenderView() {
            return null;
        }

        @Nullable
        @Override
        public SurfaceHolder getSurfaceHolder() {
            return null;
        }

        @Nullable
        @Override
        public Surface openSurface() {
            return null;
        }

        @Nullable
        @Override
        public SurfaceTexture getSurfaceTexture() {
            return null;
        }

        @Override
        public Surface getSurface() {
            return null;
        }

        @Override
        public void release() {

        }
    }

    private static final class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    public interface OnInitCompletionListener {
        void onCompletionListener(GLSurfaceRenderView view);
    }
}
