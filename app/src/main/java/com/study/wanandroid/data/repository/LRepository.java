package com.study.wanandroid.data.repository;

import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.UserBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.LRService;

import io.reactivex.rxjava3.core.Observable;

/**
 * 登录注册 Repository
 */
public class LRepository extends BaseRepository {
    private static volatile LRepository instance;
    private final LRService service;

    private LRepository() {
        service = RetrofitClient.getInstance().getService(LRService.class);
    }

    public static LRepository getInstance() {
        if (instance == null) {
            synchronized (LRepository.class) {
                if (instance == null) {
                    instance = new LRepository();
                }
            }
        }
        return instance;
    }

    public Observable<ResponseBean<UserBean>> login(String username, String pwd) {
        return service.login(username, pwd);
    }


    public Observable<ResponseBean<UserBean>> register(String username, String pwd, String repwd) {
        return service.register(username, pwd, repwd);
    }

}
