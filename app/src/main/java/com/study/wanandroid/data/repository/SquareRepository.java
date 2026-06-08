package com.study.wanandroid.data.repository;

import com.study.wanandroid.data.model.GuideBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.SquareService;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class SquareRepository {
    private static SquareRepository instance;
    private final SquareService service;


    private SquareRepository() {
        service = RetrofitClient.getInstance().getService(SquareService.class);
    }

    public static SquareRepository getInstance() {
        if (instance == null) {
            instance = new SquareRepository();
        }
        return instance;
    }

    public Observable<ResponseBean<List<SystemBean>>> getSystemData() {
        return service.getSystemData();
    }

    public Observable<ResponseBean<List<GuideBean>>> getGuideData() {
        return service.getGuideData();
    }


}
