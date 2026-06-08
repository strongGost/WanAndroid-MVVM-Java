package com.study.wanandroid.ui.square.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.study.wanandroid.base.BaseFragment;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.GuideBean;
import com.study.wanandroid.databinding.FragmentSystemBinding;
import com.study.wanandroid.ui.WebViewActivity;
import com.study.wanandroid.ui.square.adapter.LabelClassfiyAdapter;
import com.study.wanandroid.utils.Constant;

public class GuideFragment extends BaseFragment<FragmentSystemBinding> implements LabelClassfiyAdapter.OnLabelClickListener<ArticleBean> {

    private LabelClassfiyAdapter<GuideBean> adapter;
    private GuideViewModel viewModel;

    public static GuideFragment newInstance() {
        Bundle args = new Bundle();
        GuideFragment fragment = new GuideFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(GuideViewModel.class);
        viewModel.getNetworkStatus().observe(getViewLifecycleOwner(), resource -> {
            binding.stateLayout.switchView(resource.getState());
        });
        viewModel.getGuides().observe(getViewLifecycleOwner(), guides -> {
            adapter.setData(guides);
        });
    }

    @Override
    protected void initViews() {
        binding.stateLayout.setListener(() -> viewModel.onRetry());
        binding.recycleContent.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LabelClassfiyAdapter<GuideBean>();
        adapter.setListener(this);  // 文本点击
        binding.recycleContent.setAdapter(adapter);
    }

    @Override
    protected FragmentSystemBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSystemBinding.inflate(inflater, container, false);
    }

    @Override
    public void onClickLabel(ArticleBean bean, int curPos, int parentPos) {
        Intent intent = new Intent(requireContext(), WebViewActivity.class)
            .putExtra(Constant.EXTRA_URL, bean.getLink());
            startActivity(intent);
    }


}
