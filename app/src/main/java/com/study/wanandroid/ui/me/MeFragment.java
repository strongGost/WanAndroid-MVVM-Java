package com.study.wanandroid.ui.me;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.study.wanandroid.MyApplication;
import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseFragment;
import com.study.wanandroid.data.model.UserBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.databinding.FragmentMeBinding;
import com.study.wanandroid.databinding.FragmentSystemBinding;
import com.study.wanandroid.ui.login.LoginActivity;
import com.study.wanandroid.ui.me.college.CollegeActivity;
import com.study.wanandroid.ui.me.score.ScoreActivity;
import com.study.wanandroid.utils.Constant;
import com.study.wanandroid.utils.SharePreferenceUtil;
import com.study.wanandroid.utils.ToastUtil;

public class MeFragment extends BaseFragment<FragmentMeBinding> {

    private MeViewModel viewModel;
    private int score;
    private AlertDialog loadingDialog;

    @Override
    protected FragmentMeBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentMeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initViews() {
        // 状态栏文字设为浅色
        MyApplication.updateStatusBarTextColor(requireActivity(), false);
        binding.headerLayout.tvAccount.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
        });

        binding.ivIcon.setOnClickListener(v -> goToActivity(0));
        binding.tvMyScore.setOnClickListener(v -> goToActivity(0));
        binding.tvNowScore.setOnClickListener(v -> goToActivity(0));
        binding.ivRight.setOnClickListener(v -> goToActivity(0));

        binding.ivIcon2.setOnClickListener(v -> goToActivity(1));
        binding.tvMyCollege.setOnClickListener(v -> goToActivity(1));
        binding.ivRight2.setOnClickListener(v -> goToActivity(1));

        binding.ivIcon3.setOnClickListener(v -> goToActivity(2));
        binding.tvShare.setOnClickListener(v -> goToActivity(2));
        binding.ivShare.setOnClickListener(v -> goToActivity(2));

        binding.tvCacheSize.setOnClickListener(v -> showDialog());
        binding.ivGithub.setOnClickListener(v -> showDialog());

        // 退出登录
        binding.tvLogout.setOnClickListener(v -> viewModel.logOut());
    }


    /**
     * 去积分页面
     */
    private void goToActivity(int pos) {
        // 未登录状态：跳转到登录页面
        if (SharePreferenceUtil.getObj(Constant.ME_INFO, UserBean.class) == null) { //
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            return;
        }
        Intent intent = null;
        switch (pos) {
            case 0: // 积分页面
                intent = new Intent(requireContext(), ScoreActivity.class)
                        .putExtra(Constant.Data_KEY, score);
                break;
            case 1: // 收藏页面
                intent = new Intent(requireContext(), CollegeActivity.class);
                break;
            case 2: // 分享页面
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }


    /**
     * 弹出弹窗，询问是否清理缓存
     */
    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.clear_cache))
                .setMessage(getString(R.string.clear_cache_content))
                .setPositiveButton(getString(R.string.yes), (d, which) -> {
                    // 显示 Loading、删除
                    showLoadingDialog();
                    viewModel.clearCache();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }


    /**
     * 显示 删除中 loading...
     */
    private void showLoadingDialog() {
        LinearLayout loadingView = new LinearLayout(requireContext()); // 动态创建加载布局
        loadingView.setOrientation(LinearLayout.VERTICAL);
        loadingView.setPadding(16, 16, 16, 16);
        loadingView.setGravity(Gravity.CENTER_VERTICAL);
        loadingView.addView(new ProgressBar(requireContext()));
        TextView textView = new TextView(requireContext());
        textView.setText(getString(R.string.clear_loading));
        loadingView.addView(textView);

        loadingDialog = new AlertDialog.Builder(requireContext())
                .setView(loadingView)
                .setCancelable(false)   // 不可取消
                .show();
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MeViewModel.class);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void initObservers() {
        viewModel.getState().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getState() == UIState.ERROR) {
                ToastUtil.show(requireContext(), resource.getMsg());
            }
        });
        viewModel.getMeInfo().observe(getViewLifecycleOwner(), data -> {
            if(data == null) return;
            score = data.getUserInfo().getCoinCount();
            binding.headerLayout.tvAccount.setText(data.getUserInfo().getUsername());
            binding.headerLayout.tvId.setText(String.format("ID.%d", data.getUserInfo().getId()));
            binding.headerLayout.tvLevel.setText("lv." + data.getCoinInfo().getLevel());
            binding.tvNowScore.setText("当前积分：" + data.getUserInfo().getCoinCount());
            if (data.getCoinInfo().getUsername() != null && !data.getCoinInfo().getUsername().isEmpty()) {
                binding.headerLayout.tvLevel.setVisibility(View.VISIBLE);
                binding.headerLayout.tvId.setVisibility(View.VISIBLE);
            } else {
                binding.headerLayout.tvLevel.setVisibility(View.GONE);
                binding.headerLayout.tvId.setVisibility(View.GONE);
            }
        });
        viewModel.getCacheSize().observe(getViewLifecycleOwner(), size -> {
            binding.tvCacheSize.setText(size);
        });
        viewModel.getClearCacheSuccess().observe(getViewLifecycleOwner(), event -> {
            Boolean success = event.getContentIfNotHandled();
            if (success == null) return;

            dismissLoadingDialog(); // 销毁 “删除对话框”

            ToastUtil.show(requireContext(), success ? "清理缓存成功" : "部分缓存清理失败");
            viewModel.calculateCacheSize();
        });
    }

    /**
     * 关闭加载中 弹窗
     */
    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void initData() {
        viewModel.getMeData();
        viewModel.calculateCacheSize();
    }

}