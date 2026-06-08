package com.study.wanandroid.data.remote.api;

import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.ScoreBean;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ScoreService {
    @GET("lg/coin/list/{page}/json")
    Observable<ResponseBean<PageDataBean<List<ScoreBean>>>> getScoreList(@Path("page") int page);
}
