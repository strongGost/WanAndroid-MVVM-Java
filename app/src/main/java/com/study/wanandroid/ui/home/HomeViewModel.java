package com.study.wanandroid.ui.home;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.BannerBean;
import com.study.wanandroid.data.model.HomeData;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.repository.HomeRepository;
import com.study.wanandroid.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends BaseViewModel {
    private final HomeRepository repository;
    private final HomeData homeData = new HomeData();
    private final MutableLiveData<List<BannerBean>> bannerLiveData = new MutableLiveData<>();   // banner 数据
    private final MutableLiveData<List<ArticleBean>> articleLiveData = new MutableLiveData<>(); // 文章数据
    private final MutableLiveData<Resource> pageState = new MutableLiveData<>();    /* 页面状态 */
    private final MutableLiveData<Resource> refreshState = new MutableLiveData<>();    /* 刷新状态 */
    private final MutableLiveData<Resource> loadMoreState = new MutableLiveData<>();  /* 加载更多状态 */
    HomeViewModel() {
        repository = HomeRepository.getInstance();
        observableDatabase();
    }

    public LiveData<List<BannerBean>> getBannerLiveData() {
        return bannerLiveData;
    }

    public LiveData<List<ArticleBean>> getArticleLiveData() {
        return articleLiveData;
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
     * 监听 ROOM 数据库
     */
    private void observableDatabase() {
        // 获取 Room 缓存（如果有数据则立即展示 并通知 ui）
        addDatabaseDisposable(
                repository.getCachedFirstPage()
                        .replay(1)  /* 遇到新的订阅：立即收到上次缓存的结果，无需 IO 读取 */
                        .refCount() // replay 返回的 ConnectableObservable，需要手动调用 connect(), 而 refCount()帮我们自动处理了 connect() 和 disconnect()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response != null && response.getData() != null) {
                                List<ArticleBean> cached = response.getData().getDatas();

                                if (cached != null) {  // 有缓存，当然由于在doNext()中存储了，也可以选择不通知
                                    homeData.setArticles(cached);
                                    articleLiveData.setValue(new ArrayList<>(cached));
                                }
                            }
                            // 是页面状态 且 无缓存 才展示 Loading 状态
                            if (isPageState() && (response == null || response.getData() == null || response.getData().getDatas() == null))
                                pageState.setValue(Resource.loading());

                        }, throwable -> LogUtil.debug(HomeViewModel.class, "home文章数据库监听异常: " + throwable.getMessage()))
        );
    }


    /**
     * 先读缓存，再网络获取更新
     */
    public void firstLoad() {

//        // 当缓存没有数据时，才显示 Loading
//        if (homeData.getArticles() == null) {
//            pageState.setValue(Resource.loading());
//        }

        // 网络请求
        getBannerTopAndArticle();
    }


    /**
     * 加载第一页的数据，只有 第一次进入（包含重试）、刷新 才会执行
     */
    @SuppressLint("CheckResult")
    private void getBannerTopAndArticle() {

        /* 多个Observable 压缩为一个Observe，并行发送 */
        Observable<HomeData> zipObservable = Observable.zip(
                repository.getBanners().onErrorReturnItem(new ResponseBean<>()),    // 防止 Banner 异常结束
                repository.getTopArticles().onErrorReturnItem(new ResponseBean<>()),    // 同上
                repository.getArticles(0),  // 如果只有文章加载失败，那也没必要完全可以重新加载
                (bannerResponse, topResponse, articleResponse) ->{
                    HomeData homeData = new HomeData();
                    /*banner*/
                    if (bannerResponse.isSuccess() && bannerResponse.getData() != null)
                        homeData.setBanners(bannerResponse.getData());

                    /* 存储 top 文章 + 普通文章 */
                    ArrayList<ArticleBean> list = new ArrayList<>();

                    /*top*/
                    if (topResponse.isSuccess() && topResponse.getData() != null && !topResponse.getData().isEmpty())
                        list.addAll(topResponse.getData());

                    /*article*/
                    if (articleResponse.isSuccess() && articleResponse.getData() != null && articleResponse.getData().getDatas() != null) {
                        homeData.setPageData(articleResponse.getData());
                        list.addAll(homeData.getPageData().getDatas());
                    } else homeData.setErrorMsg(articleResponse.getErrorMsg());

                    homeData.setArticles(list);
                    return homeData;
                });

        addDisposable(
                zipObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(data -> {
                    // 更新 banners 和 pageData
                    // rxjava3 中流不允许传递 null
                    if (data.getBanners() != null)
                        bannerLiveData.setValue(data.getBanners());
                    if (data.getPageData() != null)
                        homeData.setPageData(data.getPageData());

                    //这里先更新文章列表，不等 Room 的异步通知（因为 Room 是异步的，而 onCompletable可能先完成）
                    // 且 adapter 使用的是 DiffUtil 对比，如果数据无变化不会执行操作
                    if (data.getArticles() != null && !data.getArticles().isEmpty()) {
                        homeData.setArticles(data.getArticles());
                        articleLiveData.setValue(new ArrayList<>(data.getArticles()));
                    }
                })
                .observeOn(Schedulers.io())
                .flatMapCompletable((data -> {
                    // 数据缓存到数据库
                    List<ArticleBean> articles = data.getArticles();
                    if (articles != null)
                        return repository.cacheFirstPage(articles);
                    return Completable.complete();
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    /* 不论刷新、有无缓存 都会更新状态为 成功
                    * 但刷新：这里只会走成功这一选项
                    */
                    if (isRefreshState()) { // 更新刷新状态
                        refreshState.setValue(Resource.success(""));
                    } else if (isPageState()) {  // 更新页面状态
                        if (homeData == null || homeData.getArticles() == null || homeData.getArticles().isEmpty())
                            pageState.setValue(Resource.none());
                        else
                            pageState.setValue(Resource.success(""));
                    }
                    }, throwable -> {
                        if (isRefreshState()) {
                            // 1. 是下拉刷新失败，通知 refreshState 结束刷新动画
                            refreshState.setValue(Resource.error(throwable.getMessage()));
                        } else if (homeData != null && homeData.getArticles() != null && !homeData.getArticles().isEmpty()) {
                            // 2. 如果有缓存数据，网络请求失败了，保持 pageState 为 SUCCESS
                            // 这样 StateLayout 就不会切到全屏错误页，而是继续展示缓存
                            pageState.setValue(Resource.success(""));
                        } else {
                            // 3. 不是刷新，又没有缓存（彻底的第一次进入且断网），才显示全屏错误
                            pageState.setValue(Resource.error(throwable.getMessage()));
                        }
                    LogUtil.error(HomeViewModel.class, "数据请求失败：" + throwable.getMessage());
                })
        );
    }


    private boolean isPageState() {
        return pageState.getValue() != null && pageState.getValue().getState() == UIState.LOADING;
    }

    private boolean isRefreshState() {
        return refreshState.getValue() != null && Objects.requireNonNull(refreshState.getValue()).getState() == UIState.LOADING;
    }

    private boolean isLoadMoreState() {
        return loadMoreState.getValue() != null && Objects.requireNonNull(loadMoreState.getValue()).getState() == UIState.LOADING;
    }


    /**
     * 更新刷新状态
     * @param resource 本次网络请求状态
     */
    private void updateStatus(Resource resource) {
        if (isPageState())
            pageState.setValue(resource);
        else if (isRefreshState())
            refreshState.setValue(resource);
        else if (isLoadMoreState())
            loadMoreState.setValue(resource);
    }

    /**
     * 加载更多文章
     */
    public void loadMore() {
        PageDataBean<List<ArticleBean>> pageData = homeData.getPageData();

        if (pageData == null) {
            loadMoreState.setValue(Resource.error("网络错误"));
            return;
        }
        // 没有更多数据（当前已达最大页码）
        if (pageData.getCurPage() >= pageData.getPageCount()) {
            loadMoreState.setValue(Resource.none());
            return;
        }

        loadMoreState.setValue(Resource.loading());
        addDisposable(repository.getArticles(pageData.getCurPage())    /* 因为页码从0开始，此方法的curPge为1开始，所以直接访问这即可 */
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccess()) {
                        if (response.getData() != null && response.getData().getDatas() != null) {
                            List<ArticleBean> list = response.getData().getDatas();
                            if (!list.isEmpty()) {
                                homeData.getArticles().addAll(list);
                                articleLiveData.setValue(new ArrayList<>(homeData.getArticles()));
                                homeData.setPageData(response.getData());
                                updateStatus(Resource.success(""));
                                return;
                            }
                        }
                        updateStatus(Resource.none());
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
     * 刷新页面
     */
    public void refresh() {
        // 刷新时应取消其它请求（如加载更多）
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
        pageState.setValue(Resource.loading());
        firstLoad();
    }

}