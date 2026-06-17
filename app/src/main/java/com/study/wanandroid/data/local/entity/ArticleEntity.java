package com.study.wanandroid.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章缓存表，不同模块的文章用 category 区分
 */
@Entity(tableName = "articles", primaryKeys = {"id", "category"})
public class ArticleEntity {

    public int id;
    public String title;
    public String author;

    @ColumnInfo(name = "super_chapter_name")
    public String superChapterName;

    @ColumnInfo(name = "chapter_name")
    public String chapterName;

    public String link;

    @ColumnInfo(name = "nice_date")
    public String niceDate;

    public boolean collect;
    public boolean fresh;
    public int type;

    @NonNull
    public String category;
    public int page;


    @Ignore
    public ArticleEntity() {}

    public ArticleEntity(int id, String title, String author,
                         String superChapterName, String chapterName, String link,
                         String niceDate, boolean collect, boolean fresh, int type,
                         String category, int page) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.superChapterName = superChapterName;
        this.chapterName = chapterName;
        this.link = link;
        this.niceDate = niceDate;
        this.collect = collect;
        this.fresh = fresh;
        this.type = type;
        this.category = category;
        this.page = page;
    }

    // ==================== 转换 ====================

    public static ArticleEntity fromBean(ArticleBean bean, String category, int page) {
        return new ArticleEntity(
                bean.getId(), bean.getTitle(), bean.getPrimaryInfo(),
                bean.getSuperChapterName(), bean.getChapterName(), bean.getDisplayLink(),
                bean.getDisplayDate(), bean.isCollected(), bean.isNew(),
                bean.getType(), category, page
        );
    }

    public static List<ArticleEntity> fromBeans(List<ArticleBean> beans, String category, int page) {
        List<ArticleEntity> entities = new ArrayList<>();
        for (ArticleBean b : beans) {
            entities.add(fromBean(b, category, page));
        }
        return entities;
    }

    public ArticleBean toBean() {
        ArticleBean bean = new ArticleBean();
        bean.setId(this.id);
        bean.setTitle(this.title);
        bean.setAuthor(this.author);
        bean.setSuperChapterName(this.superChapterName);
        bean.setChapterName(this.chapterName);
        bean.setLink(this.link);
        bean.setNiceDate(this.niceDate);
        bean.setCollect(this.collect);
        bean.setFresh(this.fresh);
        bean.setType(this.type);
        return bean;
    }

    public static List<ArticleBean> toBeans(List<ArticleEntity> entities) {
        List<ArticleBean> beans = new ArrayList<>();
        for (ArticleEntity e : entities) {
            beans.add(e.toBean());
        }
        return beans;
    }


    // ==================== 转换 ====================

    public static ResponseBean<PageDataBean<List<ArticleBean>>> entitiesToResponse(
            List<ArticleEntity> entities) {
        List<ArticleBean> articles = ArticleEntity.toBeans(entities);
        PageDataBean<List<ArticleBean>> pageData = new PageDataBean<>();
        pageData.setDatas(articles);
        pageData.setCurPage(entities.get(0).page);
        pageData.setPageCount(Integer.MAX_VALUE);    // 如果显示的是本地缓存，那么不用去加载下一页

        ResponseBean<PageDataBean<List<ArticleBean>>> response = new ResponseBean<>();
        response.setData(pageData);
        response.setErrorCode(0);
        return response;
    }
}
