package com.study.wanandroid.data.repository;

import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.SystemService;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;


/**
 * wechat
 */
public class SystemRepository extends BaseRepository {
    private static volatile SystemRepository instance;
    private final SystemService service;

    private SystemRepository(){
        service = RetrofitClient.getInstance().getService(SystemService.class);
    }

    public static SystemRepository getInstance() {
        if (instance == null) {
            synchronized (SystemRepository.class) {
                if (instance == null) {
                    instance = new SystemRepository();
                }
            }
        }
        return instance;
    }

    public Observable<ResponseBean<PageDataBean<List<ArticleBean>>>> getSystemArticle(int page, int cid) {
        return service.getSystemArticle(page, cid);
    }
}
