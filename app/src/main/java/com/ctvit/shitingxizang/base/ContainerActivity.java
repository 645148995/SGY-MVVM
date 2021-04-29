package com.ctvit.shitingxizang.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.gyf.immersionbar.ImmersionBar;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import androidx.annotation.CallSuper;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import me.goldze.mvvmhabit.R;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;


/**
 * 盛装Fragment的一个容器(代理)Activity
 * 普通界面只需要编写Fragment,使用此Activity盛装,这样就不需要每个界面都在AndroidManifest中注册一遍
 */
public class ContainerActivity extends SupportActivity implements LifecycleProvider<ActivityEvent> {
    private static final String FRAGMENT_TAG = "content_fragment_tag";
    public static final String FRAGMENT = "fragment";
    public static final String BUNDLE = "bundle";
    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();


    public static void start(Context context, String canonicalName, Bundle bundle) {
        Intent intent = new Intent(context, ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        intent.putExtra(ContainerActivity.BUNDLE, bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initFragment();
        initImmersionBar();
    }

    protected int getLayoutId() {
        return R.layout.activity_container;
    }


    private void initFragment() {

        loadRootFragment(R.id.content, initFromIntent(getIntent()));
    }

    protected SupportFragment initFromIntent(Intent data) {
        if (data == null) {
            throw new RuntimeException(
                    "you must provide a page info to display");
        }
        try {
            String fragmentName = data.getStringExtra(FRAGMENT);
            if (fragmentName == null || "".equals(fragmentName)) {
                throw new IllegalArgumentException("can not find page fragmentName");
            }
            Class<?> fragmentClass = Class.forName(fragmentName);
            SupportFragment fragment = (BaseFragment) fragmentClass.newInstance();
            Bundle args = data.getBundleExtra(BUNDLE);
            if (args != null) {
                fragment.setArguments(args);
            }
            return fragment;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("fragment initialization failed!");
    }


    @Override
    @NonNull
    @CheckResult
    public final Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    @CallSuper
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }


    public void initImmersionBar() {


        ImmersionBar.with(this)
                .statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
                .navigationBarColorInt(Color.WHITE)
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .statusBarColor(R.color.white)
                .fitsSystemWindows(true) //使用该属性必须指定状态栏的颜色，不然状态栏透明，很难看
                .init();

    }

}
