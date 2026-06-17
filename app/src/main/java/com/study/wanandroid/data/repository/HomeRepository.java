package com.study.wanandroid.data.repository;

import android.annotation.SuppressLint;

import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.local.AppDatabase;
import com.study.wanandroid.data.local.dao.ArticleDao;
import com.study.wanandroid.data.local.entity.ArticleEntity;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.BannerBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.HomeService;
import com.study.wanandroid.MyApplication;
import com.study.wanandroid.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeRepository extends BaseRepository {
    private static volatile HomeRepository instance;
    private final HomeService service;  // 网络获取
    private final ArticleDao articleDao;    // 本地缓存获取
    private static final String CATEGORY = "home";

    private HomeRepository() {
        service = RetrofitClient.getInstance().getService(HomeService.class);
        articleDao = AppDatabase.getInstance(MyApplication.getAppContext()).articleDao();
    }

    public static HomeRepository getInstance() {
        if (instance == null) {
            synchronized (HomeRepository.class) {
                if (instance == null) {
                    instance = new HomeRepository();
                }
            }
        }
        return instance;
    }

    public Observable<ResponseBean<List<BannerBean>>> getBanners() {
        return service.getBanners();
    }

    public Observable<ResponseBean<List<ArticleBean>>> getTopArticles() {
        return service.getTopArticle();
    }

    /**
     * 网络请求文章列表（仅缓存第一页，符合主流做法）
     */
    public Observable<ResponseBean<PageDataBean<List<ArticleBean>>>> getArticles(int page) {
        return service.getArticle(page);
    }

    /**
     * 读取 Room 第一页缓存（冷启动秒开用）
     */
    public Observable<ResponseBean<PageDataBean<List<ArticleBean>>>> getCachedFirstPage() {
        return articleDao.getArticles(CATEGORY)
                .map(ArticleEntity::entitiesToResponse)
                .subscribeOn(Schedulers.io());
    }


    /**
     * 数据缓存到 room
     * @param beans 要缓存到本地的数据
     */
    public Completable cacheFirstPage(List<ArticleBean> beans) {
        return articleDao.clearByCategory(CATEGORY)
                .andThen(articleDao.insertAll(ArticleEntity.fromBeans(beans, CATEGORY, 0)));
    }

    /**
     * 刷新时清除 home 分类缓存
     */
    public Completable clearHomeCache() {
        return articleDao.clearByCategory(CATEGORY)
                .subscribeOn(Schedulers.io());
    }

}