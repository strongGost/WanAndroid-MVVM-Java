package com.study.wanandroid.ui.me.college;


import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.CollectBean;
import com.study.wanandroid.data.model.IBaseArticle;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.remote.api.CollectService;
import com.study.wanandroid.data.repository.CollectRepository;
import com.study.wanandroid.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 收藏相关操作
 */
public class CollectViewModel extends BaseViewModel {
    private final CollectRepository repository = CollectRepository.getInstance();
    private final MutableLiveData<Resource> status = new MutableLiveData<>(); // 网络状态（收藏结果）
    private final MutableLiveData<Resource> listStatus = new MutableLiveData<>(); // 网络状态（收藏文章列表）
    private final MutableLiveData<List<CollectBean>> articles = new MutableLiveData<>();
    private final List<CollectBean> allArticles = new ArrayList<>();
    private PageDataBean<List<CollectBean>> pageData = null;  // 分页信息

    public LiveData<List<CollectBean>> getArticles() {
        return articles;
    }

    public LiveData<Resource> getStatus() {
        return status;
    }

    public LiveData<Resource> getListStatus() {
        return listStatus;
    }

    /**
     * 收藏、取消收藏在此判断
     * @param isCollege : 当前处于状态：收藏 true / 取消收藏 false
     */
    public <T> void collectAndUnCollect(Boolean isCollege, IBaseArticle data) {
        if (isCollege) {  // 取消收藏------
            if(data instanceof CollectBean) {   // 我的收藏页面
                CollectBean bean = (CollectBean) data;
                unCollect(bean.getId(), bean.getOriginId(), data);
            } else {    // 文章列表
                unCollect(data.getArticleOriginId(), data);
            }
        } else {    // 收藏----
            ArticleBean bean = (ArticleBean) data;
            if (TextUtils.isEmpty(bean.getAuthor())) {   // 站外文章（没有作者，只有分享者）
                collect(bean.getTitle(), bean.getAuthor(), bean.getLink());
            } else {     // 站内文章
                collect(bean.getId());
            }
        }
    }


    /**
     * 收藏站内文章
     * @param id
     */
    @SuppressLint("CheckResult")
    private void collect(int id) {
        if (isLoading(status)) return;

        disposable.add(
            repository.collect(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resp -> {
                        if (resp.isSuccess()) { // 成功
                            status.setValue(Resource.success("收藏成功"));
                            // 更改“点击文章”的收藏状态
                        } else if (resp.isOutLogin()){  // 未登录
                            status.setValue(Resource.outLogin());
                        } else {
                            LogUtil.error(CollectService.class, "数据请求失败" + resp.getErrorMsg());
                            status.setValue(Resource.error(resp.getErrorMsg()));
                        }
                    }, throwable -> {
                        LogUtil.error(CollectService.class, "数据请求失败" + throwable.getMessage());
                        status.setValue(Resource.error(throwable.getMessage()));
                    })
        );
            }

    /**
     * @return 当前是否处于加载状态
     */
    private boolean isLoading(MutableLiveData<Resource> data) {
        return data.getValue() != null && data.getValue().getState() == UIState.LOADING;
    }

    /**
     * 从列表中移除指定文章（已删除成员变量，改用此方法）
     * @param uniqueId 要移除的文章唯一标识
     */
    private void removeArticleById(String uniqueId) {
        for (int i = 0; i < allArticles.size(); i++) {
            if (allArticles.get(i).getUniqueId().equals(uniqueId)) {
                allArticles.remove(i);
                articles.setValue(new ArrayList<>(allArticles));
                break;
            }
        }
    }

    /**
     * 获取 收藏文章列表
     */
    public void getCollegeArticles() {
        if (isLoading(listStatus)) return;
        int page = pageData == null ? 0: pageData.getCurPage();
        listStatus.setValue(Resource.loading());

        // 超过最大页码
        if (pageData != null && page >= pageData.getPageCount()) {
            listStatus.setValue(Resource.none());
            return;
        }

        disposable.add(
                repository.getCollegeArticles(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> {
                            if (resp.isSuccess()) {
                                pageData = resp.getData();
                                if (pageData.getDatas() != null) {
                                    listStatus.setValue(Resource.success(""));
                                    allArticles.addAll(pageData.getDatas());
                                    articles.setValue(new ArrayList<>(allArticles));
                                } else {
                                    listStatus.setValue(Resource.none());
                                }
                            } else if (resp.isOutLogin()) {
                                listStatus.setValue(Resource.outLogin());
                            } else {
                                listStatus.setValue(Resource.error(resp.getErrorMsg()));
                                LogUtil.error(CollectViewModel.class, "网络响应错误：" + resp.getErrorMsg());
                            }
                        }, throwable -> {
                            listStatus.setValue(Resource.error(throwable.getMessage()));
                            LogUtil.error(CollectViewModel.class, "网络请求失败：" + throwable.getMessage());
                        })
        );
    }

    @SuppressLint("CheckResult")
    private void collect(String title, String author, String link) {
        if (isLoading(status)) return;

        disposable.add(
            repository.collect(title, author, link)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resp -> {
                        if (resp.isSuccess()) { // 成功
                            status.setValue(Resource.success("收藏成功"));
                        } else if (resp.isOutLogin()){
                            status.setValue(Resource.outLogin());
                        } else {
                            status.setValue(Resource.error(resp.getErrorMsg()));
                            LogUtil.error(CollectService.class, "数据请求失败: " + resp.getErrorMsg());
                        }
                    }, throwable -> {
                        status.setValue(Resource.error(throwable.getMessage()));
                        LogUtil.error(CollectService.class, "数据请求失败" + throwable.getMessage());
                    })
        );

    }
    @SuppressLint("CheckResult")
    private void unCollect(int id, IBaseArticle data) {
        if (isLoading(status)) return;

        disposable.add(
            repository.unCollect(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resp -> {
                        if (resp.isSuccess()) {
                            status.setValue(Resource.success("已取消收藏"));
                            removeArticleById(data.getUniqueId());
                        } else if (resp.isOutLogin()) {
                            status.setValue(Resource.outLogin());
                        } else {
                            status.setValue(Resource.error(resp.getErrorMsg()));
                            LogUtil.error(CollectService.class, "数据请求失败" + resp.getErrorMsg());
                        }
                    }, throwable -> {
                        status.setValue(Resource.error(throwable.getMessage()));
                        LogUtil.error(CollectService.class, "数据请求失败" + throwable.getMessage());
                    })
        );

    };


    /**
     * 收藏界面（取消收藏）
     * @param id
     * @param originId
     */
    @SuppressLint("CheckResult")
    private void unCollect(int id, int originId, IBaseArticle data) {
        if (isLoading(status)) return;

        disposable.add(
            repository.unCollect(id, originId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resp -> {
                        if (resp.isSuccess()) {
                            status.setValue(Resource.success("已取消收藏"));
                            removeArticleById(data.getUniqueId());
                        } else if (resp.isOutLogin()) {
                            status.setValue(Resource.outLogin());
                        } else {
                            status.setValue(Resource.error(resp.getErrorMsg()));
                            LogUtil.error(CollectService.class, "数据请求失败" + resp.getErrorMsg());
                        }
                    }, throwable -> {
                        status.setValue(Resource.error(throwable.getMessage()));
                        LogUtil.error(CollectService.class, "数据请求失败" + throwable.getMessage());
                    })
        );
    }
}
