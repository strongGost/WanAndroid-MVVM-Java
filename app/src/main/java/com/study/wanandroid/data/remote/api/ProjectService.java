package com.study.wanandroid.data.remote.api;

import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.SystemBean;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProjectService {

    /* 项目分类 tab 栏 */
    @GET("project/tree/json")
    Observable<ResponseBean<List<SystemBean>>> getProjectCategory();

    /* 获取指定分类下的项目文章 */
    @GET("project/list/{page}/json")
    Observable<ResponseBean<PageDataBean<List<ArticleBean>>>> getProjectList(@Path("page") int page, @Query("cid") int cid);
}
