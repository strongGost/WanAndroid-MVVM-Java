package com.study.wanandroid.ui.square.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.study.wanandroid.ui.square.guide.GuideFragment;
import com.study.wanandroid.ui.square.system.SystemFragment;

public class SquareStateAdapter extends FragmentStateAdapter {
    public SquareStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? SystemFragment.newInstance() : GuideFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
