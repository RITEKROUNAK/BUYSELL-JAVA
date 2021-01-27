package com.panaceasoft.psbuyandsell.ui.subcategory.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.databinding.DataBindingUtil;

import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.databinding.ItemSubCategoryAdapterBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundViewHolder;
import com.panaceasoft.psbuyandsell.utils.Objects;
import com.panaceasoft.psbuyandsell.viewobject.ItemSubCategory;


public class SubCategoryAdapter extends DataBoundListAdapter<ItemSubCategory, ItemSubCategoryAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final SubCategoryClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;
    private int lastPosition = -1;

    public SubCategoryAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                              SubCategoryClickCallback callback,
                              DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;

    }

    @Override
    protected ItemSubCategoryAdapterBinding createBinding(ViewGroup parent) {
        ItemSubCategoryAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_sub_category_adapter, parent, false,
                        dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            ItemSubCategory subCategory = binding.getSubCategory();
            if (subCategory != null && callback != null) {
                callback.onClick(subCategory);
            }
        });
        return binding;
    }

    @Override
    public void bindView(DataBoundViewHolder<ItemSubCategoryAdapterBinding> holder, int position) {
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
    protected void bind(ItemSubCategoryAdapterBinding binding, ItemSubCategory item) {
        binding.setSubCategory(item);

    }

    @Override
    protected boolean areItemsTheSame(ItemSubCategory oldItem, ItemSubCategory newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.name.equals(newItem.name);
    }

    @Override
    protected boolean areContentsTheSame(ItemSubCategory oldItem, ItemSubCategory newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.name.equals(newItem.name);
    }

    public interface SubCategoryClickCallback {
        void onClick(ItemSubCategory subCategory);
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
