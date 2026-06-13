package com.study.wanandroid.ui.me.share;

import android.util.Patterns;
import android.view.View;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.study.wanandroid.base.BaseActivity;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.databinding.ActivityAddBinding;
import com.study.wanandroid.utils.ToastUtil;

import java.util.regex.Pattern;

public class AddShareActivity extends BaseActivity<ActivityAddBinding> {

    private AddShareViewModel viewModel;

    @Override
    protected void initViews() {
        binding.toolbar.ivBack.setOnClickListener(v -> finish());
        binding.toolbar.ivAdd.setVisibility(View.GONE); // 不显示添加按钮
        binding.btnShare.setOnClickListener(v-> shareArticle());
    }


    /**
     * 分享文章
     */
    private void shareArticle() {
        // 标题、分享链接 不能为null
        String title = binding.tvTitle.getText().toString().strip();
        String link = binding.tvLink.getText().toString().strip();

        if (title.isEmpty() || link.isEmpty()) {
            ToastUtil.show(this, "文章标题和文章链接不能为空");
        } else if (!Patterns.WEB_URL.matcher(link).matches()){
            ToastUtil.show(this, "网址不符合规范");
        } else {
            viewModel.share(title, link);
        }
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(AddShareViewModel.class);
        viewModel.getStatus().observe(this, resource -> {
            UIState state = resource.getState();
            if (state == UIState.LOADING) {   // 显示 Loading 条
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                ToastUtil.show(this, resource.getMsg());

                // 分享成功则，退出此页面
                if (state == UIState.SUCCESS) finish();
            }
        });
    }



    @Override
    protected ActivityAddBinding getViewBinding() {
        return ActivityAddBinding.inflate(getLayoutInflater());
    }
}