package com.study.wanandroid.ui.square.guide;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.GuideBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.repository.SquareRepository;
import com.study.wanandroid.utils.LogUtil;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GuideViewModel extends BaseViewModel {
    private SquareRepository repository;
    private MutableLiveData<Resource> networkStatus = new MutableLiveData<>();
    private MutableLiveData<List<GuideBean>> guides = new MutableLiveData<>();
    public GuideViewModel() {
        repository = SquareRepository.getInstance();
        getGuidesData();
    }

    public LiveData<Resource> getNetworkStatus() {
        return networkStatus;
    }

    public LiveData<List<GuideBean>> getGuides() {
        return guides;
    }

    @SuppressLint("CheckResult")
    public void getGuidesData() {
        if (networkStatus.getValue() != null && networkStatus.getValue().getState() == UIState.LOADING) {
            return;
        }
        networkStatus.setValue(Resource.loading());
        addDisposable(
            repository.getGuideData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                                if (response.isSuccess()) {
                                    List<GuideBean> data = response.getData();
                                    LogUtil.error(GuideViewModel.class, "请求响应成功，is null：" + (data == null) + " 数据大小" + (data.size()));
                                    if (data == null || data.isEmpty()) {
                                        networkStatus.setValue(Resource.none());
                                    } else {
                                        networkStatus.setValue(Resource.success(""));
                                    }
                                    guides.setValue(data);
                                } else {
                                    networkStatus.setValue(Resource.error(response.getErrorMsg()));
                                }
                            },
                            throwable -> {
                                networkStatus.setValue(Resource.error(throwable.getMessage()));
                                LogUtil.error(GuideViewModel.class, "数据获取失败：" + throwable.getMessage());
                            })
        );
    }

    public void onRetry() {
        getGuidesData();
    }
}
