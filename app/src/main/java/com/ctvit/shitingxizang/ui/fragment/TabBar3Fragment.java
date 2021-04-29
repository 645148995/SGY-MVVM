package com.ctvit.shitingxizang.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;


import com.ctvit.shitingxizang.BR;
import com.ctvit.shitingxizang.R;

import com.ctvit.shitingxizang.base.BaseFragment;

/**
 * Created by goldze on 2018/7/18.
 */

public class TabBar3Fragment extends BaseFragment {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_tab_bar_3;
    }
    public static TabBar3Fragment newInstance() {

        Bundle args = new Bundle();

        TabBar3Fragment fragment = new TabBar3Fragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

}
