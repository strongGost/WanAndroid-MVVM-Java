package com.study.wanandroid.ui.square.system;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.study.wanandroid.base.BaseFragment;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.databinding.FragmentSystemBinding;
import com.study.wanandroid.ui.square.adapter.LabelClassfiyAdapter;
import com.study.wanandroid.ui.system.SystemActivity;
import com.study.wanandroid.utils.Constant;

public class SystemFragment extends BaseFragment<FragmentSystemBinding> implements LabelClassfiyAdapter.OnLabelClickListener<SystemBean> {
    private SystemViewModel viewModel;
    private LabelClassfiyAdapter<SystemBean> adapter;

    public static SystemFragment newInstance() {
        return new SystemFragment();
    }


    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(SystemViewModel.class);
    }

    @Override
    protected void initObservers() {
        viewModel.getNetworkStatus().observe(getViewLifecycleOwner(), resource -> {
            binding.stateLayout.switchView(resource.getState());
        });
        viewModel.getSystems().observe(getViewLifecycleOwner(), systems -> {
            adapter.setData(systems);
        });
    }

    @Override
    protected void initViews() {
        binding.recycleContent.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LabelClassfiyAdapter<SystemBean>();
        adapter.setListener(this);  // 点击事件
        binding.recycleContent.setAdapter(adapter);
        binding.stateLayout.setListener(() -> viewModel.onRetry());
    }


    @Override
    protected FragmentSystemBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSystemBinding.inflate(inflater, container, false);
    }


    /**
     * 点击事件，点击后跳转到该体系下所有文章
     */
    @Override
    public void onClickLabel(SystemBean bean, int curPos, int parentPos) {
        // 将 children 传递
        Intent intent = new Intent(requireContext(), SystemActivity.class)
                .putExtra(Constant.EXTRA_CID, curPos)
                .putExtra(Constant.Data_KEY, viewModel.getChildren(parentPos));
        startActivity(intent);
    }

}
