package com.study.wanandroid.ui.wechat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.study.wanandroid.base.BaseFragment;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.databinding.FragmentWechatBinding;
import com.study.wanandroid.ui.wechat.adapter.TabTextAdapter;
import com.study.wanandroid.ui.widget.StateLayout;
import com.study.wanandroid.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class WeChatFragment extends BaseFragment<FragmentWechatBinding> implements BaseQuickAdapter.OnItemClickListener<SystemBean> {

    private TabTextAdapter tabTextAdapter;
    private WxTabViewModel viewModel;
    private int lastWxId = -1;   /* 上次选中的 id */
    private int curPosition = 0;    /* 当前选中 tab 位置*/



    @Override
    protected FragmentWechatBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWechatBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initViews() {
        // 设置边距
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets)-> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        binding.stateLayout.setListener(() -> viewModel.onRetry());
        binding.recycleLeft.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        tabTextAdapter = new TabTextAdapter();
        tabTextAdapter.setOnItemClickListener(this);
        binding.recycleLeft.setAdapter(tabTextAdapter);
    }

    @Override
    protected void initData() {
        viewModel.getWeChatList();
    }

    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(WxTabViewModel.class);
    }

    @Override
    protected void initObservers() {
        viewModel.getNetwork().observe(getViewLifecycleOwner(), resource -> {
            binding.stateLayout.switchView(resource.getState());
        });
        viewModel.getWxList().observe(getViewLifecycleOwner(), data -> {
            tabTextAdapter.submitList(data);
            /* 默认展示第一个 tab 内容 */
            if (lastWxId == -1) {
                tabTextAdapter.setSelectPos(0);
                switchFragment(data.get(0).getId());
            } else {
                tabTextAdapter.setSelectPos(curPosition);
                switchFragment(data.get(curPosition).getId());
            }
        });
    }

    /**
     * 切换内容区域的 Fragment
     * @param wxId 当前 Fragment id
     */
    private void switchFragment(int wxId) {
        // 选中位置没有改变
        if (wxId == lastWxId) return;

        /* 1. 获取当前要显示的 Fragment */
        String tag = "WX_" + wxId;
        Fragment targetFragment = getChildFragmentManager().findFragmentByTag(tag); // 当前要显示的 Fragment
        FragmentTransaction transaction = getChildFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true);

        /* 根据当前要显示的 Fragment 是否在容器中，进行显示 or 添加  */
        if (targetFragment == null) {   // 不在：创建
            targetFragment = WxArticleFragment.newInstance(wxId);
            transaction.add(binding.fragmentContainer.getId(), targetFragment, tag);
        } else { // 在：显示
            transaction.show(targetFragment);
        }

        /* 隐藏上次选中的 Fragment */
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment != targetFragment) {
                transaction.hide(fragment);
            }
        }
        transaction.commit();
        lastWxId = wxId;    // 更新选中 id
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            lastWxId = savedInstanceState.getInt("LAST_WX_ID", -1);
            curPosition = savedInstanceState.getInt("CUR_POSITION", 0);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("LAST_WX_ID", lastWxId);
        outState.putInt("CUR_POSITION", curPosition);
    }

    @Override
    public void onClick(@NonNull BaseQuickAdapter<SystemBean, ?> baseQuickAdapter, @NonNull View view, int i) {
        SystemBean item = baseQuickAdapter.getItem(i);
        tabTextAdapter.setSelectPos(i);
        switchFragment(item.getId());
        curPosition = i;
    }
}
