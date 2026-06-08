package com.study.wanandroid.data.remote;

import androidx.annotation.NonNull;

import com.study.wanandroid.utils.Constant;
import com.study.wanandroid.utils.LogUtil;
import com.study.wanandroid.utils.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class WanCookieJar implements CookieJar {

    /**
     * Response 中拿取 Cookie 保存到 sp
     * @param httpUrl
     * @param cookies
     */
    @Override
    public void saveFromResponse(@NonNull HttpUrl httpUrl, @NonNull List<Cookie> cookies) {
        // 只保存“登录”响应的 cookie
        if (!httpUrl.encodedPath().equals(Constant.LOGIN_PATH)) {
            return;
        }
        // 服务器返回 Cookie
        if (!cookies.isEmpty()) {
            HashSet<String> cookieSet = new HashSet<>();
            // 直接存储 cookie 字符串
            for (Cookie cookie : cookies) {
                cookieSet.add(cookie.toString());
            }
            LogUtil.debug(this.getClass(), "Cookie: " + cookieSet.toString());
            // 异步存储， key: 域名
            SharePreferenceUtil.saveCookies(httpUrl.host(), cookieSet);
        }
    }


    /**
     * 从 sp 中获取 Cookie 添加到 request
     * @param httpUrl
     * @return
     */
    @NonNull
    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl httpUrl) {
        Set<String> cookieSet = SharePreferenceUtil.getCookies(httpUrl.host());
        List<Cookie> cookies = new ArrayList<>();
        if (cookieSet != null) {
            for (String s : cookieSet) {
                // 字符串转回 Cookie 对象
                Cookie cookie = Cookie.parse(httpUrl, s);
                if (cookie != null) {
                    cookies.add(cookie);
                }
            }
        }
        return cookies;
    }

    /**
     * 清除当前 host 对应的 Cookie
     */
    public void clear(HttpUrl httpUrl) {
        SharePreferenceUtil.remove(httpUrl.host());
    }
}
