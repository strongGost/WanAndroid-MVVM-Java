package com.study.wanandroid.ui.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseFragment;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.databinding.FragmentProjectBinding;
import com.study.wanandroid.ui.project.adaper.CategoryStateAdapter;

public class ProjectFragment extends BaseFragment<FragmentProjectBinding> {
    private CategoryStateAdapter adapter;
    private TabsViewModel viewModel;


    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(TabsViewModel.class);
    }

    @Override
    protected void initObservers() {
        viewModel.getCategroys().observe(getViewLifecycleOwner(), data -> {
            adapter.setData(data);
            initTabLayout();
        });
        viewModel.getNetworkStatus().observe(getViewLifecycleOwner(), resource -> {
            binding.stateLayout.switchView(resource.getState());
        });
    }

    @Override
    protected void initData() {
        viewModel.getCategory();
    }


    @Override
    protected void initViews() {
        // 设置边距
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets)-> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        adapter = new CategoryStateAdapter(this);
        binding.vp2List.setAdapter(adapter);
    }

    private void initTabLayout() {
        new TabLayoutMediator(binding.tabLayout, binding.vp2List, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                SystemBean item = adapter.getItem(position);
                if (item != null) {
                    tab.setText(item.getName());
                }
            }
        }).attach();
    }


    @Override
    protected FragmentProjectBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentProjectBinding.inflate(inflater, container, false);
    }
}
