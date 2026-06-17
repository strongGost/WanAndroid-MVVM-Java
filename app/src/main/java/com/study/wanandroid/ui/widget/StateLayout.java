package com.study.wanandroid.ui.widget;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.study.wanandroid.R;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.utils.LogUtil;
import com.study.wanandroid.utils.NetWorkUtil;
import com.study.wanandroid.utils.ToastUtil;

import io.reactivex.rxjava3.functions.Action;

/**
 * 网络状态布局：根据网络状态显示相应布局
 */
public class StateLayout extends FrameLayout {

    private View loading, error, empty, content;
    /* Loading 防抖机制：如果超过了 指定时间还没完成（读本地缓存or网络加载），就显示 Loading 视图弹出来*/
    private final static int LOADING_DELAY = 300; // ms
    private Runnable showLoadingRunnable; /* 延迟执行线程 */

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
            LogUtil.error(StateLayout.class, "StateLayout must have only one child");
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
//        showLoading(); /* 若默认显示 Loading，则读取本地缓存时有可能会闪烁 Loading */
        content.setVisibility(VISIBLE);
        loading.setVisibility(GONE);
        error.setVisibility(GONE);
        empty.setVisibility(GONE);
    }


    /**
     * 根据网络状态显示相应布局
     */
    public void switchView(UIState state) {
        /* 若在 Loading 的过程中 执行完毕，需清除该 Loading 线程 */
        if (showLoadingRunnable != null) {
            removeCallbacks(showLoadingRunnable);   // 默认调用主线程的Handler
        }
        switch (state) {
            case LOADING: {
                showLoadingDelayed();
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


    /**
     * 延迟指定 ms 后显示 Loading 布局
     */
    private void showLoadingDelayed() {
        if (loading.getVisibility() == VISIBLE) return;
        showLoadingRunnable = () -> {
            content.setVisibility(GONE);
            error.setVisibility(GONE);
            empty.setVisibility(GONE);
            loading.setVisibility(VISIBLE);
        };
        postDelayed(showLoadingRunnable, LOADING_DELAY);
    }

    private void showEmpty() {
        if (empty.getVisibility() == VISIBLE) return;
        content.setVisibility(GONE);
        error.setVisibility(GONE);
        loading.setVisibility(GONE);
        empty.setVisibility(VISIBLE);
    }

    private void showError() {
        if (error.getVisibility() == VISIBLE) return;
        content.setVisibility(GONE);
        loading.setVisibility(GONE);
        empty.setVisibility(GONE);
        error.setVisibility(VISIBLE);
    }

    private void showContent() {
        if (content.getVisibility() == VISIBLE) return;
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
