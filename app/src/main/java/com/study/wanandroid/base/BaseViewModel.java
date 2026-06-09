package com.study.wanandroid.base;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.study.wanandroid.data.remote.Resource;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BaseViewModel extends ViewModel {
    protected CompositeDisposable disposable = new CompositeDisposable();
    public BaseViewModel() {

    }
    protected void addDisposable(Disposable d) {
        disposable.add(d);
    }


    /**
     * 清除 disposable, 以免内存泄漏
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();

    }

}
