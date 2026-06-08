package com.study.wanandroid.ui.project.adaper;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.ui.project.ProjectListFragment;

import java.util.List;

public class CategoryStateAdapter extends FragmentStateAdapter {
    private List<SystemBean> data;

    public CategoryStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        SystemBean systemBean = data.get(position);
        return ProjectListFragment.newInstance(systemBean.getId());
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }



    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<SystemBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    /**
     * @param position
     * @return 当前点击下标的item
     */
    public SystemBean getItem(int position) {
        if (data != null && data.size() > position) {
            return data.get(position);
        }
        return null;
    }
}
