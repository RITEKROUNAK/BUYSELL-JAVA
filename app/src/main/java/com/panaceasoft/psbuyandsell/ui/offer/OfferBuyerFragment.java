package com.panaceasoft.psbuyandsell.ui.offer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.OfferFragmentBuyerBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.ui.offer.adapter.OfferBuyerAdapter;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.offer.OfferViewModel;
import com.panaceasoft.psbuyandsell.viewobject.Offer;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import java.util.List;

public class OfferBuyerFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private OfferViewModel offerViewModel;

    @VisibleForTesting
    private AutoClearedValue<OfferFragmentBuyerBinding> binding;
    private AutoClearedValue<OfferBuyerAdapter> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        OfferFragmentBuyerBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.offer_fragment_buyer, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

        binding.get().offerBuyerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                if (layoutManager != null) {

                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();

                    if (lastPosition == adapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !offerViewModel.forceEndLoading) {

                            if (connectivity.isConnected()) {

                                offerViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                                int limit = Config.LIST_CATEGORY_COUNT;
                                offerViewModel.offset = offerViewModel.offset + limit;

                                offerViewModel.setNextPageOfferFromSellerObj(loginUserId, offerViewModel.holder.getBuyerOfferList(), String.valueOf(Config.OFFER_LIST_COUNT), String.valueOf(offerViewModel.offset));
                            }
                        }
                    }
                }
            }
        });

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            offerViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset productViewModel.offset
            offerViewModel.offset = 0;

            // reset productViewModel.forceEndLoading
            offerViewModel.forceEndLoading = false;

            // update live data
            if (!loginUserId.isEmpty()) {
                offerViewModel.setOfferListObj(loginUserId, offerViewModel.holder.getBuyerOfferList(), String.valueOf(Config.OFFER_LIST_COUNT), String.valueOf(offerViewModel.offset));
            }
        });

    }

    @Override
    protected void initViewModels() {

        offerViewModel = new ViewModelProvider(this, viewModelFactory).get(OfferViewModel.class);
    }

    @Override
    protected void initAdapters() {
        OfferBuyerAdapter offerBuyerAdapter = new OfferBuyerAdapter(dataBindingComponent,
                (offerFromBuyer, id) -> navigationController.navigateToChatActivity(getActivity(),
                        offerFromBuyer.itemId,
                        offerFromBuyer.buyerUserId,
                        offerFromBuyer.buyerUser.userName,
                        offerFromBuyer.item.defaultPhoto.imgPath,
                        offerFromBuyer.item.title,
                        offerFromBuyer.item.itemCurrency.currencySymbol,
                        offerFromBuyer.item.price,
                        offerFromBuyer.item.itemCondition.name,
                        Constants.CHAT_FROM_BUYER,
                        offerFromBuyer.buyerUser.userProfilePhoto,
                        Constants.REQUEST_CODE__BUYER_CHAT_FRAGMENT), this);
        this.adapter = new AutoClearedValue<>(this, offerBuyerAdapter);
        binding.get().offerBuyerList.setAdapter(offerBuyerAdapter);

    }

    @Override
    protected void initData() {
        loadData();
    }

    private void loadData() {

        if (!loginUserId.isEmpty()) {

            offerViewModel.setOfferListObj(loginUserId, offerViewModel.holder.getBuyerOfferList(), String.valueOf(Config.OFFER_LIST_COUNT), String.valueOf(offerViewModel.offset));
        }

        LiveData<Resource<List<Offer>>> offerListListData = offerViewModel.getOfferListData();

        if (offerListListData != null) {

            offerListListData.observe(this, listResource -> {
                if (listResource != null) {

                    Utils.psLog("Got Data" + listResource.message + listResource.toString());

                    switch (listResource.status) {
                        case LOADING:
                            // Loading State
                            // Data are from Local DB

                            if (listResource.data != null) {
                                //fadeIn Animation
                                fadeIn(binding.get().getRoot());

                                // Update the data
                                replaceData(listResource.data);

                            }

                            break;

                        case SUCCESS:
                            // Success State
                            // Data are from Server

                            if (listResource.data != null) {
                                // Update the data
                                replaceData(listResource.data);
                            }

                            offerViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            offerViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                    if (offerViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        offerViewModel.forceEndLoading = true;
                    }

                }

            });
        }

        offerViewModel.getNextPageOfferListData().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {
                    Utils.psLog("Next Page State : " + state.data);

                    offerViewModel.setLoadingState(false);
                    offerViewModel.forceEndLoading = true;
                }
            }
        });

        offerViewModel.getLoadingState().observe(this, loadingState -> {

            binding.get().setLoadingMore(offerViewModel.isLoading);

            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }

        });

    }
    private void replaceData(List<Offer> categoryList) {

        adapter.get().replace(categoryList);
        binding.get().executePendingBindings();

    }

    @Override
    public void onDispatched() {

        if (offerViewModel.loadingDirection == Utils.LoadingDirection.bottom) {

            if (binding.get().offerBuyerList != null) {

                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        binding.get().offerBuyerList.getLayoutManager();

                if (layoutManager != null) {
                    layoutManager.scrollToPosition(0);
                }
            }
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE__BUYER_CHAT_FRAGMENT) {

            offerViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset productViewModel.offset
            offerViewModel.offset = 0;

            // reset productViewModel.forceEndLoading
            offerViewModel.forceEndLoading = false;

            // update live data
            if (!loginUserId.isEmpty()) {
                offerViewModel.setOfferListObj(loginUserId, offerViewModel.holder.getBuyerOfferList(), String.valueOf(Config.OFFER_LIST_COUNT), String.valueOf(offerViewModel.offset));
            }

        }

    }
}
