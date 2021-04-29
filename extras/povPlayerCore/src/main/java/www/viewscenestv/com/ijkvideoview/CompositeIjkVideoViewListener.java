package www.viewscenestv.com.ijkvideoview;

/**
 * Created by Viewscene on 19/04/2017.
 */

class CompositeIjkVideoViewListener extends CompositeCallback<IjkVideoView.IjkVideoViewListener>
        implements IjkVideoView.IjkVideoViewListener {

    @Override
    public void onViewChangeEnd(Integer result){
        for (IjkVideoView.IjkVideoViewListener listener : mCallbacks){
            listener.onViewChangeEnd(result);
        }
    }

    @Override
    public void onBitRateChanged(String bitrate){
        for (IjkVideoView.IjkVideoViewListener listener : mCallbacks){
            listener.onBitRateChanged(bitrate);
        }
    }

    @Override
    public void onVolumeChanged(int currentVolume){
        for (IjkVideoView.IjkVideoViewListener listener : mCallbacks){
            listener.onVolumeChanged(currentVolume);
        }
    }

    @Override
    public void onIJKNeedRetry(int retryReason){
        for (IjkVideoView.IjkVideoViewListener listener : mCallbacks){
            listener.onIJKNeedRetry(retryReason);
        }
    }



    @Override
    public void onIAEvent(String eventContent) {
        for (IjkVideoView.IjkVideoViewListener listener : mCallbacks) {
            listener.onIAEvent(eventContent);
        }
    }

    @Override
    public void onRenderingStart() {
        for (IjkVideoView.IjkVideoViewListener listener : mCallbacks) {
            listener.onRenderingStart();
        }
    }

    @Override
    public void onAudioRenderingStart() {
        for (IjkVideoView.IjkVideoViewListener listener : mCallbacks) {
            listener.onAudioRenderingStart();
        }
    }

    @Override
    public void onIjkplayerCompleted() {
        for (IjkVideoView.IjkVideoViewListener listener : mCallbacks) {
            listener.onIjkplayerCompleted();
        }
    }

    @Override
    public void onBufferingUpdate(final int percent) {
        for (IjkVideoView.IjkVideoViewListener listener : mCallbacks) {
            listener.onBufferingUpdate(percent);
        }
    }

    @Override
    public void onBufferingStart() {
        for (IjkVideoView.IjkVideoViewListener listener : mCallbacks) {
            listener.onBufferingStart();
        }
    }

    @Override
    public void onBufferingEnd() {
        for (IjkVideoView.IjkVideoViewListener listener : mCallbacks) {
            listener.onBufferingEnd();
        }
    }

    @Override
    public void onClosed(){
        for (IjkVideoView.IjkVideoViewListener listener : mCallbacks) {
            listener.onClosed();
        }
    }
}
