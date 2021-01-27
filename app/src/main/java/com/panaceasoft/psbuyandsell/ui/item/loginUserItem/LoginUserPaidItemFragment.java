package com.panaceasoft.psbuyandsell.ui.item.loginUserItem;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.MainActivity;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentLoginUserPaidItemBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.ui.item.promote.adapter.ItemPromoteVerticalListAdapter;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.ItemPaidHistoryViewModel.ItemPaidHistoryViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ItemPaidHistory;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import java.util.List;

public class LoginUserPaidItemFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private ItemPaidHistoryViewModel itemPaidHistoryViewModel;

    @VisibleForTesting
    private AutoClearedValue<FragmentLoginUserPaidItemBinding> binding;
    private AutoClearedValue<ItemPromoteVerticalListAdapter> itemPromoteVerticalListAdapter;

    //endregion

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentLoginUserPaidItemBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_user_paid_item, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        binding.get().setLoadingMore(connectivity.isConnected());

        return binding.get().getRoot();

        
    }

    @Override
    public void onDispatched() {
        if (itemPaidHistoryViewModel.loadingDirection == Utils.LoadingDirection.top) {

            if (binding.get().loginUserPaidItemRecycler != null) {

                GridLayoutManager layoutManager = (GridLayoutManager)
                        binding.get().loginUserPaidItemRecycler.getLayoutManager();

                if (layoutManager != null) {
                    layoutManager.scrollToPosition(0);
                }
            }
        }
    }

    @Override
    protected void initUIAndActions() {

        if (getActivity() instanceof MainActivity) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.global__primary));
            ((MainActivity) getActivity()).updateToolbarIconColor(Color.WHITE);
            ((MainActivity) getActivity()).updateMenuIconWhite();
            ((MainActivity) getActivity()).refreshPSCount();
        }

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            binding.get().adView.loadAd(adRequest);

        } else {
            binding.get().adView.setVisibility(View.GONE);
        }

        binding.get().loginUserPaidItemRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager layoutManager = (GridLayoutManager)
                        recyclerView.getLayoutManager();

                if (layoutManager != null) {

                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();

                    if (lastPosition == itemPromoteVerticalListAdapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !itemPaidHistoryViewModel.forceEndLoading) {

                            if (connectivity.isConnected()) {
                                itemPaidHistoryViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                                int limit = Config.ITEM_COUNT;
                                itemPaidHistoryViewModel.offset = itemPaidHistoryViewModel.offset + limit;
                                itemPaidHistoryViewModel.setNextPagePaidItemHistory(Utils.checkUserId(loginUserId),String.valueOf(Config.ITEM_COUNT), String.valueOf(itemPaidHistoryViewModel.offset));
                            }
                        }
                    }
                }
            }
        });
        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            itemPaidHistoryViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset productViewModel.offset
            itemPaidHistoryViewModel.offset = 0;

            // reset productViewModel.forceEndLoading
            itemPaidHistoryViewModel.forceEndLoading = false;

            // update live data
            itemPaidHistoryViewModel.setPaidItemHistory(Utils.checkUserId(loginUserId), String.valueOf(Config.ITEM_COUNT), String.valueOf(itemPaidHistoryViewModel.offset));

        });
    }

    @Override
    protected void initViewModels() {
        itemPaidHistoryViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemPaidHistoryViewModel.class);
    }

    @Override
    protected void initAdapters() {
        ItemPromoteVerticalListAdapter itemPromoteVerticalListAdapter = new ItemPromoteVerticalListAdapter(dataBindingComponent,
                new ItemPromoteVerticalListAdapter.NewsClickCallback() {
                    @Override
                    public void onClick(ItemPaidHistory itemPaidHistory) {
                        navigationController.navigateToItemDetailActivity(LoginUserPaidItemFragment.this.getActivity(), itemPaidHistory.item.id);
                    }
                },this);
        this.itemPromoteVerticalListAdapter = new AutoClearedValue<>(this, itemPromoteVerticalListAdapter);
        binding.get().loginUserPaidItemRecycler.setAdapter(itemPromoteVerticalListAdapter);
    }

    @Override
    protected void initData() {
        String userId = "";
        try {
            if (getActivity() != null) {
                if (getActivity().getIntent() != null) {
                    if (getActivity().getIntent().getExtras() != null) {
                        userId = getActivity().getIntent().getExtras().getString(Constants.USER_ID);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        itemPaidHistoryViewModel.holder.userId = userId;

        //Item
        itemPaidHistoryViewModel.setPaidItemHistory(Utils.checkUserId(loginUserId), String.valueOf(Config.ITEM_COUNT),  String.valueOf(itemPaidHistoryViewModel.offset));

        LiveData<Resource<List<ItemPaidHistory>>> itemList = itemPaidHistoryViewModel.getPaidItemHistory();

        if (itemList != null) {

            itemList.observe(this, listResource -> {
                if (listResource != null) {

                    switch (listResource.status) {
                        case LOADING:
                            // Loading State
                            // Data are from Local DB

                            if (listResource.data != null) {
                                //fadeIn Animation
                                fadeIn(binding.get().getRoot());

                                // Update the data
                                itemReplaceData(listResource.data);

                            }

                            break;

                        case SUCCESS:
                            // Success State
                            // Data are from Server

                            if (listResource.data != null) {
                                // Update the data
                                itemReplaceData(listResource.data);
                            }

                            itemPaidHistoryViewModel.setLoadingState(false);


                            break;

                        case ERROR:
                            // Error State

                            itemPaidHistoryViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data

                    if (itemPaidHistoryViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        itemPaidHistoryViewModel.forceEndLoading = true;
                    }

                }

            });
        }


        itemPaidHistoryViewModel.getNextPagePaidItemHistory().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {

                    itemPaidHistoryViewModel.setLoadingState(false);//hide
                    itemPaidHistoryViewModel.forceEndLoading = true;//stop

                }
            }
        });

        itemPaidHistoryViewModel.getLoadingState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loadingState) {
                binding.get().setLoadingMore(itemPaidHistoryViewModel.isLoading);

                if (loadingState != null && !loadingState) {
                    binding.get().swipeRefresh.setRefreshing(false);
                }
            }
        });

    }

    private void itemReplaceData(List<ItemPaidHistory> itemPaindList) {
        itemPromoteVerticalListAdapter.get().replace(itemPaindList);
        binding.get().executePendingBindings();
    }
}
