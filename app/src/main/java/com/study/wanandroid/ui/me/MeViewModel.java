package com.study.wanandroid.ui.me;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.data.local.AppDatabase;
import com.study.wanandroid.data.model.MeInfo;
import com.study.wanandroid.data.remote.Event;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.remote.WanCookieJar;
import com.study.wanandroid.data.repository.MeRepository;
import com.study.wanandroid.utils.CacheUtil;
import com.study.wanandroid.utils.Constant;
import com.study.wanandroid.utils.LogUtil;
import com.study.wanandroid.utils.SharePreferenceUtil;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MeViewModel extends AndroidViewModel {
    private final MeRepository repository = MeRepository.getInstance();
    private final MutableLiveData<Resource> state = new MutableLiveData<>();  // 网络状态
    private final MutableLiveData<MeInfo> meInfo = new MutableLiveData<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MutableLiveData<String> cacheSize = new MutableLiveData<>();  // 应用所占缓存大小
    private final MutableLiveData<Event<Boolean>> clearCacheSuccess = new MutableLiveData<>();  // 删除缓存是否成功


    public MeViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Resource> getState() {
        return state;
    }

    public LiveData<Event<Boolean>> getClearCacheSuccess() {
        return clearCacheSuccess;
    }

    public LiveData<MeInfo> getMeInfo() {
        return meInfo;
    }

    public LiveData<String> getCacheSize() {
        return cacheSize;
    }

    /**
     * 计算缓存（IO 线程）
     */
    public void calculateCacheSize() {
        disposable.add(
                CacheUtil.getTotalCacheSize(getApplication())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(cacheSize::setValue,
                                throwable -> LogUtil.error(MeViewModel.class, "计算缓存失败：" + throwable.getMessage()))
        );
    }

    /**
     * 删除应用缓存（IO 线程），完成后通过 clearCacheSuccess 通知结果
     */
    public void clearCache() {
        // 1. 清除 Glide 内存缓存（必须在主线程）
        CacheUtil.clearGlideMemory(getApplication());

        // 2. 清除磁盘 + Glide + Room 数据库缓存（必须在后台线程）
        disposable.add(
                CacheUtil.clearAll(getApplication())
                        .flatMapCompletable(success ->
                                AppDatabase.getInstance(getApplication()).clearAllCaches()
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            clearCacheSuccess.setValue(new Event<>(true));
                            calculateCacheSize();  // 重新计算
                        }, throwable -> {
                            clearCacheSuccess.setValue(new Event<>(false));
                            LogUtil.error(MeViewModel.class, "清理缓存失败：" + throwable.getMessage());
                        })
        );
    }

    /**
     * 先获取本地，再网络请求用户信息、等级、id
     */
    public void getMeData() {
        // 1. 先读取本地缓存
        MeInfo localData = SharePreferenceUtil.getObj(Constant.ME_INFO, MeInfo.class);
        if (localData != null && localData.getUserInfo() != null) {
            meInfo.setValue(localData);
        } else if (localData != null && localData.getUserInfo() == null) {
            // 旧版登录存入的 UserBean 数据被错误反序列化，视为无效缓存
            SharePreferenceUtil.remove(Constant.ME_INFO);
            meInfo.setValue(null);
        } else {
            meInfo.setValue(null);  // 未登录时也要触发 observer，让 UI 刷新为未登录状态
        }
        // 再网络
        state.setValue(Resource.loading());
        disposable.add(
                repository.getMeInfo()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response.isSuccess()) {
                                state.setValue(Resource.success(""));
                                meInfo.setValue(response.getData());
                                SharePreferenceUtil.saveTo(Constant.ME_INFO, response.getData());
                            } else if (response.isOutLogin()) {
                                // Cookie 过期 / 未登录：清除本地数据 + 清 Room 缓存
                                SharePreferenceUtil.remove(Constant.ME_INFO);
                                SharePreferenceUtil.remove(okhttp3.HttpUrl.parse(Constant.BASE_URL).host());
                                meInfo.setValue(null);
                                AppDatabase.getInstance(getApplication()).clearAllCaches().subscribe();
                                state.setValue(Resource.outLogin());
                            } else {
                                state.setValue(Resource.error(response.getErrorMsg()));
                                LogUtil.error(MeViewModel.class, "状态码错误：" + response.getErrorCode());
                            }
                        }, throwable -> {
                            state.setValue(Resource.error(throwable.getMessage()));
                            LogUtil.error(MeViewModel.class, "请求发送失败：" + throwable.getMessage());
                        })
        );
    }


    /**
     * 退出登录、成功后清除 Cookie、用户数据
     */
    @SuppressLint("CheckResult")
    public void logOut() {
        // 未登录状态
        if (!SharePreferenceUtil.hasObj(Constant.ME_INFO)) return;

        // 加载中
        Resource currentState = state.getValue();
        if (currentState != null && currentState.getState() == UIState.LOADING) {
            return;
        }

        state.setValue(Resource.loading());

        disposable.add(
                repository.logOut()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> {
                            if (resp.isSuccess()) {
                                SharePreferenceUtil.remove(Constant.ME_INFO);
                                SharePreferenceUtil.remove(okhttp3.HttpUrl.parse(Constant.BASE_URL).host());
                                AppDatabase.getInstance(getApplication()).clearAllCaches().subscribe();
                                state.setValue(Resource.success("退出登录"));
                                meInfo.setValue(null);
                            } else {
                                LogUtil.error(MeViewModel.class, "退出登录失败：" + resp.getErrorMsg());
                                state.setValue(Resource.error(resp.getErrorMsg()));
                            }
                        }, throwable -> {
                            LogUtil.error(MeViewModel.class, "网络请求错误：" + throwable.getMessage());
                            state.setValue(Resource.error(throwable.getMessage()));
                        })
        );
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();

    }
}
