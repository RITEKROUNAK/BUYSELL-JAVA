package com.panaceasoft.psbuyandsell.ui.city.selectedcity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.android.billingclient.api.ConsumeParams;
import com.google.android.gms.ads.AdRequest;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.MainActivity;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentSelectedCityBinding;
import com.panaceasoft.psbuyandsell.ui.category.adapter.CityCategoryAdapter;
import com.panaceasoft.psbuyandsell.ui.category.list.CategoryListFragment;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.ui.dashboard.adapter.DashBoardViewPagerAdapter;
import com.panaceasoft.psbuyandsell.ui.item.adapter.ItemHorizontalListAdapter;
import com.panaceasoft.psbuyandsell.ui.item.detail.ItemActivity;
import com.panaceasoft.psbuyandsell.ui.item.detail.ItemFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.blog.BlogViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.item.FeaturedItemViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.item.ItemViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.item.PopularItemViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.item.RecentItemViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.itemcategory.ItemCategoryViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.itemfromfollower.ItemFromFollowerViewModel;
import com.panaceasoft.psbuyandsell.viewobject.Blog;
import com.panaceasoft.psbuyandsell.viewobject.Item;
import com.panaceasoft.psbuyandsell.viewobject.ItemCategory;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;
import com.panaceasoft.psbuyandsell.viewobject.holder.ItemParameterHolder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import com.panaceasoft.psbuyandsell.viewmodel.clearalldata.ClearAllDataViewModel;

public class SelectedCityFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemCategoryViewModel itemCategoryViewModel;
    private PopularItemViewModel popularItemViewModel;
    private FeaturedItemViewModel featuredItemViewModel;
    private RecentItemViewModel recentItemViewModel;
    private BlogViewModel blogViewModel;
    private ItemViewModel itemViewModel;
    private ImageView[] dots;
    private boolean layoutDone = false;
    private int loadingCount = 0;
    private PSDialogMsg psDialogMsg;
    private ItemParameterHolder itemParameterHolder = new ItemParameterHolder();

    //    private PSAPPLoadingViewModel psappLoadingViewModel;
    //    private PSAppInfoViewModel psAppInfoViewModel;
//    private ClearAllDataViewModel clearAllDataViewModel;
    private ItemFromFollowerViewModel itemFromFollowerViewModel;
    private ItemParameterHolder searchItemParameterHolder = new ItemParameterHolder().getRecentItem();

    private Runnable update;
    private int NUM_PAGES = 10;
    private int currentPage = 0;
    private boolean touched = false;
    private Timer unTouchedTimer;
    private Handler handler = new Handler();
    private boolean searchKeywordOnFocus = false;

    @VisibleForTesting
    private AutoClearedValue<FragmentSelectedCityBinding> binding;
    private AutoClearedValue<ItemHorizontalListAdapter> popularItemListAdapter;
    private AutoClearedValue<ItemHorizontalListAdapter> featuredItemListAdapter;
    private AutoClearedValue<ItemHorizontalListAdapter> recentItemListAdapter;
    private AutoClearedValue<ItemHorizontalListAdapter> followerItemListAdapter;
    private AutoClearedValue<DashBoardViewPagerAdapter> dashBoardViewPagerAdapter;
    private AutoClearedValue<CityCategoryAdapter> cityCategoryAdapter;
    private AutoClearedValue<ViewPager> viewPager;
    private AutoClearedValue<LinearLayout> pageIndicatorLayout;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentSelectedCityBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_selected_city, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        binding.get().setLoadingMore(connectivity.isConnected());



        return binding.get().getRoot();


    }

    @Override
    protected void initUIAndActions() {

        if (!connectivity.isConnected()) {
            Toast.makeText(getContext(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
        }

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.layout__primary_background));
            ((MainActivity) getActivity()).updateToolbarIconColor(Color.GRAY);
            ((MainActivity) getActivity()).updateMenuIconGrey();
            ((MainActivity) getActivity()).refreshPSCount();
        }

        getIntentData();



        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest2 = new AdRequest.Builder()
                    .build();
            binding.get().adView2.loadAd(adRequest2);
            AdRequest adRequest3 = new AdRequest.Builder()
                    .build();
            binding.get().adView3.loadAd(adRequest3);
        } else {
            binding.get().adView2.setVisibility(View.GONE);
            binding.get().adView3.setVisibility(View.GONE);
        }

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        viewPager = new AutoClearedValue<>(this, binding.get().blogViewPager);

        pageIndicatorLayout = new AutoClearedValue<>(this, binding.get().pagerIndicator);

        binding.get().blogViewAllTextView.setOnClickListener(v -> navigationController.navigateToBlogList(getActivity()));

        binding.get().popularViewAllTextView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(getActivity(), popularItemViewModel.popularItemParameterHolder, getString(R.string.selected_city_popular_item), selectedCityLat, selectedCityLng, Constants.MAP_MILES));

        binding.get().featuredViewAllTextView.setOnClickListener(v -> navigationController.navigateToFeaturedActivity(getActivity()));

        binding.get().followerViewAllTextView.setOnClickListener(v -> navigationController.navigateToItemListFromFollower(getActivity()));

        binding.get().recentItemViewAllTextView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(getActivity(), recentItemViewModel.recentItemParameterHolder, getString(R.string.selected_city_recent), selectedCityLat, selectedCityLng, Constants.MAP_MILES));

        binding.get().categoryViewAllTextView.setOnClickListener(v -> navigationController.navigateToCategoryActivity(getActivity()));

        binding.get().addItemButton.setOnClickListener(v -> {

            Utils.navigateOnUserVerificationActivity(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController, new Utils.NavigateOnUserVerificationActivityCallback() {
                @Override
                public void onSuccess() {
                    navigationController.navigateToItemEntryActivity(SelectedCityFragment.this.getActivity(), Constants.ADD_NEW_ITEM, recentItemViewModel.locationId, recentItemViewModel.locationName);
                }
            });

        });

        binding.get().locationTextView.setOnClickListener(v -> navigationController.navigateToLocationActivity(getActivity(), Constants.SELECT_LOCATION_FROM_HOME,selected_location_id,itemViewModel.itemId));


