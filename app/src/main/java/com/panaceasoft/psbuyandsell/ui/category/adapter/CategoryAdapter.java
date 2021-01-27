package com.panaceasoft.psbuyandsell.ui.category.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.databinding.DataBindingUtil;

import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.databinding.ItemCategoryAdapterBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundViewHolder;
import com.panaceasoft.psbuyandsell.utils.Objects;
import com.panaceasoft.psbuyandsell.viewobject.ItemCategory;

public class CategoryAdapter extends DataBoundListAdapter<ItemCategory, ItemCategoryAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final CategoryClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;
    private int lastPosition = -1;


    public CategoryAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                           CategoryClickCallback callback,
                           DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;
    }

    @Override
    protected ItemCategoryAdapterBinding createBinding(ViewGroup parent) {
        ItemCategoryAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_category_adapter, parent, false,
                        dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            ItemCategory category = binding.getCategory();
            if (category != null && callback != null) {
                callback.onClick(category);
            }
        });
        return binding;
    }

    @Override
    public void bindView(DataBoundViewHolder<ItemCategoryAdapterBinding> holder, int position) {
        super.bindView(holder, position);

        setAnimation(holder.itemView, position);
    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemCategoryAdapterBinding binding, ItemCategory item) {
        binding.setCategory(item);

    }

    @Override
    protected boolean areItemsTheSame(ItemCategory oldItem, ItemCategory newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.name.equals(newItem.name);
    }

    @Override
    protected boolean areContentsTheSame(ItemCategory oldItem, ItemCategory newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.name.equals(newItem.name);
    }

    public interface CategoryClickCallback {
        void onClick(ItemCategory category);
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        } else {
            lastPosition = position;
        }
    }
}
