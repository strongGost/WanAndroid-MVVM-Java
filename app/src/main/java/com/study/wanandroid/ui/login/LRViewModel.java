package com.study.wanandroid.ui.login;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.MeInfo;
import com.study.wanandroid.data.model.UserBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.repository.LRepository;
import com.study.wanandroid.utils.Constant;
import com.study.wanandroid.utils.LogUtil;
import com.study.wanandroid.utils.SharePreferenceUtil;
import com.study.wanandroid.utils.ToastUtil;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 登录、注册
 */
public class LRViewModel extends BaseViewModel {
    private LRepository repository = LRepository.getInstance();
    private MutableLiveData<Resource> states = new MutableLiveData<>();
    private MutableLiveData<UserBean> userInfo = new MutableLiveData<>();

    public LiveData<Resource> getStates() {
        return states;
    }

    public LiveData<UserBean> getUserInfo() {
        return userInfo;
    }


    /**
     * 用户登录，登录成功后信息保存到 SharePreference
     * @param username
     * @param pwd
     */
    @SuppressLint("CheckResult")
    public void login(String username, String pwd) {

        if(isLoading()) {
            return;
        }
        states.setValue(Resource.loading());
        disposable.add(
                repository.login(username, pwd)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response.isSuccess()) {
                                states.setValue(Resource.success("登录成功"));
                                userInfo.setValue(response.getData());
                                /* 当前用户信息包装为 MeInfo 保存到 SharePreference */
                                MeInfo meInfo = new MeInfo();
                                meInfo.setUserInfo(response.getData());
                                SharePreferenceUtil.saveTo(Constant.ME_INFO, meInfo);
                            } else {
                                states.setValue(Resource.error(response.getErrorMsg()));
                            }
                            LogUtil.debug(LRViewModel.class, "网络请求发送成功：" + response.getErrorMsg() + "\ndata:"+response.getData());
                        }, throwable -> {
                            states.setValue(Resource.error(throwable.getMessage()));
                            LogUtil.error(LRViewModel.class, "网络请求发送失败：" + throwable.getMessage());
                        })
        );
    }


    /**
     * 检测当前是否处于加载状态
     * @return
     */
    private boolean isLoading() {
        if (states.getValue() != null && states.getValue().getState() == UIState.LOADING) {
            return true;
        }
        return false;
    }

    /**
     * 用户注册
     * @param username
     * @param pwd
     * @param repwd
     */
    @SuppressLint("CheckResult")
    public void register(String username, String pwd, String repwd) {
        if (isLoading()) {
            return;
        }
        disposable.add(
                repository.register(username, pwd, repwd)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response.isSuccess()) {
                                states.setValue(Resource.success("注册成功"));
                                userInfo.setValue(response.getData());
                            } else {
                                states.setValue(Resource.error(response.getErrorMsg()));
                            }
                            LogUtil.error(LRViewModel.class, "网络请求发送成功：" + response.getErrorMsg() + "\ndata:"+response.getData());
                        }, throwable -> {
                            states.setValue(Resource.error(throwable.getMessage()));
                            LogUtil.error(LRViewModel.class, "网络请求发送失败：" + throwable.getMessage());
                        })
        );
    }
}
