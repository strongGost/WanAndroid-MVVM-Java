package com.study.wanandroid.data.local.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.GuideBean;
import com.study.wanandroid.data.model.SystemBean;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Room TypeConverter：List ↔ JSON String
 */
public class ListTypeConverter {

    private static final Gson gson = new Gson();

    // === ArticleBean ===
    @TypeConverter
    public static String fromArticleList(List<ArticleBean> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<ArticleBean> toArticleList(String json) {
        Type type = new TypeToken<List<ArticleBean>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // === SystemBean（分类子节点）===
    @TypeConverter
    public static String fromSystemList(List<SystemBean> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<SystemBean> toSystemList(String json) {
        Type type = new TypeToken<List<SystemBean>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // === GuideBean ===
    @TypeConverter
    public static String fromGuideList(List<GuideBean> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<GuideBean> toGuideList(String json) {
        Type type = new TypeToken<List<GuideBean>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
