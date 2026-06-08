package com.study.wanandroid.ui.me;

import android.support.v4.os.IResultReceiver;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.MeInfo;
import com.study.wanandroid.data.model.UserBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.repository.MeRepository;
import com.study.wanandroid.utils.Constant;
import com.study.wanandroid.utils.LogUtil;
import com.study.wanandroid.utils.SharePreferenceUtil;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MeViewModel extends BaseViewModel {
    private MeRepository repository = MeRepository.getInstance();
    private MutableLiveData<Resource> state = new MutableLiveData<>();  // 网络状态
    private MutableLiveData<MeInfo> meInfo = new MutableLiveData<>();

    public LiveData<Resource> getState() {
        return state;
    }

    public LiveData<MeInfo> getMeInfo() {
        return meInfo;
    }

    /**
     * 先获取本地，再网络请求用户信息、等级、id
     */
    public void getMeData() {
        // 1. 先读取本地缓存
        MeInfo localData = SharePreferenceUtil.getObj(Constant.ME_INFO, MeInfo.class);
        if (localData != null) {
            meInfo.setValue(localData);
        }
        // 再网络
        state.setValue(Resource.loading());
        addDisposable(repository.getMeInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccess()) {
                        /* 响应成功，更新 sp 文件的个人信息 */
                        state.setValue(Resource.success(""));
                        meInfo.setValue(response.getData());
                        SharePreferenceUtil.saveTo(Constant.ME_INFO, response.getData());
                    } else {
                        state.setValue(Resource.error(response.getErrorMsg()));
                        LogUtil.error(MeViewModel.class, "状态码错误：" + response.getErrorCode() + "data: " + response.getErrorMsg());
                    }
                }, throwable -> {
                    state.setValue(Resource.error(throwable.getMessage()));
                    LogUtil.error(MeViewModel.class, "请求发送失败：" + throwable.getMessage());
                }));
    }

}
