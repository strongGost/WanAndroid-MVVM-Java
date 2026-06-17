package com.study.wanandroid.data.repository;

import com.study.wanandroid.MyApplication;
import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.local.AppDatabase;
import com.study.wanandroid.data.local.dao.ArticleDao;
import com.study.wanandroid.data.local.dao.SystemDao;
import com.study.wanandroid.data.local.entity.SystemEntity;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.WeChatService;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WeChatRepository extends BaseRepository {
    private static volatile WeChatRepository instance;
    private final WeChatService service;
    private final SystemDao systemDao;
    private final ArticleDao articleDao;

    private WeChatRepository() {
        service = RetrofitClient.getInstance().getService(WeChatService.class);
        systemDao = AppDatabase.getInstance(MyApplication.getAppContext()).systemDao();
        articleDao = AppDatabase.getInstance(MyApplication.getAppContext()).articleDao();
    }

    public static WeChatRepository getInstance() {
        if (instance == null) {
            synchronized (WeChatRepository.class) {
                if (instance == null) {
                    instance = new WeChatRepository();
                }
            }
        }
        return instance;
    }


    /**
     * 公众号 左侧列表
     * @return  本地缓存中的数据
     */
    public Observable<ResponseBean<List<SystemBean>>> getCacheWeChatList() {
        // 先拿本地缓存
        return systemDao.getData("wechat")
                .map(SystemEntity::entitiesToSystemResponse)
                .subscribeOn(Schedulers.io());
    }

    /**
     * 公众号 左侧列表
     * @return  网络请求的数据
     */
    public Observable<ResponseBean<List<SystemBean>>> getWeChatList() {
        return service.getWeChatList()
                .subscribeOn(Schedulers.io());
    }


    /**
     * 缓存到本地
     * @param beans
     * @return
     */
    public Completable cacheWeChatList(List<SystemBean> beans) {
        return systemDao.clearByCategory("wechat")
                .andThen(systemDao.insertAll(SystemEntity.toSystemEntities(beans, "wechat")));
    }


    /**
     * 查看指定公众号下的历史数据列表
     * @param id
     * @param page
     * @return
     */
    public Observable<ResponseBean<PageDataBean<List<ArticleBean>>>> getWeChatHistory(int id, int page) {
        return service.getWeChatHistory(id, page)
                .subscribeOn(Schedulers.io());

    }

}
