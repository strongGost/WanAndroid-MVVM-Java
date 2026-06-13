package com.study.wanandroid.data.repository;

import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.WeChatService;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class WeChatRepository extends BaseRepository {
    private static volatile WeChatRepository instance;
    private final WeChatService service;

    private WeChatRepository() {
        service = RetrofitClient.getInstance().getService(WeChatService.class);
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

    public Observable<ResponseBean<List<SystemBean>>> getWeChatList() {
        return service.getWeChatList();
    }

    public Observable<ResponseBean<PageDataBean<List<ArticleBean>>>> getWeChatHistory(int id, int page) {
        return service.getWeChatHistory(id, page);
    }

}
