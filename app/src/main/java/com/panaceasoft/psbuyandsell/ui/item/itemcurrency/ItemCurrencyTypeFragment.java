package com.panaceasoft.psbuyandsell.ui.item.itemcurrency;


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
import com.panaceasoft.psbuyandsell.databinding.FragmentItemCurrencyTypeBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.itemcurrency.ItemCurrencyViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ItemCurrency;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemCurrencyTypeFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface{

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemCurrencyViewModel itemCurrencyViewModel;
    private String currencyTypeId;

    @VisibleForTesting
    private AutoClearedValue<FragmentItemCurrencyTypeBinding> binding;
    private AutoClearedValue<ItemCurrencyAdapter> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentItemCurrencyTypeBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_currency_type, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        setHasOptionsMenu(true);

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            this.currencyTypeId = intent.getStringExtra(Constants.ITEM_CURRENCY_TYPE_ID);
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
        binding.get().itemCurrencyRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                if (layoutManager != null) {

                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();

                    if (lastPosition == adapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !itemCurrencyViewModel.forceEndLoading) {

                            itemCurrencyViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                            int limit = Config.LIST_CURRENCY_COUNT;

                            itemCurrencyViewModel.offset = itemCurrencyViewModel.offset + limit;

                            itemCurrencyViewModel.setNextPageLoadingStateObj( String.valueOf(limit), String.valueOf(itemCurrencyViewModel.offset));

                            itemCurrencyViewModel.setLoadingState(true);
                        }
                    }
                }
            }
        });

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            itemCurrencyViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset productViewModel.offset
            itemCurrencyViewModel.offset = 0;

            // reset productViewModel.forceEndLoading
            itemCurrencyViewModel.forceEndLoading = false;

            // update live data
            itemCurrencyViewModel.setItemCurrencyListObj(String.valueOf(Config.LIST_CURRENCY_COUNT), String.valueOf(itemCurrencyViewModel.offset));

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
            this.currencyTypeId = "";

            initAdapters();

            initData();

            if(this.getActivity() != null) {
                navigationController.navigateBackToItemCurrencyTypeFragment(this.getActivity(), this.currencyTypeId, "");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViewModels() {

        itemCurrencyViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemCurrencyViewModel.class);
    }

    @Override
    protected void initAdapters() {

        ItemCurrencyAdapter nvadapter = new ItemCurrencyAdapter(dataBindingComponent,
                (itemCurrency, id) -> {

                    if (ItemCurrencyTypeFragment.this.getActivity() != null) {
                        navigationController.navigateBackToItemCurrencyTypeFragment(ItemCurrencyTypeFragment.this.getActivity(), itemCurrency.id, itemCurrency.currencySymbol);

                        ItemCurrencyTypeFragment.this.getActivity().finish();
                    }
                }, this.currencyTypeId);
        this.adapter = new AutoClearedValue<>(this, nvadapter);
        binding.get().itemCurrencyRecyclerView.setAdapter(nvadapter);

    }

    @Override
    protected void initData() {
        loadCategory();
    }

    private void loadCategory() {

        // Load Category List
        itemCurrencyViewModel.categoryParameterHolder.cityId = selectedCityId;

        itemCurrencyViewModel.setItemCurrencyListObj("", String.valueOf(itemCurrencyViewModel.offset));

        LiveData<Resource<List<ItemCurrency>>> news = itemCurrencyViewModel.getItemCurrencyListData();

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

                            itemCurrencyViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            itemCurrencyViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                    if (itemCurrencyViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        itemCurrencyViewModel.forceEndLoading = true;
                    }

                }

            });
        }

        itemCurrencyViewModel.getNextPageLoadingStateData().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {
                    Utils.psLog("Next Page State : " + state.data);

                    itemCurrencyViewModel.setLoadingState(false);
                    itemCurrencyViewModel.forceEndLoading = true;
                }
            }
        });

        itemCurrencyViewModel.getLoadingState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loadingState) {
                binding.get().setLoadingMore(itemCurrencyViewModel.isLoading);

                if (loadingState != null && !loadingState) {
                    binding.get().swipeRefresh.setRefreshing(false);
                }
            }
        });

    }

    private void replaceData(List<ItemCurrency> categoryList) {

        adapter.get().replace(categoryList);
        binding.get().executePendingBindings();

    }

    @Override
    public void onDispatched() {
        if (itemCurrencyViewModel.loadingDirection == Utils.LoadingDirection.top) {
            binding.get();
            LinearLayoutManager layoutManager = (LinearLayoutManager)
                    binding.get().itemCurrencyRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPosition(0);
            }
        }
    }
}