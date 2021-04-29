package com.ctvit.shitingxizang.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ctvit.shitingxizang.R;
import com.ctvit.shitingxizang.net.URL;
import com.ctvit.shitingxizang.cardgroups.Constant;
import com.ctvit.shitingxizang.utils.AppUtils;

import com.gyf.immersionbar.ImmersionBar;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ctvit.shitingxizang.base.BaseActivity;

import me.goldze.mvvmhabit.utils.SPUtils;

/**
 * Created by A on 2018/2/27.
 */

public class FlashActivity extends BaseActivity {

    private ImageView ivFlash;
    private TextView tvCountdown;
    private WebView webView;
    private String TAG = "FlashActivity";
    private final int DEFAULT_TIME = 1; //闪屏默认倒计时
    private Map<String, String> adMap = new HashMap<>();
    private Handler mHandler = new Handler();
    private int countdown;
    private String txtSkip = "跳过";
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            tvCountdown.setText(String.format(Locale.getDefault(), "%d" + txtSkip, countdown));
            if (countdown > 0) {
                mHandler.postDelayed(r, 100);
            } else {
                startAppMain(null);
            }
            countdown--;
        }
    };
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };
    private List<String> missingPermission = new ArrayList<>();
    private final int REQUEST_PERMISSION_CODE = 0x123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fullScreenMode();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .navigationBarColorInt(Color.WHITE)
                .navigationBarDarkIcon(true)
                .transparentNavigationBar()
                .init();
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_flash;
    }

    public void initData() {
        ivFlash = findViewById(R.id.iv_flash);
        tvCountdown = findViewById(R.id.tv_countdown);
        webView = findViewById(R.id.wv_flash);
        if (SPUtils.getBoolean( Constant.APP_FIRST_INSTALL, true)) {
            SPUtils.putBoolean(Constant.APP_FIRST_INSTALL, false);
            Map<String, Object> uMap = new HashMap<>();
            uMap.put("statisticsname", "版本安装");
            uMap.put("versioncode", AppUtils.getAppVersionName());
            uMap.put("channel", AppUtils.getChannel(this, "UMENG_CHANNEL"));
            //友盟统计
//            MobclickAgent.onEventObject(this, "operationalstatistics", uMap);
        }
        preCheck();

//        if (SharePrefUtil.getBoolean(this, C.APP_AGREE_PROTOCOL, false)) {   //是否同意客户端协议
//            preCheck();
//        } else {
////            initWebView();
//        }
    }

    private void agreeAppProtocol() {
        SPUtils.putBoolean(Constant.APP_AGREE_PROTOCOL, true);
        preCheck();
    }

    private void initWebView() {
        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        AppUtils.webViewHttp2Https(settings, TAG);
        webView.addJavascriptInterface(new AndroidToJs(), "android");
        webView.loadUrl(URL.H5.PROTOCOL_CLIENT);
        webView.setVisibility(View.VISIBLE);
    }

    private void preCheck() {
        webView.setVisibility(View.GONE);
        if (hasPermissionsGranted()) {
            reqSplash();
        } else {
            checkAndRequestPermissions();
        }
    }

    private void reqSplash() {
//        CtvitHttp.post(URL.CMS.SPLASH).execute(new CallBack<SplashBean>() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(ApiException e) {
//                setCountdown(DEFAULT_TIME, null);
//            }
//
//            @Override
//            public void onSuccess(SplashBean splashBean) {
//                if (splashBean == null || splashBean.getSplashes() == null || splashBean.getSplashes().size() == 0 || splashBean.getSucceed() == 0) {
//                    setCountdown(DEFAULT_TIME, null);
//                    return;
//                }
//                List<SplashBean.SplashesBean> list = new ArrayList<>();
//                long cTime = System.currentTimeMillis();
//                for (int i = 0; i < splashBean.getSplashes().size(); i++) {
//                    SplashBean.SplashesBean s = splashBean.getSplashes().get(i);
//                    if (cTime > DateUtil.getTimeStampLong(s.getStart_at()) && cTime < DateUtil.getTimeStampLong(s.getEnd_at()))
//                        list.add(s);
//                }
//                if (list.isEmpty()) {
//                    setCountdown(DEFAULT_TIME, null);
//                    return;
//                }
//                int adIndex = new Random().nextInt(list.size());
//                SplashBean.SplashesBean s = list.get(adIndex);
//                adMap.put("adtitle", "");
//                adMap.put("adid", s.getAdvertiseid());
//                adMap.put("adurl", s.getUrl());
//                if (s.getImage().contains(".gif")) {
//                    Glide.with(FlashActivity.this).asGif().load(s.getImage()).into(ivFlash);
//                } else {
//                    Glide.with(FlashActivity.this).load(s.getImage()).into(ivFlash);
//                }
//                if (s.getSplashIfAd() == 1) {
//                    txtSkip = txtSkip + "广告";
//                }
//                tvCountdown.setVisibility(View.VISIBLE);
//                setCountdown(s.getPlay_time(), s.getUrl());
//            }
//        });
        setCountdown(DEFAULT_TIME, null);
    }



    /**
     * @param countdown 倒计时
     * @param adUrl     adUrl 为 null 时，表示接口请求失败。
     */
    private void setCountdown(int countdown, String adUrl) {
        this.countdown = countdown;
        mHandler.post(r);      //无论失败与成功都要倒计时，并跳转主页
        tvCountdown.setOnClickListener(v -> {   //点击跳过倒计时
            mHandler.removeCallbacks(r);
            startAppMain(null);
            adMap.put("type", "跳过");
//            MobclickAgent.onEvent(FlashActivity.this, "startad", adMap);
        });
        if (!TextUtils.isEmpty(adUrl)) {    // 广告地址有效时，添加点击事件
            ivFlash.setOnClickListener(v -> {       //点击进入广告
                mHandler.removeCallbacks(r);
                startAppMain(adUrl);
                adMap.put("type", "查看");
//                MobclickAgent.onEvent(FlashActivity.this, "startad", adMap);
            });
        }
    }

    private void startAppMain(String adUrl) {
        Intent i = new Intent(FlashActivity.this, MainActivity.class);
        if (!TextUtils.isEmpty(adUrl))
            i.putExtra("ad", adUrl);
        startActivity(i);
        finish();
    }

    private boolean hasPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSION_LIST) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void checkAndRequestPermissions() {
        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }
        // Request for missing permissions
        if (!missingPermission.isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(new String[0]),
                    REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        fullScreenMode();
        reqSplash();
//        if (missingPermission.isEmpty()) {
//        } else {
//            Toast.makeText(this, "Missing permissions!", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }


    @Override
    public int initVariableId() {
        return 0;
    }


    private class AndroidToJs {
        //0是不同意，1是同意
        @JavascriptInterface
        public void isAgree(String code) {
            if ("0".equals(code)) {
                finish();
            } else {
                runOnUiThread(FlashActivity.this::agreeAppProtocol);
            }
        }

        //1是隐私，2是用户协议
        @JavascriptInterface
        public void isJump(String code) {
            if ("1".equals(code)) {
//                startActivity(PrivacyActivity.class);
            } else if ("2".equals(code)) {
//                startActivity(AgreementActivity.class);
            }
        }
    }

    private void fullScreenMode() {
        View decorView = getWindow().getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        int flags = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        systemUiVisibility |= flags;
        getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

}
