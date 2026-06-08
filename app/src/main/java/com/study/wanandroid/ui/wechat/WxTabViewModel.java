package com.study.wanandroid.ui.wechat;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.repository.WeChatRepository;
import com.study.wanandroid.utils.LogUtil;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WxTabViewModel extends BaseViewModel {
    private WeChatRepository repository = WeChatRepository.getInstance();
    private MutableLiveData<Resource> network = new MutableLiveData<>();
    private MutableLiveData<List<SystemBean>> wxList = new MutableLiveData<>(); /* 微信公众号 */


    public LiveData<Resource> getNetwork() {
        return network;
    }

    public LiveData<List<SystemBean>> getWxList() {
        return wxList;
    }

    /**
     * 获取微信公众号列表
     */
    @SuppressLint("CheckResult")
    public void getWeChatList() {
        if (network.getValue() != null && network.getValue().getState() == UIState.LOADING) {
            return;
        }
        addDisposable(
                repository.getWeChatList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response.isSuccess()) {
                                List<SystemBean> body = response.getData();
                                if (body != null && !body.isEmpty()) {
                                    this.wxList.postValue(body);
                                    network.postValue(Resource.success(""));
                                } else {
                                    network.postValue(Resource.none());
                                }
                            } else {
                                network.postValue(Resource.error(response.getErrorMsg()));
                            }
                        }, throwable -> {
                            network.postValue(Resource.error(throwable.getMessage()));
                            LogUtil.error(WxTabViewModel.class, "数据请求失败：" + throwable.getMessage());
                        })
        );

    }


    public void onRetry() {
        getWeChatList();
    }

}
