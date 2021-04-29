package com.ctvit.shitingxizang.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;
import com.billy.android.loading.Gloading;
import com.gyf.immersionbar.ImmersionBar;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import me.goldze.mvvmhabit.R;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.base.BaseViewModel.ParameterField;
import me.goldze.mvvmhabit.base.IBaseView;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.utils.MaterialDialogUtils;
import me.yokeyword.fragmentation.SupportActivity;


/**
 * Created by goldze on 2017/6/15.
 * 一个拥有DataBinding框架的基Activity
 * 这里根据项目业务可以换成你自己熟悉的BaseActivity, 但是需要继承RxAppCompatActivity,方便LifecycleProvider管理生命周期
 *
 * **已继承fragmentation的SupportActivity，同时支持mvvm和fragmentation
 */

public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends SupportActivity implements IBaseView, LifecycleProvider<ActivityEvent> {
    protected V binding;
    protected VM viewModel;
    private int viewModelId;
    private MaterialDialog dialog;
    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();
    //网络错误提示
    public Gloading.Holder mHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //页面接受的参数方法
        initParam();
        //私有的初始化Databinding和ViewModel方法
        initViewDataBinding(savedInstanceState);
        //私有的ViewModel与View的契约事件回调逻辑
        registorUIChangeLiveDataCallBack();
        //沉浸式
        initImmersionBar();

        //页面数据初始化方法
        initData();
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
        //注册RxBus(项目使用livedatabus，这里没删)
        viewModel.registerRxBus();
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        //gload初始化
        initLoadingStatusViewIfNeed();
    }

    public void initImmersionBar() {
//        View statusView = new View(this);
//        statusView.setLayoutParams(new LinearLayout.LayoutParams(-1, 0));
//        statusView.setBackgroundResource(R.color.white);
//
//        FrameLayout mTarget = findViewById(Window.ID_ANDROID_CONTENT);
//        LinearLayout container = new LinearLayout(this);
//        container.setOrientation(LinearLayout.VERTICAL);
//        ViewGroup.LayoutParams lp = mTarget.getLayoutParams();
//        ViewParent parent = mTarget.getParent();
//        ViewGroup group = (ViewGroup) parent;
//        int index = group.indexOfChild(mTarget);
//        group.removeView(mTarget);
//        group.addView(container, index, lp);
//        container.addView(statusView);
//        container.addView(mTarget);

        ImmersionBar.with(this)
                .statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
                .navigationBarColorInt(Color.WHITE)
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .statusBarColor(R.color.white)
                .fitsSystemWindows(true) //使用该属性必须指定状态栏的颜色，不然状态栏透明，很难看
                .init();

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
    protected void onDestroy() {
        super.onDestroy();
        //解除Messenger注册
        Messenger.getDefault().unregister(viewModel);
        if (viewModel != null) {
            viewModel.removeRxBus();
        }
        if(binding != null){
            binding.unbind();
        }
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
    }

    /**
     * 注入绑定
     */
    private void initViewDataBinding(Bundle savedInstanceState) {
        //DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }, 同步后会自动关联android.databinding包
        binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState));
        viewModelId = initVariableId();
        viewModel = initViewModel();
        if (viewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel.class;
            }
            viewModel = (VM) createViewModel(this, modelClass);
        }
        //关联ViewModel
        binding.setVariable(viewModelId, viewModel);
        //支持LiveData绑定xml，数据改变，UI自动会更新
        binding.setLifecycleOwner(this);
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(viewModel);
        //注入RxLifecycle生命周期
        viewModel.injectLifecycleProvider(this);
    }

    //刷新布局
    public void refreshLayout() {
        if (viewModel != null) {
            binding.setVariable(viewModelId, viewModel);
        }
    }


    /**
     * =====================================================================
     **/
    //注册ViewModel与View的契约UI回调事件
    protected void registorUIChangeLiveDataCallBack() {


        //加载对话框显示  （Gload库）
        viewModel.getUC().getshowGloadDialogEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                showLoading();
            }
        });
        //加载对话框消失（Gload库）
        viewModel.getUC().getdismissGloadDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                showLoadSuccess();
            }
        });
        //调用接口失败（Gload库）
        viewModel.getUC().getloadfaild().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                showLoadFailed();
            }
        });



        //加载对话框显示
        viewModel.getUC().getShowDialogEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                showDialog(title);
            }
        });
        //加载对话框消失
        viewModel.getUC().getDismissDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                dismissDialog();
            }
        });
        //跳入新页面
        viewModel.getUC().getStartActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                Class<?> clz = (Class<?>) params.get(ParameterField.CLASS);
                Bundle bundle = (Bundle) params.get(ParameterField.BUNDLE);
                startActivity(clz, bundle);
            }
        });
        //跳入ContainerActivity
        viewModel.getUC().getStartContainerActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                String canonicalName = (String) params.get(ParameterField.CANONICAL_NAME);
                Bundle bundle = (Bundle) params.get(ParameterField.BUNDLE);
                startContainerActivity(canonicalName, bundle);
            }
        });
        //关闭界面
        viewModel.getUC().getFinishEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                finish();
            }
        });
        //关闭上一层
        viewModel.getUC().getOnBackPressedEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                onBackPressed();
            }
        });
    }

    public void showDialog(String title) {
        if (dialog != null) {
            dialog = dialog.getBuilder().title(title).build();
            dialog.show();
        } else {
            MaterialDialog.Builder builder = MaterialDialogUtils.showIndeterminateProgressDialog(this, title, true);
            dialog = builder.show();
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle);
        }
        startActivity(intent);
    }

    /**
     * =====================================================================
     **/
    @Override
    public void initParam() {

    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    public abstract int initContentView(Bundle savedInstanceState);

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    public abstract int initVariableId();

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    public VM initViewModel() {
        return null;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViewObservable() {

    }

    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return ViewModelProviders.of(activity).get(cls);
    }




    //gload初始化
    protected void initLoadingStatusViewIfNeed() {
        if (mHolder == null) {
            //bind status view to activity root view by default
            mHolder = Gloading.getDefault().wrap(this).withRetry(new Runnable() {
                @Override
                public void run() {
                    onLoadRetry();
                }
            });
        }
    }

    protected void onLoadRetry() {
        // override this method in subclass to do retry task
    }

    public void showLoading() {

        mHolder.showLoading();
    }

    public void showLoadSuccess() {

        mHolder.showLoadSuccess();
    }

    public void showLoadFailed() {

        mHolder.showLoadFailed();
    }

    public void showEmpty() {

        mHolder.showEmpty();
    }

}
