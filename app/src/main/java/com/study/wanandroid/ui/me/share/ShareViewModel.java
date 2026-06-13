package com.study.wanandroid.ui.me.share;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ShareArticle;
import com.study.wanandroid.data.remote.Event;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.repository.ShareRepository;
import com.study.wanandroid.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ShareViewModel extends BaseViewModel {
    private  final ShareRepository repository;

    private final MutableLiveData<Resource> pageStatus = new MutableLiveData<>(); // 页面状态
    private final MutableLiveData<Resource> refreshStatus = new MutableLiveData<>(); // 刷新状态
    private final MutableLiveData<Resource> loadMoreStatus = new MutableLiveData<>(); // 加载更多状态
    private final MutableLiveData<Event<Resource>> deleteStatus = new MutableLiveData<>(); // 删除状态


    private PageDataBean<List<ArticleBean>> pageData; // 分页信息
    private final List<ArticleBean> allArticles = new ArrayList<>();  // 全部文章列表
    private final MutableLiveData<List<ArticleBean>> articles = new MutableLiveData<>();


    public LiveData<Event<Resource>> getDeleteStatus() {
        return deleteStatus;
    }

    public LiveData<List<ArticleBean>> getArticles() {
        return articles;
    }

    public LiveData<Resource> getPageStatus() {
        return pageStatus;
    }

    public LiveData<Resource> getRefreshStatus() {
        return refreshStatus;
    }

    public LiveData<Resource> getLoadMoreStatus() {
        return loadMoreStatus;
    }

    public ShareViewModel() {
        repository = ShareRepository.getInstance();
    }

    private void getShareArticles(int page) {

        // 页码从 1 开始
        addDisposable(
                repository.getShareArticles(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> {
                            if (resp.isSuccess()) {
                                ShareArticle respData = resp.getData();
                                if (respData != null) {
                                    pageData = respData.getShareArticles();
                                    List<ArticleBean> data = pageData.getDatas();

                                    if (data != null && !data.isEmpty()) {
                                        // （首次进入、加载更多）追加数据； 刷新（重置数据）
                                        if (refreshStatus.getValue() != null && refreshStatus.getValue().getState() == UIState.LOADING) {
                                            allArticles.clear();     // 刷新状态
                                        }
                                        allArticles.addAll(data);
                                        articles.setValue(new ArrayList<>(allArticles));

                                        updateStatus(Resource.success("加载成功"));
                                        return;
                                    }
                                }
                                updateStatus(Resource.none());

                            } else {
                                updateStatus(Resource.error(resp.getErrorMsg()));
                                LogUtil.error(ShareViewModel.class, "网络请求失败：" + resp.getErrorMsg());
                            }
                        }, throwable -> {
                            updateStatus(Resource.error(throwable.getMessage()));
                            LogUtil.error(ShareViewModel.class, "网络请求失败：" + throwable.getMessage());
                        })
        );
    }


    /**
     * @return 当前是否处于加载状态
     */
    private boolean isLoading() {
        Resource page = pageStatus.getValue();
        Resource refresh = refreshStatus.getValue();
        Resource loadMore = loadMoreStatus.getValue();

        boolean res = false;
        if (page != null)  res |= page.getState() == UIState.LOADING;

        if (refresh != null)  res |= refresh.getState() == UIState.LOADING;

        if (loadMore != null)  res |= loadMore.getState() == UIState.LOADING;

        return res;
    }


    /**
     * 更新网络请求状态
     * @param resource
     */
    private void updateStatus(Resource resource) {
        Resource page = pageStatus.getValue();
        Resource refresh = refreshStatus.getValue();
        Resource loadMore = loadMoreStatus.getValue();

        if (page != null && page.getState() == UIState.LOADING) {
            pageStatus.setValue(resource);
        } else if (refresh != null && refresh.getState() == UIState.LOADING) {
            refreshStatus.setValue(resource);
        } else if (loadMore != null && loadMore.getState() == UIState.LOADING) {
            loadMoreStatus.setValue(resource);
        }
    }


    /**
     * 进入页面时，首次加载
     */
    public void firstLoad() {
        // 防止数据倒灌
        allArticles.clear();
        pageData = null;

        pageStatus.setValue(Resource.loading());
        getShareArticles(1);
    }

    /**
     * 刷新
     */
    public void refresh() {
        // 处于刷新状态时，还有其它请求直接清除
        if (disposable != null) {
            disposable.clear();
            updateStatus(null);
        }

        refreshStatus.setValue(Resource.loading());
        getShareArticles(1);
    }

    /**
     * 加载更多
     */
    public void loadMore() {
        int page = pageData == null ? 1: pageData.getCurPage() + 1;

        // 不管是刷新 or 第一次进入 处于加载状态时，加载更多都不应该执行
        if (isLoading()) return;

        if (pageData != null && page > pageData.getPageCount()) {
            loadMoreStatus.setValue(Resource.none());
        } else {
            loadMoreStatus.setValue(Resource.loading());
            getShareArticles(page);
        }
    }

    public void onRetry() {
        if (isLoading()) return;
        firstLoad();
    }

    /**
     * 删除我的分享文章
     * @param id 分享文章 ID
     */
    public void deleteShare(int id) {
        addDisposable(
                repository.deleteShare(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> {
                            if (resp.isSuccess()) {
                                deleteStatus.setValue(new Event<>(Resource.success("删除成功")));
                                removeArticleById(id);
                            } else {
                                deleteStatus.setValue(new Event<>(Resource.error(resp.getErrorMsg())));
                                LogUtil.error(ShareViewModel.class, "删除失败：" + resp.getErrorMsg());
                            }
                        }, throwable -> {
                            deleteStatus.setValue(new Event<>(Resource.error(throwable.getMessage())));
                            LogUtil.error(ShareViewModel.class, "网络请求失败：" + throwable.getMessage());
                        })
        );
    }

    /**
     * 从列表中移除指定文章
     */
    private void removeArticleById(int id) {
        for (int i = 0; i < allArticles.size(); i++) {
            if (allArticles.get(i).getId() == id) {
                allArticles.remove(i);
                articles.setValue(new ArrayList<>(allArticles));
                return;
            }
        }
    }
}
