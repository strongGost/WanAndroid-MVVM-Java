package com.study.wanandroid.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseFragment;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.databinding.FragmentLoginBinding;
import com.study.wanandroid.utils.LogUtil;
import com.study.wanandroid.utils.ToastUtil;

public class LoginFragment extends BaseFragment<FragmentLoginBinding> {

    private LRViewModel viewModel;

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentLoginBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentLoginBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(LRViewModel.class);
    }

    @Override
    protected void initObservers() {
        viewModel.getStates().observe(getViewLifecycleOwner(), resource -> {
            // 提示用户是否登录成功
            if (resource.getState() != UIState.LOADING) {
                ToastUtil.show(requireContext(), resource.getMsg());
                if (resource.getState() == UIState.SUCCESS) {
                    // 回到上个页面
                    requireActivity().finish();
                }
            }
        });
    }

    @Override
    protected void initViews() {
        binding.tvRegister.setOnClickListener(v -> {
            showFragment();
        });
        binding.ivWrong.setOnClickListener(v -> {
            requireActivity().finish(); // 回到首页
        });
        binding.btnLogIn.setOnClickListener(v -> {
            // 登录
            String username = binding.editUsername.getText().toString();
            String pwd = binding.editPwd.getText().toString();
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
                ToastUtil.show(requireContext(), "用户名或密码不能为空");
                return;
            }
            LogUtil.debug(LoginFragment.class, username + " " + pwd);
            viewModel.login(username, pwd);
        });
    }


    /**
     * 隐藏当前内容，显示 register fragment
     */
    private void showFragment() {
        FragmentActivity activity = requireActivity();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .hide(this)     // this: 当前 Fragment 的实例
                .add(R.id.fragment_container, RegisterFragment.newInstance())
                .addToBackStack(null)   // 添加到回退栈，back 键
                .commit();
    }
}
