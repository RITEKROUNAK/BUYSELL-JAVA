package com.panaceasoft.psbuyandsell.ui.item.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.databinding.ItemItemTagAdapterBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.utils.Objects;
import com.panaceasoft.psbuyandsell.viewobject.holder.TabObject;

public class ItemTagAdapter extends DataBoundListAdapter<TabObject, ItemItemTagAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private SimilarItemClickCallBack callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface = null;


    public ItemTagAdapter(androidx.databinding.DataBindingComponent dataBindingComponent, SimilarItemClickCallBack similarItemClickCallBack) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = similarItemClickCallBack;
    }

    @Override
    protected ItemItemTagAdapterBinding createBinding(ViewGroup parent) {
        ItemItemTagAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_item_tag_adapter, parent, false,
                        dataBindingComponent);

        binding.getRoot().setOnClickListener((View v) -> {
            TabObject tabObject = binding.getTab();
            if (tabObject != null && callback != null) {

                callback.onClick(tabObject, tabObject.tag_id);
            }
        });
        return binding;
    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemItemTagAdapterBinding binding, TabObject item) {
        binding.setTab(item);
    }

    @Override
    protected boolean areItemsTheSame(TabObject oldItem, TabObject newItem) {
        return Objects.equals(oldItem.tag_id, newItem.tag_id)
                && oldItem.tag_name.equals(newItem.tag_name);
    }

    @Override
    protected boolean areContentsTheSame(TabObject oldItem, TabObject newItem) {
        return Objects.equals(oldItem.tag_id, newItem.tag_id)
                && oldItem.tag_name.equals(newItem.tag_name);
    }

    public interface SimilarItemClickCallBack {
        void onClick(TabObject tabObject, String selectedTabId);
    }


}
