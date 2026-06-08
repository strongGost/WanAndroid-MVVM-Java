package com.study.wanandroid.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static final int LEVEL = 2;
    private static final int DEBUG_LEVEL = 1;
    private static final int APP_LEVEL = 2;

    public static void show(Context ctx, String msg) {
        if (APP_LEVEL >= LEVEL) {
            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
        }
    }
    public static void debug(Context ctx, String msg) {
        if (DEBUG_LEVEL >= LEVEL) {
            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
