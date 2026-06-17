package com.study.wanandroid.data.local.entity;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 体系 model 和 公众号 model 的 Bean 类 结构一致，共用
 */
@Entity(tableName = "system", primaryKeys = {"id", "category"})
public class SystemEntity {
    public int id;

    @NonNull
    public String category;    // "system" / "wechat" / "project"
    public String author;
    public int courseId;
    public String cover;
    public String desc;
    public String lisense;
    @ColumnInfo(name = "lisense_link")
    public String lisenseLink;
    public String name;
    public int order;
    @ColumnInfo(name = "parent_chapter_id")
    public int parentChapterId;
    public int type;
    @ColumnInfo(name = "user_control_set_top")
    public boolean userControlSetTop;
    public int visible;
    public List<ArticleBean> articleList;
    public List<SystemBean> children;

    @Ignore
    public SystemEntity() {
    }

    public SystemEntity(int id, String category, String author, int courseId, String cover, String desc, String lisense, String lisenseLink, String name, int order, int parentChapterId, int type, boolean userControlSetTop, int visible, List<ArticleBean> articleList, List<SystemBean> children) {
        this.id = id;
        this.category = category;
        this.author = author;
        this.courseId = courseId;
        this.cover = cover;
        this.desc = desc;
        this.lisense = lisense;
        this.lisenseLink = lisenseLink;
        this.name = name;
        this.order = order;
        this.parentChapterId = parentChapterId;
        this.type = type;
        this.userControlSetTop = userControlSetTop;
        this.visible = visible;
        this.articleList = articleList;
        this.children = children;
    }


    // ==================== 转换 ====================
    /**
     * 将 bean 列表转为 entity 列表
     * @param beans
     * @return
     */
    public static List<SystemEntity> toSystemEntities(List<SystemBean> beans, String category) {
        List<SystemEntity> entities = new ArrayList<>();
        for (SystemBean bean : beans)
            entities.add(toSystemEntity(bean, category));
        return entities;
    }

    /**
     * 将 bean 转为 entity
     * @param bean
     * @return
     */
    private static SystemEntity toSystemEntity(SystemBean bean, String category) {
        return new SystemEntity(
                bean.getId(), category,
                bean.getAuthor(), bean.getCourseId(), bean.getCover(), bean.getDesc(),
                bean.getLisense(), bean.getLisenseLink(), bean.getName(), bean.getOrder(),
                bean.getParentChapterId(), bean.getType(), bean.isUserControlSetTop(),
                bean.getVisible(), bean.getArticleList(), bean.getChildren()
        );
    }



    /**
     * room 的 entity 转换为 bean
     * @param bean
     * @param category
     * @return
     */
    public SystemEntity fromBean(SystemBean bean, String category) {
        return new SystemEntity(
                bean.getId(), category,
                bean.getAuthor(), bean.getCourseId(), bean.getCover(), bean.getDesc(),
                bean.getLisense(), bean.getLisenseLink(), bean.getName(), bean.getOrder(),
                bean.getParentChapterId(), bean.getType(), bean.isUserControlSetTop(),
                bean.getVisible(), bean.getArticleList(), bean.getChildren()
        );
    }


    /**
     * room 的 Entity 转换为 bean
     * @param entity
     * @return
     */
    public static SystemBean toBean(SystemEntity entity) {
        SystemBean bean = new SystemBean();
        bean.setId(entity.id);
        bean.setAuthor(entity.author);
        bean.setCourseId(entity.courseId);
        bean.setCover(entity.cover);
        bean.setDesc(entity.desc);
        bean.setLisense(entity.lisense);
        bean.setLisenseLink(entity.lisenseLink);
        bean.setName(entity.name);
        bean.setOrder(entity.order);
        bean.setParentChapterId(entity.parentChapterId);
        bean.setType(entity.type);
        bean.setUserControlSetTop(entity.userControlSetTop);
        bean.setVisible(entity.visible);
        bean.setArticleList(entity.articleList);
        bean.setChildren(entity.children);
        return bean;
    }

    /**
     * entity列表转为bean列表
     * @param entities
     * @return
     */
    public static List<SystemBean> toBeans(List<SystemEntity> entities) {
        List<SystemBean> beans = new java.util.ArrayList<>();
        for (SystemEntity e : entities) beans.add(toBean(e));
        return beans;
    }

    /**
     * 转为请求的响应对象类型
     * @param entities
     * @return
     */
    public static ResponseBean<List<SystemBean>> entitiesToSystemResponse(List<SystemEntity> entities) {
        LogUtil.debug(SystemEntity.class, "room数据库中读取数据是否为 null ? " +(entities == null));
        ResponseBean<List<SystemBean>> response = new ResponseBean<>();
        response.setData(SystemEntity.toBeans(entities));
        response.setErrorCode(0);
        return response;
    }
}
