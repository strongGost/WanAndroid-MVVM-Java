package com.study.wanandroid.ui.square.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.study.wanandroid.data.model.ArticleBean;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.databinding.ItemLabelTvBinding;

import java.util.List;

/**
 * label 文本 adapter
 * @param <T>
 */
public class ChildrenTextAdapter <T>  extends RecyclerView.Adapter<ChildrenTextAdapter.ViewHolder> {

    private LabelClassfiyAdapter.OnLabelClickListener listener;
    private List<T> data;
    private int parentPos;  // 当前体系所属二级体系 在总数据中的索引

    public ChildrenTextAdapter(List<T> data, int parentPos) {
        this.data = data;
        this.parentPos = parentPos;
    }

    public void setListener(LabelClassfiyAdapter.OnLabelClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(holder, listener, data.get(position), position, parentPos);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }



    public static class ViewHolder <T> extends RecyclerView.ViewHolder {
        ItemLabelTvBinding binding;
        public ViewHolder(@NonNull ItemLabelTvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public static ViewHolder create(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemLabelTvBinding binding = ItemLabelTvBinding.inflate(inflater, parent, false);
            return new ViewHolder(binding);
        }
        public void bindTo(ViewHolder vh, LabelClassfiyAdapter.OnLabelClickListener listener, T t, int pos, int parentPos) {
            // 标签点击
            vh.binding.tvLabel.setOnClickListener(v -> {
                if (listener != null)
                    listener.onClickLabel(t, pos, parentPos);
            });
            // 标签文本
            if (t instanceof SystemBean) {
                SystemBean bean = (SystemBean) t;
                binding.tvLabel.setText(bean.getName());
            } else if (t instanceof ArticleBean) {
                ArticleBean bean = (ArticleBean) t;
                binding.tvLabel.setText(bean.getTitle());
            }
        }
    }

}
