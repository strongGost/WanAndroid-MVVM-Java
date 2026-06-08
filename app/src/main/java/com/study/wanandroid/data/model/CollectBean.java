package com.study.wanandroid.data.model;

import android.text.TextUtils;

import java.util.Objects;

public class CollectBean implements IBaseArticle {

    private String author;
    private int chapterId;
    private String chapterName;
    private int courseId;
    private String desc;
    private String envelopePic;
    private int id;
    private String link;
    private String niceDate;
    private String origin;
    private int originId;
    private Long publishTime;
    private String title;
    private int userId;
    private int visible;
    private int zan;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEnvelopePic() {
        return envelopePic;
    }

    public void setEnvelopePic(String envelopePic) {
        this.envelopePic = envelopePic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getNiceDate() {
        return niceDate;
    }

    public void setNiceDate(String niceDate) {
        this.niceDate = niceDate;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getOriginId() {
        return originId;
    }

    public void setOriginId(int originId) {
        this.originId = originId;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public int getZan() {
        return zan;
    }

    public void setZan(int zan) {
        this.zan = zan;
    }

    @Override
    public String getUniqueId() {
        return "collect_" + getId();
    }

    @Override
    public String getDisplayTitle() {
        return getTitle();
    }

    @Override
    public String getPrimaryInfo() {
        return getAuthor().isEmpty() ? "匿名": getAuthor();
    }

    @Override
    public String getSecondaryInfo() {
        return getChapterName();
    }

    @Override
    public String getDisplayDate() {
        return niceDate;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public boolean isTop() {
        return false;
    }

    @Override
    public boolean isCollected() {
        return true;
    }

    @Override
    public String getDisplayLink() {
        return getLink();
    }

    @Override
    public int getArticleOriginId() {
        return getOriginId();
    }

    @Override
    public void setCollected(boolean collected) {
        // 收藏页通常是直接移除item，暂时 留空
    }

    // 为 DiffUtil 实现 equals 和 hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectBean that = (CollectBean) o;
        return originId == that.originId && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originId, title);
    }
}
