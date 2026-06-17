package com.study.wanandroid.ui.square.system;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.repository.SquareRepository;
import com.study.wanandroid.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SystemViewModel extends BaseViewModel {
    private SquareRepository repository;
    private MutableLiveData<Resource> networkStatus = new MutableLiveData<>();
    private MutableLiveData<List<SystemBean>> systems = new MutableLiveData<>();
    /** 数据库是否已响应，用于防止网络错误在缓存查询完成前抢先设置 error 状态 */
    private boolean databaseResponded = false;

    public SystemViewModel() {
        repository = SquareRepository.getInstance();
        observableDatabase();
    }


    /**
     * 观察数据库
     */
    private void observableDatabase() {
        addDatabaseDisposable(
          repository.getCacheSystemData()
                  .replay(1)
                  .refCount()
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(resp -> {
                      databaseResponded = true;
                      if (resp != null) {
                          List<SystemBean> data = resp.getData();
                          if (data != null && !data.isEmpty()) {   // 有缓存
                              systems.setValue(data);
                              networkStatus.setValue(Resource.success(""));
                          } else {
                              // 缓存为空或无意义，显示 loading
                              networkStatus.setValue(Resource.loading());
                          }
                      } else {
                          networkStatus.setValue(Resource.loading());
                      }
                  }, throwable -> {
                      databaseResponded = true;
                      LogUtil.error(SystemViewModel.class, "system 数据库监听异常：" + throwable.getMessage());
                  })
        );
    }

    public LiveData<Resource> getNetworkStatus() {
        return networkStatus;
    }

    public LiveData<List<SystemBean>> getSystems() {
        return systems;
    }

    @SuppressLint("CheckResult")
    public void getSystemData() {
        // 当前无数据显示时，先展示 loading 状态，避免网络请求期间出现空白
        if (systems.getValue() == null || systems.getValue().isEmpty()) {
            networkStatus.setValue(Resource.loading());
        }

        addDisposable(
                repository.getSystemData()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(resp -> {
                            /* 避免 网络先执行完毕后 再数据库执行赋值造成的 ui 闪烁 */
                            if (resp != null && resp.getData() != null) {
                                systems.setValue(resp.getData());
                                networkStatus.setValue(Resource.success(""));
                            }
                        })
                        .observeOn(Schedulers.io())
                        .flatMapCompletable(resp -> {   // 缓存到数据库
                            if (resp != null) {
                                List<SystemBean> data = resp.getData();
                                if (data != null) return repository.cacheSystemData(data);
                            }
                            return Completable.complete();
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            if (systems.getValue() != null && !systems.getValue().isEmpty())
                                networkStatus.setValue(Resource.success(""));
                            else networkStatus.setValue(Resource.none());
                            }, throwable -> {
                            // 数据库尚未响应，缓存数据可能还在路上，不抢先设置状态，交给 observableDatabase 回调决定
                            if (!databaseResponded) return;

                            // 有缓存，页面状态为 success 即还是显示内容布局，否则 ui 将会改变
                            if (systems != null && systems.getValue() != null && !systems.getValue().isEmpty())
                                networkStatus.setValue(Resource.success("无网络"));
                            else
                                networkStatus.setValue(Resource.error(throwable.getMessage()));
                            LogUtil.error(SystemViewModel.class, "数据获取失败：" + throwable.getMessage());
                        })
        );
    }


    private boolean isLoading() {
        return networkStatus.getValue() != null && networkStatus.getValue().getState() == UIState.LOADING;
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
        if (systems.getValue() != null)
            return systems.getValue().get(pos);
        return  new SystemBean();
    }
}
