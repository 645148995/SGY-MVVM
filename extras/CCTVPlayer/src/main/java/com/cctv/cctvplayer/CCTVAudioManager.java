package com.cctv.cctvplayer;

import android.content.Context;

public class CCTVAudioManager {

    public static CCTVAudioView mCctvAudioView = null;


    public static CCTVAudioView getInstance(Context context) {
        if (null == mCctvAudioView) {
            mCctvAudioView = new CCTVAudioView(context);
        }
        return mCctvAudioView;
    }
}
