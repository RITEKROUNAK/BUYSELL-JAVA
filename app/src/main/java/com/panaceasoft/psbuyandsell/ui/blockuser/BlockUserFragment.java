package com.panaceasoft.psbuyandsell.ui.blockuser;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.MainActivity;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentBlockUserBinding;
import com.panaceasoft.psbuyandsell.ui.blockuser.adapter.BlockUserAdapter;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.blockuser.BlockUserViewModel;
import com.panaceasoft.psbuyandsell.viewobject.BlockUser;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import java.util.List;

public class BlockUserFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface{

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private BlockUserViewModel blockUserViewModel;
    private PSDialogMsg psDialogMsg;



    @VisibleForTesting
    private AutoClearedValue<FragmentBlockUserBinding> binding;
    private AutoClearedValue<BlockUserAdapter> blockUserAdapter;
    AutoClearedValue<ProgressDialog> progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentBlockUserBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_block_user, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }
    @Override
    public void onDispatched() {
        if (blockUserViewModel.loadingDirection == Utils.LoadingDirection.bottom) {

            if (binding.get().blockUserRecyclerView != null) {

                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        binding.get().blockUserRecyclerView.getLayoutManager();

                if (layoutManager != null) {
                    layoutManager.scrollToPosition(0);
                }
            }
        }
    }

    @Override
    protected void initUIAndActions() {

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        progressDialog = new AutoClearedValue<>(this, new ProgressDialog(getActivity()));
        progressDialog.get().setMessage(getString(R.string.message__please_wait));
        progressDialog.get().setCancelable(false);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setToolbarText(((MainActivity) getActivity()).binding.toolbar, getString(R.string.menu__blocked_users));
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.global__primary));
            ((MainActivity) getActivity()).updateMenuIconWhite();
            ((MainActivity) getActivity()).updateToolbarIconColor(Color.WHITE);
            ((MainActivity) getActivity()).refreshPSCount();
        }



        binding.get().blockUserRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                if (layoutManager != null) {

                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();
                    if (lastPosition == blockUserAdapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !blockUserViewModel.forceEndLoading) {

                            blockUserViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                            int limit = Config.BLOCK_USER_COUNT;
                            blockUserViewModel.offset = blockUserViewModel.offset + limit;
                            blockUserViewModel.setLoadingState(true);
                            blockUserViewModel.setNextPageBlockUserObj(String.valueOf(Config.BLOCK_USER_COUNT),
                                    String.valueOf(blockUserViewModel.offset),loginUserId);
                        }
                    }
                }
            }
        });



        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            blockUserViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset productViewModel.offset

            blockUserViewModel.offset = 0;

            // reset productViewModel.forceEndLoading
            blockUserViewModel.forceEndLoading = false;

            blockUserViewModel.setBlockUserObj(String.valueOf(Config.BLOCK_USER_COUNT), String.valueOf(blockUserViewModel.offset),loginUserId);

            // update live data

        });
    }

    @Override
    protected void initViewModels() {
        blockUserViewModel = new ViewModelProvider(this,viewModelFactory).get(BlockUserViewModel.class);

    }

    @Override
    protected void initAdapters() {
        BlockUserAdapter blockUserAdapter = new BlockUserAdapter(dataBindingComponent,
                new BlockUserAdapter.BlockUserClickCallback() {
                    @Override
                    public void onClick(BlockUser blockUser) {
                        navigationController.navigateToUserDetail(BlockUserFragment.this.getActivity(), blockUser.userId, blockUser.userName);
                    }

                    @Override
                    public void onUnBlockClick(BlockUser blockUser) {


                        PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
                        psDialogMsg.showConfirmDialog(getString(R.string.unblock_confirm_dialig),getString(R.string.app__ok),getString(R.string.app__cancel));
                        psDialogMsg.show();


                        psDialogMsg.okButton.setOnClickListener(v -> {
                            psDialogMsg.cancel();
                            blockUserViewModel.setUnblockUserStatusObj(loginUserId,blockUser.userId);

                        });

                        psDialogMsg.cancelButton.setOnClickListener(v -> psDialogMsg.cancel());
                    }
                });
        this.blockUserAdapter = new AutoClearedValue<>(this, blockUserAdapter);
        binding.get().blockUserRecyclerView.setAdapter(blockUserAdapter);

    }


    @Override
    protected void initData() {


        blockUserViewModel.setBlockUserObj(String.valueOf(Config.BLOCK_USER_COUNT),String.valueOf(blockUserViewModel.offset),loginUserId);
        blockUserViewModel.getBlockUserData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {
                    case SUCCESS:
                        replaceItemBlockUserData(result.data);
                        blockUserViewModel.setLoadingState(false);
                        break;

                    case LOADING:
                        replaceItemBlockUserData(result.data);
                        break;

                    case ERROR:

                        blockUserViewModel.setLoadingState(false);
                        break;
                }
            }

        });


        blockUserViewModel.getUnblockUserStatusData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {
                    case LOADING:
                        break;

                    case SUCCESS:

                        progressDialog.get().cancel();
                        break;

                    case ERROR:
                        PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
                        psDialogMsg.showErrorDialog(result.message, getString(R.string.message__ok_close));

                        progressDialog.get().cancel();
                        psDialogMsg.show();

                        break;
                }
            }
        });

        blockUserViewModel.getNextPageBlockUserData().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {

                    blockUserViewModel.setLoadingState(false);
                    blockUserViewModel.forceEndLoading = true;
                }
            }
        });


        blockUserViewModel.getLoadingState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loadingState) {

                binding.get().setLoadingMore(blockUserViewModel.isLoading);

                if (loadingState != null && !loadingState) {
                    binding.get().swipeRefresh.setRefreshing(false);
                }

            }
        });

    }

    private void replaceItemBlockUserData(List<BlockUser> blockUsers) {
        blockUserAdapter.get().replace(blockUsers);
        binding.get().executePendingBindings();

    }
}

