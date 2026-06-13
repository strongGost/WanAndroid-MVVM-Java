package com.study.wanandroid.ui.me.share;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.study.wanandroid.base.BaseViewModel;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.data.repository.ShareRepository;
import com.study.wanandroid.utils.LogUtil;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 分享文章
 */
public class AddShareViewModel extends BaseViewModel {
    private final ShareRepository repository;
    private final MutableLiveData<Resource> status = new MutableLiveData<>();

    public AddShareViewModel() {
        repository = ShareRepository.getInstance();
    }


    public LiveData<Resource> getStatus() {
        return status;
    }

    /**
     * 分享文章
     * @param title
     * @param link
     */
    public void share(String title, String link) {
        if (status.getValue() != null && status.getValue().getState() == UIState.LOADING)
            return;

        status.setValue(Resource.loading());

        addDisposable(
                repository.shareArticle(title, link)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resp -> {
                            if (resp.isSuccess()) {
                                status.setValue(Resource.success("分享成功"));
                            } else {
                                status.setValue(Resource.error(resp.getErrorMsg()));
                                LogUtil.error(AddShareViewModel.class, "分享文章失败：" + resp.getErrorMsg());
                            }
                        }, throwable -> {
                            status.setValue(Resource.error(throwable.getMessage()));
                            LogUtil.error(AddShareViewModel.class, "分享文章失败：" + throwable.getMessage());
                        })
        );
    }
}
