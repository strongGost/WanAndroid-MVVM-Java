package com.study.wanandroid.data.remote.api;

import com.study.wanandroid.data.model.GuideBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.SystemBean;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface SquareService {

    /* 体系数据 */
    @GET("tree/json")
    Observable<ResponseBean<List<SystemBean>>> getSystemData();


    /* 导航数据 */
    @GET("navi/json")
    Observable<ResponseBean<List<GuideBean>>> getGuideData();
}
