package com.easefun.povplayer.core.util;

import android.content.Context;
import android.media.AudioManager;

import com.easefun.povplayer.core.video.PolyvVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

import static android.content.Context.AUDIO_SERVICE;

public class PolyvAudioFocusManager implements AudioManager.OnAudioFocusChangeListener {
    private AudioManager audioManager;
    private PolyvVideoView videoView;
    private boolean isPlayingOnPause;
    private boolean isPausedByFocusLossTransient;

    public PolyvAudioFocusManager(Context context) {
        audioManager = (AudioManager) context.getApplicationContext().getSystemService(AUDIO_SERVICE);
    }

    public void addPlayer(PolyvVideoView videoView) {
        this.videoView = videoView;
    }

    public boolean requestAudioFocus() {
        return audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (videoView == null)
            return;
        switch (focusChange) {
            // 重新获得焦点
            case AudioManager.AUDIOFOCUS_GAIN:
                if (isPausedByFocusLossTransient && isPlayingOnPause) {
                    // 通话结束，恢复播放
                    videoView.start();
                }
                // 恢复音量
                IMediaPlayer iMediaPlayer = videoView.getMediaPlayer();
                if (iMediaPlayer != null) {
                    iMediaPlayer.setVolume(1, 1);
                }
                isPausedByFocusLossTransient = false;
                isPlayingOnPause = false;
                break;
            // 永久丢失焦点，如被其他播放器抢占
            case AudioManager.AUDIOFOCUS_LOSS:
                // add
                isPlayingOnPause = videoView.isPlaying() || videoView.getSubVideoView().isShow();
                videoView.pause(false);
                isPausedByFocusLossTransient = true;
                break;
            // 短暂丢失焦点，如来电
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                isPlayingOnPause = videoView.isPlaying() || videoView.getSubVideoView().isShow();
                videoView.pause(false);
                isPausedByFocusLossTransient = true;
                break;
            // 瞬间丢失焦点，如通知
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // 音量减小为一半
                IMediaPlayer mediaPlayer = videoView.getMediaPlayer();
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0.5f, 0.5f);
                }
                break;
        }
    }
}
