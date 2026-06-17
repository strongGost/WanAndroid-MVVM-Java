package com.study.wanandroid.data.repository;

import android.annotation.SuppressLint;

import androidx.room.Entity;

import com.study.wanandroid.MyApplication;
import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.local.AppDatabase;
import com.study.wanandroid.data.local.dao.GuideDao;
import com.study.wanandroid.data.local.dao.SystemDao;
import com.study.wanandroid.data.local.entity.GuideEntity;
import com.study.wanandroid.data.local.entity.SystemEntity;
import com.study.wanandroid.data.model.GuideBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.SquareService;
import com.study.wanandroid.utils.LogUtil;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SquareRepository extends BaseRepository {
    private static volatile SquareRepository instance;
    private final SquareService service;
    private final SystemDao systemDao;
    private final GuideDao guideDao;


    private SquareRepository() {
        service = RetrofitClient.getInstance().getService(SquareService.class);
        systemDao = AppDatabase.getInstance(MyApplication.getAppContext()).systemDao();
        guideDao = AppDatabase.getInstance(MyApplication.getAppContext()).guideDao();
    }

    public static SquareRepository getInstance() {
        if (instance == null) {
            synchronized (SquareRepository.class) {
                if (instance == null) {
                    instance = new SquareRepository();
                }
            }
        }
        return instance;
    }


    /**
     * 获取本地缓存
     *
     * @return
     */
    public Observable<ResponseBean<List<SystemBean>>> getCacheSystemData() {
        return systemDao.getData("system")
                .map(SystemEntity::entitiesToSystemResponse)
                .subscribeOn(Schedulers.io());
    }

    /**
     * 网络请求
     *
     * @return
     */
    public Observable<ResponseBean<List<SystemBean>>> getSystemData() {
        // 网络请求
        return service.getSystemData()
                .subscribeOn(Schedulers.io());
    }

    /**
     * 获取本地缓存
     *
     * @return
     */
    public Observable<ResponseBean<List<GuideBean>>> getCacheGuideData() {
        return guideDao.getData()
                .map(GuideEntity::entitiesToGuideResponse)
                .subscribeOn(Schedulers.io());
    }

    /**
     * 网络请求导航数据
     *
     * @return
     */
    public Observable<ResponseBean<List<GuideBean>>> getGuideData() {
        return service.getGuideData()
                .subscribeOn(Schedulers.io());
    }


    /**
     * 缓存导航数据到数据库
     * @param beans
     * @return
     */
    public Completable cacheGuideData(List<GuideBean> beans) {
        return guideDao.clearAll()
                .andThen(guideDao.insertAll(GuideEntity.toGuideEntities(beans)));
    }

    public Completable cacheSystemData(List<SystemBean> beans) {
        return systemDao.clearByCategory("system")
                .andThen(systemDao.insertAll(SystemEntity.toSystemEntities(beans, "system")));
    }


}
