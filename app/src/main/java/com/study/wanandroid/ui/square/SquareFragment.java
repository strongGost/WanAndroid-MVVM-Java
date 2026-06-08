package com.study.wanandroid.ui.square;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseFragment;
import com.study.wanandroid.databinding.FragmentSquareBinding;
import com.study.wanandroid.ui.square.adapter.SquareStateAdapter;

public class SquareFragment extends BaseFragment<FragmentSquareBinding> {


    /**
     * ViewPager2设置适配器
     */
    private void initViewPager2() {
        SquareStateAdapter adapter = new SquareStateAdapter(this);
        binding.vp2.setAdapter(adapter);
    }

    /**
     * 联动 TabLayout 与 viewpage2
     */
    private void initTabLayout() {
        new TabLayoutMediator(binding.tabLayout, binding.vp2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(position == 0 ? R.string.system_tx : R.string.guide_tx);
            }
        }).attach();
    }



    @Override
    protected FragmentSquareBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSquareBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initViews() {
        // 设置边距
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets)-> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViewPager2();
        initTabLayout();
    }
}
