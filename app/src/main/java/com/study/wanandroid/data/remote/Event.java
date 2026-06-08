package com.study.wanandroid.data.remote;

import androidx.annotation.Nullable;

/**
 * 单次消费事件包装类，解决 LiveData 粘性问题（数据倒灌）。
 * 通过 getContentIfNotHandled() 获取数据，每个观察者各自只能消费一次。
 *
 * @param <T> 事件数据类型
 */
public class Event<T> {
    private boolean hasBeenHandled = false; // 事件是否已消费
    private final T content;    // 内容

    public Event(@Nullable T content) {
        this.content = content;
    }

    /**
     * 获取内容（仅首次调用），之后返回 null
     */
    @Nullable
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        }
        hasBeenHandled = true;
        return content;
    }

    /**
     * 查看内容（不消费，可重复获取）
     */
    @Nullable
    public T peekContent() {
        return content;
    }
}
