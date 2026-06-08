package com.study.wanandroid.data.repository;

import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.BannerBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.HomeService;
import com.study.wanandroid.MyApplication;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class HomeRepository extends BaseRepository {
    private static HomeRepository instance;
    private final HomeService service;

    private HomeRepository() {
        service = RetrofitClient.getInstance().getService(HomeService.class);
    }

    public synchronized static HomeRepository getInstance() {
        if (instance == null) {
            instance = new HomeRepository();
        }
        return instance;
    }

    public Observable<ResponseBean<List<BannerBean>>> getBanners() {
        return service.getBanners();
    }

    public Observable<ResponseBean<List<ArticleBean>>> getTopArticles() {
        return service.getTopArticle();
    }

    public Observable<ResponseBean<PageDataBean<List<ArticleBean>>>> getArticles(int page) {
        return service.getArticle(page);
    }


}