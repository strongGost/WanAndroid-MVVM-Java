package com.study.wanandroid.data.repository;

import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.ShareArticle;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.ShareService;

import io.reactivex.rxjava3.core.Observable;

public class ShareRepository extends BaseRepository {
    private static volatile ShareRepository instance;
    private static ShareService service;


    private ShareRepository() {
        service = RetrofitClient.getInstance().getService(ShareService.class);
    }

    public static ShareRepository getInstance() {
        if (instance == null) {
            synchronized (ShareRepository.class) {
                if (instance == null) {
                    instance = new ShareRepository();
                }
            }
        }
        return instance;
    }


    /**
     * 分享文章
     * @param title
     * @param link
     * @return
     */
    public Observable<ResponseBean<Object>> shareArticle(String title, String link) {
        return service.shareArticle(title, link);
    }


    /**
     * 获取我的分享文章列表
     * @param page
     * @return
     */
    public Observable<ResponseBean<ShareArticle>> getShareArticles(int page) {
        return service.getShareArticle(page);
    }

    public Observable<ResponseBean<Object>> deleteShare(int id) {
        return service.deleteShare(id);
    }
}
