package com.study.wanandroid.ui.me.score;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ScoreBean;
import com.study.wanandroid.data.model.MeInfo;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.repository.ScoreRepository;
import com.study.wanandroid.utils.Constant;
import com.study.wanandroid.utils.LogUtil;
import com.study.wanandroid.utils.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ScoreViewModel extends BaseViewModel {
    private ScoreRepository repository = ScoreRepository.getInstance();
    private MutableLiveData<Resource> status = new MutableLiveData<>();
    private PageDataBean<List<ScoreBean>> pageData = null;
    private MutableLiveData<List<ScoreBean>> scores = new MutableLiveData<>();
    private List<ScoreBean> allScores = new ArrayList<>();  // diffUtil 计算列表差异


    public LiveData<Resource> getStatus() {
        return status;
    }

    public LiveData<List<ScoreBean>> getScores() {
        return scores;
    }

    @SuppressLint("CheckResult")
    public void getScoreList() {
        if (status.getValue() != null && status.getValue().getState() == UIState.LOADING) {
            return;
        }
        // 未登录状态
        MeInfo meInfo = SharePreferenceUtil.getObj(Constant.ME_INFO, MeInfo.class);
        if (meInfo == null || meInfo.getUserInfo() == null) {
            status.setValue(Resource.error("未登录"));
            return;
        }
        // 当前应访问页码
        int page = pageData == null ? 0 : pageData.getCurPage() + 1;    // 页码 0 开始
        // 超出最大页码
        if (pageData != null && page > pageData.getPageCount()) {
            status.setValue(Resource.none());
            return;
        }

        status.setValue(Resource.loading());    // 更新网络状态
        repository.getScoreList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    if (resp.isSuccess()) {
                        pageData = resp.getData();
                        if (pageData.getDatas() != null && !pageData.getDatas().isEmpty()) {
                            status.setValue(Resource.success(""));
                            allScores.addAll(pageData.getDatas());
                            scores.setValue(new ArrayList<>(allScores));
                        } else {
                            status.setValue(Resource.none());
                        }
                    } else {
                        status.setValue(Resource.error(resp.getErrorMsg()));
                        LogUtil.error(ScoreViewModel.class, "数据响应错误：" + resp.getErrorMsg());
                    }
                }, throwable -> {
                    status.setValue(Resource.error(throwable.getMessage()));
                    LogUtil.error(ScoreViewModel.class, "数据请求失败：" + throwable.getMessage());
                });
    };

}
