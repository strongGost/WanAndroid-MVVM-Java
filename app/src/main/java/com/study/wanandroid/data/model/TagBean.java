package com.study.wanandroid.data.model;

public class TagBean {
    private String name;
    private String url;
    public TagBean() {

    }
    public TagBean(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
