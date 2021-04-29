package com.ctvit.shitingxizang.app;


import com.billy.android.loading.Gloading;
import com.ctvit.shitingxizang.R;
import com.ctvit.shitingxizang.ui.main.MainActivity;
import com.ctvit.shitingxizang.widget.GlobalAdapter;


import me.goldze.mvvmhabit.BuildConfig;

import com.ctvit.shitingxizang.base.BaseApplication;

import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;
import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;

/**
 * Created by goldze on 2017/7/16.
 */

public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
        //初始化全局异常崩溃
        initCrash();
        //内存泄漏检测
//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }

        initFragmentation();

        //Gloading初始化
        Gloading.initDefault(new GlobalAdapter());
    }

    private void initFragmentation() {
        Fragmentation.builder()
                // 设置 栈视图 模式为 （默认）悬浮球模式   SHAKE: 摇一摇唤出  NONE：隐藏， 仅在Debug环境生效
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(true) // 实际场景建议.debug(BuildConfig.DEBUG)
                /**
                 * 可以获取到{@link me.yokeyword.fragmentation.exception.AfterSaveStateTransactionWarning}
                 * 在遇到After onSaveInstanceState时，不会抛出异常，会回调到下面的ExceptionHandler
                 */
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        // 以Bugtags为例子: 把捕获到的 Exception 传到 Bugtags 后台。
                        // Bugtags.sendException(e);
                    }
                })
                .install();
    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                .restartActivity(MainActivity.class) //重新启动后的activity
//                .errorActivity(MainActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }
}
