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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.Keep;

import com.easefun.povplayer.core.R;

@Keep
public class Settings {
    private Context mAppContext;
    private SharedPreferences mSharedPreferences;

    public static final int FIT_PARENT = IRenderView.AR_ASPECT_FIT_PARENT;
    public static final int FILL_PARENT = IRenderView.AR_ASPECT_FILL_PARENT;
    public static final int WRAP_CONTENT = IRenderView.AR_ASPECT_WRAP_CONTENT;
    public static final int MATCH_PARENT = IRenderView.AR_MATCH_PARENT;
    public static final int FIT_PARENT_16_9 = IRenderView.AR_16_9_FIT_PARENT;
    public static final int FIT_PARENT_4_3 = IRenderView.AR_4_3_FIT_PARENT;

    public static final int PV_PLAYER__Auto = 0;
    public static final int PV_PLAYER__AndroidMediaPlayer = 1;
    public static final int PV_PLAYER__IjkMediaPlayer = 2;
    public static final int PV_PLAYER__IjkExoMediaPlayer = 3;

    public Settings(Context context) {
        mAppContext = context.getApplicationContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    }

    public boolean getEnableBackgroundPlay() {
        String key = mAppContext.getString(R.string.pref_key_enable_background_play);
        return mSharedPreferences.getBoolean(key, false);
    }

    public int getPlayer() {
        String key = mAppContext.getString(R.string.pref_key_player);
        String value = mSharedPreferences.getString(key, "");
        try {
            return Integer.valueOf(value).intValue();
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setParams(int aspectRatio,
                          String pixelFormat,
                          boolean isUsingMediaCodec,
                          boolean isUsingMediaCodecAutoRotate,
                          boolean isUsingOpenSLES,
                          boolean enableTextureView) {
        mSharedPreferences.edit()
                .putInt(mAppContext.getString(R.string.pref_key_aspect_ratio),
                        aspectRatio)
                .putString(mAppContext.getString(R.string.pref_key_pixel_format),
                        pixelFormat)
                .putBoolean(mAppContext.getString(R.string.pref_key_using_media_codec),
                        isUsingMediaCodec)
                .putBoolean(mAppContext.getString(R.string.pref_key_using_media_codec_auto_rotate),
                        isUsingMediaCodecAutoRotate)
                .putBoolean(mAppContext.getString(R.string.pref_key_using_opensl_es),
                        isUsingOpenSLES)
                .putBoolean(mAppContext.getString(R.string.pref_key_enable_texture_view),
                        enableTextureView)
                .commit();
    }

    public void setAspecRatio(int aspecRatio) {
        String key = mAppContext.getString(R.string.pref_key_aspect_ratio);
        mSharedPreferences.edit().putInt(key, aspecRatio).commit();
    }

    public int getAspectRatio() {
        String key = mAppContext.getString(R.string.pref_key_aspect_ratio);
        return mSharedPreferences.getInt(key, FIT_PARENT);
    }

    public void setPlayer(int playerType) {
        String key = mAppContext.getString(R.string.pref_key_player);
        mSharedPreferences.edit().putString(key, String.valueOf(playerType)).apply();
    }

    public void usingMediaCodec(boolean usingMediaCodec) {
        String key = mAppContext.getString(R.string.pref_key_using_media_codec);
        mSharedPreferences.edit().putBoolean(key, usingMediaCodec).apply();
    }

    public boolean getUsingMediaCodec() {
        String key = mAppContext.getString(R.string.pref_key_using_media_codec);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getUsingMediaCodecAutoRotate() {
        String key = mAppContext.getString(R.string.pref_key_using_media_codec_auto_rotate);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getMediaCodecHandleResolutionChange() {
        String key = mAppContext.getString(R.string.pref_key_media_codec_handle_resolution_change);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getUsingOpenSLES() {
        String key = mAppContext.getString(R.string.pref_key_using_opensl_es);
        return mSharedPreferences.getBoolean(key, false);
    }

    public String getPixelFormat() {
        String key = mAppContext.getString(R.string.pref_key_pixel_format);
        return mSharedPreferences.getString(key, "");
    }

    public boolean getEnableNoView() {
        String key = mAppContext.getString(R.string.pref_key_enable_no_view);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getEnableSurfaceView() {
        String key = mAppContext.getString(R.string.pref_key_enable_surface_view);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getEnableTextureView() {
        String key = mAppContext.getString(R.string.pref_key_enable_texture_view);
        return mSharedPreferences.getBoolean(key, false);
    }

    public void enableTextureView(boolean enableTextureView) {
        String key = mAppContext.getString(R.string.pref_key_enable_texture_view);
        mSharedPreferences.edit().putBoolean(key, enableTextureView).commit();
    }

    public boolean getEnableDetachedSurfaceTextureView() {
        String key = mAppContext.getString(R.string.pref_key_enable_detached_surface_texture);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getUsingMediaDataSource() {
        String key = mAppContext.getString(R.string.pref_key_using_mediadatasource);
        return mSharedPreferences.getBoolean(key, false);
    }

    public String getLastDirectory() {
        String key = mAppContext.getString(R.string.pref_key_last_directory);
        return mSharedPreferences.getString(key, "/");
    }

    public void setLastDirectory(String path) {
        String key = mAppContext.getString(R.string.pref_key_last_directory);
        mSharedPreferences.edit().putString(key, path).apply();
    }

    private static final String POLYV_PREF_RENDER_VIEW_TYPE = "polyv.pref.render_view_type";

    /**
     * 获取渲染控件类型
     * @return 渲染控件类型
     */
    public int getRenderViewType() {
        return mSharedPreferences.getInt(POLYV_PREF_RENDER_VIEW_TYPE, IjkVideoView.RENDER_TEXTURE_VIEW);
    }

    /**
     * 设置渲染控件类型，{@link IjkVideoView#RENDER_TEXTURE_VIEW} or {@link IjkVideoView#RENDER_SURFACE_VIEW}。
     * @param renderViewType 渲染控件类型
     */
    public void setRenderViewType(int renderViewType) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(POLYV_PREF_RENDER_VIEW_TYPE, renderViewType);
        editor.apply();
    }
}
