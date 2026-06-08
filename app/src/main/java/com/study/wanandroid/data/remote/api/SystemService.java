package com.study.wanandroid.data.remote.api;

import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SystemService {
    /* 知识体系下的文章 */
    @GET("article/list/{page}/json")
    Observable<ResponseBean<PageDataBean<List<ArticleBean>>>> getSystemArticle(@Path("page") int page, @Query("cid") int cid);

}
