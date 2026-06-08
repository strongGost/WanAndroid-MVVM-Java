package com.study.wanandroid.ui.square.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.study.wanandroid.data.model.GuideBean;
import com.study.wanandroid.data.model.SystemBean;
import com.study.wanandroid.databinding.ItemLabelBinding;

import java.util.List;

/**
 * label 列表 adapter
 *
 * @param <T>
 */
public class LabelClassfiyAdapter<T> extends RecyclerView.Adapter<LabelClassfiyAdapter.VH> {

    private List<T> data;
    private OnLabelClickListener listener;

    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setListener(OnLabelClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return VH.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bindTo(holder, listener,  data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    public static class VH<T> extends RecyclerView.ViewHolder {
        private final ItemLabelBinding binding;
        public VH(@NonNull ItemLabelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public static VH create(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemLabelBinding binding = ItemLabelBinding.inflate(inflater, parent, false);
            return new VH(binding);
        }

        public void bindTo(VH vh, OnLabelClickListener listener, T t, int pos) {
            if (t instanceof SystemBean) {
                SystemBean data = (SystemBean) t;
                vh.binding.tvArticleTitle.setText(data.getName());
            } else if (t instanceof GuideBean) {
                GuideBean data = (GuideBean) t;
                vh.binding.tvArticleTitle.setText(data.getName());
            }
            /* 为内部 children 设置 adapter (复用管理器，但监听事件的对像不复用) */
            initChildren(vh, listener, t, pos);
        }


        /**
         * 为 内部 RecycleView -children 设置 adapter
         * @param vh
         * @param children
         */
        private void initChildren(VH vh, OnLabelClickListener listener, T children, int pos) {
            // recycle 设置 flexbox 管理器
            FlexboxLayoutManager flexboxManager = new FlexboxLayoutManager(vh.itemView.getContext());
            flexboxManager.setFlexDirection(FlexDirection.ROW);
            flexboxManager.setFlexWrap(FlexWrap.WRAP);
            flexboxManager.setJustifyContent(JustifyContent.FLEX_START);
            vh.binding.recycleChildren.setLayoutManager(flexboxManager);

            // flexbox 设置 adapter 数据
            ChildrenTextAdapter textAdapter = null;
            if (children instanceof SystemBean) {
                SystemBean bean = (SystemBean) children;
                textAdapter = new ChildrenTextAdapter(bean.getChildren(), pos);
            } else if (children instanceof GuideBean) {
                GuideBean bean = (GuideBean) children;
                textAdapter = new ChildrenTextAdapter(bean.getArticles(), pos);
            }
            // 设置 adapter 点击事件
            if (textAdapter != null && listener != null) {
                textAdapter.setListener(listener);
            }
            // 设置 adapter 到 recycleView
            binding.recycleChildren.setAdapter(textAdapter);
        }
    }

    /**
     * 文本点击事件
     */
    public static interface OnLabelClickListener <T> {
        /**
         * 标签点击事件
         * @param t
         * @param curPosition 当前点击的条目（二级分类下的索引）
         * @param parentPos 二级分类索引
         */
        void onClickLabel(T t, int curPosition, int parentPos);
    }
}
