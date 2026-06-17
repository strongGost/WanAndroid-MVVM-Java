package com.study.wanandroid.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.study.wanandroid.data.local.entity.SystemEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface SystemDao {
    /* 增 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<SystemEntity> entities);

    /* 删 */
    @Query("DELETE FROM system WHERE category = :category")
    Completable clearByCategory(String category);
    @Query("DELETE FROM system")
    Completable clearAll();

    /* 查 */
    @Query("SELECT * FROM system WHERE category = :category")
    Observable<List<SystemEntity>> getData(String category);


}
