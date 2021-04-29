package com.ctvit.shitingxizang.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ctvit.shitingxizang.BR;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.subjects.BehaviorSubject;
import me.goldze.mvvmhabit.R;
import me.yokeyword.fragmentation.SupportFragment;


/**
 * 盛装Fragment的一个容器(代理)Activity
 * 继承自BaseActivity的ContainerActivity，暂时用不到
 */
public class BaseContainerActivity extends BaseActivity {
    private static final String FRAGMENT_TAG = "content_fragment_tag";
    public static final String FRAGMENT = "fragment";
    public static final String BUNDLE = "bundle";
    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();


    public static void start(Context context, String canonicalName, Bundle bundle) {
        Intent intent = new Intent(context, BaseContainerActivity.class);
        intent.putExtra(BaseContainerActivity.FRAGMENT, canonicalName);
        intent.putExtra(BaseContainerActivity.BUNDLE, bundle);
        context.startActivity(intent);
    }


    @Override
    public void initData() {
        initFragment();
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
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_container;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


}
