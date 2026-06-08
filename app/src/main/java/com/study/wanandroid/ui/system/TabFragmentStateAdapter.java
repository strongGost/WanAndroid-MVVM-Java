package com.study.wanandroid.ui.system;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.study.wanandroid.data.model.SystemBean;

public class TabFragmentStateAdapter extends FragmentStateAdapter {
    private final SystemBean data; // 一级分类数据

    public TabFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, SystemBean system) {
        super(fragmentActivity);
        this.data = system;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ChildrenFragment.newInstance(data.getChildren().get(position));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : (data.getChildren() == null ? 0 : data.getChildren().size());
    }
}
