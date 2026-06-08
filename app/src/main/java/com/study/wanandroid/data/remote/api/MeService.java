package com.study.wanandroid.data.remote.api;

import com.study.wanandroid.data.model.MeInfo;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.UserBean;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface MeService {
    @GET("user/lg/userinfo/json")
    Observable<ResponseBean<MeInfo>>getMeInfo();

}
