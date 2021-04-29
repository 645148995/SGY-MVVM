package com.easefun.povplayer.core.video;

import androidx.annotation.IntDef;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import www.viewscenestv.com.ijkvideoview.IjkVideoView;

/**
 * 渲染组件类型
 * @author Lionel 2018-11-28
 */
public class PolyvRenderType {

    public static final int RENDER_NONE = IjkVideoView.RENDER_NONE;
    public static final int RENDER_SURFACE_VIEW = IjkVideoView.RENDER_SURFACE_VIEW;
    public static final int RENDER_TEXTURE_VIEW = IjkVideoView.RENDER_TEXTURE_VIEW;
    public static final int RENDER_GLSURFACE_VIEW = 3;
    public static final int RENDER_GLTEXTURE_VIEW = 4;

    @IntDef({
            RENDER_NONE,
            RENDER_SURFACE_VIEW,
            RENDER_TEXTURE_VIEW,
            RENDER_GLSURFACE_VIEW,
            RENDER_GLTEXTURE_VIEW
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface RenderType {

    }
}
