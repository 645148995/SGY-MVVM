package com.ctvit.shitingxizang.ui.channel;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ctvit.shitingxizang.cardgroups.view.CardGroupsFragment;
import com.ctvit.shitingxizang.entity.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ChayChan
 * @description: 频道的adapter
 * @date 2017/6/16  9:45
 */

public class ChannelPagerAdapter extends FragmentStatePagerAdapter {

    private List<CardGroupsFragment> mFragments;
    private List<Channel> mChannels;

    public ChannelPagerAdapter(List<CardGroupsFragment> fragmentList, List<Channel> channelList, FragmentManager fm) {
        super(fm);
        mFragments = fragmentList != null ? fragmentList : new ArrayList<>();
        mChannels = channelList != null ? channelList : new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mChannels.get(position).title;
    }

    @Override
    public int getItemPosition(Object object) {
        //        if (mChildCount > 0) {
        //            mChildCount--;
        return POSITION_NONE;
        //        }
        //        return super.getItemPosition(object);
    }
}
