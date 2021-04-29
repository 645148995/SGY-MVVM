package com.ctvit.shitingxizang.ui.twinklingrefreshlayout;


import androidx.databinding.BindingAdapter;


import com.scwang.smart.refresh.layout.SmartRefreshLayout;


import me.goldze.mvvmhabit.binding.command.BindingCommand;


/**
 * Created by goldze on 2017/6/16.
 * TwinklingRefreshLayout列表刷新的绑定适配器
 * 使用databinding
 */
public class ViewAdapter {

    @BindingAdapter(value = {"onRefreshCommand", "onLoadMoreCommand"}, requireAll = false)
    public static void onRefreshAndLoadMoreCommand(SmartRefreshLayout layout, final BindingCommand onRefreshCommand, final BindingCommand onLoadMoreCommand) {

        layout.setOnRefreshListener(refreshLayout -> {
            if (onRefreshCommand != null) {
                onRefreshCommand.execute();
            }
        });
        layout.setOnLoadMoreListener(refreshLayout -> {
            if (onLoadMoreCommand != null) {
                onLoadMoreCommand.execute();
            }
        });


    }
}
