package com.panaceasoft.psbuyandsell.ui.category.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.databinding.ItemCityCategoryAdapterBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundViewHolder;
import com.panaceasoft.psbuyandsell.utils.Objects;
import com.panaceasoft.psbuyandsell.viewobject.ItemCategory;

import androidx.databinding.DataBindingUtil;

public class CityCategoryAdapter extends DataBoundListAdapter<ItemCategory, ItemCityCategoryAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final CityCategoryClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface ;
    private int lastPosition = -1;

    public CityCategoryAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                               CityCategoryClickCallback callback,
                               DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;
    }

    @Override
    protected ItemCityCategoryAdapterBinding createBinding(ViewGroup parent) {
        ItemCityCategoryAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_city_category_adapter, parent, false,
                        dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            ItemCategory category = binding.getItemCategory();
            if (category != null && callback != null) {
                callback.onClick(category);
            }
        });
        return binding;
    }

    // For general animation
    @Override
    public void bindView(DataBoundViewHolder<ItemCityCategoryAdapterBinding> holder, int position) {
        super.bindView(holder, position);

        //setAnimation(holder.itemView, position);
    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemCityCategoryAdapterBinding binding, ItemCategory item) {

        binding.setItemCategory(item);

        binding.itemCategoryImageView.setOnClickListener(view -> callback.onClick(item));

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

    public interface CityCategoryClickCallback {
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
