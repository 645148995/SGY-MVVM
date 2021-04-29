package com.ctvit.shitingxizang.net;

import com.ctvit.shitingxizang.entity.CardGroupsEntity;

import io.reactivex.observers.DisposableObserver;
import me.goldze.mvvmhabit.base.AppManager;
import me.goldze.mvvmhabit.http.NetworkUtil;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * Created by goldze on 2017/5/10.
 * 统一的Code封装处理。该类仅供参考，实际业务逻辑, 根据需求来定义，
 */

public abstract class ApiDisposableObserver<T> extends DisposableObserver<T> {
    public abstract void onResult(T t);

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (e instanceof ResponseThrowable) {
            ResponseThrowable rError = (ResponseThrowable) e;
            ToastUtils.showShort(rError.message);
            return;
        }
        //其他全部甩锅网络异常
        ToastUtils.showShort("网络异常");
    }

    @Override
    public void onStart() {
        super.onStart();

        // if  NetworkAvailable no !   must to call onCompleted
        if (!NetworkUtil.isNetworkAvailable(Utils.getContext())) {
            KLog.d("无网络");
            onComplete();
        }
    }

    @Override
    public void onNext(Object o) {
        CardGroupsEntity baseResponse = (CardGroupsEntity) o;
        KLog.i("onError-----" + baseResponse.getSucceed());
        KLog.i("无网络，读取缓存数据" + baseResponse);

        switch (baseResponse.getSucceed()) {
            case 1:
                //请求成功, 正确的操作方式
//                if (baseResponse!= null || baseResponse.getCardgroups()!= null || !baseResponse.getCardgroups().isEmpty()) {
                    onResult((T) o);
//                }
                break;
            case CodeRule.CODE_510:
                //无效的Token，提示跳入登录页
                ToastUtils.showShort("token已过期，请重新登录");
                //关闭所有页面
                AppManager.getAppManager().finishAllActivity();
                //跳入登录界面
                //*****该类仅供参考，实际业务Code, 根据需求来定义，******//
                break;
            case CodeRule.CODE_530:
                ToastUtils.showShort("请先登录");
                break;
            default:
                onResult((T) o);
//                ToastUtils.showShort("错误代码:", baseResponse.getCode());
                break;
        }
    }



    public static final class CodeRule {
        //请求成功, 正确的操作方式
        static final int CODE_200 = 200;
        //请求成功, 消息提示
        static final int CODE_220 = 220;
        //请求失败，不打印Message
        static final int CODE_300 = 300;
        //请求失败，打印Message
        static final int CODE_330 = 330;
        //服务器内部异常
        static final int CODE_500 = 500;
        //参数为空
        static final int CODE_503 = 503;
        //没有数据
        static final int CODE_502 = 502;
        //无效的Token
        static final int CODE_510 = 510;
        //未登录
        static final int CODE_530 = 530;
        //请求的操作异常终止：未知的页面类型
        static final int CODE_551 = 551;
    }
}