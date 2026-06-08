package com.study.wanandroid.ui.login;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.study.wanandroid.base.BaseActivity;
import com.study.wanandroid.databinding.ActivityLoginBinding;

/**
 * 展示 “登录”、“注册” Fragment
 */
public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    @Override
    protected void initViews() {
        createFragment(LoginFragment.newInstance());
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置边距
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets)-> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * 创建 Fragment 并添加到 Activity 中
     * @param fragment  要创建的 fragment
     */
    private void createFragment(Fragment fragment) {
        if (!fragment.isAdded()) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(binding.fragmentContainer.getId(), fragment);
            transaction.commit();
        }
    }

    @Override
    protected ActivityLoginBinding getViewBinding() {
        return ActivityLoginBinding.inflate(getLayoutInflater());
    }
}