package com.study.wanandroid.data.repository;

import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.ScoreBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.ScoreService;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Path;

public class ScoreRepository extends BaseRepository {
    private ScoreService service;
    private static volatile ScoreRepository instance;

    private ScoreRepository() {
        service = RetrofitClient.getInstance().getService(ScoreService.class);
    }

    public static synchronized ScoreRepository getInstance() {
        if (instance == null) {
            synchronized (ScoreRepository.class) {
                if (instance == null) {
                    instance = new ScoreRepository();
                }
            }
        }
        return instance;
    }


    public Observable<ResponseBean<PageDataBean<List<ScoreBean>>>> getScoreList(int page) {
        return service.getScoreList(page);
    };

}
