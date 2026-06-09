package com.study.wanandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.study.wanandroid.MyApplication;

import java.util.HashSet;
import java.util.Set;

/**
 * 读、写 SharePreference 文件
 */
public class SharePreferenceUtil {

    private static final Gson gson = new Gson();
    private static SharedPreferences sp;


    /**
     * 打开 指定的 SharePreference
     */
    private static void openSP() {
        if (sp == null) {
            sp = MyApplication.getAppContext().getSharedPreferences(Constant.SPFILE, Context.MODE_PRIVATE);
        }
    }

    /**
     * 数据写入, 直接存 json 字符串
     * @param key
     * @param t
     */
    public static <T> void saveTo(String key, T t) {
        openSP();
        if (t != null) {
            sp.edit()
                    .putString(key, gson.toJson(t))
                    .apply();
        } else {
            LogUtil.error(SharePreferenceUtil.class, "t is null:");
        }
    }

    public static Boolean hasObj(String key) {
        openSP();
        return sp.contains(key);
    }


    /**
     * 读取 sp 文件的指定内容
     * @param key
     * @return
     */
    public static <T> T getObj(String key, Class<T> cls) {
        openSP();
        String json_str = sp.getString(key, "");
        if (json_str.isEmpty()) {
            return null;
        }
        return gson.fromJson(json_str, cls);
    }


    /**
     * 获取 保存的 cookies
     * @param key
     * @return
     */
    public static Set<String> getCookies(String key) {
        openSP();
        Set<String> set = sp.getStringSet(key, null);   // 不建议直接拿来用作可变集合
        return set == null ? null : new HashSet<>(set);
    }


    /**
     * 保存 Cookies
     * @param key
     * @param cookieSet
     */
    public static void saveCookies(String key, Set<String> cookieSet) {
        openSP();
        sp.edit().putStringSet(key, cookieSet).apply();
    }


    /**
     * 删除指定 key 的内容
     * @param key
     */
    public static void remove(String key) {
        openSP();
        sp.edit().remove(key).apply();
    }
}
