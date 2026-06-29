package com.study.wanandroid.ui.me.score;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.study.wanandroid.MyApplication;
import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseActivity;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.databinding.ActivityScoreBinding;
import com.study.wanandroid.utils.Constant;
import com.study.wanandroid.utils.ToastUtil;

public class ScoreActivity extends BaseActivity<ActivityScoreBinding> {
    private ScoreViewModel viewModel;
    private ScoreAdapter scoreAdapter;

    @Override
    protected void initViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 状态栏颜色
        MyApplication.updateStatusBar(this, getColor(R.color.main_color));
        MyApplication.updateStatusBarTextColor(this, false);

        // 积分头部：从 Intent 读取积分值并动画展示
        int score = getIntent() != null ? getIntent().getIntExtra(Constant.Data_KEY, 0) : 0;
        MyApplication.startAnim(binding.tvScoreValue, score, 2000);

        // 手动控制积分头部淡出速度：multiplier 越大消失越快
        binding.appBarLayout.addOnOffsetChangedListener(
                new AppBarLayout.OnOffsetChangedListener() {
                    private final float FADE_SPEED = 3.0f; // 可调整：1.0=最慢(默认), 3.0=快速淡化

                    @Override
                    public void onOffsetChanged(@NonNull AppBarLayout appBarLayout, int verticalOffset) {
                        int totalRange = appBarLayout.getTotalScrollRange();
                        float progress = Math.abs(verticalOffset) * 1.0f / totalRange;
                        binding.layoutScoreHeader.setAlpha(1 - Math.min(progress * FADE_SPEED, 1f));
                    }
                });

        // Toolbar
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // 列表加载 & 状态
        binding.stateLayout.setListener(this::onRetry);
        binding.layoutSmart.setOnLoadMoreListener(refreshLayout -> viewModel.getScoreList());
        scoreAdapter = new ScoreAdapter();
        binding.recycleScore.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recycleScore.setAdapter(scoreAdapter);
    }

    private void onRetry() {
        viewModel.getScoreList();
    }

    @Override
    protected ActivityScoreBinding getViewBinding() {
        return ActivityScoreBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ScoreViewModel.class);
        viewModel.getStatus().observe(this, resource -> {
            UIState state = resource.getState();
            if (binding.layoutSmart.isLoading()) {  // 组件网络状态
                if (state == UIState.LOADING) return;
                if (state == UIState.EMPTY) {   // 没有更多数据
                    binding.layoutSmart.finishLoadMoreWithNoMoreData();
                } else if (state == UIState.SUCCESS){ // 加载成功
                    binding.layoutSmart.finishLoadMore(true);
                } else {    // 加载失败
                    binding.layoutSmart.finishLoadMore(false);
                    ToastUtil.show(this, resource.getMsg());
                }
            } else {
                binding.stateLayout.switchView(state);
            }
        });
        viewModel.getScores().observe(this, scores -> {
            scoreAdapter.submitList(scores);
        });
    }


    @Override
    protected void initData() {
        viewModel.getScoreList();
    }



}