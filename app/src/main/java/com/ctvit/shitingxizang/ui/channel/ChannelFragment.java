package com.ctvit.shitingxizang.ui.channel;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.ctvit.shitingxizang.BR;
import com.ctvit.shitingxizang.R;
import com.ctvit.shitingxizang.base.BaseFragment;
import com.ctvit.shitingxizang.cardgroups.Constant;
import com.ctvit.shitingxizang.cardgroups.view.CardGroupsFragment;
import com.ctvit.shitingxizang.databinding.FragmentHomeBindingImpl;
import com.ctvit.shitingxizang.entity.Channel;
import com.ctvit.shitingxizang.utils.UIUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.SPUtils;


/**
 * Created by goldze on 2018/7/18.
 */

public class ChannelFragment extends BaseFragment<FragmentHomeBindingImpl, BaseViewModel> implements OnChannelListener {
    private List<Channel> mSelectedChannels = new ArrayList<>();
    private List<Channel> mUnSelectedChannels = new ArrayList<>();
    private List<CardGroupsFragment> mChannelFragments = new ArrayList<>();
    private Gson mGson = new Gson();
    private ChannelPagerAdapter mChannelPagerAdapter;
    private String[] mChannelCodes;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    public static ChannelFragment newInstance() {
        Bundle args = new Bundle();
        ChannelFragment fragment = new ChannelFragment();
        fragment.setArguments(args);
        return fragment;

    }

