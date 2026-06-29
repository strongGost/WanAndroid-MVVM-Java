package com.study.wanandroid.ui.me.college;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.study.wanandroid.MyApplication;
import com.study.wanandroid.R;
import com.study.wanandroid.base.BaseActivity;
import com.study.wanandroid.data.model.CollectBean;
import com.study.wanandroid.data.model.IBaseArticle;
import com.study.wanandroid.data.remote.Event;
import com.study.wanandroid.data.remote.Resource;
import com.study.wanandroid.data.remote.UIState;
import com.study.wanandroid.databinding.ActivityCollegeBinding;
import com.study.wanandroid.ui.WebViewActivity;
import com.study.wanandroid.ui.home.ArticleAdapter;
import com.study.wanandroid.ui.login.LoginActivity;
import com.study.wanandroid.utils.Constant  ;
import com.study.wanandroid.utils.SharePreferenceUtil;
import com.study.wanandroid.utils.ToastUtil;

public class CollegeActivity extends BaseActivity<ActivityCollegeBinding> {


    private CollectViewModel collectViewModel;
    private ArticleAdapter<CollectBean> articleAdapter;
    private String userName;   // 用户名

    @Override
    protected void initViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), ((v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        }));
        if (getIntent() != null) {
            userName = getIntent().getStringExtra(Constant.ME_INFO);
        }
        // 设置状态栏颜色、状态栏文字颜色
        MyApplication.updateStatusBar(this, getColor(R.color.main_color));
        MyApplication.updateStatusBarTextColor(this, false);
        initToolBar();
        initRecycleView();
        initSmartRefresh();
    }

    private void initToolBar() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle("");
    }

    private void initSmartRefresh() {
        //TODO: 需修复：页面数据为空，没有显示空视图、 没有数据时还能上滑加载数据
        binding.smartRefresh.setOnLoadMoreListener(refreshLayout -> collectViewModel.getCollegeArticles());
        binding.stateLayout.setListener(() -> collectViewModel.getCollegeArticles());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecycleView() {
        binding.recycleCollege.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        articleAdapter = new ArticleAdapter<>();
        articleAdapter.setOnItemClickListener((adapter, view, pos) -> {
            IBaseArticle item = articleAdapter.getItem(pos);
            Intent intent = new Intent(this, WebViewActivity.class)
                    .putExtra(Constant.EXTRA_URL, item.getDisplayLink());
            startActivity(intent);
        });
        // 收藏/取消收藏 事件
        articleAdapter.addOnItemChildClickListener(R.id.favorite, ((adapter, view, pos) -> {
            IBaseArticle bean = articleAdapter.getItem(pos);
            collectViewModel.collectAndUnCollect(bean.isCollected(), bean);
        }));
        binding.recycleCollege.setAdapter(articleAdapter);
        initCollapsingToolbar();
        // TODO：显示当前用户信息
    }

    private void initCollapsingToolbar() {  // 可删除（使用MotionLayout组件后）
        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int totalRange = appBarLayout.getTotalScrollRange();
            float progress = Math.abs(verticalOffset) * 1.0f / totalRange;   // 0~1

            // 展开布局逐渐淡出
            binding.layoutExpanded.setAlpha(1 - Math.min(progress * 1.8f, 1f));

            // ==================== 折叠后布局平滑进入 ====================
            float collapsedAlpha = Math.max((progress - 0.4f) / 0.6f, 0f);   // 从40%开始显示
            float translationX = (1 - collapsedAlpha) * 80;                 // 从右边往左移动

            binding.layoutCollapsed.setAlpha(collapsedAlpha);
            binding.layoutCollapsed.setTranslationX(-translationX);         // 负值表示向左移动

            // 可选：让小头像在折叠过程中轻微缩小（增加高级感）
            float scale = 1 - (collapsedAlpha * 0.15f); // 从1.0缩到0.85
            binding.ivAvatarCollapsed.setScaleX(scale);
            binding.ivAvatarCollapsed.setScaleY(scale);

            // 设置用户名（建议只设置一次）
            if (binding.tvUsernameCollapsed.getText().length() == 0) {
                binding.tvUsernameCollapsed.setText(userName);
            }
        });
    }

    @Override
    protected ActivityCollegeBinding getViewBinding() {
        return ActivityCollegeBinding.inflate(getLayoutInflater());
    }


    @Override
    protected void initViewModel() {
        collectViewModel = new ViewModelProvider(this).get(CollectViewModel.class);
        collectViewModel.getArticles().observe(this, data -> articleAdapter.submitList(data));
        collectViewModel.getListStatus().observe(this, status -> {  // 加载文章列表
           if (binding.smartRefresh.isLoading()) {  // 组件触发
               switch (status.getState()) {
                   case SUCCESS:
                       binding.smartRefresh.finishLoadMore(true);
                       break;
                   case ERROR:
                       binding.smartRefresh.finishLoadMore(false);
                       break;
                   case EMPTY:
                       binding.smartRefresh.finishLoadMoreWithNoMoreData();
                       break;
               }
           } else { // 页面触发
               binding.stateLayout.switchView(status.getState());
           }
        });
        collectViewModel.getStatus().observe(this, event -> {
            Resource status = event.getContentIfNotHandled();
            if (status == null) return;
            ToastUtil.show(CollegeActivity.this, status.getMsg());
            if (status.getState() == UIState.NEED_LOGIN) {    // 需要登录
                Intent intent = new Intent(CollegeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void initData() {
        collectViewModel.getCollegeArticles();
    }
}