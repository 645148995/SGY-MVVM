package com.ctvit.shitingxizang.ui.main;

import android.os.Bundle;


import androidx.core.content.ContextCompat;


import com.ctvit.shitingxizang.BR;
import com.ctvit.shitingxizang.R;
import com.ctvit.shitingxizang.cardgroups.Constant;
import com.ctvit.shitingxizang.cardgroups.view.CardGroupsFragment;
import com.ctvit.shitingxizang.databinding.ActivityMainBinding;

import com.ctvit.shitingxizang.ui.channel.ChannelFragment;
import com.ctvit.shitingxizang.ui.fragment.TabBar2Fragment;
import com.jeremyliao.liveeventbus.LiveEventBus;


import com.ctvit.shitingxizang.base.BaseActivity;

import me.goldze.mvvmhabit.utils.ToastUtils;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;
import me.yokeyword.fragmentation.SupportFragment;


/**
 * 底部tab按钮
 * Created by GG on 2020/7/17.
 */

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    private long touchTime = 0; // 再点一次退出程序时间设置
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;
    public static final int FIVE = 4;
    private SupportFragment[] mFragments = new SupportFragment[5];

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
//        初始化Fragment
        initFragment();
        //初始化底部Button
        initBottomTab();


    }

    private void initFragment() {
        SupportFragment firstFragment = findFragment(CardGroupsFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = ChannelFragment.newInstance();
            mFragments[SECOND] = TabBar2Fragment.newInstance();
            mFragments[THIRD] = TabBar2Fragment.newInstance();
            mFragments[FOURTH] = TabBar2Fragment.newInstance();
            mFragments[FIVE] = TabBar2Fragment.newInstance();

            loadMultipleRootFragment(R.id.frameLayout, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOURTH],
                    mFragments[FIVE]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findFragment(TabBar2Fragment.class);
            mFragments[THIRD] = findFragment(TabBar2Fragment.class);
            mFragments[FOURTH] = findFragment(TabBar2Fragment.class);
            mFragments[FIVE] = findFragment(TabBar2Fragment.class);
        }


    }

    private void initBottomTab() {
        NavigationController navigationController = binding.pagerBottomTab.material()
                .addItem(R.mipmap.yingyong, "广播电视")
                .addItem(R.mipmap.yingyong, "新闻")
                .addItem(R.mipmap.huanzhe, "视听")
                .addItem(R.mipmap.xiaoxi_select, "直播", ContextCompat.getColor(this, R.color.colorAccent))
                .addItem(R.mipmap.wode_select, "我的")
//                .addItem(R.mipmap.wode_select, "我的")
                .setDefaultColor(ContextCompat.getColor(this, R.color.textColorVice))
                .build();
        //底部按钮的点击事件监听
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                showHideFragment(mFragments[index], mFragments[old]);
            }

            @Override
            public void onRepeat(int index) {
                // 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
//                LiveEventBus.get(Constant.LiveDataBus.TABAUTOREFRESH)
//                        .post(index);
            }
        });
    }

    @Override
    public void onBackPressedSupport() {

        if (System.currentTimeMillis() - touchTime < 2000) {
            finish();
        } else {
            touchTime = System.currentTimeMillis();
            ToastUtils.showShort(R.string.press_again_exit);
        }
    }

}
