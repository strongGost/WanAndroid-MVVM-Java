package com.study.wanandroid.ui.square.system;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.repository.SquareRepository;
import com.study.wanandroid.utils.LogUtil;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SystemViewModel extends BaseViewModel {
    private SquareRepository repository;
    private MutableLiveData<Resource> networkStatus = new MutableLiveData<>();
    private MutableLiveData<List<SystemBean>> systems = new MutableLiveData<>();
    public SystemViewModel() {
        repository = SquareRepository.getInstance();
        getSystemData();
    }

    public LiveData<Resource> getNetworkStatus() {
        return networkStatus;
    }

    public LiveData<List<SystemBean>> getSystems() {
        return systems;
    }

    @SuppressLint("CheckResult")
    public void getSystemData() {
        if (networkStatus.getValue() != null && networkStatus.getValue().getState() == UIState.LOADING) {
            return;
        }
        networkStatus.setValue(Resource.loading());
        addDisposable(
                repository.getSystemData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                                    if (response.isSuccess()) {
                                        List<SystemBean> data = response.getData();
                                        LogUtil.error(SystemViewModel.class, "请求响应成功，is null：" + (data == null) + " 数据大小" + (data.size()));
                                        if (data == null || data.isEmpty()) {
                                            networkStatus.setValue(Resource.none());
                                        } else {
                                            networkStatus.setValue(Resource.success(""));
                                        }
                                        systems.setValue(data);
                                    } else {
                                        networkStatus.setValue(Resource.error(response.getErrorMsg()));
                                    }
                                },
                                throwable -> {
                                    networkStatus.setValue(Resource.error(throwable.getMessage()));
                                    LogUtil.error(SystemViewModel.class, "数据获取失败：" + throwable.getMessage());
                                })
        );
    }

    public void onRetry() {
        getSystemData();
    }

    /**
     * 返回 指定大体系的内容
     * @param pos 位置
     * @return
     */
    public SystemBean getChildren(int pos) {
        return systems.getValue().get(pos);
    }
}
