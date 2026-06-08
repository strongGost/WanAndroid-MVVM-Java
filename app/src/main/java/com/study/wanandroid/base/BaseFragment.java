package com.study.wanandroid.base;

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
import androidx.viewbinding.ViewBinding;

public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {
    protected VB binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = getViewBinding(inflater, container);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initViews();
        initObservers();
        initData();
    }


    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    /**
     * 获取 binding 对象
     * @param inflater
     * @param container
     * @return
     */
    protected abstract VB getViewBinding(LayoutInflater inflater, ViewGroup container);

    /**
     * 初始化 viewModel
     */
    protected void initViewModel(){};


    /**
     * 初始化视图、组件
     */
    protected abstract void initViews();


    /**
     * 初始化 viewModel 观察
     */
    protected void initObservers(){};

    /**
     * 获取数据
     */
    protected void initData(){};

}
