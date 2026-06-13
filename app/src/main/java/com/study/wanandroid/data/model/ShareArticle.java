package com.study.wanandroid.data.model;

import java.util.List;

public class ShareArticle {
    private CoinInfoBean coinInfo;
    private PageDataBean<List<ArticleBean>> shareArticles;

    public ShareArticle() {
    }

    public ShareArticle(CoinInfoBean coinInfo, PageDataBean<List<ArticleBean>> shareArticles) {
        this.coinInfo = coinInfo;
        this.shareArticles = shareArticles;
    }

    public CoinInfoBean getCoinInfo() {
        return coinInfo;
    }

    public void setCoinInfo(CoinInfoBean coinInfo) {
        this.coinInfo = coinInfo;
    }

    public PageDataBean<List<ArticleBean>> getShareArticles() {
        return shareArticles;
    }

    public void setShareArticles(PageDataBean<List<ArticleBean>> shareArticles) {
        this.shareArticles = shareArticles;
    }
}
