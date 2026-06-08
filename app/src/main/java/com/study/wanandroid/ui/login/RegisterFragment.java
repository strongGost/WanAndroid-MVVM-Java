package com.study.wanandroid.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.study.wanandroid.base.BaseFragment;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.databinding.FragmentRegisterBinding;
import com.study.wanandroid.utils.ToastUtil;

public class RegisterFragment extends BaseFragment<FragmentRegisterBinding> {
    private LRViewModel viewModel;

    public static RegisterFragment newInstance() {
        Bundle args = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentRegisterBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentRegisterBinding.inflate(inflater, container, false);
    }


    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(LRViewModel.class);
    }

    @Override
    protected void initObservers() {
        viewModel.getStates().observe(getViewLifecycleOwner(), resource -> {
            // 提示用户是否注册成功
            if (resource.getState() != UIState.LOADING) {
                ToastUtil.show(requireContext(), resource.getMsg());
                if (resource.getState() == UIState.SUCCESS) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    @Override
    protected void initViews() {
        binding.ivBack.setOnClickListener(v -> {
            // 销毁当前 Fragment（下次还要创建） or 移除当前 Fragment (选择）
//            requireActivity()
//                    .getSupportFragmentManager()
//                    .beginTransaction()
//                    .setReorderingAllowed(true)
//                    .hide(this);
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        binding.btnRegister.setOnClickListener(v -> {
            String username = binding.editUsername.getText().toString();
            String pwd = binding.editPwd.getText().toString();
            String repwd = binding.editRepwd.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(repwd)) {
                ToastUtil.show(requireContext(), "用户名、密码和二次确认密码不能为空");
                return;
            }
            if (!pwd.equals(repwd)) {
                ToastUtil.show(requireContext(), "两次输入的密码不一致");
                return;
            }
            viewModel.register(username, pwd, repwd);
        });
    }
}
