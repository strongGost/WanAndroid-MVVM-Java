package com.study.wanandroid.data.repository;

import com.study.wanandroid.MyApplication;
import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.local.AppDatabase;
import com.study.wanandroid.data.local.dao.ArticleDao;
import com.study.wanandroid.data.local.dao.SystemDao;
import com.study.wanandroid.data.local.entity.ArticleEntity;
import com.study.wanandroid.data.local.entity.SystemEntity;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.ProjectService;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProjectRepository extends BaseRepository {
    private final ProjectService service;
    private static volatile ProjectRepository instance;
    private final SystemDao systemDao;
    private final ArticleDao articleDao;


    private ProjectRepository() {
        service = RetrofitClient.getInstance().getService(ProjectService.class);
        systemDao = AppDatabase.getInstance(MyApplication.getAppContext()).systemDao();
        articleDao = AppDatabase.getInstance(MyApplication.getAppContext()).articleDao();
    }

    public static ProjectRepository getInstance() {
        if (instance == null) {
            synchronized (ProjectRepository.class) {
                if (instance == null) {
                    instance = new ProjectRepository();
                }
            }
        }
        return instance;
    }

    public Observable<ResponseBean<List<SystemBean>>> getCacheCategory() {
        return systemDao.getData("project")
                .map(SystemEntity::entitiesToSystemResponse)
                .subscribeOn(Schedulers.io());
    }
    public Observable<ResponseBean<List<SystemBean>>> getCategory() {
        return service.getProjectCategory();
    }

    public Completable cacheCategory(List<SystemBean> beans) {
        return systemDao.clearByCategory("project")
                        .andThen(systemDao.insertAll(SystemEntity.toSystemEntities(beans, "project")));
    }

    public Observable<ResponseBean<PageDataBean<List<ArticleBean>>>> getProjectList(int page, int cid) {
        return service.getProjectList(page, cid)
                .subscribeOn(Schedulers.io());
    }
}
