package com.study.wanandroid.ui.project;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.GuideBean;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.repository.ProjectRepository;
import com.study.wanandroid.ui.square.guide.GuideViewModel;
import com.study.wanandroid.utils.LogUtil;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TabsViewModel extends BaseViewModel {
    private final ProjectRepository repository = ProjectRepository.getInstance();
    private final MutableLiveData<Resource> networkStatus = new MutableLiveData<>();
    private final MutableLiveData<List<SystemBean>> categorys = new MutableLiveData<>();
    private boolean databaseResponded = false;

    public TabsViewModel() {
        observableDatabase();
    }

    private void observableDatabase() {
        addDatabaseDisposable(
                repository.getCacheCategory()
                        .replay(1)
                        .refCount()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> {
                            if (resp != null) {
                                List<SystemBean> data = resp.getData();
                                if (data != null && !data.isEmpty()) {   // 有缓存
                                    categorys.setValue(data);
                                    networkStatus.setValue(Resource.success(""));
                                } else {
                                    // 缓存为空或无意义（size=0），显示 loading
                                    networkStatus.setValue(Resource.loading());
                                }
                            } else {
                                networkStatus.setValue(Resource.loading());
                            }
                            databaseResponded = true;
                        },throwable -> {
                            databaseResponded = true;
                            LogUtil.error(TabsViewModel.class, "project读取数据失败：" + throwable.getMessage());
                        })
        );
    }

    @SuppressLint("CheckResult")
    public void getCategory() {
        addDisposable(
                repository.getCategory()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(resp -> {
                            /* 避免 网络先执行完毕后 再数据库执行赋值造成的 ui 闪烁 */
                            if (resp != null && resp.getData() != null) {
                                categorys.setValue(resp.getData());
                                networkStatus.setValue(Resource.success(""));
                            }
                        })
                        .observeOn(Schedulers.io())
                        .flatMapCompletable(resp -> {   // 缓存到数据库
                            if (resp != null) {
                                List<SystemBean> data = resp.getData();
                                if (data != null) return repository.cacheCategory(data);
                            }
                            return Completable.complete();
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            if (categorys.getValue() != null && !categorys.getValue().isEmpty())
                                networkStatus.setValue(Resource.success(""));
                            else networkStatus.setValue(Resource.none());
                        }, throwable -> {
                            // 数据库尚未响应，缓存数据可能还在路上，不抢先设置状态，交给 observableDatabase 回调决定
                            if (!databaseResponded) return;

                            // 有缓存，页面状态为 success 即还是显示内容布局，否则 ui 将会改变
                            if (categorys != null && categorys.getValue() != null && !categorys.getValue().isEmpty())
                                networkStatus.setValue(Resource.success("无网络"));
                            else
                                networkStatus.setValue(Resource.error(throwable.getMessage()));
                            LogUtil.error(GuideViewModel.class, "数据获取失败：" + throwable.getMessage());
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
