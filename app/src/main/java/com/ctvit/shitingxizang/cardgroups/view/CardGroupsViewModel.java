package com.ctvit.shitingxizang.cardgroups.view;

import android.app.Application;

import androidx.annotation.NonNull;

import com.ctvit.shitingxizang.entity.CardGroupsEntity;
import com.ctvit.shitingxizang.net.ApiDisposableObserver;
import com.ctvit.shitingxizang.utils.CardGroupsUtils;
import com.ctvit.shitingxizang.utils.RetrofitClient;


import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class CardGroupsViewModel extends BaseViewModel {
    public SingleLiveEvent<CardGroupsViewModel> deleteItemLiveData = new SingleLiveEvent<>();
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadmore = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent<CardGroupsEntity> mCardGroupsEntitySingleLiveEvent = new SingleLiveEvent<>();
    }

    public CardGroupsViewModel(@NonNull Application application) {
        super(application);
    }

    boolean isRefresh = false;
    //下拉刷新
    public BindingCommand onRefreshCommand = new BindingCommand(() -> {
        ToastUtils.showShort("下拉刷新");
        isRefresh = true;
        requestNetWork();
    });
    //上拉加载
    public BindingCommand onLoadMoreCommand = new BindingCommand(() -> {

//            //模拟网络上拉加载更多

        uc.finishLoadmore.call();

    });


    /**
     * 网络请求方法，在ViewModel中调用Model层，通过Okhttp+Retrofit+RxJava发起请求
     */
    public void requestNetWork() {
        //可以调用addSubscribe()添加Disposable，请求与View周期同步
//        model.demoGet()
//        RetrofitClient.getApiCms().
//                getCardGroups(CardGroupsUtils.getCardGroupsParams("HOUR", 10, 1, ""))
//                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) // 请求与View周期同步
//                .compose(RxUtils.schedulersTransformer()) //线程调度
//                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换
//                .doOnSubscribe(this)//请求与ViewModel周期同步
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//                        if (!isRefresh) {
//                            //加载数据提示框
//                            showDialog();
//                            isRefresh = false;
//                        }
//                    }
//                })
//                .subscribe(new DisposableObserver<CardGroupsEntity>() {
//
//                    @Override
//                    public void onNext(CardGroupsEntity cardGroupsEntity) {
//                        //把数据用LiveDate传给View
//
//                        uc.mCardGroupsEntitySingleLiveEvent.setValue(cardGroupsEntity);
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//
//                        KLog.i("onError222" + throwable.getMessage());
//                        //关闭对话框
//                        loadfaild();
//                        //请求刷新完成收回
//                        if (throwable instanceof ResponseThrowable) {
//                            ToastUtils.showShort(((ResponseThrowable) throwable).message);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        //关闭对话框
////                        dismissDialog();
//                        //请求刷新完成收回
//                        uc.finishRefreshing.call();
//                    }
//                });
//
//

        RetrofitClient.getApiCms()
                .getCardGroups(CardGroupsUtils.getCardGroupsParams("HOUR", 10, 1, ""))
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) // 请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换
                .doOnSubscribe(this)//请求与ViewModel周期同步
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (!isRefresh) {
                            //加载数据提示框
                            showDialog();
                            isRefresh = false;
                        }
                    }
                })
                .subscribe(new ApiDisposableObserver<CardGroupsEntity>() {
                    @Override
                    public void onResult(CardGroupsEntity cardGroupsEntity) {
                        uc.mCardGroupsEntitySingleLiveEvent.setValue(cardGroupsEntity);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        //关闭对话框
                        loadfaild();
                        //请求刷新完成收回
                        uc.finishRefreshing.call();
                    }

                    @Override
                    public void onComplete() {
                        //关闭对话框
                        dismissDialog();
                        //请求刷新完成收回
                        uc.finishRefreshing.call();
                    }
                });


    }

}
