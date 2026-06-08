package com.study.wanandroid.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewbinding.ViewBinding;

import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseActivity;
import com.study.wanandroid.databinding.ActivityWebViewBinding;
import com.study.wanandroid.utils.Constant;

/**
 * 展示文章
 */
public class WebViewActivity extends BaseActivity<ActivityWebViewBinding> {

    private String url;     /* 文章 url */

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initViews() {
        // 设置边距
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets)-> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WebSettings settings = binding.webView.getSettings();
        settings.setJavaScriptEnabled(true); // 开启，防止 某些页面打不开
        settings.setDomStorageEnabled(true); // 开启 DOM 存储，防止部分 H5 无法加载
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        // 设置 WebViewClient，防止跳转到外部浏览器
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }


    @Override
    protected void initData() {
        if (getIntent() != null) {
            url = getIntent().getStringExtra(Constant.EXTRA_URL);
            binding.webView.loadUrl(url);
        }
    }

    @Override
    protected ActivityWebViewBinding getViewBinding() {
        return ActivityWebViewBinding.inflate(getLayoutInflater());
    }

    /**
     * 避免 无法销毁 webView ，而引起的内存泄漏
     */
    @Override
    protected void onDestroy() {
        if (binding != null && binding.webView != null) {
            binding.webView.stopLoading();
            binding.webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            binding.webView.clearHistory();
            binding.webView.destroy();
        }
        super.onDestroy();
    }
}