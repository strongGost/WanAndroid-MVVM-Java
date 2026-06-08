package com.study.wanandroid.ui.home;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.study.wanandroid.R;
import com.study.wanandroid.data.model.IBaseArticle;
import com.study.wanandroid.databinding.ItemArticleBinding;

public class ArticleAdapter<T extends IBaseArticle> extends BaseQuickAdapter<T, ArticleAdapter.AVH> {


    public ArticleAdapter() {
        super(new DiffUtil.ItemCallback<T>() {
            @Override
            public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return oldItem.getUniqueId().equals(newItem.getUniqueId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    protected AVH onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        ItemArticleBinding articleBinding = ItemArticleBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        return new AVH(articleBinding);
    }

    @Override
    protected void onBindViewHolder(@NonNull AVH viewHolder, int position, @Nullable T article) {
        if(article != null) {
            viewHolder.articleBinding.tvAuthor.setText(article.getPrimaryInfo());
            viewHolder.articleBinding.tvNew.setVisibility(article.isNew() ? View.VISIBLE : View.GONE);
            viewHolder.articleBinding.tvTitle.setText(article.getDisplayTitle());
            viewHolder.articleBinding.tvUp.setVisibility(article.isTop() ? View.VISIBLE : View.GONE);
            viewHolder.articleBinding.tvChapter.setText(article.getSecondaryInfo());
            viewHolder.articleBinding.tvTime.setText(article.getDisplayDate());
            viewHolder.articleBinding.favorite.setImageResource(article.isCollected() ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);
        }
    }


    /**
     * Item_Article 的 ViewHolder
     */
    public static class AVH extends RecyclerView.ViewHolder {
        private final ItemArticleBinding articleBinding;
        public AVH(@NonNull ItemArticleBinding binding) {
            super(binding.getRoot());
            this.articleBinding = binding;
        }
    }

}
