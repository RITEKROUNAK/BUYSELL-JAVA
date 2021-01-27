package com.panaceasoft.psbuyandsell.ui.item.itemcondition;


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
import com.panaceasoft.psbuyandsell.databinding.FragmentItemConditionBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.itemcondition.ItemConditionViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ItemCondition;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemConditionFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface{

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemConditionViewModel itemConditionViewModel;
    public String itemConditionId;

    @VisibleForTesting
    private AutoClearedValue<FragmentItemConditionBinding> binding;
    private AutoClearedValue<ItemConditionAdapter> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentItemConditionBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_condition, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        setHasOptionsMenu(true);

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            this.itemConditionId = intent.getStringExtra(Constants.ITEM_CONDITION_TYPE_ID);
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
        binding.get().itemConditionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                if (layoutManager != null) {

                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();

                    if (lastPosition == adapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !itemConditionViewModel.forceEndLoading) {

                            itemConditionViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                            int limit = Config.LIST_CONDITION_COUNT;

                            itemConditionViewModel.offset = itemConditionViewModel.offset + limit;

                            itemConditionViewModel.setNextPageLoadingStateObj( String.valueOf(limit), String.valueOf(itemConditionViewModel.offset));

                            itemConditionViewModel.setLoadingState(true);
                        }
                    }
                }
            }
        });

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            itemConditionViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset productViewModel.offset
            itemConditionViewModel.offset = 0;

            // reset productViewModel.forceEndLoading
            itemConditionViewModel.forceEndLoading = false;

            // update live data
            itemConditionViewModel.setItemConditionListObj(String.valueOf(Config.LIST_CONDITION_COUNT), String.valueOf(itemConditionViewModel.offset));

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
            this.itemConditionId = "";

            initAdapters();

            initData();

            if(this.getActivity() != null)
            navigationController.navigateBackToItemConditionFragment(this.getActivity(), this.itemConditionId, "");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViewModels() {

        itemConditionViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemConditionViewModel.class);
    }

    @Override
    protected void initAdapters() {

        ItemConditionAdapter nvadapter = new ItemConditionAdapter(dataBindingComponent,
                (itemCondition, id) -> {
                    if(this.getActivity() != null)
                    navigationController.navigateBackToItemConditionFragment(ItemConditionFragment.this.getActivity(), itemCondition.id, itemCondition.name);

                    if (ItemConditionFragment.this.getActivity() != null) {
                        ItemConditionFragment.this.getActivity().finish();
                    }
                }, this.itemConditionId);
        this.adapter = new AutoClearedValue<>(this, nvadapter);
        binding.get().itemConditionRecyclerView.setAdapter(nvadapter);

    }

    @Override
    protected void initData() {
        loadCategory();
    }

    private void loadCategory() {

        // Load Category List
        itemConditionViewModel.categoryParameterHolder.cityId = selectedCityId;

        itemConditionViewModel.setItemConditionListObj("", String.valueOf(itemConditionViewModel.offset));

        LiveData<Resource<List<ItemCondition>>> news = itemConditionViewModel.getItemConditionListData();

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

                            itemConditionViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            itemConditionViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                    if (itemConditionViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        itemConditionViewModel.forceEndLoading = true;
                    }

                }

            });
        }

        itemConditionViewModel.getNextPageLoadingStateData().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {
                    Utils.psLog("Next Page State : " + state.data);

                    itemConditionViewModel.setLoadingState(false);
                    itemConditionViewModel.forceEndLoading = true;
                }
            }
        });

        itemConditionViewModel.getLoadingState().observe(this, loadingState -> {
            binding.get().setLoadingMore(itemConditionViewModel.isLoading);

            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }
        });

    }

    private void replaceData(List<ItemCondition> categoryList) {

        adapter.get().replace(categoryList);
        binding.get().executePendingBindings();

    }

    @Override
    public void onDispatched() {
        if (itemConditionViewModel.loadingDirection == Utils.LoadingDirection.top) {
            binding.get();
            LinearLayoutManager layoutManager = (LinearLayoutManager)
                    binding.get().itemConditionRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPosition(0);
            }
        }
    }
}