package com.study.wanandroid.ui.system;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseFragment;
import com.study.wanandroid.data.model.IBaseArticle;
import com.study.wanandroid.ui.me.college.CollectViewModel;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.databinding.FragmentHomeBinding;
import com.study.wanandroid.ui.WebViewActivity;
import com.study.wanandroid.ui.home.ArticleAdapter;
import com.study.wanandroid.ui.login.LoginActivity;
import com.study.wanandroid.utils.Constant;
import com.study.wanandroid.utils.NetWorkUtil;
import com.study.wanandroid.utils.ToastUtil;

/** * 二级分类
 */
public class ChildrenFragment extends BaseFragment<FragmentHomeBinding> {
    private SystemViewModel systemViewModel;
    private CollectViewModel collectViewModel;
    private ArticleAdapter<ArticleBean> articleAdapter;

    public static ChildrenFragment newInstance(SystemBean bean) {
        Bundle args = new Bundle();
        args.putParcelable(Constant.Data_KEY, bean);
        ChildrenFragment fragment = new ChildrenFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected FragmentHomeBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void initViews() {
        binding.stateLayout.setListener(() -> systemViewModel.loadMore());
        // 设置 adapter
        articleAdapter = new ArticleAdapter<>();
        binding.recycleHome.setAdapter(articleAdapter);
        binding.recycleHome.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        // item 点击事件
        articleAdapter.setOnItemClickListener((adapter, view, pos) -> {
            IBaseArticle item = adapter.getItem(pos);
            Intent intent = new Intent(requireContext(), WebViewActivity.class)
                    .putExtra(Constant.EXTRA_URL, item.getDisplayLink());
            startActivity(intent);
        });

        // 收藏/取消收藏 事件
        articleAdapter.addOnItemChildClickListener(R.id.favorite, ((adapter, view, pos) -> {
            IBaseArticle bean = adapter.getItem(pos);
            collectViewModel.collectAndUnCollect(bean.isCollected(), bean);
        }));

        // 刷新、加载更多监听事件
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            if (NetWorkUtil.isOnline(requireContext())) {
                systemViewModel.refresh();
            }
        });
        binding.refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            if (NetWorkUtil.isOnline(requireContext())) {
                systemViewModel.loadMore();
            }
        });
    }


    @Override
    protected void initViewModel() {
        systemViewModel = new ViewModelProvider(this).get(SystemViewModel.class);
        collectViewModel = new ViewModelProvider(this).get(CollectViewModel.class);
    }

    @Override
    protected void initObservers() {
        collectViewModel.getStatus().observe(getViewLifecycleOwner(), resource -> {
            ToastUtil.show(requireContext(), resource.getMsg());
            if (resource.getState() == UIState.NEED_LOGIN) {    // 需要登录
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        systemViewModel.getList().observe(getViewLifecycleOwner(), data -> articleAdapter.submitList(data));
        systemViewModel.getStatus().observe(getViewLifecycleOwner(), resource -> {
            UIState state = resource.getState();
            if(binding.refreshLayout.isRefreshing()) {
                if (state != UIState.LOADING)
                    binding.refreshLayout.finishRefresh(state != UIState.ERROR);
            } else if (binding.refreshLayout.isLoading()){
                if (state != UIState.LOADING) {
                    if (state == UIState.EMPTY)
                        binding.refreshLayout.finishLoadMoreWithNoMoreData();
                    else
                        binding.refreshLayout.finishLoadMore(state != UIState.ERROR);
                }
            } else {
                binding.stateLayout.switchView(state);
            }
        });
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            SystemBean bean;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bean = getArguments().getParcelable(Constant.Data_KEY, SystemBean.class);
            } else {
                bean = getArguments().getParcelable(Constant.Data_KEY);
            }
            if (bean != null) {
                systemViewModel.setCid(bean.getId());
                systemViewModel.loadMore();
            }
        }

    }

}
