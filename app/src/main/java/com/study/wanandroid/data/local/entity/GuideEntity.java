package com.study.wanandroid.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.GuideBean;
import com.study.wanandroid.data.model.ResponseBean;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "guide")
public class GuideEntity {
    @PrimaryKey
    public int cid;
    public String name;
    public List<ArticleBean> articles;

    @Ignore
    public GuideEntity() {}

    public GuideEntity(int cid, String name, List<ArticleBean> articles) {
        this.cid = cid;
        this.name = name;
        this.articles = articles;
    }

    // ==================== 转换 ====================

    public static GuideEntity fromBean(GuideBean bean) {
        return new GuideEntity(bean.getCid(), bean.getName(), bean.getArticles());
    }

    public static List<GuideEntity> fromBeans(List<GuideBean> beans) {
        List<GuideEntity> entities = new ArrayList<>();
        for (GuideBean b : beans) entities.add(fromBean(b));
        return entities;
    }

    public GuideBean toBean() {
        GuideBean bean = new GuideBean();
        bean.setCid(this.cid);
        bean.setName(this.name);
        bean.setArticles(this.articles);
        return bean;
    }

    public static List<GuideBean> toBeans(List<GuideEntity> entities) {
        List<GuideBean> beans = new ArrayList<>();
        for (GuideEntity e : entities) beans.add(e.toBean());
        return beans;
    }

    public static ResponseBean<List<GuideBean>> entitiesToGuideResponse(List<GuideEntity> entities) {
        ResponseBean<List<GuideBean>> response = new ResponseBean<>();
        response.setData(GuideEntity.toBeans(entities));
        response.setErrorCode(0);
        return response;
    }

    public static List<GuideEntity> toGuideEntities(List<GuideBean> beans) {
        return GuideEntity.fromBeans(beans);
    }
}
