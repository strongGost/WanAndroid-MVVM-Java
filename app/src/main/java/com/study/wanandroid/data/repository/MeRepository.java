package com.study.wanandroid.data.repository;

import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.model.MeInfo;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.MeService;

import io.reactivex.rxjava3.core.Observable;

public class MeRepository extends BaseRepository {
    private static volatile MeRepository instance;
    private final MeService service;

    private MeRepository() {
        service = RetrofitClient.getInstance().getService(MeService.class);
    }

    public static MeRepository getInstance() {
        if (instance == null) {
            synchronized (MeRepository.class) {
                if (instance == null) {
                    instance = new MeRepository();
                }
            }
        }
        return instance;
    }

    public Observable<ResponseBean<MeInfo>> getMeInfo() {
        return service.getMeInfo();
    }

    public Observable<ResponseBean<Object>> logOut() {
        return service.logOut();
    }
}
