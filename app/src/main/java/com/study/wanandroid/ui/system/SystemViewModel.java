package com.study.wanandroid.ui.system;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.repository.SystemRepository;
import com.study.wanandroid.ui.home.ArticleAdapter;
import com.study.wanandroid.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SystemViewModel extends BaseViewModel {
    private SystemRepository repository = SystemRepository.getInstance();
    private MutableLiveData<List<ArticleBean>> list = new MutableLiveData<>();  // 因为adapter 使用的 diff 计算，所以我们每次需要使用不同对象列表
    private List<ArticleBean> allList = new ArrayList<>();
    private PageDataBean<List<ArticleBean>> pageData = null;

    private MutableLiveData<Resource> status = new MutableLiveData<>();
    private boolean refresh = false;
    private int cid;    //

    public LiveData<List<ArticleBean>> getList() {
        return list;
    }

    public LiveData<Resource> getStatus() {
        return status;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }



    @SuppressLint("CheckResult")
    private void getSystemArticle() {
        // 处于加载状态
        if (status.getValue() != null && status.getValue().getState() == UIState.LOADING)
            return;

        // 加载中
        status.setValue(Resource.loading());

        // 当前应该访问页面 页码从 0 开始， cur 是下一页页码
        int page = refresh ? 0 : (pageData == null ? 0: pageData.getCurPage());
        if (pageData != null && page >= pageData.getPageCount()) {
            status.setValue(Resource.none());
            return;
        }

        repository.getSystemArticle(page, cid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    if (resp.isSuccess()) {
                        status.setValue(Resource.success(resp.getErrorMsg()));
                        LogUtil.error(SystemViewModel.class, "数据响应成功：" + resp.getData().getDatas().size());
                        pageData = resp.getData();  // 更新页码
                        if (refresh) {  // 清空原有数据
                            allList.clear();
                            refresh = false;
                        }
                        if (pageData.getDatas() == null || pageData.getDatas().isEmpty()) {
                            status.setValue(Resource.none());
                            return;
                        }
                        allList.addAll(pageData.getDatas());    // 更新数据
                        list.setValue(new ArrayList<>(allList));
                    } else {
                        status.setValue(Resource.error(resp.getErrorMsg()));
                        LogUtil.error(SystemViewModel.class, "数据响应失败：" + resp.getErrorMsg());
                    }
                }, throwable -> {
                    status.setValue(Resource.error(throwable.getMessage()));
                    LogUtil.error(SystemViewModel.class, "数据请求失败：" + throwable.getMessage());
                });
    }

    public void refresh() {
        refresh = true;
        getSystemArticle();
    }

    public void loadMore() {
        getSystemArticle();
    }
}
