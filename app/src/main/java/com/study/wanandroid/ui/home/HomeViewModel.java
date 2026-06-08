package com.study.wanandroid.ui.home;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.collection.MutableObjectList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.BannerBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.repository.HomeRepository;
import com.study.wanandroid.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends BaseViewModel {
    private HomeRepository repository;
    private MutableLiveData<List<BannerBean>> bannerLiveData = new MutableLiveData<>();
    private List<ArticleBean> allArticles = new ArrayList<>();  /* 存储所有文章列表 */
    private MutableLiveData<List<ArticleBean>> articleLiveData = new MutableLiveData<>();
    private MutableLiveData<Resource> pageState = new MutableLiveData<>();    /* 页面状态 */
    private MutableLiveData<Resource> refreshState = new MutableLiveData<>();    /* 刷新状态 */
    private MutableLiveData<Resource> loadMoreState = new MutableLiveData<>();  /* 加载更多状态 */
    private PageDataBean pageData;  /* 存储页码信息 */


    HomeViewModel() {
        repository = HomeRepository.getInstance();
    }

    public LiveData<List<BannerBean>> getBannerLiveData() {
        return bannerLiveData;
    }

    public LiveData<List<ArticleBean>> getArticleLiveData() {
        return articleLiveData;
    }

    public PageDataBean getPageData() {
        return pageData;
    }

    public LiveData<Resource> getRefreshState() {
        return refreshState;
    }

    public LiveData<Resource> getPageState() {
        return pageState;
    }

    public LiveData<Resource> getLoadMoreState() {
        return loadMoreState;
    }

    /**
     * 进入首页
     * 获取 Banner、置顶文章 and 文章列表
     */
    @SuppressLint("CheckResult")
    private void getBannerTopAndArticle() {
        Observable<List<ArticleBean>> zipObservable = Observable.zip(
                repository.getBanners(),
                repository.getTopArticles(),
                repository.getArticles(0),  // 获取第一页,页码从 0 开始
                (bannerResponse, topResponse, articleResponse) -> {
                    /* banners 数据 */
                    if (bannerResponse.isSuccess()) {
                        List<BannerBean> banners = bannerResponse.getData();
                        if (banners == null || banners.isEmpty()) {   // 数据为空
                            updateStatus(Resource.none());
                        } else {    // banner 可直接清空
                            bannerLiveData.postValue(banners);
                        }
                    } else {
                        updateStatus(Resource.error(bannerResponse.getErrorMsg()));
//                        return null;
                    }

                    // 置顶文章和文章列表需合并
                    /* 置顶文章 */
                    ArrayList<ArticleBean> articles = new ArrayList<>();
                    if (topResponse.isSuccess() && topResponse.getData() != null && !topResponse.getData().isEmpty()) {
                        articles.addAll(topResponse.getData());
                    }

                    /* 第一页文章 */
                    if (articleResponse.isSuccess() &&
                            articleResponse.getData() != null &&
                            articleResponse.getData().getDatas() != null &&
                            !articleResponse.getData().getDatas().isEmpty()) {
                        // 更新
                        pageData = articleResponse.getData();
                        articles.addAll(articleResponse.getData().getDatas());
                    }
                    return articles;
                }
        );

        addDisposable(
                zipObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(articles -> {
                    LogUtil.error(HomeViewModel.class, "数据获取成功：" + (articles != null ) + " size: " + articles.size());
                    if (articles.isEmpty()) {
                        updateStatus(Resource.none());
                    } else {    // 更新
                        updateStatus(Resource.success(""));
                        allArticles.addAll(articles);
                        articleLiveData.postValue(new ArrayList<>(allArticles));    /* 由于 adapter 使用的是 diff，不能是同一个引用对象 */
                    }
                }, throwable -> {
                    updateStatus(Resource.error(throwable.getMessage()));
                    LogUtil.error(HomeViewModel.class, "数据请求失败：" + throwable.getMessage());
                })
        );
    }


    /**
     * 更新刷新状态
     * @param resource 本次网络请求状态
     */
    private void updateStatus(Resource resource) {
        if (pageState.getValue() != null && Objects.requireNonNull(pageState.getValue()).getState() == UIState.LOADING) {    // 页面初始内容
            pageState.postValue(resource);
        } else if (refreshState.getValue() != null && Objects.requireNonNull(refreshState.getValue()).getState() == UIState.LOADING){    // 刷新
            if (resource.getState() == UIState.SUCCESS) {   // 刷新成功清空数据
                allArticles.clear();
            }
            refreshState.postValue(resource);
        } else {    // 加载更多
            loadMoreState.postValue(resource);
        }
    }

    /**
     * 加载更多文章
     */
    public void loadMore() {
        loadMoreState.setValue(Resource.loading());
        if (pageData.getCurPage() >= pageData.getPageCount()) {
            updateStatus(Resource.none());
            return;
        }
        disposable.add(repository.getArticles(pageData.getCurPage())    /* 由于是0开始，所以 curPage 就是下一页*/
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccess()) {
                        if (response.getData() != null && response.getData().getDatas() != null) {
                            allArticles.addAll(response.getData().getDatas());
                            articleLiveData.postValue(new ArrayList<>(allArticles));
                            updateStatus(Resource.success(""));
                            pageData = response.getData();
                        }
                    } else {
                        updateStatus(Resource.error(response.getErrorMsg()));
                    }
                }, throwable -> {
                    updateStatus(Resource.error(throwable.getMessage()));
                    LogUtil.error(HomeViewModel.class, "数据请求失败：" + throwable.getMessage());
                })
        );
    }

    /**
     * 首次加载
     */
    public void firstLoad() {
        pageState.setValue(Resource.loading());
        getBannerTopAndArticle();
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        // 如果用户确定刷新，此时应取消其它请求（如加载更多）
        if (disposable != null) {
            disposable.clear();
        }
        refreshState.setValue(Resource.loading());
        getBannerTopAndArticle();
    }

    /**
     * 重试 只有首次进入请求失败才会显示
     */
    public void onRetry() {
        firstLoad();
    }

}