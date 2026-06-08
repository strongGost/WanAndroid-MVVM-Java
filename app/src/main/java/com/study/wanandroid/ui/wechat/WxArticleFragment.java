package com.study.wanandroid.ui.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.study.wanandroid.base.BaseFragment;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.databinding.FragmentWxArticleBinding;
import com.study.wanandroid.ui.WebViewActivity;
import com.study.wanandroid.ui.wechat.adapter.WxArticleAdapter;
import com.study.wanandroid.utils.Constant;

public class WxArticleFragment extends BaseFragment<FragmentWxArticleBinding> implements BaseQuickAdapter.OnItemClickListener<ArticleBean> {

    private int id; // 当前分类 id
    private WxArticleViewModel viewModel;
    private WxArticleAdapter adapter;

    public static WxArticleFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(Constant.EXTRA_CID, id);
        WxArticleFragment fragment = new WxArticleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentWxArticleBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWxArticleBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initViews() {
        binding.stateLayout.setListener(() -> viewModel.onRetry());
        initRecycleView();
        initSmartRefresh();
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            id = getArguments().getInt(Constant.EXTRA_CID);
            viewModel.setWxId(id);
            viewModel.firstLoad();
        }
    }

    @Override
    protected void initObservers() {
        viewModel.get_first_network_state().observe(getViewLifecycleOwner(), resource -> {
            binding.stateLayout.switchView(resource.getState());
        });
        viewModel.getLoad_more_network_state().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getState() != UIState.LOADING) {
                if (resource.getState() == UIState.EMPTY)
                    binding.smartRefresh.finishLoadMoreWithNoMoreData();
                else
                    binding.smartRefresh.finishLoadMore(resource.getState() != UIState.ERROR);
            }
        });
        viewModel.getHistoryList().observe(getViewLifecycleOwner(), data -> {
            adapter.submitList(data);
        });
    }

    private void initSmartRefresh() {
        binding.smartRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                viewModel.loadMore();
            }
        });
    }

    private void initRecycleView() {
        adapter = new WxArticleAdapter();
        adapter.setOnItemClickListener(this);
        binding.recycleWxList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycleWxList.setAdapter(adapter);
        binding.recycleWxList.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }


    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(WxArticleViewModel.class);
    }



    @Override
    public void onClick(@NonNull BaseQuickAdapter<ArticleBean, ?> baseQuickAdapter, @NonNull View view, int i) {
        ArticleBean item = baseQuickAdapter.getItem(i);
        Intent intent = new Intent(requireContext(), WebViewActivity.class)
                .putExtra(Constant.EXTRA_URL, item.getLink());
        startActivity(intent);
    }

}
