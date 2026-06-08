package com.study.wanandroid.ui.me.score;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.study.wanandroid.data.model.ScoreBean;
import com.study.wanandroid.databinding.ItemScoreBinding;

public class ScoreAdapter extends BaseQuickAdapter<ScoreBean, ScoreAdapter.ViewHolder> {

    public ScoreAdapter() {
        super(new DiffUtil.ItemCallback<ScoreBean>() {
            @Override
            public boolean areItemsTheSame(@NonNull ScoreBean oldItem, @NonNull ScoreBean newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull ScoreBean oldItem, @NonNull ScoreBean newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    protected ScoreAdapter.ViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return ViewHolder.create(inflater, viewGroup);
    }

    @Override
    protected void onBindViewHolder(@NonNull ScoreAdapter.ViewHolder viewHolder, int i, @Nullable ScoreBean scoreBean) {
            viewHolder.bindTo(getItem(i));
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemScoreBinding binding;

        public ViewHolder(@NonNull ItemScoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public static ViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            ItemScoreBinding scoreBinding = ItemScoreBinding.inflate(inflater, parent, false);
            return new ViewHolder(scoreBinding);
        }

        public void bindTo(ScoreBean item) {
            binding.tvReason.setText(item.getReason());
            binding.tvAddScore.setText(String.format("+%d", item.getCoinCount()));
            try {
                String[] desc = item.getDesc().split(" , ");
                binding.tvDatetime.setText(desc[0].split(" ")[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
