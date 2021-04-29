package com.ctvit.shitingxizang.ui.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/*
   项目名：CtvitXiZang
   包名:   com.ctvit.ctvitxizang.ui.main
   创建者：孙光远
   创建时间：2020/8/17 14:12
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mfragmentList;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.mfragmentList = fragmentList;
    }

    //获取集合中的某个项
    @Override
    public Fragment getItem(int position) {
        return mfragmentList.get(position);
    }

    //返回绘制项的数目
    @Override
    public int getCount() {
        return mfragmentList.size();
    }
}
