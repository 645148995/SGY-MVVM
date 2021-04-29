package com.ctvit.shitingxizang.ui.main;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ctvit.shitingxizang.BR;
import com.ctvit.shitingxizang.R;
import com.ctvit.shitingxizang.base.BaseActivity;
import com.ctvit.shitingxizang.cardgroups.Constant;
import com.ctvit.shitingxizang.cardgroups.view.CardGroupsFragment;
import com.ctvit.shitingxizang.databinding.ActivityMainBinding;
import com.ctvit.shitingxizang.ui.channel.ChannelFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.ToastUtils;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;
import me.yokeyword.fragmentation.SupportFragment;


/**
 * 底部tab按钮
 * Created by GG on 2020/7/17.
 */

public class Main2Activity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    private long touchTime = 0; // 再点一次退出程序时间设置
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;
    private SupportFragment[] mFragments = new SupportFragment[4];

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    ViewPager mViewPager;


    @Override
    public void initData() {
//        初始化Fragment
        initFragment();
        //初始化底部Button
        initBottomTab();


    }

    private void initFragment() {

//        mViewPager = findViewById(R.id.view_pager);
        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(ChannelFragment.newInstance());
        mFragments.add(CardGroupsFragment.newInstance());
        mFragments.add(CardGroupsFragment.newInstance());

        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(myFragmentPagerAdapter);
//        mViewPager.setOffscreenPageLimit(2);

    }


    private void initBottomTab() {
        NavigationController navigationController = binding.pagerBottomTab.material()
                .addItem(R.mipmap.yingyong, "广播电视")
                .addItem(R.mipmap.yingyong, "新闻")
                .addItem(R.mipmap.huanzhe, "视听")
                .addItem(R.mipmap.xiaoxi_select, "直播", ContextCompat.getColor(this, R.color.colorAccent))
                .addItem(R.mipmap.wode_select, "我的")
                .setDefaultColor(ContextCompat.getColor(this, R.color.textColorVice))
                .build();


//        navigationController.setupWithViewPager(mViewPager);

        //底部按钮的点击事件监听
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
//                showHideFragment(mFragments[index], mFragments[old]);
                mViewPager.setCurrentItem(index);
            }

            @Override
            public void onRepeat(int index) {
                // 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新

                LiveEventBus.get(Constant.LiveDataBus.TABAUTOREFRESH)
                        .post(index);
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
