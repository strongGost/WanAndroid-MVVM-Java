package com.study.wanandroid.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.Glide;

import java.io.File;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;

/**
 * 应用缓存相关（计算大小、清除...)
 */
public class CacheUtil {

    /**
     * 返回当前应用所使用的总缓存
     *
     * @param context
     * @return
     */
    public static Observable<String> getTotalCacheSize(Context context) {
        return Observable.fromCallable(() -> {
            long cacheSize = getFolderSize(context.getCacheDir());

            // 外部存储
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                cacheSize += getFolderSize(context.getExternalCacheDir());
            }

            // Room 数据库 getDatabasePath():主数据库文件路径
            File dbDir = context.getDatabasePath(Constant.DB_NAME).getParentFile();
            cacheSize += getFolderSize(dbDir);

            // 格式化，保留一位字符
            return getFormatSize(cacheSize);
        });

    }


    /**
     * 格式化单位，将 byte 转换为 KB、MB、GB
     * @param size
     * @return
     */
    @SuppressLint("DefaultLocale")
    private static String getFormatSize(double size) {
        // 转 KB
        double kb = size / 1024;
        if (kb < 1) return "0 KB";

        // 转 MB
        double mb = kb / 1024;
        if (mb < 1) return String.format("%.1f KB", kb);

        // 转 GB
        double gb = mb / 1024;
        if (gb < 1) return String.format("%.1f MB", mb);

        return String.format("%.1f GB", gb);
    }


    /**
     * 清空应用缓存
     * @return 是否删除成功
     */
    public static Observable<Boolean> clearAll(Context context) {
        // 使用 fromCallable() 将同步执行 转换为异步 Observable
        return Observable.fromCallable(() -> {
            // 清空内部缓存
            boolean cacheSuccess = deleteDir(context.getCacheDir());
            boolean externalSuccess = true;

            // 清空外部缓存
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                externalSuccess = deleteDir(context.getExternalCacheDir());
            }

            // 清除 GLide 的外部缓存
            Glide.get(context).clearDiskCache();

            return cacheSuccess && externalSuccess;
        });
    }


    /**
     * 清除指定路径中的内容
     * @param file
     * @return 是否全部删除成功
     */
    private static boolean deleteDir(File file) {
        if (file == null || !file.exists()) return true;  // 空指针 or 路径不存在

        if (file.isFile()) return file.delete();   // 直接删除文件

        boolean allSuccess = true; // 本次删除是否成功

        try {
            if (file.isDirectory()) {   // delete 只能删除 空文件夹、文件
                File[] files = file.listFiles();

                if (files == null) return file.delete();   // 空文件夹

                for (File child : files) {
                    allSuccess &= deleteDir(child);
                }
            }
            allSuccess &= file.delete();  // 文件夹下的内容删完了，将当前这个空文件夹删除

        } catch (Exception e) {
            LogUtil.error(CacheUtil.class, "清除缓存失败: " + e.getMessage());
        }
        return allSuccess;
    }

    /**
     * 计算当前路径下的内容 大小
     * @param file 路径
     * @return 文件/文件夹大小
     */

    private static long getFolderSize(File file) {
        // 空对象 or 路径不存在
        if (file == null || !file.exists()) return 0;

        long total = 0;

        // 是文件
        if (file.isFile()) return file.length();    // 当前文件的大小（字节）

        // 文件夹（递归遍历其中所有的文件、文件夹）
        if (file.isDirectory()) {
            File[] files = file.listFiles();

            if (files == null) return total;    // 是空文件夹

            for (File child : files) {  // 遍历这个文件夹下的所有内容
                total += getFolderSize(child);
            }
        }

        return total;
    }

    public static void clearGlideMemory(Context context) {
        Glide.get(context).clearMemory(); // 删除内部缓存（必须在主线程）
    }
}
