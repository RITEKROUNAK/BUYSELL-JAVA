package com.panaceasoft.psbuyandsell.ui.item.promote.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.databinding.ItemPromoteHorizontalWithUserBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundViewHolder;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Objects;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewobject.ItemPaidHistory;

public class ItemPromoteHorizontalListAdapter  extends DataBoundListAdapter<ItemPaidHistory, ItemPromoteHorizontalWithUserBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final ItemPromoteHorizontalListAdapter.NewsClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;

    public ItemPromoteHorizontalListAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                            ItemPromoteHorizontalListAdapter.NewsClickCallback callback,
                                     DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;
    }

    @Override
    protected ItemPromoteHorizontalWithUserBinding createBinding(ViewGroup parent) {
        ItemPromoteHorizontalWithUserBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_promote_horizontal_with_user, parent, false,
                        dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            ItemPaidHistory itemPaidHistory = binding.getPaidHistory();
            if (itemPaidHistory != null && callback != null) {
                callback.onClick(itemPaidHistory);
            }
        });
        return binding;
        

    }


    @Override
    public void bindView(DataBoundViewHolder<ItemPromoteHorizontalWithUserBinding> holder, int position) {
        super.bindView(holder, position);
    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemPromoteHorizontalWithUserBinding binding, ItemPaidHistory itemPaidHistory) {

        binding.setPaidHistory(itemPaidHistory);

        binding.conditionTextView.setText(binding.getRoot().getResources().getString(R.string.item_condition__type, itemPaidHistory.item.itemCondition.name));
        String currencySymbol = itemPaidHistory.item.itemCurrency.currencySymbol;
        String price;
        try {
            price = Utils.format(Double.parseDouble(itemPaidHistory.item.price));
        } catch (Exception e) {
            price = itemPaidHistory.item.price;
        }

        String currencyPrice;
        if (Config.SYMBOL_SHOW_FRONT) {
            currencyPrice = currencySymbol + " " + price;
        } else {
            currencyPrice = price + " " + currencySymbol;
        }
        binding.priceTextView.setText(currencyPrice);


        String amount;
        try{
            amount = Utils.format(Double.parseDouble(itemPaidHistory.amount));
        }catch (Exception e) {
            amount = itemPaidHistory.amount;
        }

        String currencyAmount;
        if(Config.SYMBOL_SHOW_FRONT){
            currencyAmount = currencySymbol + " " + amount;
        } else{
            currencyAmount = amount + " " + currencySymbol;
        }
        binding.amountTextView.setText(currencyAmount);


        switch (itemPaidHistory.paidStatus) {
            case Constants.ADSPROGRESS:
                binding.isPaidTextView.setText(R.string.paid__ads_in_progress);
                binding.isPaidTextView.setBackgroundColor( binding.getRoot().getResources().getColor( R.color.paid_ad));
                break;
            case Constants.ADSFINISHED:
                binding.isPaidTextView.setText(R.string.paid__ads_in_completed);
                binding.isPaidTextView.setBackgroundColor( binding.getRoot().getResources().getColor( R.color.paid_ad_completed));
                break;
            case Constants.ADSNOTYETSTART:
                binding.isPaidTextView.setText(R.string.paid__ads_is_not_yet_start);
                binding.isPaidTextView.setBackgroundColor( binding.getRoot().getResources().getColor( R.color.paid_ad_is_not_start));

                break;
            default:
                binding.isPaidTextView.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    protected boolean areItemsTheSame(ItemPaidHistory oldItem, ItemPaidHistory newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.item.title.equals(newItem.item.title)
                && oldItem.item.isFavourited.equals(newItem.item.isFavourited)
                && oldItem.item.favouriteCount.equals(newItem.item.favouriteCount)
                && oldItem.item.isSoldOut.equals(newItem.item.isSoldOut);
    }

    @Override
    protected boolean areContentsTheSame(ItemPaidHistory oldItem, ItemPaidHistory newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.item.title.equals(newItem.item.title)
                && oldItem.item.isFavourited.equals(newItem.item.isFavourited)
                && oldItem.item.favouriteCount.equals(newItem.item.favouriteCount)
                && oldItem.item.isSoldOut.equals(newItem.item.isSoldOut);
    }

    public interface NewsClickCallback {
        void onClick(ItemPaidHistory itemPaidHistory);
    }


}



