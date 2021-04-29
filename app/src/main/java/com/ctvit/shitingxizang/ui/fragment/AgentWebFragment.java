package com.ctvit.shitingxizang.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ctvit.shitingxizang.BR;
import com.ctvit.shitingxizang.R;
import com.ctvit.shitingxizang.base.BaseFragment;
import com.ctvit.shitingxizang.cardgroups.Constant;
import com.ctvit.shitingxizang.databinding.FragmentAgentwebBinding;
import com.just.agentweb.AgentWeb;


import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.KLog;

/**
 * Created by cenxiaozhong on 2017/5/15.
 * source code  https://github.com/Justson/AgentWeb
 */

public class AgentWebFragment extends BaseFragment<FragmentAgentwebBinding, BaseViewModel> {

    private AgentWeb mAgentWeb;

    @Override
    public void initParam() {
        //获取列表传入的实体
        Bundle mBundle = getArguments();
        if (mBundle != null) {
            mBundle.getString(Constant.EXTRA_PAGE_ID);
            KLog.i("getArguments" + mBundle.getString("123"));
        }
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_agentweb;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData(View view) {

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) binding.linearLayout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go("http://www.jd.com");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }
}
