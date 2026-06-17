package com.study.wanandroid.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络有关工具类
 */
public class NetWorkUtil {

    /**
     * 检查网络是否可用
     * @param context
     * @return
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();   /* 获取已连接网络接口 */
        /* 发送请求前，检查网络是否可用 */
        if (networkInfo != null && networkInfo.isAvailable()) {
            return true;
        } else {
            ToastUtil.show(context, "当前网络不可用");
            return false;
        }
    }
}
