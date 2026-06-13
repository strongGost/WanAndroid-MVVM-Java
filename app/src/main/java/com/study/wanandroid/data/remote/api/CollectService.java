package com.study.wanandroid.data.remote.api;

import androidx.annotation.Nullable;

import com.study.wanandroid.data.model.CollectBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * 收藏相关
 */
public interface CollectService {
    /* 获取已收藏的文章列表 */
    @GET("lg/collect/list/{page}/json")
    Observable<ResponseBean<PageDataBean<List<CollectBean>>>> getCollegeArticles(@Path("page") int page);
    /* 收藏（站内文章） */
    @POST("lg/collect/{id}/json")
    Observable<ResponseBean<Object>> collect(@Path("id") int id);

    /* 收藏（站外文章） */
    @FormUrlEncoded
    @POST("lg/collect/add/json") // title，author，link
    Observable<ResponseBean<Object>> collect(@Field("title") String title, @Field("author") String author, @Field("link") String link);

    /* 取消收藏 （文章列表）*/
    @POST("lg/uncollect_originId/{id}/json")
    Observable<ResponseBean<Object>> unCollect(@Path("id")int id);

    /* 取消收藏（我的收藏页面）*/
    @FormUrlEncoded
    @POST("lg/uncollect/{id}/json") // originId 代表的是收藏之前的那篇文章本身的id,没有为-1
    Observable<ResponseBean<Object>> unCollect(@Path("id") int id, @Field("originId") int originId);

}
