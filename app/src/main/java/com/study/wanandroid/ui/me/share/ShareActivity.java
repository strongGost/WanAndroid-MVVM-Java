package com.study.wanandroid.ui.me.share;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseActivity;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.IBaseArticle;
import com.study.wanandroid.data.remote.Event;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.databinding.ActivityShareBinding;
import com.study.wanandroid.ui.WebViewActivity;
import com.study.wanandroid.ui.home.ArticleAdapter;
import com.study.wanandroid.ui.login.LoginActivity;
import com.study.wanandroid.ui.me.college.CollectViewModel;
import com.study.wanandroid.utils.Constant;
import com.study.wanandroid.utils.ToastUtil;

import java.util.List;

public class ShareActivity extends BaseActivity<ActivityShareBinding> {


    private ShareViewModel viewModel;
    private ArticleAdapter<ArticleBean> adapter;
    private CollectViewModel collectViewModel;

    @Override
    protected void initViews() {
        binding.toolbar.ivBack.setOnClickListener(v -> finish());
        binding.toolbar.ivAdd.setOnClickListener(v-> {
            Intent intent = new Intent(this, AddShareActivity.class);
            startActivity(intent);
        });

        binding.stateLayout.setListener(() -> viewModel.onRetry());

        binding.smart.setOnRefreshListener(refreshLayout -> viewModel.refresh());
        binding.smart.setOnLoadMoreListener(loadMoreLayout -> viewModel.loadMore());

        adapter = new ArticleAdapter<>();
        // 进入文章详情页面
        adapter.setOnItemClickListener(((baseQuickAdapter, view, i) -> {
            ArticleBean item = adapter.getItem(i);
            Intent intent = new Intent(this, WebViewActivity.class)
                    .putExtra(Constant.EXTRA_URL, item.getDisplayLink());
            startActivity(intent);
        }));
        // 收藏、取消收藏
        adapter.addOnItemChildClickListener(R.id.favorite, ((baseQuickAdapter, view, i) -> {
            ArticleBean item = adapter.getItem(i);
            collectViewModel.collectAndUnCollect(item.isCollected(), item);
        }));
        binding.recycleList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recycleList.setAdapter(adapter);

        // 右滑删除
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.RED);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(48f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textOffset = (fm.descent + fm.ascent) / 2f;

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getBindingAdapterPosition();   // 该条目在 adapter 中的位置
                ArticleBean item = adapter.getItem(pos);
                // 乐观删除：先从 UI 移除
                adapter.removeAt(pos);
                viewModel.deleteShare(item.getId());
            }

        });
        itemTouchHelper.attachToRecyclerView(binding.recycleList);
    }

    @Override
    protected ActivityShareBinding getViewBinding() {
        return ActivityShareBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ShareViewModel.class);
        collectViewModel = new ViewModelProvider(this).get(CollectViewModel.class);
        initObserver();
    }

    private void initObserver() {
        viewModel.getPageStatus().observe(this, resource -> {
            if (resource != null)  binding.stateLayout.switchView(resource.getState());
        });
        viewModel.getRefreshStatus().observe(this, resource -> {
            if (resource != null) {
                UIState state = resource.getState();
                if (state != UIState.LOADING) binding.smart.finishRefresh(state != UIState.ERROR);
            }
        });
        viewModel.getLoadMoreStatus().observe(this, resource -> {
            if (resource != null) {
                UIState state = resource.getState();
                if (state == UIState.EMPTY) binding.smart.finishLoadMoreWithNoMoreData();
                else if (state != UIState.LOADING) binding.smart.finishLoadMore(state != UIState.ERROR);
            }
        });
        viewModel.getArticles().observe(this, data -> {
            adapter.submitList(data);
        });

        collectViewModel.getStatus().observe(this, event -> {
            Resource resource = event.getContentIfNotHandled();
            if (resource == null) return;
            ToastUtil.show(this, resource.getMsg());
            if (resource.getState() == UIState.NEED_LOGIN) {    // 需要登录
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        });
        collectViewModel.getCollectedChanged().observe(this, article -> {
            if (article != null) {
                List<ArticleBean> items = adapter.getItems();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getUniqueId().equals(article.getUniqueId())) {
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        });
        viewModel.getDeleteStatus().observe(this, event -> {
            Resource resource = event.getContentIfNotHandled();
            if (resource == null) return;
            ToastUtil.show(this, resource.getMsg());
            if (resource.getState() == UIState.NEED_LOGIN) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        viewModel.firstLoad();
    }
}