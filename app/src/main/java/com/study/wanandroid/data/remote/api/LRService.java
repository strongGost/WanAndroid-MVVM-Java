package com.study.wanandroid.data.remote.api;

import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.UserBean;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * login & Register Service
 *
 */
public interface LRService {
    @FormUrlEncoded
    @POST("user/login")
    Observable<ResponseBean<UserBean>> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("user/register")
    Observable<ResponseBean<UserBean>> register(@Field("username") String username, @Field("password") String password, @Field("repassword") String repassword);
}
