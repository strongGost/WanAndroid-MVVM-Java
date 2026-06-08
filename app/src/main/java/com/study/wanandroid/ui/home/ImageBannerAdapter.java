package com.study.wanandroid.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter4.BaseSingleItemAdapter;
import com.study.wanandroid.R;
import com.study.wanandroid.data.model.BannerBean;
import com.study.wanandroid.databinding.ViewBannerBinding;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.List;

public class ImageBannerAdapter extends  BaseSingleItemAdapter<List<BannerBean>, ImageBannerAdapter.VH> {

    private final LifecycleOwner lifecycleOwner;

    ImageBannerAdapter(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }


    @NonNull
    @Override
    protected ImageBannerAdapter.VH onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        ViewBannerBinding binding = ViewBannerBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        return new VH(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull ImageBannerAdapter.VH holder, @Nullable List<BannerBean> banners) {
        if (banners != null && !banners.isEmpty()) {
            /* 为 Banner 设置 adapter */
            holder.binding.banner.setAdapter(new BannerImageAdapter<BannerBean>(banners) {
                @Override
                public void onBindView(BannerImageHolder holder, BannerBean banner, int position, int size) {
                    if (banner != null) {
                        // TODO: 后续考虑显示 Banner 占位符（加载中、加载失败）
                        Glide.with(holder.imageView)
                                .load(banner.getImagePath())
                                // .placeholder() // 加上占位图
                                // .error()             // 加上错误图
                                .into(holder.imageView);
                    }
                }
            }).setIndicator(new CircleIndicator(getContext()))
                .setIndicatorSelectedColorRes(R.color.main_color)
                .addBannerLifecycleObserver(lifecycleOwner);

        }
    }

    public static class VH extends RecyclerView.ViewHolder {
        ViewBannerBinding binding;
        public VH(ViewBannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
