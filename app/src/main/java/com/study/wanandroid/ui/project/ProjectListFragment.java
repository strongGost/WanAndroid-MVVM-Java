package com.study.wanandroid.ui.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseFragment;
import com.study.wanandroid.data.model.IBaseArticle;
import com.study.wanandroid.ui.me.college.CollectViewModel;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.databinding.FragmentProjectListBinding;
import com.study.wanandroid.ui.WebViewActivity;
import com.study.wanandroid.ui.login.LoginActivity;
import com.study.wanandroid.ui.project.adaper.ProjectListAdapter;
import com.study.wanandroid.utils.Constant;
import com.study.wanandroid.utils.ToastUtil;

public class ProjectListFragment extends BaseFragment<FragmentProjectListBinding> implements BaseQuickAdapter.OnItemClickListener<ArticleBean> {

    private ProjectListViewModel viewModel;
    private ProjectListAdapter adapter;
    private CollectViewModel collectViewModel;

    public static ProjectListFragment newInstance(int cid) {
        Bundle args = new Bundle();
        args.putInt(Constant.EXTRA_CID, cid);
        ProjectListFragment fragment = new ProjectListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecycleView() {
        binding.recycleList.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ProjectListAdapter();
        adapter.setOnItemClickListener(this);
        binding.recycleList.setAdapter(adapter);
        binding.recycleList.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        // item 点击事件
        adapter.setOnItemClickListener((projectAdapter, view, pos) -> {
            IBaseArticle item = projectAdapter.getItem(pos);
            Intent intent = new Intent(requireContext(), WebViewActivity.class)
                    .putExtra(Constant.EXTRA_URL, item.getDisplayLink());
            startActivity(intent);
        });

        // 收藏/取消收藏 事件
        adapter.addOnItemChildClickListener(R.id.iv_favorite, ((projectAdapter, view, pos) -> {
            IBaseArticle bean = projectAdapter.getItem(pos);
            collectViewModel.collectAndUnCollect(bean.isCollected(), bean);
        }));

    }

    private void initSmartLayout() {
        binding.smartRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                viewModel.loadMore();
            }
        });
        binding.smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                viewModel.refresh();
            }
        });
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ProjectListViewModel.class);
        collectViewModel = new ViewModelProvider(this).get(CollectViewModel.class);
    }

    @Override
    protected void initViews() {
        initSmartLayout();
        initRecycleView();
        binding.stateLayout.setListener(() -> viewModel.onRetry());
    }


    @Override
    protected void initData() {
        if (getArguments() != null) {
            /* 当前项目分类 id */
            int cid = getArguments().getInt(Constant.EXTRA_CID);
            viewModel.firstLoad(cid);
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
        viewModel.getRefresh_more_network_state().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getState() != UIState.LOADING)
                binding.smartRefresh.finishRefresh(resource.getState() != UIState.ERROR);
        });
        viewModel.getProjectList().observe(getViewLifecycleOwner(), data -> {
            adapter.submitList(data);
        });
        collectViewModel.getStatus().observe(getViewLifecycleOwner(), resource -> {
            ToastUtil.show(requireContext(), resource.getMsg());
            if (resource.getState() == UIState.NEED_LOGIN) {    // 需要登录
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected FragmentProjectListBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return binding = FragmentProjectListBinding.inflate(inflater, container, false);
    }


    @Override
    public void onClick(@NonNull BaseQuickAdapter<ArticleBean, ?> baseQuickAdapter, @NonNull View view, int i) {
        ArticleBean item = adapter.getItem(i);
        Intent intent = new Intent(requireContext(), WebViewActivity.class)
                .putExtra(Constant.EXTRA_URL, item.getLink());
        startActivity(intent);
    }

}
