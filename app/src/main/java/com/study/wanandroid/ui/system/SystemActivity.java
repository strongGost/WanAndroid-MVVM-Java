package com.study.wanandroid.ui.system;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.study.wanandroid.MyApplication;
import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseActivity;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.databinding.ActivitySystemBinding;
import com.study.wanandroid.ui.home.ArticleAdapter;
import com.study.wanandroid.ui.project.adaper.CategoryStateAdapter;
import com.study.wanandroid.utils.Constant;
import com.study.wanandroid.utils.LogUtil;

import java.util.List;

/**
 * 指定体系下的不同类型文章
 */
public class SystemActivity extends BaseActivity<ActivitySystemBinding> {

    private SystemBean system;
    private int pos;    // 当前选择的 tab

    @Override
    protected void initViews() {
        // 设置边距
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets)-> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 设置状态栏颜色
        MyApplication.updateStatusBar(this, getColor(R.color.main_color));
        if (getIntent() != null) {
            pos = getIntent().getIntExtra(Constant.EXTRA_CID, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                system = getIntent().getParcelableExtra(Constant.Data_KEY, SystemBean.class);
            } else {
                system = getIntent().getParcelableExtra(Constant.Data_KEY);
            }
        }
        binding.ivBack.setOnClickListener(v-> finish());
        binding.tvSystemName.setText(system.getName());
        TabFragmentStateAdapter adapter = new TabFragmentStateAdapter(this, system);
        binding.vp2.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.vp2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                LogUtil.info(SystemActivity.class, system.toString());
                tab.setText(system.getChildren().get(position).getName());
            }
        }).attach();
        // 设置默认选中
//        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(pos)); // 通过 tabLayout选中会触发 ViewPager2的切换动画
        binding.vp2.setCurrentItem(pos, false); // 当前切换禁用切换动画
    }

    @Override
    protected ActivitySystemBinding getViewBinding() {
        return ActivitySystemBinding.inflate(getLayoutInflater())   ;
    }


}