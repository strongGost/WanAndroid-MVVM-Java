package com.study.wanandroid.base;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.study.wanandroid.utils.NetWorkUtil;
import com.study.wanandroid.utils.ToastUtil;

public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {
    protected VB binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = getViewBinding();
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);

        if (!NetWorkUtil.isOnline(this))
            ToastUtil.show(this, "当前网络不可用");

        // 初始化
        initViewModel();
        initViews();
        initData();
    }

    /**
     * 初始化视图、组件
     */
    protected abstract void initViews();

    /**
     * 初始化 viewModel
     */
    protected void initViewModel(){};

    /**
     * 初始化数据
     */
    protected void initData(){};


    /**
     * 获取 xml 的 binding 布局 Id
     * @return xml的
     */
    protected abstract VB getViewBinding();

    @Override
    protected void onDestroy() {
        if (binding != null) {
            binding = null;
        }
        super.onDestroy();
    }
}
