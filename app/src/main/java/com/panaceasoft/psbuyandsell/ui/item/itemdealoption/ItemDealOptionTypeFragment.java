package com.panaceasoft.psbuyandsell.ui.item.itemdealoption;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentItemDealOptionTypeBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.itemdealoption.ItemDealOptionViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ItemDealOption;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemDealOptionTypeFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface{

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemDealOptionViewModel itemDealOptionViewModel;
    public String priceTypeId;

    @VisibleForTesting
    private AutoClearedValue<FragmentItemDealOptionTypeBinding> binding;
    private AutoClearedValue<ItemDealOptionAdapter> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentItemDealOptionTypeBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_deal_option_type, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        setHasOptionsMenu(true);

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            this.priceTypeId = intent.getStringExtra(Constants.ITEM_OPTION_TYPE_ID);
        }

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            binding.get().adView.loadAd(adRequest);
        } else {
            binding.get().adView.setVisibility(View.GONE);
        }

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {
        binding.get().itemDealOptionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                if (layoutManager != null) {

                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();

                    if (lastPosition == adapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !itemDealOptionViewModel.forceEndLoading) {

                            itemDealOptionViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                            int limit = Config.LIST_DEAL_OPTION_COUNT;

                            itemDealOptionViewModel.offset = itemDealOptionViewModel.offset + limit;

                            itemDealOptionViewModel.setNextPageLoadingStateObj( String.valueOf(limit), String.valueOf(itemDealOptionViewModel.offset));

                            itemDealOptionViewModel.setLoadingState(true);
                        }
                    }
                }
            }
        });

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            itemDealOptionViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset productViewModel.offset
            itemDealOptionViewModel.offset = 0;

            // reset productViewModel.forceEndLoading
            itemDealOptionViewModel.forceEndLoading = false;

            // update live data
            itemDealOptionViewModel.setItemDealOptionListObj(String.valueOf(Config.LIST_DEAL_OPTION_COUNT), String.valueOf(itemDealOptionViewModel.offset));

        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.clear_button, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.clear) {
            this.priceTypeId = "";

            initAdapters();

            initData();

            if(this.getActivity() != null)
            navigationController.navigateBackToItemDealOptionTypeFragment(this.getActivity(), this.priceTypeId, "");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViewModels() {

        itemDealOptionViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemDealOptionViewModel.class);
    }

    @Override
    protected void initAdapters() {

        ItemDealOptionAdapter nvadapter = new ItemDealOptionAdapter(dataBindingComponent,
                (category, id) -> {
                    if(this.getActivity() != null)
                    navigationController.navigateBackToItemDealOptionTypeFragment(this.getActivity(), category.id, category.name);

                    if (getActivity() != null) {
                        this.getActivity().finish();
                    }
                }, this.priceTypeId);
        this.adapter = new AutoClearedValue<>(this, nvadapter);
        binding.get().itemDealOptionRecyclerView.setAdapter(nvadapter);

    }

    @Override
    protected void initData() {
        loadCategory();
    }

    private void loadCategory() {

        // Load Category List
        itemDealOptionViewModel.categoryParameterHolder.cityId = selectedCityId;

        itemDealOptionViewModel.setItemDealOptionListObj("", String.valueOf(itemDealOptionViewModel.offset));

        LiveData<Resource<List<ItemDealOption>>> news = itemDealOptionViewModel.getItemDealOptionListData();

        if (news != null) {

            news.observe(this, listResource -> {
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

                            itemDealOptionViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            itemDealOptionViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                    if (itemDealOptionViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        itemDealOptionViewModel.forceEndLoading = true;
                    }

                }

            });
        }

        itemDealOptionViewModel.getNextPageLoadingStateData().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {
                    Utils.psLog("Next Page State : " + state.data);

                    itemDealOptionViewModel.setLoadingState(false);
                    itemDealOptionViewModel.forceEndLoading = true;
                }
            }
        });

        itemDealOptionViewModel.getLoadingState().observe(this, loadingState -> {
            binding.get().setLoadingMore(itemDealOptionViewModel.isLoading);

            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }
        });

    }

    private void replaceData(List<ItemDealOption> categoryList) {

        adapter.get().replace(categoryList);
        binding.get().executePendingBindings();

    }

    @Override
    public void onDispatched() {
        if (itemDealOptionViewModel.loadingDirection == Utils.LoadingDirection.top) {
            binding.get();
            LinearLayoutManager layoutManager = (LinearLayoutManager)
                    binding.get().itemDealOptionRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPosition(0);
            }
        }
    }
}