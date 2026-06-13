package com.study.wanandroid.utils;

import android.util.Log;

public class LogUtil {
    private static final int CUR_LEVEL = 6;
    private static final int INFO_LEVEL = 1;
    private static final int DEBUG_LEVEL = 2;
    private static final int WARN_LEVEL = 3;
    private static final int ERROR_LEVEL = 4;


    public static <T> void info (Class<T> c, String msg) {
        if (isShow(INFO_LEVEL)) {
            Log.i(c.getSimpleName(), msg);
        }
    }


    public static <T> void debug (Class<T> c, String msg) {
            if (isShow(DEBUG_LEVEL)) {
                Log.d(c.getSimpleName(), msg);
            }
        }


    public static <T> void warn (Class<T> c, String msg) {
            if (isShow(WARN_LEVEL)) {
                Log.i(c.getSimpleName(), msg);
            }
        }


    public static <T> void error (Class<T> c, String msg) {
            if (isShow(ERROR_LEVEL)) {
                Log.i(c.getSimpleName(), msg);
            }
        }


    private static boolean isShow(int level) {
        return CUR_LEVEL >= level;
    }

}
