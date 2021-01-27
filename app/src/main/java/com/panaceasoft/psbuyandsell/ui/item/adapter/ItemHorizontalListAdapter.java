package com.panaceasoft.psbuyandsell.ui.item.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.databinding.ItemItemHorizontalWithUserBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundViewHolder;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Objects;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewobject.Item;

public class ItemHorizontalListAdapter extends DataBoundListAdapter<Item, ItemItemHorizontalWithUserBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final ItemHorizontalListAdapter.NewsClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;

    public ItemHorizontalListAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                     ItemHorizontalListAdapter.NewsClickCallback callback,
                                     DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;
    }

    @Override
    protected ItemItemHorizontalWithUserBinding createBinding(ViewGroup parent) {
        ItemItemHorizontalWithUserBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_item_horizontal_with_user, parent, false,
                        dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            Item item = binding.getItem();
            if (item != null && callback != null) {
                callback.onClick(item);
            }
        });
        return binding;


    }


    @Override
    public void bindView(DataBoundViewHolder<ItemItemHorizontalWithUserBinding> holder, int position) {
        super.bindView(holder, position);
    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemItemHorizontalWithUserBinding binding, Item item) {

        binding.setItem(item);

        binding.conditionTextView.setText(binding.getRoot().getResources().getString(R.string.item_condition__type, item.itemCondition.name));
        String currencySymbol = item.itemCurrency.currencySymbol;
        String price;
        try {
            price = Utils.format(Double.parseDouble(item.price));
        } catch (Exception e) {
            price = item.price;
        }

        String currencyPrice;
        if (Config.SYMBOL_SHOW_FRONT) {
            currencyPrice = currencySymbol + " " + price;
        } else {
            currencyPrice = price + " " + currencySymbol;
        }

        if(!item.price.equals("0") && !item.price.equals("")) {
            binding.priceTextView.setText(currencyPrice);
        } else {
            binding.priceTextView.setText(R.string.item_price_free);
        }

        if (item.isSoldOut.equals(Constants.ONE)) {
            binding.isSoldTextView.setVisibility(View.VISIBLE);
        } else {
            binding.isSoldTextView.setVisibility(View.GONE);
        }

        if (item.paidStatus.equals(Constants.ADSPROGRESS)){
            binding.addedDateStrTextView.setText(R.string.paid__sponsored);
        } else{
            binding.addedDateStrTextView.setText(item.addedDateStr);
        }
    }

    @Override
    protected boolean areItemsTheSame(Item oldItem, Item newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.title.equals(newItem.title)
                && oldItem.isFavourited.equals(newItem.isFavourited)
                && oldItem.favouriteCount.equals(newItem.favouriteCount)
                && oldItem.isSoldOut.equals(newItem.isSoldOut)
                && oldItem.addedUserId.equals(newItem.addedUserId);
    }

    @Override
    protected boolean areContentsTheSame(Item oldItem, Item newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.title.equals(newItem.title)
                && oldItem.isFavourited.equals(newItem.isFavourited)
                && oldItem.favouriteCount.equals(newItem.favouriteCount)
                && oldItem.isSoldOut.equals(newItem.isSoldOut)
                && oldItem.addedUserId.equals(newItem.addedUserId);
    }

    public interface NewsClickCallback {
        void onClick(Item item);
    }


}


