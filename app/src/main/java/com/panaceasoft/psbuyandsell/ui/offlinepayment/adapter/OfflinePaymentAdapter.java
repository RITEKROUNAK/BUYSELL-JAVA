package com.panaceasoft.psbuyandsell.ui.offlinepayment.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.databinding.ItemOfflinepaymentBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Objects;
import com.panaceasoft.psbuyandsell.viewobject.OfflinePayment;

public class OfflinePaymentAdapter extends DataBoundListAdapter<OfflinePayment, ItemOfflinepaymentBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private OfflinePaymentClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;

    public OfflinePaymentAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                   OfflinePaymentClickCallback callback,
                                   DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;

    }

    @Override
    protected ItemOfflinepaymentBinding createBinding(ViewGroup parent) {
        ItemOfflinepaymentBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_offlinepayment, parent, false,
                        dataBindingComponent);

        binding.getRoot().setOnClickListener(v -> {
            OfflinePayment offlinePayment = binding.getOfflinePayment();
            if (offlinePayment != null && callback != null) {
                callback.onClick(offlinePayment);
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
    protected void bind(ItemOfflinepaymentBinding binding, OfflinePayment offlinePaymentMethod) {
        binding.setOfflinePayment(offlinePaymentMethod);
        if (offlinePaymentMethod.equals(Constants.ONE)) {
            binding.OfflinePaymentConstraintLayout.setBackgroundColor(binding.getRoot().getResources().getColor(R.color.md_grey_200));
        } else {
            binding.OfflinePaymentConstraintLayout.setBackgroundColor(binding.getRoot().getResources().getColor(R.color.md_white_1000));
        }
        binding.titleTextView.setText(offlinePaymentMethod.title);
        binding.descriptionTextView.setText(offlinePaymentMethod.description);


    }

    @Override
    protected boolean areItemsTheSame(OfflinePayment oldItem, OfflinePayment newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.title.equals(newItem.title)
                && oldItem.description.equals(newItem.description);
    }

    @Override
    protected boolean areContentsTheSame(OfflinePayment oldItem, OfflinePayment newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.title.equals(newItem.title)
                && oldItem.description.equals(newItem.description);
    }


    public interface OfflinePaymentClickCallback {
        void onClick(OfflinePayment offlinePaymentMethod);
    }
}
