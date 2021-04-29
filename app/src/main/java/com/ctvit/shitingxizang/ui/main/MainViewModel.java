package com.ctvit.shitingxizang.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;



import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

/**
 * Created by goldze on 2017/7/17.
 */

public class MainViewModel extends BaseViewModel {
    //使用Observable
    public SingleLiveEvent<Boolean> requestCameraPermissions = new SingleLiveEvent<>();

    public SingleLiveEvent<String> loadUrlEvent = new SingleLiveEvent<>();

    public MainViewModel(@NonNull Application application) {
        super(application);

    }




}
