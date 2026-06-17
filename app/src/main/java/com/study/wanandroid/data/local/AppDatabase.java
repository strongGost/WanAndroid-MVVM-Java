package com.study.wanandroid.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.study.wanandroid.data.local.converter.ListTypeConverter;
import com.study.wanandroid.data.local.dao.ArticleDao;
import com.study.wanandroid.data.local.dao.GuideDao;
import com.study.wanandroid.data.local.dao.SystemDao;
import com.study.wanandroid.data.local.entity.ArticleEntity;
import com.study.wanandroid.data.local.entity.GuideEntity;
import com.study.wanandroid.data.local.entity.SystemEntity;
import com.study.wanandroid.utils.Constant;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Database(entities = {ArticleEntity.class, SystemEntity.class, GuideEntity.class}, version = 1)
@androidx.room.TypeConverters({ListTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            Constant.DB_NAME
                    ).build();
                }
            }
        }
        return instance;
    }

    public abstract ArticleDao articleDao();
    public abstract SystemDao systemDao();
    public abstract GuideDao guideDao();

    /**
     * 清空所有 Room 缓存（退出登录 / Cookie 过期时调用）
     */
    public Completable clearAllCaches() {
        return Completable.mergeArray(
                articleDao().clearAll(),
                systemDao().clearAll(),
                guideDao().clearAll()
        ).subscribeOn(Schedulers.io());
    }
}
