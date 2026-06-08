package com.study.wanandroid.data.model;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 体系 model 和 公众号 model 的 Bean 类 结构一致，共用
 */
public class SystemBean implements Parcelable{

    private String author;
    private int courseId;
    private String cover;
    private String desc;
    private int id;
    private String lisense;
    private String lisenseLink;
    private String name;
    private int order;
    private int parentChapterId;
    private int type;
    private boolean userControlSetTop;
    private int visible;
    private List<ArticleBean> articleList;
    private List<SystemBean> children;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLisense() {
        return lisense;
    }

    public void setLisense(String lisense) {
        this.lisense = lisense;
    }

    public String getLisenseLink() {
        return lisenseLink;
    }

    public void setLisenseLink(String lisenseLink) {
        this.lisenseLink = lisenseLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getParentChapterId() {
        return parentChapterId;
    }

    public void setParentChapterId(int parentChapterId) {
        this.parentChapterId = parentChapterId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isUserControlSetTop() {
        return userControlSetTop;
    }

    public void setUserControlSetTop(boolean userControlSetTop) {
        this.userControlSetTop = userControlSetTop;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public List<?> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<ArticleBean> articleList) {
        this.articleList = articleList;
    }

    public List<SystemBean> getChildren() {
        return children;
    }

    public void setChildren(List<SystemBean> children) {
        this.children = children;
    }

    public SystemBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeInt(this.courseId);
        dest.writeString(this.cover);
        dest.writeString(this.desc);
        dest.writeInt(this.id);
        dest.writeString(this.lisense);
        dest.writeString(this.lisenseLink);
        dest.writeString(this.name);
        dest.writeInt(this.order);
        dest.writeInt(this.parentChapterId);
        dest.writeInt(this.type);
        dest.writeByte(this.userControlSetTop ? (byte) 1 : (byte) 0);
        dest.writeInt(this.visible);
        dest.writeList(this.articleList);
        dest.writeTypedList(this.children);
    }

    protected SystemBean(android.os.Parcel in) {
        this.author = in.readString();
        this.courseId = in.readInt();
        this.cover = in.readString();
        this.desc = in.readString();
        this.id = in.readInt();
        this.lisense = in.readString();
        this.lisenseLink = in.readString();
        this.name = in.readString();
        this.order = in.readInt();
        this.parentChapterId = in.readInt();
        this.type = in.readInt();
        this.userControlSetTop = in.readByte() != 0;
        this.visible = in.readInt();
        this.articleList = new ArrayList<>();
        in.readList(this.articleList, ArticleBean.class.getClassLoader());
        this.children = in.createTypedArrayList(SystemBean.CREATOR);
    }

    public static final Creator<SystemBean> CREATOR = new Creator<SystemBean>() {
        @Override
        public SystemBean createFromParcel(android.os.Parcel source) {
            return new SystemBean(source);
        }

        @Override
        public SystemBean[] newArray(int size) {
            return new SystemBean[size];
        }
    };
}
