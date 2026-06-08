package com.study.wanandroid.data.model;

import java.io.Serializable;

/**
 * 通用显示接口， 用于 ArticleAdapter 中的 item 显示数据
 */
public interface IBaseArticle extends Serializable {
    // --- 给 DiffUtil 用的 ---
    String getUniqueId(); // 返回一个全局唯一的ID，比如 "article_123"

    // --- 给 Adapter 绑定 UI 用的 ---
    String getDisplayTitle();       // 获取要显示的标题
    String getPrimaryInfo();      // 获取“主要信息”，如作者
    String getSecondaryInfo();    // 获取“次要信息”，如分类
    String getDisplayDate();        // 获取要显示的日期
    boolean isNew();                // 是不是新的
    boolean isTop();                // 是不是置顶
    boolean isCollected();          // 是不是已收藏
    String getDisplayLink();        // 跳转链接

    // --- 给业务逻辑用的 ---
    int getArticleOriginId();    // 获取文章原始ID，用于收藏/取消收藏
    void setCollected(boolean collected); // 点击收藏后更新状态

    // --- 固定写法，为了让 DiffUtil 工作 ---
    @Override
    boolean equals(Object o);
    @Override
    int hashCode();
}
