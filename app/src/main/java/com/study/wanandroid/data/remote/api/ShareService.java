package com.study.wanandroid.data.remote.api;

import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.ShareArticle;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ShareService {
    /* 分享文章 */
    @FormUrlEncoded
    @POST("lg/user_article/add/json")
    Observable<ResponseBean<Object>> shareArticle(@Field("title") String title, @Field("link") String link);

    /* 获取 “我的分享”文章列表 */
    @GET("user/lg/private_articles/{page}/json")
    Observable<ResponseBean<ShareArticle>> getShareArticle(@Path("page") int page);

    @POST("lg/user_article/delete/{id}/json")
    Observable<ResponseBean<Object>> deleteShare(@Path("id") int id);
}
