package com.study.wanandroid.data.repository;

import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.model.CollectBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.CollectService;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class CollectRepository extends BaseRepository {
    private static volatile CollectRepository instance;
    private final CollectService service;

    private CollectRepository() {
        service = RetrofitClient.getInstance().getService(CollectService.class);
    }

    public static CollectRepository getInstance() {
        if (instance == null) {
            synchronized (CollectRepository.class) {
                if (instance == null) {
                    instance = new CollectRepository();
                }
            }
        }
        return instance;
    }

    public Observable<ResponseBean<PageDataBean<List<CollectBean>>>> getCollegeArticles(int page){
        return service.getCollegeArticles(page);
    };

    public Observable<ResponseBean<Object>> collect(int id) {
        return service.collect(id);
    }

    public Observable<ResponseBean<Object>> collect(String title, String author, String link) {
        return service.collect(title, author, link);
    }

    public Observable<ResponseBean<Object>> unCollect(int id) {
        return service.unCollect(id);
    }

    public Observable<ResponseBean<Object>> unCollect(int id, int originId) {
        return service.unCollect(id, originId);
    }
}
