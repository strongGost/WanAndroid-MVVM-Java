package com.study.wanandroid.ui.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.databinding.ItemTabTextBinding;

public class TabTextAdapter extends BaseQuickAdapter<SystemBean, TabTextAdapter.VH> {
    private int selectPos = -1;  // 当前选中下标
    @NonNull
    @Override
    protected TabTextAdapter.VH onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return VH.create(context, viewGroup);
    }

    /**
     * 选中位置改变
     * @param selectPos
     */
    public void setSelectPos(int selectPos) {
        int oldPos = this.selectPos;
        this.selectPos = selectPos;
        if (selectPos >= 0) {
            notifyItemChanged(oldPos);
        }
        notifyItemChanged(selectPos);
    }

    @Override
    protected void onBindViewHolder(@NonNull TabTextAdapter.VH vh, int i, @Nullable SystemBean systemBean) {
        vh.bindTo(vh, systemBean, i, selectPos);
    }


    public static class VH extends RecyclerView.ViewHolder {
        private final ItemTabTextBinding binding;

        public VH(@NonNull ItemTabTextBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public static VH create(Context context, ViewGroup viewGroup) {
            ItemTabTextBinding binding = ItemTabTextBinding.inflate(LayoutInflater.from(context), viewGroup, false);
            return new VH(binding);
        }

        public void bindTo(VH vh, SystemBean bean, int pos, int selectPos) {
            binding.tvTabName.setText(bean.getName());
            vh.itemView.setSelected(pos == selectPos);
            binding.tvTabName.setSelected(pos == selectPos);
        }
    }
}
