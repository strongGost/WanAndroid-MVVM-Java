package com.study.wanandroid.ui.me.score;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseSingleItemAdapter;
import com.study.wanandroid.MyApplication;
import com.study.wanandroid.databinding.ViewScoreBinding;

public class SingleItemViewAdapter<T> extends BaseSingleItemAdapter<T, SingleItemViewAdapter.ViewHolder> {
    @Override
    protected void onBindViewHolder(@NonNull SingleItemViewAdapter.ViewHolder viewHolder, @Nullable T t) {
        if (t instanceof Integer)
            MyApplication.startAnim(viewHolder.binding.tvScoreValue, (int) t, 2000);
    }

    @NonNull
    @Override
    protected SingleItemViewAdapter.ViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewScoreBinding binding = ViewScoreBinding.inflate(inflater, viewGroup, false);
        return new ViewHolder(binding);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewScoreBinding binding;
        public ViewHolder(@NonNull ViewScoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
