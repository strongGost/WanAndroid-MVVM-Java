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
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WxTabViewModel extends BaseViewModel {
    private final WeChatRepository repository = WeChatRepository.getInstance();
    private final MutableLiveData<Resource> network = new MutableLiveData<>();
    private final MutableLiveData<List<SystemBean>> wxList = new MutableLiveData<>(); /* 微信公众号 */

    private boolean databaseResponded = false;  // 读取本地缓存是否已完成，用来隔断 网络比本地化先执行完毕

    public WxTabViewModel() {
        observableDatabase();
    }


    public LiveData<Resource> getNetwork() {
        return network;
    }

    public LiveData<List<SystemBean>> getWxList() {
        return wxList;
    }


    private void observableDatabase() {
        addDatabaseDisposable(
                repository.getCacheWeChatList()
                        .replay(1)
                        .refCount()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> {
                            if (resp != null) {
                                List<SystemBean> data = resp.getData();
                                if (data != null && !data.isEmpty()) {   // 有缓存
                                    wxList.setValue(data);
                                    network.setValue(Resource.success("")); // 避免竞态
                                }
                            }
                            databaseResponded = true;
                        }, throwable -> {
                            databaseResponded = true;
                            LogUtil.error(WxTabViewModel.class, "公众号读取本地缓存报错：" + throwable.getMessage());
                        })
        );
    }



    /**
     * 获取微信公众号列表
     */
    @SuppressLint("CheckResult")
    public void getWeChatList() {
        if (isLoading()) return;

        // 无数据显示时，展示 Loading，避免网络请求 or 读取本地缓存时 展示空白内容
        if (wxList.getValue() == null || wxList.getValue().isEmpty()) {
            network.setValue(Resource.loading());
        }

        addDisposable(
                repository.getWeChatList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(resp -> {
                            if (resp != null && resp.isSuccess()) {
                                List<SystemBean> data = resp.getData();
                                if (data != null) {
                                    wxList.setValue(data);
                                    network.setValue(Resource.success(""));
                                }
                            }
                        })
                        .observeOn(Schedulers.io())
                        .flatMapCompletable(resp -> {
                            if (resp != null && resp.isSuccess()) {
                                List<SystemBean> data = resp.getData();
                                if (data != null) {
                                    // 保存到数据库
                                    return repository.cacheWeChatList(data);
                                }
                            }
                            return Completable.complete();
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            if (wxList.getValue() != null && !wxList.getValue().isEmpty())
                                network.setValue(Resource.success(""));
                            else network.setValue(Resource.none());
                        }, throwable -> {
                            // 数据库尚未响应，缓存数据可能还在路上，不抢先设置状态，交给 observableDatabase 回调决定
                            if (!databaseResponded) return;

                            // 有缓存，页面状态为 success 即还是显示内容布局，否则 ui 将会改变
                            if (wxList.getValue() != null && !wxList.getValue().isEmpty())
                                network.setValue(Resource.success("无网络"));
                            else
                                network.setValue(Resource.error(throwable.getMessage()));

                            LogUtil.error(WxTabViewModel.class, "数据请求失败：" + throwable.getMessage());
                        })
        );

    }

    private boolean isLoading() {
        return network.getValue() != null && network.getValue().getState() == UIState.LOADING;
    }


    public void onRetry() {
        getWeChatList();
    }

}