//        binding.get().blogViewPager.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                binding.get().searchBoxEditText.clearFocus();
//            }
//        });

        binding.get().searchBoxEditText.setOnFocusChangeListener((v, hasFocus) -> {

            searchKeywordOnFocus = hasFocus;
            Utils.psLog("Focus " + hasFocus);
        });
        binding.get().searchBoxEditText.setOnKeyListener((v, keyCode, event) -> {

            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                binding.get().searchBoxEditText.clearFocus();
                searchKeywordOnFocus = false;
                callSearchList();
                Utils.psLog("Down");

                return false;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {

                Utils.psLog("Up");
            }
            return false;
        });
        binding.get().searchImageButton.setOnClickListener(v -> SelectedCityFragment.this.callSearchList());

        if (viewPager.get() != null && viewPager.get() != null && viewPager.get() != null) {
            viewPager.get().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    if (searchKeywordOnFocus) {
                        binding.get().searchBoxEditText.clearFocus();
                    }
                }

                @Override
                public void onPageSelected(int position) {

                    currentPage = position;

                    if (pageIndicatorLayout.get() != null) {

                        setupSliderPagination();
                    }

                    for (ImageView dot : dots) {
                        if (dots != null) {
                            dot.setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
                        }
                    }

                    if (dots != null && dots.length > position) {
                        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
                    }

                    touched = true;

                    handler.removeCallbacks(update);

                    setUnTouchedTimer();

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        startPagerAutoSwipe();

        if (force_update) {
            navigationController.navigateToForceUpdateActivity(this.getActivity(), force_update_title, force_update_msg);
        }

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            if (!connectivity.isConnected()) {
                Toast.makeText(getContext(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
            }

            itemCategoryViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset itemCategoryViewModel.offset
            itemCategoryViewModel.offset = 0;

            // reset itemCategoryViewModel.forceEndLoading
            itemCategoryViewModel.forceEndLoading = false;

            // update live data
            itemCategoryViewModel.setCategoryListObj(String.valueOf(Config.LIST_CATEGORY_COUNT), String.valueOf(itemCategoryViewModel.offset));
            recentItemViewModel.setRecentItemListByKeyObj(Utils.checkUserId(loginUserId), Config.LIMIT_FROM_DB_COUNT, Constants.ZERO, recentItemViewModel.recentItemParameterHolder);
            popularItemViewModel.setPopularItemListByKeyObj(Utils.checkUserId(loginUserId), Config.LIMIT_FROM_DB_COUNT, Constants.ZERO, popularItemViewModel.popularItemParameterHolder);
            featuredItemViewModel.setFeaturedItemListByKeyObj(Utils.checkUserId(loginUserId), Config.LIMIT_FROM_DB_COUNT, Constants.ZERO, featuredItemViewModel.featuredItemParameterHolder);
            blogViewModel.setNewsFeedObj(String.valueOf(Config.LIST_NEW_FEED_COUNT_PAGER), String.valueOf(blogViewModel.offset));
            itemFromFollowerViewModel.setItemFromFollowerListObj(Utils.checkUserId(loginUserId), Config.LIMIT_FROM_DB_COUNT, Constants.ZERO);
        });
    }

    private void callSearchList() {

        searchItemParameterHolder.keyword = binding.get().searchBoxEditText.getText().toString();

        navigationController.navigateToHomeFilteringActivity(getActivity(), searchItemParameterHolder, searchItemParameterHolder.keyword, selectedCityLat, selectedCityLng, Constants.MAP_MILES);

    }


    @Override
    protected void initViewModels() {
        itemCategoryViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemCategoryViewModel.class);
        recentItemViewModel = new ViewModelProvider(this, viewModelFactory).get(RecentItemViewModel.class);
        popularItemViewModel = new ViewModelProvider(this, viewModelFactory).get(PopularItemViewModel.class);
        featuredItemViewModel = new ViewModelProvider(this, viewModelFactory).get(FeaturedItemViewModel.class);
        blogViewModel = new ViewModelProvider(this, viewModelFactory).get(BlogViewModel.class);
        itemFromFollowerViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemFromFollowerViewModel.class);
        itemViewModel = new ViewModelProvider(this,viewModelFactory).get(ItemViewModel.class);
