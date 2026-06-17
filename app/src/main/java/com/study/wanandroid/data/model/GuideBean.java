package com.study.wanandroid.data.model;

import java.util.List;
import java.util.Objects;

public class GuideBean {

    private int cid;
    private String name;
    private List<ArticleBean> articles;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ArticleBean> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleBean> articles) {
        this.articles = articles;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GuideBean guideBean = (GuideBean) o;
        return cid == guideBean.cid && Objects.equals(name, guideBean.name) && Objects.equals(articles, guideBean.articles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid, name, articles);
    }
}
