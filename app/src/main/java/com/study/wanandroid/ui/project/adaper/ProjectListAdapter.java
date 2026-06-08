package com.study.wanandroid.ui.project.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.study.wanandroid.R;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.databinding.ItemProjectBinding;

public class ProjectListAdapter extends BaseQuickAdapter<ArticleBean, ProjectListAdapter.VH> {
    @NonNull
    @Override
    protected VH onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return VH.create(context, viewGroup);
    }

    @Override
    protected void onBindViewHolder(@NonNull VH vh, int i, @Nullable ArticleBean articleBean) {
        vh.bindTo(vh, getItem(i));
    }

    public static class VH extends RecyclerView.ViewHolder {
        private ItemProjectBinding binding;
        public VH(@NonNull ItemProjectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public static VH create(Context context, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            ItemProjectBinding binding = ItemProjectBinding.inflate(inflater, parent, false);
            return new VH(binding);
        }

        public void bindTo(VH vh, ArticleBean item) {
            //TODO: 图像优化占位符
            Glide.with(vh.itemView)
                    .load(item.getEnvelopePic())
                    .transform(new CenterCrop(), new RoundedCorners(12))
                    .into(binding.ivAvator);
            binding.tvTitle.setText(item.getTitle());
            binding.tvAuthor.setText(item.getAuthor());
            binding.tvDesc.setText(item.getDesc());
            binding.ivFavorite.setImageResource(item.isCollect() ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);
        }
    }
}
