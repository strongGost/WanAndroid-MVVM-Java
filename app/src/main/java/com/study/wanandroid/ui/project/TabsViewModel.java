package com.study.wanandroid.ui.project;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.repository.ProjectRepository;
import com.study.wanandroid.utils.LogUtil;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TabsViewModel extends BaseViewModel {
    private final ProjectRepository repository = ProjectRepository.getInstance();
    private MutableLiveData<Resource> networkStatus = new MutableLiveData<>();
    private MutableLiveData<List<SystemBean>> categorys = new MutableLiveData<>();

    @SuppressLint("CheckResult")
    void getCategory() {
        addDisposable(
                repository.getCategory()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response.isSuccess()) {
                                List<SystemBean> data = response.getData();
                                if (data == null || data.isEmpty()) {  // data is empty
                                    networkStatus.setValue(Resource.none());
                                } else {
                                    networkStatus.setValue(Resource.success(""));
                                }
                                categorys.setValue(data);
                            } else {
                                networkStatus.setValue(Resource.error(response.getErrorMsg()));
                                LogUtil.error(TabsViewModel.class, "解析响应失败：" + response.getErrorMsg());
                            }
                        }, throwable -> {
                            LogUtil.error(TabsViewModel.class, "数据请求失败：" + throwable.getMessage());
                            networkStatus.setValue(Resource.error(throwable.getMessage()));
                        })
        );

    }

    public LiveData<Resource> getNetworkStatus() {
        return networkStatus;
    }

    public LiveData<List<SystemBean>> getCategroys() {
        return categorys;
    }




}
