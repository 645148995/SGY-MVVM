package com.easefun.povplayer.core.video;



import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import www.viewscenestv.com.ijkvideoview.IRenderView;

/**
 * 画面填充模式
 */
public class PolyvPlayerScreenRatio {
    /**
     * 比例缩放
     */
    public static final int AR_ASPECT_FIT_PARENT = IRenderView.AR_ASPECT_FIT_PARENT;
    /**
     * 充满父窗(与视频比例一致，可能会裁剪)
     */
    public static final int AR_ASPECT_FILL_PARENT = IRenderView.AR_ASPECT_FILL_PARENT;
    /**
     * 充满父窗(可能会与视频比例不一致)
     */
    public static final int AR_MATCH_PARENT = IRenderView.AR_MATCH_PARENT;
    /**
     * 匹配内容(使用视频的分辨率)
     */
    public static final int AR_ASPECT_WRAP_CONTENT = IRenderView.AR_ASPECT_WRAP_CONTENT;
    /**
     * 16:9比例缩放
     */
    public static final int AR_16_9_FIT_PARENT = IRenderView.AR_16_9_FIT_PARENT;
    /**
     * 4:3比例缩放
     */
    public static final int AR_4_3_FIT_PARENT = IRenderView.AR_4_3_FIT_PARENT;

    @IntDef({
            AR_ASPECT_FIT_PARENT,
            AR_ASPECT_FILL_PARENT,
            AR_MATCH_PARENT,
            AR_ASPECT_WRAP_CONTENT,
            AR_16_9_FIT_PARENT,
            AR_4_3_FIT_PARENT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface RenderScreenRatio {
    }
}
