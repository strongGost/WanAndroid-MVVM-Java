package com.study.wanandroid.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.QuickAdapterHelper;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.study.wanandroid.MyApplication;
import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseFragment;
import com.study.wanandroid.data.model.IBaseArticle;
import com.study.wanandroid.ui.me.college.CollectViewModel;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.databinding.FragmentHomeBinding;
import com.study.wanandroid.ui.WebViewActivity;
import com.study.wanandroid.ui.login.LoginActivity;
import com.study.wanandroid.utils.Constant;
import com.study.wanandroid.utils.LogUtil;
import com.study.wanandroid.utils.NetWorkUtil;
import com.study.wanandroid.utils.ToastUtil;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> {
    private HomeViewModel viewModel;
    private ImageBannerAdapter bannerAdapter;
    private ArticleAdapter<ArticleBean> articleAdapter;
    private QuickAdapterHelper adapterHelper;
    private CollectViewModel collectViewModel;


    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        collectViewModel = new ViewModelProvider(this).get(CollectViewModel.class);
    }

    @Override
    protected void initObservers() {
        viewModel.getPageState().observe(getViewLifecycleOwner(), resource -> {
            binding.stateLayout.switchView(resource.getState());
        });
        viewModel.getRefreshState().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getState() != UIState.LOADING) {
                binding.refreshLayout.finishRefresh(resource.getState() != UIState.ERROR);
            }
        });
        viewModel.getLoadMoreState().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getState() != UIState.LOADING) {
                if (resource.getState() == UIState.EMPTY)
                    binding.refreshLayout.finishLoadMoreWithNoMoreData();
                else
                    binding.refreshLayout.finishLoadMore(resource.getState() != UIState.ERROR);
            }
        });
        viewModel.getBannerLiveData().observe(getViewLifecycleOwner(), banners -> {
            if (banners != null && !banners.isEmpty()) {
                bannerAdapter.setItem(banners);
            }
            LogUtil.debug(HomeFragment.class, "banners is null" + (banners == null || banners.isEmpty()));
        });
        viewModel.getArticleLiveData().observe(getViewLifecycleOwner(), articles -> {
            if (articles != null && !articles.isEmpty()) {
                articleAdapter.submitList(articles);
            }
        });
        collectViewModel.getStatus().observe(getViewLifecycleOwner(), resource -> {
            //TODO: 每次进入都会弹出上次操作的提示
            ToastUtil.show(requireContext(), resource.getMsg());
            if (resource.getState() == UIState.NEED_LOGIN) {    // 需要登录
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
            }

        });
    }

    @Override
    protected void initData() {
        if (NetWorkUtil.isOnline(requireContext())) {
            viewModel.firstLoad();  /* 首次加载 */
        }
    }

    @Override
    protected void initViews() {
        initRecycleView();
        initRefreshLayout();
        setupStatusBar();
        binding.stateLayout.setListener(() -> viewModel.onRetry());
    }


    /**
     * 监听滑动状态改变状态栏
     */
    private void setupStatusBar() {
        binding.recycleHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager == null) return;

                // 获取当前屏幕 第一个可见项
                int firstPosition = manager.findFirstVisibleItemPosition();
                View firstView = manager.findViewByPosition(firstPosition);

                if (firstPosition == 0 && firstView != null) { // 当前屏幕第一个显示的是 Banner View
                    // Banner 可见，状态栏透明
                    MyApplication.updateStatusBar(requireActivity(), Color.TRANSPARENT);
                } else {
                    // Banner 不可见，状态栏显示白色
                    MyApplication.updateStatusBar(requireActivity(), Color.WHITE);
                }
            }
        });
    }



    /**
     * 为 RecyclerView 设置 adapter，同时组合 Banner + 列表
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecycleView() {
        bannerAdapter = new ImageBannerAdapter(getViewLifecycleOwner());
        articleAdapter = new ArticleAdapter<>();
        // item 点击事件
        articleAdapter.setOnItemClickListener((adapter, view, pos) -> {
            ArticleBean item = adapter.getItem(pos);
            Intent intent = new Intent(requireContext(), WebViewActivity.class)
                    .putExtra(Constant.EXTRA_URL, item.getDisplayLink());
            startActivity(intent);
        });
        // 收藏/取消收藏 事件
        articleAdapter.addOnItemChildClickListener(R.id.favorite, ((adapter, view, pos) -> {
            ArticleBean bean = adapter.getItem(pos);
            collectViewModel.collectAndUnCollect(bean.isCollected(), bean);
        }));
        // 注意, 执行时 ArticleAdapter 可能为 null
        adapterHelper = new QuickAdapterHelper.Builder(articleAdapter)
                .build();
        adapterHelper.addBeforeAdapter(bannerAdapter);
        binding.recycleHome.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.recycleHome.setAdapter(adapterHelper.getAdapter());
    }


    /**
     * 初始化加载布局
     */
    private void initRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {    /* 下滑刷新 */
                if (NetWorkUtil.isOnline(requireContext())) {
                    viewModel.refresh();
                }
            }
        });
        binding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {  /* 上滑加载更多 */
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                if (NetWorkUtil.isOnline(requireContext())) {
                    viewModel.loadMore();
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    protected FragmentHomeBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }


    @Override
    public void onPause() {
        super.onPause();
        MyApplication.updateStatusBar(requireActivity(), Color.TRANSPARENT);
    }
}
