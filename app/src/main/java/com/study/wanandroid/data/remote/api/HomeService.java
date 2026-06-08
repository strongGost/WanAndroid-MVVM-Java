package com.study.wanandroid.data.remote.api;


import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.BannerBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface HomeService {
    @GET("banner/json")
    Observable<ResponseBean<List<BannerBean>>> getBanners();

    /* 置顶文章 */
    @GET("article/top/json")
    Observable<ResponseBean<List<ArticleBean>>> getTopArticle();

    @GET("article/list/{page}/json")
    Observable<ResponseBean<PageDataBean<List<ArticleBean>>>> getArticle(@Path("page") int page);
}
