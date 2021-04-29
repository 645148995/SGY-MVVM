package com.ctvit.shitingxizang.cardgroups.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctvit.shitingxizang.BR;
import com.ctvit.shitingxizang.R;
import com.ctvit.shitingxizang.base.BaseViewType;
import com.ctvit.shitingxizang.cardgroups.Constant;
import com.ctvit.shitingxizang.cardgroups.adapter.CardGroupsAdapter;
import com.ctvit.shitingxizang.databinding.FragmentCardgroupsBinding;
import com.ctvit.shitingxizang.entity.CardGroupsEntity;
import com.ctvit.shitingxizang.ui.main.MainActivity;
import com.jeremyliao.liveeventbus.LiveEventBus;


import java.util.ArrayList;
import java.util.List;

import com.ctvit.shitingxizang.base.BaseFragment;


/**
 * Created by goldze on 2018/7/18.
 */

public class CardGroupsFragment extends BaseFragment<FragmentCardgroupsBinding, CardGroupsViewModel> {

    private CardGroupsAdapter cardGroupsAdapter;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_cardgroups;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    public static CardGroupsFragment newInstance() {
        Bundle args = new Bundle();
        CardGroupsFragment fragment = new CardGroupsFragment();
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
        // Adapter属于View层的东西, 不建议定义到ViewModel中绑定，以免内存泄漏
        cardGroupsAdapter = new CardGroupsAdapter(getContext(), "1");
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);//(垂直布局、水平布局)
        binding.recycleview.setLayoutManager(layout);
        //RecyclerView的缓存
        binding.recycleview.setItemViewCacheSize(15);
        //当知道Adapter内Item的改变不会影响RecyclerView宽高的时候，可以设置为true让RecyclerView避免重新计算大小。
        binding.recycleview.setHasFixedSize(true);
        binding.recycleview.setAdapter(cardGroupsAdapter);
        //请求网络数据
        viewModel.requestNetWork();
    }

    @Override
    public void initViewObservable() {

       // 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
        LiveEventBus
                .get(Constant.LiveDataBus.TABAUTOREFRESH, int.class)
                .observe(this, position -> {
                    if (position!= MainActivity.FIRST) return;
                    if (!binding.recycleview.canScrollVertically(-1)) {
                        binding.smartRefreshLayout.autoRefresh();
                    } else {
                        binding.recycleview.smoothScrollToPosition(0);
                    }
                });

        //首页的数据在这里返回
        viewModel.uc.mCardGroupsEntitySingleLiveEvent.observe(this, cardGroupsEntity -> {
            addDataToAdapter(true, cardGroupsEntity);

        });


        //监听下拉刷新完成
        viewModel.uc.finishRefreshing.observe(this, o -> {
            //结束刷新
            binding.smartRefreshLayout.finishRefresh();

        });
        //监听上拉加载完成
        viewModel.uc.finishLoadmore.observe(this, o -> {
            //结束刷新
            binding.smartRefreshLayout.finishLoadMore();
            binding.smartRefreshLayout.finishLoadMoreWithNoMoreData();  //全部加载完成,没有数据了调用此方法
        });


    }

    @Override
    protected void onLoadRetry() {
        super.onLoadRetry();
        //请求网络数据
        viewModel.requestNetWork();
    }

    /**
     * 将数据加入到adapter
     */
    List<BaseViewType> dataList;

    private void addDataToAdapter(boolean isLoadMore, CardGroupsEntity cardGroupsEntity) {
        //将数据添加至adapter
        dataList = new ArrayList<>();
        dataList.addAll(cardGroupsEntity.getCardgroups());

        if (!isLoadMore) {
            //如果不是加载更多 就把已有数据清空 不然会数据重复
            cardGroupsAdapter.clean();
        }
        cardGroupsAdapter.addData(dataList);
        cardGroupsAdapter.notifyDataSetChanged();
        showLoadSuccess();
    }
}
