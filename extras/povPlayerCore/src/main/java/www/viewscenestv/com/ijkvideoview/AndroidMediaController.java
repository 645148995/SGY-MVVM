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

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import android.util.AttributeSet;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;

import java.util.ArrayList;

public class AndroidMediaController extends MediaController implements IMediaController {
    private ActionBar mActionBar;

    public AndroidMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AndroidMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
        initView(context);
    }

    public AndroidMediaController(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
    }

    public void setSupportActionBar(@Nullable ActionBar actionBar) {
        mActionBar = actionBar;
        if (isShowing()) {
            actionBar.show();
        } else {
            actionBar.hide();
        }
    }

    @Override
    public void show() {
        super.show();
        if (mActionBar != null)
            mActionBar.show();
    }

    @Override
    public void hide() {
        super.hide();
        if (mActionBar != null)
            mActionBar.hide();
        for (View view : mShowOnceArray)
            view.setVisibility(View.GONE);
        mShowOnceArray.clear();
    }

    // JS_MODIFY
    private MediaPlayerControl _player;
    private int _progress;

    @Override
    public void setMediaPlayer(MediaPlayerControl playerControl) {
        super.setMediaPlayer(playerControl);
        _player = playerControl;
    }

    public void setOwnSeekBar() {
        final int id = getResources().getIdentifier("mediacontroller_progress", "id", "android");
        final SeekBar seekbar = (SeekBar) this.findViewById(id);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;

                _progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                show(3600000);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //int progress = seekBar.getProgress();
                int progress = _progress;
                int max_progress = seekBar.getMax();
                int duration = _player.getDuration();
                int position = Math.round((float) duration * (float) progress / (float) max_progress);
                if (position < 500) position = 500;

                show(3000);

                _player.seekTo(position);
            }
        });
    }
    // JS_MODIFY END

    //----------
    // Extends
    //----------
    private ArrayList<View> mShowOnceArray = new ArrayList<View>();

    public void showOnce(@NonNull View view) {
        mShowOnceArray.add(view);
        view.setVisibility(View.VISIBLE);
        show();
    }
}
