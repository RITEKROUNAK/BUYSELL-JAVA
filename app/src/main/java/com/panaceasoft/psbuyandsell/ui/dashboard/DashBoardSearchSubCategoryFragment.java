package com.panaceasoft.psbuyandsell.ui.dashboard;

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
import com.panaceasoft.psbuyandsell.databinding.FragmentSearchCategoryBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.ui.subcategory.adapter.SearchSubCategoryAdapter;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.itemsubcategory.ItemSubCategoryViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ItemSubCategory;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import java.util.List;

public class DashBoardSearchSubCategoryFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface{

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemSubCategoryViewModel itemSubCategoryViewModel;
    private String catId;
    private String subCatId;

    @VisibleForTesting
    private AutoClearedValue<FragmentSearchCategoryBinding> binding;
    private AutoClearedValue<SearchSubCategoryAdapter> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentSearchCategoryBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_category, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        setHasOptionsMenu(true);

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            this.catId = intent.getStringExtra(Constants.CATEGORY_ID);
            this.subCatId = intent.getStringExtra(Constants.SUBCATEGORY_ID);
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
        binding.get().subCategoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                if (layoutManager != null) {

                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();

                    if (lastPosition == adapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !itemSubCategoryViewModel.forceEndLoading) {

                            itemSubCategoryViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                            int limit = Config.LIST_SEARCH_SUB_CAT_COUNT;

                            itemSubCategoryViewModel.offset = itemSubCategoryViewModel.offset + limit;

                            itemSubCategoryViewModel.setNextPageLoadingStateObj( catId,String.valueOf(limit), String.valueOf(itemSubCategoryViewModel.offset));

                            itemSubCategoryViewModel.setLoadingState(true);
                        }
                    }
                }
            }
        });

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            itemSubCategoryViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset productViewModel.offset
            itemSubCategoryViewModel.offset = 0;

            // reset productViewModel.forceEndLoading
            itemSubCategoryViewModel.forceEndLoading = false;

            // update live data
            itemSubCategoryViewModel.setSubCategoryListByCatIdObj(catId,String.valueOf(Config.LIST_SEARCH_SUB_CAT_COUNT), String.valueOf(itemSubCategoryViewModel.offset));

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
            this.subCatId = "";

            initAdapters();

            initData();

            navigationController.navigateBackToSearchFragmentFromSubCategory(this.getActivity(), this.subCatId, "");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViewModels() {

        itemSubCategoryViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemSubCategoryViewModel.class);
    }

    @Override
    protected void initAdapters() {

        SearchSubCategoryAdapter nvadapter = new SearchSubCategoryAdapter(dataBindingComponent,
                new SearchSubCategoryAdapter.NewsClickCallback() {
                    @Override
                    public void onClick(ItemSubCategory subCategory) {

                            navigationController.navigateBackToSearchFragmentFromSubCategory(DashBoardSearchSubCategoryFragment.this.getActivity(), subCategory.id, subCategory.name);

                            if (DashBoardSearchSubCategoryFragment.this.getActivity() != null) {
                                DashBoardSearchSubCategoryFragment.this.getActivity().finish();
                            }
                    }
                }, this.subCatId);


        this.adapter = new AutoClearedValue<>(this, nvadapter);
        binding.get().subCategoryRecyclerView.setAdapter(nvadapter);
    }

    @Override
    protected void initData() {
        loadCategory();
    }

    private void loadCategory() {

        // Load Category List
//        itemSubCategoryViewModel.setNextPageSubCategoryListByCatIdObj(loginUserId, String.valueOf(Config.LIST_CATEGORY_COUNT), String.valueOf(subCategoryViewModel.offset), this.catId);
        itemSubCategoryViewModel.setSubCategoryListByCatIdObj(this.catId, "", String.valueOf(itemSubCategoryViewModel.offset));

        LiveData<Resource<List<ItemSubCategory>>> news = itemSubCategoryViewModel.getSubCategoryListByCatIdData();

        if (news != null) {

            news.observe(this, listResource -> {
                if (listResource != null) {

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

                            itemSubCategoryViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            itemSubCategoryViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data

                    if (itemSubCategoryViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        itemSubCategoryViewModel.forceEndLoading = true;
                    }

                }

            });
        }

        itemSubCategoryViewModel.getNextPageLoadingStateData().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {

                    itemSubCategoryViewModel.setLoadingState(false);
                    itemSubCategoryViewModel.forceEndLoading = true;
                }
            }
        });

        itemSubCategoryViewModel.getLoadingState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loadingState) {
                binding.get().setLoadingMore(itemSubCategoryViewModel.isLoading);

                if (loadingState != null && !loadingState) {
                    binding.get().swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    private void replaceData(List<ItemSubCategory> categoryList) {

        adapter.get().replace(categoryList);
        binding.get().executePendingBindings();

    }

    @Override
    public void onDispatched() {
        if (itemSubCategoryViewModel.loadingDirection == Utils.LoadingDirection.top) {
            binding.get();
            LinearLayoutManager layoutManager = (LinearLayoutManager)
                    binding.get().subCategoryRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPosition(0);
            }
        }
    }
}
