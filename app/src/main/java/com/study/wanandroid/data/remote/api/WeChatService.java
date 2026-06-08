package com.study.wanandroid.data.remote.api;

import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.SystemBean;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WeChatService {
    /* 获取微信公众号列表 */
    @GET("wxarticle/chapters/json")
    Observable<ResponseBean<List<SystemBean>>> getWeChatList();

    /*获取公众号下的的历史文章 */
    @GET("wxarticle/list/{id}/{page}/json")
    Observable<ResponseBean<PageDataBean<List<ArticleBean>>>> getWeChatHistory(@Path("id") int id, @Path("page") int page);
}
