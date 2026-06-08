package com.study.wanandroid.ui.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.databinding.ItemWxArticleBinding;

public class WxArticleAdapter extends BaseQuickAdapter<ArticleBean, WxArticleAdapter.VH> {

    public WxArticleAdapter() {
        super(new DiffUtil.ItemCallback<ArticleBean>() {
            @Override
            public boolean areItemsTheSame(@NonNull ArticleBean oldItem, @NonNull ArticleBean newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull ArticleBean oldItem, @NonNull ArticleBean newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    protected WxArticleAdapter.VH onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return VH.create(context, viewGroup);
    }

    @Override
    protected void onBindViewHolder(@NonNull WxArticleAdapter.VH vh, int i, @Nullable ArticleBean articleBean) {
        vh.bindTo(articleBean);
    }

    public static class VH extends RecyclerView.ViewHolder {
        private final ItemWxArticleBinding binding;

        public VH(ItemWxArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public static VH create(Context context, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            ItemWxArticleBinding binding = ItemWxArticleBinding.inflate(inflater, parent, false);
            return new VH(binding);
        }


        public void bindTo(ArticleBean article) {
            binding.tvAuthor.setText(article.getAuthor());
            binding.tvTime.setText(article.getNiceDate());
            binding.tvTitle.setText(article.getTitle());
        }
    }
}
