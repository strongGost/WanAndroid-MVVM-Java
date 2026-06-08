package com.study.wanandroid.ui.me.score;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter4.QuickAdapterHelper;
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
        SingleItemViewAdapter<Integer> singleItemAdapter = new SingleItemViewAdapter<>();
        if (getIntent() != null) {
            singleItemAdapter.setItem(getIntent().getIntExtra(Constant.Data_KEY, 0));
        }
        binding.viewHead.tvToolTitle.setText(R.string.my_score);
        binding.viewHead.imageView.setOnClickListener(v -> finish());
        binding.stateLayout.setListener(this::onRetry);
        binding.layoutSmart.setOnLoadMoreListener(refreshLayout -> {
            viewModel.getScoreList();
        });
        scoreAdapter = new ScoreAdapter();
        QuickAdapterHelper adapterHelper = new QuickAdapterHelper.Builder(scoreAdapter)
                .build();
        adapterHelper.addBeforeAdapter(singleItemAdapter);
        binding.recycleScore.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recycleScore.setAdapter(adapterHelper.getAdapter());
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