    //不可见时
    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
    }

    //懒加载
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void initData() {
        initChannelData();
        initChannelFragments();
        initListener();
    }

    /**
     * 初始化已选频道和未选频道的数据
     */
    private void initChannelData() {
        String selectedChannelJson = SPUtils.getString(Constant.SELECTED_CHANNEL_JSON, "");
        String unselectChannel = SPUtils.getString(Constant.UNSELECTED_CHANNEL_JSON, "");

        if (TextUtils.isEmpty(selectedChannelJson) || TextUtils.isEmpty(unselectChannel)) {
            //本地没有title
            String[] channels = getResources().getStringArray(R.array.channel);
            String[] channelCodes = getResources().getStringArray(R.array.channel_code);
            //默认添加了全部频道
            for (int i = 0; i < channelCodes.length; i++) {
                String title = channels[i];
                String code = channelCodes[i];
                mSelectedChannels.add(new Channel(title, code));
            }

            selectedChannelJson = mGson.toJson(mSelectedChannels);//将集合转换成json字符串
            KLog.i("selectedChannelJson:" + selectedChannelJson);
            SPUtils.putString(Constant.SELECTED_CHANNEL_JSON, selectedChannelJson);//保存到sp
        } else {
            //之前添加过
            List<Channel> selectedChannel = mGson.fromJson(selectedChannelJson, new TypeToken<List<Channel>>() {
            }.getType());
            List<Channel> unselectedChannel = mGson.fromJson(unselectChannel, new TypeToken<List<Channel>>() {
            }.getType());
            mSelectedChannels.addAll(selectedChannel);
            mUnSelectedChannels.addAll(unselectedChannel);
        }
    }

    /**
     * 初始化已选频道的fragment的集合
     */
    private void initChannelFragments() {
        KLog.e("initChannelFragments");
        mChannelCodes = getResources().getStringArray(R.array.channel_code);
        for (Channel channel : mSelectedChannels) {
            CardGroupsFragment newsFragment = new CardGroupsFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.CHANNEL_CODE, channel.channelCode);
            bundle.putBoolean(Constant.IS_VIDEO_LIST, channel.channelCode.equals(mChannelCodes[1]));//是否是视频列表页面,根据判断频道号是否是视频
            newsFragment.setArguments(bundle);
            mChannelFragments.add(newsFragment);//添加到集合中
        }
    }



    public void initListener() {
        mChannelPagerAdapter = new ChannelPagerAdapter(mChannelFragments, mSelectedChannels,getChildFragmentManager());
        binding.vpContent.setAdapter(mChannelPagerAdapter);
        binding.vpContent.setOffscreenPageLimit(mSelectedChannels.size());



//        binding.tabChannel.setTabPaddingLeftAndRight(UIUtils.dip2Px(10), UIUtils.dip2Px(10));
        binding.tabChannel.setupWithViewPager(binding.vpContent);
        binding.tabChannel.post(new Runnable() {
            @Override
            public void run() {
                //设置最小宽度，使其可以在滑动一部分距离
                ViewGroup slidingTabStrip = (ViewGroup) binding.tabChannel.getChildAt(0);
                slidingTabStrip.setMinimumWidth(slidingTabStrip.getMeasuredWidth() + binding.ivOperation.getMeasuredWidth());
            }
        });
        //隐藏指示器
//        binding.tabChannel.setSelectedTabIndicatorHeight(0);

        binding.vpContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //当页签切换的时候，如果有播放视频，则释放资源
//                Jzvd.releaseAllVideos();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

binding.ivOperation.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        ChannelDialogFragment dialogFragment = ChannelDialogFragment.newInstance(mSelectedChannels, mUnSelectedChannels);
        dialogFragment.setOnChannelListener(ChannelFragment.this);
        dialogFragment.show(getChildFragmentManager(), "CHANNEL");
        dialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mChannelPagerAdapter.notifyDataSetChanged();
                binding.vpContent.setOffscreenPageLimit(mSelectedChannels.size());
                binding.vpContent.setCurrentItem(binding.tabChannel.getSelectedTabPosition());
//                KLog.i("binding.ivOperation"+binding.tabChannel.getSelectedTabPosition());
                ViewGroup slidingTabStrip = (ViewGroup) binding.tabChannel.getChildAt(0);
//                //注意：因为最开始设置了最小宽度，所以重新测量宽度的时候一定要先将最小宽度设置为0
                slidingTabStrip.setMinimumWidth(0);
                slidingTabStrip.measure(0, 0);
                slidingTabStrip.setMinimumWidth(slidingTabStrip.getMeasuredWidth() + binding.ivOperation.getMeasuredWidth());

                //保存选中和未选中的channel
                SPUtils.putString(Constant.SELECTED_CHANNEL_JSON, mGson.toJson(mSelectedChannels));
                SPUtils.putString(Constant.UNSELECTED_CHANNEL_JSON, mGson.toJson(mUnSelectedChannels));
            }
        });
    }
});


    }

    public String getCurrentChannelCode(){
        int currentItem = binding.vpContent.getCurrentItem();
        return mSelectedChannels.get(currentItem).channelCode;
    }




    @Override
    public void onItemMove(int starPos, int endPos) {
        listMove(mSelectedChannels, starPos, endPos);
        listMove(mChannelFragments, starPos, endPos);
    }


    @Override
    public void onMoveToMyChannel(int starPos, int endPos) {
        //移动到我的频道
        Channel channel = mUnSelectedChannels.remove(starPos);
        mSelectedChannels.add(endPos, channel);

        CardGroupsFragment newsFragment = new CardGroupsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.CHANNEL_CODE, channel.channelCode);
        bundle.putBoolean(Constant.IS_VIDEO_LIST, channel.channelCode.equals(mChannelCodes[1]));
        newsFragment.setArguments(bundle);
        mChannelFragments.add(newsFragment);
    }

    @Override
    public void onMoveToOtherChannel(int starPos, int endPos) {
        //移动到推荐频道
        mUnSelectedChannels.add(endPos, mSelectedChannels.remove(starPos));
        mChannelFragments.remove(starPos);
    }

    private void listMove(List datas, int starPos, int endPos) {
        Object o = datas.get(starPos);
        //先删除之前的位置
        datas.remove(starPos);
        //添加到现在的位置
        datas.add(endPos, o);
    }




}
