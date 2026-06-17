package com.study.wanandroid.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.study.wanandroid.data.local.entity.GuideEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface GuideDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<GuideEntity> entities);

    @Query("DELETE FROM guide")
    Completable clearAll();

    @Query("SELECT * FROM guide")
    Observable<List<GuideEntity>> getData();
}
