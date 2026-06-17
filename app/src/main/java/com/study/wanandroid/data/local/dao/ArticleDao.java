package com.study.wanandroid.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.study.wanandroid.data.local.entity.ArticleEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface ArticleDao {

    /* 增(单条） */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(ArticleEntity entity);

    /* 增（列表）*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<ArticleEntity> entities);

    /* 删(指定分类数据) */
    @Query("DELETE FROM articles WHERE category = :category")
    Completable clearByCategory(String category);

    /* 删（整个表数据）*/
    @Query("DELETE FROM articles")
    Completable clearAll();

    /* 查（指定分类）*/
    @Query("SELECT * FROM articles WHERE category = :category")
    Observable<List<ArticleEntity>> getArticles(String category);

    @Transaction
    default void replaceByCategory(String category, List<ArticleEntity> articles) {
        clearByCategory(category);
        insertAll(articles);
    }
}
