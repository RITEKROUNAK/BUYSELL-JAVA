package com.panaceasoft.psbuyandsell.ui.category.categoryfilter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.TypeFilterBinding;
import com.panaceasoft.psbuyandsell.ui.category.adapter.CategoryFilterAdapter;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.itemcategory.ItemCategoryViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.itemsubcategory.ItemSubCategoryViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ItemCategory;
import com.panaceasoft.psbuyandsell.viewobject.ItemSubCategory;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import java.util.List;

public class CategoryFilterFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private ItemCategoryViewModel itemCategoryViewModel;
    private ItemSubCategoryViewModel subCategoryViewModel;
    private String catId, subCatId;
    public Intent intent = new Intent();

    @VisibleForTesting
    private AutoClearedValue<TypeFilterBinding> binding;
    private AutoClearedValue<CategoryFilterAdapter> adapter;
    private AutoClearedValue<List<ItemCategory>> lastCategoryData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        TypeFilterBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.type_filter, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        binding.get().setLoadingMore(connectivity.isConnected());
        setHasOptionsMenu(true);

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
        binding.get().CategoryList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

//                int lastPosition = absListView.getLastVisiblePosition();

                if (firstVisibleItem + visibleItemCount == totalItemCount ) {

                    if (!binding.get().getLoadingMore() && !itemCategoryViewModel.forceEndLoading) {

                        itemCategoryViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                        int limit = Config.LIST_FILTER_CATEGORY_COUNT;

                        itemCategoryViewModel.offset = itemCategoryViewModel.offset + limit;

                        itemCategoryViewModel.setNextPageLoadingStateObj(String.valueOf(limit), String.valueOf(itemCategoryViewModel.offset));

                        itemCategoryViewModel.setLoadingState(true);
                    }
                }
            }
        });

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            itemCategoryViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset productViewModel.offset
            itemCategoryViewModel.offset = 0;

            // reset productViewModel.forceEndLoading
            itemCategoryViewModel.forceEndLoading = false;

            // update live data
            itemCategoryViewModel.setCategoryListObj(String.valueOf(Config.LIST_FILTER_CATEGORY_COUNT), String.valueOf(itemCategoryViewModel.offset));

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
            this.catId = "";
            this.subCatId = "";

            initializeAdapter();

            initData();

            if(getActivity() != null){
                navigationController.navigateBackToHomeFeaturedFragment(CategoryFilterFragment.this.getActivity(), this.catId, this.subCatId);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViewModels() {
        itemCategoryViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemCategoryViewModel.class);
        subCategoryViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemSubCategoryViewModel.class);

    }

    @Override
    protected void initAdapters() {

        try {
            if (getActivity() != null) {

                intent = getActivity().getIntent();

                this.catId = intent.getStringExtra(Constants.CATEGORY_ID);
                this.subCatId = intent.getStringExtra(Constants.SUBCATEGORY_ID);

            }
        } catch (Exception e) {
            Utils.psErrorLog("", e);
        }

        initializeAdapter();
    }

    private void initializeAdapter() {
        CategoryFilterAdapter nvAdapter = new CategoryFilterAdapter(dataBindingComponent, (catId, subCatId) -> {

            CategoryFilterFragment.this.assignCategoryId(catId, subCatId);

            if (getActivity() != null) {
                navigationController.navigateBackToHomeFeaturedFragment(CategoryFilterFragment.this.getActivity(), catId, subCatId);
                CategoryFilterFragment.this.getActivity().finish();
            }

        }, this.catId, this.subCatId);

        this.adapter = new AutoClearedValue<>(this, nvAdapter);
        binding.get().CategoryList.setAdapter(nvAdapter);
    }

    private void assignCategoryId(String catId, String subCatId) {
        this.catId = catId;
        this.subCatId = subCatId;

    }

    @Override
    protected void initData() {

        itemCategoryViewModel.categoryParameterHolder.cityId = selectedCityId;

        itemCategoryViewModel.setCategoryListObj(String.valueOf(Config.LIST_FILTER_CATEGORY_COUNT), String.valueOf(itemCategoryViewModel.offset));
        subCategoryViewModel.setAllSubCategoryListObj();

        LiveData<Resource<List<ItemCategory>>> categories = itemCategoryViewModel.getCategoryListData();
        LiveData<Resource<List<ItemSubCategory>>> subCategories = subCategoryViewModel.getAllSubCategoryListData();


        if (categories != null) {

            categories.observe(this, listResource -> {
                if (listResource != null) {

                    if (listResource.data != null && listResource.data.size() > 0) {

                        lastCategoryData = new AutoClearedValue<>(this, listResource.data);
                        replaceCategory(lastCategoryData.get());
                        itemCategoryViewModel.setLoadingState(false);

                    }

                } else {

                    // Init Object or Empty Data

                    if (itemCategoryViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        itemCategoryViewModel.forceEndLoading = true;
                    }

                }

            });
        }

        if (subCategories != null) {

            subCategories.observe(this, listResource -> {
                if (listResource != null) {


                    if (listResource.data != null && listResource.data.size() > 0) {

                        replaceSubCategory(listResource.data);
                    }


                } else {

                    // Init Object or Empty Data

                    if (subCategoryViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        subCategoryViewModel.forceEndLoading = true;
                    }

                }

            });
        }

        itemCategoryViewModel.getNextPageLoadingStateData().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {
                    Utils.psLog("Next Page State : " + state.data);

                    itemCategoryViewModel.setLoadingState(false);
                    itemCategoryViewModel.forceEndLoading = true;
                }
            }
        });

        itemCategoryViewModel.getLoadingState().observe(this, loadingState -> {
            binding.get().setLoadingMore(itemCategoryViewModel.isLoading);

            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }
        });

    }


    private void replaceCategory(List<ItemCategory> CategoryList) {

        adapter.get().replaceCategory(CategoryList);
        adapter.get().notifyDataSetChanged();
        binding.get().executePendingBindings();

    }

    private void replaceSubCategory(List<ItemSubCategory> subCategoryList) {

        adapter.get().replaceSubCategory(subCategoryList);
        adapter.get().notifyDataSetChanged();
        binding.get().executePendingBindings();

    }

    @Override
    public void onDispatched() {
//        if (itemCategoryViewModel.loadingDirection == Utils.LoadingDirection.top) {
//            binding.get();
//            LinearLayoutManager layoutManager = (LinearLayoutManager)
//                    binding.get().CategoryList.getposition();
//            if (layoutManager != null) {
//                layoutManager.scrollToPosition(0);
//            }
//        }
    }

}
