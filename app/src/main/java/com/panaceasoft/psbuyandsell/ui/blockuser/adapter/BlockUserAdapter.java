package com.panaceasoft.psbuyandsell.ui.blockuser.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.databinding.ItemBlockUserAdapterBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.utils.Objects;
import com.panaceasoft.psbuyandsell.viewobject.BlockUser;


public class BlockUserAdapter extends DataBoundListAdapter<BlockUser, ItemBlockUserAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private BlockUserClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface = null;

    public BlockUserAdapter(androidx.databinding.DataBindingComponent dataBindingComponent, BlockUserClickCallback blockUserClickCallback) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = blockUserClickCallback;
    }

    @Override
    protected ItemBlockUserAdapterBinding createBinding(ViewGroup parent) {
        ItemBlockUserAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_block_user_adapter, parent, false,
                        dataBindingComponent);

        binding.getRoot().setOnClickListener(v -> {
            BlockUser blockUser = binding.getBlockuser();
            if (blockUser != null && callback != null) {
                callback.onClick(blockUser);
            }

        });

        binding.unblockUserButton.setOnClickListener( v->{
            BlockUser blockUser = binding.getBlockuser();
            if (blockUser != null && callback != null) {
                callback.onUnBlockClick(blockUser);
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
    protected void bind(ItemBlockUserAdapterBinding binding, BlockUser item) {
        binding.setBlockuser(item);
        binding.blockuserNameTextView.setText(item.userName);

    }


    @Override
    protected boolean areItemsTheSame(BlockUser oldItem, BlockUser newItem) {
        return Objects.equals(oldItem.userId, newItem.userId)
                && oldItem.userName.equals(newItem.userName);
    }

    @Override
    protected boolean areContentsTheSame(BlockUser oldItem, BlockUser newItem) {
        return Objects.equals(oldItem.userId, newItem.userId)
                && oldItem.userName.equals(newItem.userName);
    }

    public interface BlockUserClickCallback {
        void onClick(BlockUser blockUser);
        void onUnBlockClick(BlockUser blockUser);
    }

}

