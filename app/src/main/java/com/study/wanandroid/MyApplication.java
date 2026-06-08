package com.study.wanandroid;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;


public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }


    /**
     * 更新 指定页面 状态栏颜色
     * @param activity
     * @param color
     */
    public static void updateStatusBar(Activity activity, int color) {
        if (activity == null) return;
        Window window = activity.getWindow();
        window.setStatusBarColor(color);
    }


    /**
     * 更新 指定页面 状态栏 文字颜色
     * @param activity
     * @param isDarkText  状态栏文字是否启动深色, true: 是
     */
    public static void updateStatusBarTextColor(Activity activity, boolean  isDarkText) {
        if (activity == null) return;

        Window window = activity.getWindow();
        View decorView = window.getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {   //  Android 11 (API 30) 及以上版本
            // 使用 WindowInsetsControllerCompat 来控制系统 UI 外观
            WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window, decorView);
            insetsController.setAppearanceLightStatusBars(isDarkText);
        } else {    // android 6.0 以上，（此项目最低支持8.0）
            int systemUiVisibility = decorView.getSystemUiVisibility();
            if (isDarkText) {
                // 深色文字：添加 LIGHT_STATUS_BAR 标志（使用 |= 保留其他原有标志）
                systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                // 白色文字：移除 LIGHT_STATUS_BAR 标志（使用 &= ~ 保留其他原有标志）
                systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(systemUiVisibility);
        }
    }

    /**
     * 积分显示采用动画滚动
     * @param textView
     * @param targetValue
     * @param duration
     */
    public static void startAnim(TextView textView, int targetValue, long duration) {
        ValueAnimator animator = ValueAnimator.ofInt(0, targetValue);
        animator.setDuration(duration);
        animator.addUpdateListener(animation ->  {
            int value = (int) animation.getAnimatedValue();
            textView.setText(String.format("%d", value));

        });
        animator.start();
    }
}
