package com.panaceasoft.psbuyandsell.ui.item.itemtype;


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
import com.panaceasoft.psbuyandsell.databinding.FragmentItemTypeBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.ui.item.itemtype.adapter.ItemTypeAdapter;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.itemtype.ItemTypeViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ItemType;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemTypeFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface{

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemTypeViewModel itemTypeViewModel;
    public String itemTypeId;

    @VisibleForTesting
    private AutoClearedValue<FragmentItemTypeBinding> binding;
    private AutoClearedValue<ItemTypeAdapter> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentItemTypeBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_type, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        setHasOptionsMenu(true);

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            this.itemTypeId = intent.getStringExtra(Constants.ITEM_TYPE_ID);
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
        binding.get().itemTypeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                if (layoutManager != null) {

                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();

                    if (lastPosition == adapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !itemTypeViewModel.forceEndLoading) {

                            itemTypeViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                            int limit = Config.LIST_TYPE_COUNT;

                            itemTypeViewModel.offset = itemTypeViewModel.offset + limit;

                            itemTypeViewModel.setNextPageLoadingStateObj( String.valueOf(limit), String.valueOf(itemTypeViewModel.offset));

                            itemTypeViewModel.setLoadingState(true);
                        }
                    }
                }
            }
        });

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            itemTypeViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset productViewModel.offset
            itemTypeViewModel.offset = 0;

            // reset productViewModel.forceEndLoading
            itemTypeViewModel.forceEndLoading = false;

            // update live data
            itemTypeViewModel.setItemTypeListObj(String.valueOf(Config.LIST_TYPE_COUNT), String.valueOf(itemTypeViewModel.offset));

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
            this.itemTypeId = "";

            initAdapters();

            initData();

            if(this.getActivity() != null)
            navigationController.navigateBackToItemTypeFragment(this.getActivity(), this.itemTypeId, "");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViewModels() {

        itemTypeViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemTypeViewModel.class);
    }

    @Override
    protected void initAdapters() {

        ItemTypeAdapter nvadapter = new ItemTypeAdapter(dataBindingComponent,
                (itemType, id) -> {
                    if(this.getActivity() != null)
                    navigationController.navigateBackToItemTypeFragment(ItemTypeFragment.this.getActivity(), itemType.id, itemType.name);

                    if (ItemTypeFragment.this.getActivity() != null) {
                        ItemTypeFragment.this.getActivity().finish();
                    }
                }, this.itemTypeId);
        this.adapter = new AutoClearedValue<>(this, nvadapter);
        binding.get().itemTypeRecyclerView.setAdapter(nvadapter);

    }

    @Override
    protected void initData() {
        loadCategory();
    }

    private void loadCategory() {

        // Load Category List
        itemTypeViewModel.categoryParameterHolder.cityId = selectedCityId;

        itemTypeViewModel.setItemTypeListObj("", String.valueOf(itemTypeViewModel.offset));

        LiveData<Resource<List<ItemType>>> news = itemTypeViewModel.getItemTypeListData();

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

                            itemTypeViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            itemTypeViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                    if (itemTypeViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        itemTypeViewModel.forceEndLoading = true;
                    }

                }

            });
        }

        itemTypeViewModel.getNextPageLoadingStateData().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {
                    Utils.psLog("Next Page State : " + state.data);

                    itemTypeViewModel.setLoadingState(false);
                    itemTypeViewModel.forceEndLoading = true;
                }
            }
        });

        itemTypeViewModel.getLoadingState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loadingState) {
                binding.get().setLoadingMore(itemTypeViewModel.isLoading);

                if (loadingState != null && !loadingState) {
                    binding.get().swipeRefresh.setRefreshing(false);
                }
            }
        });

    }

    private void replaceData(List<ItemType> categoryList) {

        adapter.get().replace(categoryList);
        binding.get().executePendingBindings();

    }

    @Override
    public void onDispatched() {
        if (itemTypeViewModel.loadingDirection == Utils.LoadingDirection.top) {
            binding.get();
            LinearLayoutManager layoutManager = (LinearLayoutManager)
                    binding.get().itemTypeRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPosition(0);
            }
        }
    }
}