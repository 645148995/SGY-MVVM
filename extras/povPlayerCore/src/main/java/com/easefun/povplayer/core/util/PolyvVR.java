package com.easefun.povplayer.core.util;

import android.content.Context;

import com.asha.vrlib.MD360Director;
import com.asha.vrlib.MD360DirectorFactory;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.BarrelDistortionConfig;
import com.asha.vrlib.model.MDHitEvent;
import com.asha.vrlib.model.MDPinchConfig;
import com.easefun.povplayer.core.video.PolyvCustomProjectionFactory;

import www.viewscenestv.com.ijkvideoview.PolyvGLSurfaceRenderView;
import www.viewscenestv.com.ijkvideoview.PolyvGLTextureRenderView;

/**
 * VR库初始化
 * @author Lionel 2018-11-30
 */
public class PolyvVR {

    public static MDVRLibrary vrParamInit(Context context, int displayMode, PolyvGLSurfaceRenderView view,
                                          MDVRLibrary.IOnSurfaceReadyCallback surfaceReadyCallback,
                                          MDVRLibrary.INotSupportCallback notSupportCallback,
                                          MDVRLibrary.IGestureListener gestureListener) {
        MDVRLibrary mdvrLibrary = paramInit(context, displayMode, surfaceReadyCallback, notSupportCallback, gestureListener).build(view);
        view.setMdvrLibrary(mdvrLibrary);
        otherSetting(mdvrLibrary);
        return mdvrLibrary;
    }

    public static MDVRLibrary vrParamInit(Context context, int displayMode, PolyvGLTextureRenderView view,
                                                  MDVRLibrary.IOnSurfaceReadyCallback surfaceReadyCallback,
                                                  MDVRLibrary.INotSupportCallback notSupportCallback,
                                                  MDVRLibrary.IGestureListener gestureListener) {
        MDVRLibrary mdvrLibrary = paramInit(context, displayMode, surfaceReadyCallback, notSupportCallback, gestureListener).build(view);
        view.setMdvrLibrary(mdvrLibrary);
        otherSetting(mdvrLibrary);
        return mdvrLibrary;
    }

    private static MDVRLibrary.Builder paramInit(Context context, int displayMode,
                                                  MDVRLibrary.IOnSurfaceReadyCallback surfaceReadyCallback,
                                                  MDVRLibrary.INotSupportCallback notSupportCallback,
                                                  MDVRLibrary.IGestureListener gestureListener) {
        return MDVRLibrary.with(context)
                .displayMode(displayMode)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_CARDBORAD_MOTION_WITH_TOUCH)
                .asVideo(surfaceReadyCallback)
                .ifNotSupport(notSupportCallback)
                .pinchConfig(new MDPinchConfig().setMin(1.0f).setMax(8.0f).setDefaultValue(0.1f))
                .pinchEnabled(true)
                .directorFactory(new MD360DirectorFactory() {
                    @Override
                    public MD360Director createDirector(int index) {
                        return MD360Director.builder().setPitch(270).build();
                    }
                })
                .projectionFactory(new PolyvCustomProjectionFactory())
                .barrelDistortionConfig(new BarrelDistortionConfig().setDefaultEnabled(false).setScale(0.95f))
                .listenGesture(gestureListener);
    }

    private static void otherSetting(final MDVRLibrary mdvrLibrary) {
//        mdvrLibrary.setAntiDistortionEnabled(true);
        mdvrLibrary.setTouchPickListener(new MDVRLibrary.ITouchPickListener2() {
            @Override
            public void onHotspotHit(MDHitEvent hitEvent) {

            }
        });

        mdvrLibrary.setEyePickChangedListener(new MDVRLibrary.IEyePickListener2() {
            @Override
            public void onHotspotHit(MDHitEvent hitEvent) {
                long hitTimestamp = hitEvent.getTimestamp();
                if (System.currentTimeMillis() - hitTimestamp > 5000) {
                    mdvrLibrary.resetEyePick();
                }
            }
        });
    }
}
