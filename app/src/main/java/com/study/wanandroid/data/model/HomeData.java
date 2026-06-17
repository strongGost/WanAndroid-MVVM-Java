package com.study.wanandroid.data.model;


import java.util.List;

/**
 * 首页数据
 */
public class HomeData {
    private List<BannerBean> banners;   // banner
    private List<ArticleBean> articles;     // top + 普通 文章
    private PageDataBean<List<ArticleBean>> pageData;   // 普通文章 分页信息
    private String errorMsg;    // 响应内容
    public HomeData() {
    }

    public HomeData(List<BannerBean> banners, List<ArticleBean> articles, PageDataBean<List<ArticleBean>> pageData, String errorMsg) {
        this.banners = banners;
        this.articles = articles;
        this.pageData = pageData;
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<BannerBean> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerBean> banners) {
        this.banners = banners;
    }

    public List<ArticleBean> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleBean> articles) {
        this.articles = articles;
    }

    public PageDataBean<List<ArticleBean>> getPageData() {
        return pageData;
    }

    public void setPageData(PageDataBean<List<ArticleBean>> pageData) {
        this.pageData = pageData;
    }
}