//        psAppInfoViewModel = new ViewModelProvider(this, viewModelFactory).get(PSAppInfoViewModel.class);
//        clearAllDataViewModel = new ViewModelProvider(this, viewModelFactory).get(ClearAllDataViewModel.class);
    }

    @Override
    protected void initAdapters() {


        DashBoardViewPagerAdapter nvAdapter3 = new DashBoardViewPagerAdapter(dataBindingComponent, blog -> navigationController.navigateToBlogDetailActivity(SelectedCityFragment.this.getActivity(), blog.id));

        this.dashBoardViewPagerAdapter = new AutoClearedValue<>(this, nvAdapter3);
        viewPager.get().setAdapter(dashBoardViewPagerAdapter.get());

            CityCategoryAdapter cityCategoryAdapter = new CityCategoryAdapter(dataBindingComponent,
                    new CityCategoryAdapter.CityCategoryClickCallback() {
                        @Override
                        public void onClick(ItemCategory category) {

                            itemParameterHolder.cat_id = category.id;
                            itemParameterHolder.isPaid = Constants.PAIDITEMFIRST;
                            itemParameterHolder.location_id = selected_location_id;
                            itemParameterHolder.lat = selectedLat;
                            itemParameterHolder.lng = selectedLng;

                            if(Config.SHOW_SUBCATEGORY){
                            navigationController.navigateToSubCategoryActivity(SelectedCityFragment.this.getActivity(), category.id, category.name);
                        }
                         else {
                                navigationController.navigateToHomeFilteringActivity(SelectedCityFragment.this.getActivity(), itemParameterHolder, category.name, selectedCityLat, selectedCityLng, Constants.MAP_MILES );

                            }
                        }
                    },
                    this);

        this.cityCategoryAdapter = new AutoClearedValue<>(this, cityCategoryAdapter);
        binding.get().cityCategoryRecyclerView.setAdapter(cityCategoryAdapter);


        ItemHorizontalListAdapter followerItemListAdapter = new ItemHorizontalListAdapter(dataBindingComponent, item -> navigationController.navigateToItemDetailActivity(SelectedCityFragment.this.getActivity(), item.id), this);
        this.followerItemListAdapter = new AutoClearedValue<>(this, followerItemListAdapter);
        binding.get().followerRecyclerView.setAdapter(followerItemListAdapter);

        ItemHorizontalListAdapter popularAdapter = new ItemHorizontalListAdapter(dataBindingComponent, item -> navigationController.navigateToItemDetailActivity(SelectedCityFragment.this.getActivity(), item.id), this);

        this.popularItemListAdapter = new AutoClearedValue<>(this, popularAdapter);
        binding.get().popularItemRecyclerView.setAdapter(popularAdapter);

        ItemHorizontalListAdapter featuredAdapter = new ItemHorizontalListAdapter(dataBindingComponent, item -> navigationController.navigateToItemDetailActivity(SelectedCityFragment.this.getActivity(), item.id), this);

        this.featuredItemListAdapter = new AutoClearedValue<>(this, featuredAdapter);
        binding.get().featuredItemRecyclerView.setAdapter(featuredAdapter);

        ItemHorizontalListAdapter recentAdapter = new ItemHorizontalListAdapter(dataBindingComponent, item ->
                navigationController.navigateToItemDetailActivity(this.getActivity(), item.id), this);

        this.recentItemListAdapter = new AutoClearedValue<>(this, recentAdapter);
        binding.get().recentItemRecyclerView.setAdapter(recentAdapter);


    }

    private void replaceItemFromFollowerList(List<Item> itemList) {
        this.followerItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replaceRecentItemList(List<Item> itemList) {
        this.recentItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replacePopularItemList(List<Item> itemList) {
        this.popularItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replaceFeaturedItemList(List<Item> itemList) {
        this.featuredItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replaceCityCategory(List<ItemCategory> categories) {
        cityCategoryAdapter.get().replace(categories);
        binding.get().executePendingBindings();
    }


    @Override
    protected void initData() {

        showItemFromFollower();

//        clearAllDataViewModel.getDeleteAllDataData().observe(this, result -> {
//
//            if (result != null) {
//                switch (result.status) {
//
//                    case ERROR:
//                        break;
//
//                    case SUCCESS:
//                        break;
//                }
//            }
//        });

        loadProducts();

        itemCategoryViewModel.getLoadingState().observe(this, loadingState -> {

            binding.get().setLoadingMore(itemCategoryViewModel.isLoading);

            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }

        });


    }

    private void showItemFromFollower() {
        if (loginUserId.isEmpty()) {
            hideForFollower();
        } else {
            showForFollower();
        }
    }

    private void showForFollower() {

        binding.get().followerConstraintLayout.setVisibility(View.VISIBLE);
        binding.get().followerTitleTextView.setVisibility(View.VISIBLE);
        binding.get().followerViewAllTextView.setVisibility(View.VISIBLE);
        binding.get().followerDescTextView.setVisibility(View.VISIBLE);
        binding.get().followerRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideForFollower() {

        binding.get().followerConstraintLayout.setVisibility(View.GONE);
        binding.get().followerTitleTextView.setVisibility(View.GONE);
        binding.get().followerViewAllTextView.setVisibility(View.GONE);
        binding.get().followerDescTextView.setVisibility(View.GONE);
        binding.get().followerRecyclerView.setVisibility(View.GONE);
    }


    private void getIntentData() {

        if (getActivity() != null) {
//            recentItemViewModel.locationId = getActivity().getIntent().getStringExtra(Constants.SELECTED_LOCATION_ID);
//            recentItemViewModel.locationName = getActivity().getIntent().getStringExtra(Constants.SELECTED_LOCATION_NAME);
//
//            if (getArguments() != null) {
//                recentItemViewModel.locationId = getArguments().getString(Constants.SELECTED_LOCATION_ID);
//                recentItemViewModel.locationName = getArguments().getString(Constants.SELECTED_LOCATION_NAME);
//                recentItemViewModel.locationLat = getArguments().getString(Constants.LAT);
//                recentItemViewModel.locationLng = getArguments().getString(Constants.LNG);
//            }

                itemViewModel.itemId = getActivity().getIntent().getStringExtra(Constants.ITEM_ID);
                recentItemViewModel.locationId = selected_location_id;
                recentItemViewModel.locationName = selected_location_name;
                recentItemViewModel.locationLat = selectedLat;
                recentItemViewModel.locationLng = selectedLng;

                recentItemViewModel.recentItemParameterHolder.location_id = recentItemViewModel.locationId;
                popularItemViewModel.popularItemParameterHolder.location_id = recentItemViewModel.locationId;
                featuredItemViewModel.featuredItemParameterHolder.location_id = recentItemViewModel.locationId;
                searchItemParameterHolder.location_id = recentItemViewModel.locationId;

                binding.get().locationTextView.setText(recentItemViewModel.locationName);


        }
    }

    private void loadProducts() {

        //Blog

        blogViewModel.setNewsFeedObj(String.valueOf(Config.LIST_NEW_FEED_COUNT_PAGER), String.valueOf(blogViewModel.offset));

        blogViewModel.getNewsFeedData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {
                    case SUCCESS:
                        replaceNewsFeedList(result.data);
                        blogViewModel.setLoadingState(false);
                        break;

                    case LOADING:
                        replaceNewsFeedList(result.data);
                        break;

                    case ERROR:

                        blogViewModel.setLoadingState(false);
                        break;
                }
            }

        });

        //Blog


        //City Category

        itemCategoryViewModel.setCategoryListObj(String.valueOf(Config.LIST_CATEGORY_COUNT), Constants.ZERO);

        itemCategoryViewModel.getCategoryListData().observe(this, listResource -> {

            if (listResource != null) {

                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {

                            if (listResource.data.size() > 0) {
                                replaceCityCategory(listResource.data);
                            }

                        }
                        itemCategoryViewModel.setLoadingState(false);

                        break;

                    case LOADING:

                        if (listResource.data != null) {

                            if (listResource.data.size() > 0) {
                                replaceCityCategory(listResource.data);
                            }

                        }

                        break;

                    case ERROR:
                        itemCategoryViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        //Popular Item

        popularItemViewModel.setPopularItemListByKeyObj(Utils.checkUserId(loginUserId), Config.LIMIT_FROM_DB_COUNT, Constants.ZERO, popularItemViewModel.popularItemParameterHolder);

        popularItemViewModel.getPopularItemListByKeyData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replacePopularItemList(listResource.data);
                            }
                        }

                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replacePopularItemList(listResource.data);
                            }
                        }

                        popularItemViewModel.setLoadingState(false);

                        break;

                    case ERROR:
                        break;
                }
            }
        });

        //Popular Item

        //Featured Item

        featuredItemViewModel.setFeaturedItemListByKeyObj(Utils.checkUserId(loginUserId), Config.LIMIT_FROM_DB_COUNT, Constants.ZERO, featuredItemViewModel.featuredItemParameterHolder);

        featuredItemViewModel.getFeaturedItemListByKeyData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replaceFeaturedItemList(listResource.data);
                            }
                        }

                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replaceFeaturedItemList(listResource.data);
                            }
                        }

                        featuredItemViewModel.setLoadingState(false);

                        break;

                    case ERROR:
                        break;
                }
            }
        });

        //Featured Item

        //Recent Item

        recentItemViewModel.setRecentItemListByKeyObj(Utils.checkUserId(loginUserId), Config.LIMIT_FROM_DB_COUNT, Constants.ZERO, recentItemViewModel.recentItemParameterHolder);

        recentItemViewModel.getRecentItemListByKeyData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                SelectedCityFragment.this.replaceRecentItemList(listResource.data);
                            }
                        }

                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                SelectedCityFragment.this.replaceRecentItemList(listResource.data);
                            }
                        }
                        recentItemViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        break;
                }
            }
        });

        // Item from follower

        itemFromFollowerViewModel.setItemFromFollowerListObj(Utils.checkUserId(loginUserId), Config.LIMIT_FROM_DB_COUNT, Constants.ZERO);

        itemFromFollowerViewModel.getItemFromFollowerListData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replaceItemFromFollowerList(listResource.data);
                            }
                        }

                        break;
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replaceItemFromFollowerList(listResource.data);
                                showForFollower();
                            }
                        } else {
                            hideForFollower();
                        }
                        itemFromFollowerViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        break;
                }
            }
        });

        //endregion


        viewPager.get().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {


                if (binding.get() != null && viewPager.get() != null) {
                    if (viewPager.get().getChildCount() > 0) {
                        layoutDone = true;
                        loadingCount++;
                        hideLoading();
                        viewPager.get().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }


    @Override
    public void onDispatched() {

//        if (homeLatestProductViewModel.loadingDirection == Utils.LoadingDirection.top) {
//
//            LinearLayoutManager layoutManager = (LinearLayoutManager)
//                    binding.get().productList.getLayoutManager();
//
//            if (layoutManager != null) {
//                layoutManager.scrollToPosition(0);
//            }
//
//        }
//
//        if (homeSearchProductViewModel.loadingDirection == Utils.LoadingDirection.top) {
//
//            GridLayoutManager layoutManager = (GridLayoutManager)
//                    binding.get().discountList.getLayoutManager();
//
//            if (layoutManager != null) {
//                layoutManager.scrollToPosition(0);
//            }
//
//        }
//
//        if (homeTrendingProductViewModel.loadingDirection == Utils.LoadingDirection.top) {
//
//            GridLayoutManager layoutManager = (GridLayoutManager)
//                    binding.get().trendingList.getLayoutManager();
//
//            if (layoutManager != null) {
//                layoutManager.scrollToPosition(0);
//            }
//
//        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setupSliderPagination() {

        int dotsCount = dashBoardViewPagerAdapter.get().getCount();

        if (dotsCount > 0 && dots == null) {

            dots = new ImageView[dotsCount];

            if (binding.get() != null) {
                if (pageIndicatorLayout.get().getChildCount() > 0) {
                    pageIndicatorLayout.get().removeAllViewsInLayout();
                }
            }

            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(getContext());
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(4, 0, 4, 0);

                pageIndicatorLayout.get().addView(dots[i], params);
            }

            dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

        }

    }

    private void hideLoading() {

        if (loadingCount == 3 && layoutDone) {

            binding.get().loadingView.setVisibility(View.GONE);
            binding.get().loadHolder.setVisibility(View.GONE);
        }
    }

    private void startPagerAutoSwipe() {

        update = () -> {
            if (!touched) {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }

                if (viewPager.get() != null) {
                    viewPager.get().setCurrentItem(currentPage++, true);
                }

            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!searchKeywordOnFocus) {
                    handler.post(update);
                }
            }
        }, 1000, 3000);
    }

    private void setUnTouchedTimer() {

        if (unTouchedTimer == null) {
            unTouchedTimer = new Timer();
            unTouchedTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    touched = false;
                    if (!searchKeywordOnFocus) {
                        handler.post(update);
                    }
                }
            }, 3000, 6000);
        } else {
            unTouchedTimer.cancel();
            unTouchedTimer.purge();

            unTouchedTimer = new Timer();
            unTouchedTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    touched = false;
                    if (!searchKeywordOnFocus) {
                        handler.post(update);
                    }
                }
            }, 3000, 6000);
        }
    }

    private void replaceNewsFeedList(List<Blog> blogs) {
        this.dashBoardViewPagerAdapter.get().replaceNewsFeedList(blogs);
        binding.get().executePendingBindings();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == Constants.REQUEST_CODE__SELECTED_CITY_FRAGMENT
                    && resultCode == Constants.RESULT_CODE__SEARCH_WITH_ITEM_LOCATION_TYPE) {

                recentItemViewModel.locationId = data.getStringExtra(Constants.ITEM_LOCATION_TYPE_ID);
                recentItemViewModel.locationName = data.getStringExtra(Constants.ITEM_LOCATION_TYPE_NAME);
                recentItemViewModel.locationLat = data.getStringExtra(Constants.LAT);
                recentItemViewModel.locationLng = data.getStringExtra(Constants.LNG);

                pref.edit().putString(Constants.SELECTED_LOCATION_ID, recentItemViewModel.locationId).apply();
                pref.edit().putString(Constants.SELECTED_LOCATION_NAME, recentItemViewModel.locationName).apply();
                pref.edit().putString(Constants.LAT, recentItemViewModel.locationLat).apply();
                pref.edit().putString(Constants.LNG, recentItemViewModel.locationLng).apply();


                if (getActivity() != null) {

                    navigationController.navigateToHome((MainActivity) getActivity(), true, recentItemViewModel.locationId,
                            recentItemViewModel.locationName,false);
                }

            }
        }
    }

    @Override
    public void onResume() {
        loadLoginUserId();
        super.onResume();
    }

}
