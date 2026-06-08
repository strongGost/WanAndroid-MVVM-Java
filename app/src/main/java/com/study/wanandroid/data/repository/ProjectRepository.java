package com.study.wanandroid.data.repository;

import com.study.wanandroid.base.BaseRepository;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.PageDataBean;
import com.study.wanandroid.data.model.ResponseBean;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.data.remote.RetrofitClient;
import com.study.wanandroid.data.remote.api.ProjectService;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class ProjectRepository extends BaseRepository {
    private final ProjectService service;
    private static ProjectRepository instance;

    private ProjectRepository() {
        service = RetrofitClient.getInstance().getService(ProjectService.class);
    }

    public static ProjectRepository getInstance() {
        if (instance == null) {
            synchronized (ProjectRepository.class) {
                if (instance == null) {
                    instance = new ProjectRepository();
                }
            }
        }
        return instance;
    }

    public Observable<ResponseBean<List<SystemBean>>> getCategory() {
        return service.getProjectCategory();
    }

    public Observable<ResponseBean<PageDataBean<List<ArticleBean>>>> getProjectList(int page, int cid) {
        return service.getProjectList(page, cid);
    }
}
