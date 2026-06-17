package com.study.wanandroid.ui.project;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.repository.ProjectRepository;
import com.study.wanandroid.data.repository.WeChatRepository;
import com.study.wanandroid.ui.wechat.WxArticleViewModel;
import com.study.wanandroid.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProjectListViewModel extends BaseViewModel {
    private ProjectRepository repository = ProjectRepository.getInstance();
    private MutableLiveData<Resource> first_network_state = new MutableLiveData<>();    /* 首次加载网络状态（页面状态） */
    private MutableLiveData<Resource> refresh_more_network_state = new MutableLiveData<>();    /* 刷新网络状态（组件状态）*/
    private MutableLiveData<Resource> load_more_network_state = new MutableLiveData<>();    /* 加载更多网络状态（组件状态）*/
    private MutableLiveData<PageDataBean<List<ArticleBean>>> pageData = new MutableLiveData<>();
    private MutableLiveData<List<ArticleBean>> projectList = new MutableLiveData<>();
    private List<ArticleBean> all_project_list = new ArrayList<>(); // 存储历史列表
    private int cid;

    public LiveData<Resource> getRefresh_more_network_state() {
        return refresh_more_network_state;
    }

    public LiveData<Resource> get_first_network_state() {
        return first_network_state;
    }

    public LiveData<Resource> getLoad_more_network_state() {
        return load_more_network_state;
    }

    public LiveData<List<ArticleBean>> getProjectList() {
        return projectList;
    }


    /**
     * 首次加载
     */
    @SuppressLint("CheckResult")
    private void getWeChatHistory(int page) {
        // 超过最大页码
        if (pageData.getValue() != null && page > pageData.getValue().getPageCount()) {
            updateStatus(Resource.none());
            return;
        }

        addDisposable(
                repository.getProjectList(page, cid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response.isSuccess()) {
                                PageDataBean<List<ArticleBean>> data = response.getData();
                                pageData.setValue(data);    // 更新页码信息

                                if (data.getDatas() != null && !data.getDatas().isEmpty()) {    // 更新文章列表
                                    updateStatus(Resource.success(""));
                                    all_project_list.addAll(data.getDatas());
                                    projectList.postValue(new ArrayList<>(all_project_list));
                                } else { // 数据为空
                                    updateStatus(Resource.none());
                                }
                            } else {
                                updateStatus(Resource.error(response.getErrorMsg()));
                            }
                        }, throwable -> {
                            first_network_state.postValue(Resource.error(throwable.getMessage()));
                            LogUtil.error(ProjectListViewModel.class, "数据请求失败：" + throwable.getMessage());
                        })
        );

    }


    public void firstLoad(int id) {
        this.cid = id;
        // 处于加载状态
        if (first_network_state.getValue() != null && first_network_state.getValue().getState() == UIState.LOADING) {
            return;
        }
        first_network_state.setValue(Resource.loading());
        getWeChatHistory(1);
    }

    public void onRetry() {
        firstLoad(cid);
    }


    /**
     * 加载指定分类下的历史微信文章列表
     */
    public void loadMore() {
        // 加载状态
        if (load_more_network_state.getValue() != null && load_more_network_state.getValue().getState() == UIState.LOADING) {
            return;
        }
        load_more_network_state.setValue(Resource.loading());
        getWeChatHistory(pageData.getValue().getCurPage() + 1);
    }

    public void refresh() {
        if (refresh_more_network_state.getValue() != null && refresh_more_network_state.getValue().getState() == UIState.LOADING) {
            return;
        }
        refresh_more_network_state.setValue(Resource.loading());
        getWeChatHistory(1);
    }

    /**
     * 根据加载状态 更新不同组件网络状态
     * @param resource  本次网络状态
     */
    private void updateStatus(Resource resource) {
        /* 首次加载*/
        if (first_network_state.getValue() != null && first_network_state.getValue().getState() == UIState.LOADING) {
            first_network_state.setValue(resource);
        } else if (load_more_network_state.getValue() != null && load_more_network_state.getValue().getState() == UIState.LOADING){
            load_more_network_state.setValue(resource);
        } else {
            if (resource.getState() != UIState.ERROR) { // 刷新成功需要清除原有数据
                this.all_project_list.clear();
            }
            refresh_more_network_state.setValue(resource);
        }
    }
}
