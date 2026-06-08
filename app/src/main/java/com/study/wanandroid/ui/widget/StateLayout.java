package com.study.wanandroid.ui.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.study.wanandroid.R;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.utils.NetWorkUtil;
import com.study.wanandroid.utils.ToastUtil;

/**
 * 网络状态布局：根据网络状态显示相应布局
 */
public class StateLayout extends FrameLayout {

    private View loading, error, empty, content;

    public StateLayout(@NonNull Context context) {
        this(context, null);
    }

    public StateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 当布局加载完成时调用
     * 获取 View 对象，并初始化显示状态
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (getChildCount() == 1) {
            content = getChildAt(0);    // 内容布局
        } else {
            throw new IllegalStateException("StateLayout must have only one child");
        }
        loading = inflater.inflate(R.layout.view_loading, this, false);
        error = inflater.inflate(R.layout.view_error, this, false);
        empty = inflater.inflate(R.layout.view_empty, this, false);
        error.setOnClickListener(view -> {
            if (listener != null) {
                listener.onRetry();
            }
        });
        addView(loading);
        addView(error);
        addView(empty);
        showLoading();
    }


    /**
     * 根据网络状态显示相应布局
     */
    public void switchView(UIState state) {
        switch (state) {
            case LOADING: {
                showLoading();
                break;
            }
            case SUCCESS: {
                showContent();
                break;
            }
            case ERROR: {
                showError();
                break;
            }
            case EMPTY: {
                showEmpty();
                break;
            }
        }
    }

    private void showLoading() {
        content.setVisibility(GONE);
        error.setVisibility(GONE);
        empty.setVisibility(GONE);
        loading.setVisibility(VISIBLE);
    }

    private void showEmpty() {
        content.setVisibility(GONE);
        error.setVisibility(GONE);
        loading.setVisibility(GONE);
        empty.setVisibility(VISIBLE);
    }

    private void showError() {
        content.setVisibility(GONE);
        loading.setVisibility(GONE);
        empty.setVisibility(GONE);
        error.setVisibility(VISIBLE);
    }

    private void showContent() {
        loading.setVisibility(GONE);
        empty.setVisibility(GONE);
        error.setVisibility(GONE);
        content.setVisibility(VISIBLE);
    }

    public OnRetryListener listener;

    public void setListener(OnRetryListener listener) {
        this.listener = listener;
    }


    public interface OnRetryListener {
        void onRetry();
    }

}
