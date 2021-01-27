package com.panaceasoft.psbuyandsell.ui.item.search.specialfilterbyattributes;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.ads.AdRequest;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentFilterBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.viewmodel.item.ItemViewModel;
import com.panaceasoft.psbuyandsell.viewobject.holder.ItemParameterHolder;

public class FilteringFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private ItemViewModel itemViewModel;
    private String typeId = Constants.NO_DATA;
    private String priceTypeId = Constants.NO_DATA;
    private String dealOptionId = Constants.NO_DATA;
    private String conditionId = Constants.NO_DATA;
    private String locationId = Constants.EMPTY_STRING;
    private String currencyId = Constants.EMPTY_STRING;

    @VisibleForTesting
    private AutoClearedValue<FragmentFilterBinding> binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentFilterBinding fragmentFilterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, fragmentFilterBinding);
        setHasOptionsMenu(true);

        binding.get().setLoadingMore(connectivity.isConnected());

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            binding.get().adView.loadAd(adRequest);
        } else {
            binding.get().adView.setVisibility(View.GONE);
        }

        binding.get().setItemName.setHint(R.string.sf__notSet);


        if (getActivity() != null) {

            // Get Data from Intent

            Intent intent = getActivity().getIntent();
            itemViewModel.holder = (ItemParameterHolder) intent.getSerializableExtra(Constants.FILTERING_HOLDER);

            // Name Binding

            if (itemViewModel.holder != null) {
                binding.get().setItemName.setText(itemViewModel.holder.keyword);
                binding.get().typeTextView.setText(itemViewModel.holder.type_name);
                binding.get().itemConditionTextView.setText(itemViewModel.holder.condition_name);
                binding.get().priceTypeTextView.setText(itemViewModel.holder.price_type_name);
                binding.get().dealOptionTextView.setText(itemViewModel.holder.deal_option_name);

                // Sorting Buttons Binding
                if (itemViewModel.holder.order_by != null) {

                    switch (itemViewModel.holder.order_by) {
                        case Constants.FILTERING_ADDED_DATE:
                            setSortingSelection(0);
                            break;

                        case Constants.FILTERING_FEATURE:
                            setSortingSelection(0);
                            break;

                        case Constants.FILTERING_TRENDING:
                            setSortingSelection(1);
                            break;

                        case Constants.FILTERING_NAME:

                            if (itemViewModel.holder.order_type != null) {
                                if (itemViewModel.holder.order_type.equals(Constants.FILTERING_ASC)) {
                                    setSortingSelection(2);
                                } else {
                                    setSortingSelection(3);
                                }
                            }

                            break;

                    }
                }

            }
        }

        binding.get().typeCardView.setOnClickListener(view -> navigationController.navigateToSearchViewActivity(this.getActivity(), Constants.ITEM_TYPE, typeId, priceTypeId, conditionId, dealOptionId,currencyId,locationId));

        binding.get().itemConditionCardView.setOnClickListener(view -> navigationController.navigateToSearchViewActivity(this.getActivity(), Constants.ITEM_CONDITION_TYPE, typeId, priceTypeId, conditionId, dealOptionId,currencyId,locationId));

        binding.get().priceTypeCardView.setOnClickListener(view -> navigationController.navigateToSearchViewActivity(this.getActivity(), Constants.ITEM_PRICE_TYPE, typeId, priceTypeId, conditionId, dealOptionId,currencyId,locationId));

        binding.get().dealOptionCardView.setOnClickListener(view -> navigationController.navigateToSearchViewActivity(this.getActivity(), Constants.ITEM_DEAL_OPTION_TYPE, typeId, priceTypeId, conditionId, dealOptionId,currencyId,locationId));


        binding.get().filter.setOnClickListener(view -> {

            // Get Name
            itemViewModel.holder.keyword = binding.get().setItemName.getText().toString();
            itemViewModel.holder.min_price = binding.get().lowestPriceEditText.getText().toString();
            itemViewModel.holder.max_price = binding.get().highestPriceEditText.getText().toString();

            itemViewModel.holder.type_name = binding.get().typeTextView.getText().toString();
            itemViewModel.holder.price_type_name = binding.get().priceTypeTextView.getText().toString();
            itemViewModel.holder.condition_name = binding.get().itemConditionTextView.getText().toString();
            itemViewModel.holder.deal_option_name = binding.get().dealOptionTextView.getText().toString();

            // Set to Intent

            navigationController.navigateBackToHomeFeaturedFragmentFromFiltering(FilteringFragment.this.getActivity(), itemViewModel.holder);
            FilteringFragment.this.getActivity().finish();

        });

        binding.get().recentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSortingSelection(0);
            }
        });

        binding.get().popularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSortingSelection(1);
            }
        });

        binding.get().lowestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSortingSelection(3);
            }
        });

        binding.get().highestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSortingSelection(2);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ok_button, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.clearButton) {

            binding.get().setItemName.setText( Constants.EMPTY_STRING);
            itemViewModel.holder.keyword = Constants.EMPTY_STRING;

            binding.get().lowestPriceEditText.setText( Constants.EMPTY_STRING);
            binding.get().highestPriceEditText.setText( Constants.EMPTY_STRING);
            itemViewModel.holder.min_price = Constants.EMPTY_STRING;
            itemViewModel.holder.max_price = Constants.EMPTY_STRING;

            binding.get().typeTextView.setText( Constants.EMPTY_STRING);
            binding.get().priceTypeTextView.setText( Constants.EMPTY_STRING);
            binding.get().itemConditionTextView.setText( Constants.EMPTY_STRING);
            binding.get().dealOptionTextView.setText( Constants.EMPTY_STRING);
            this.typeId = Constants.EMPTY_STRING;
            this.priceTypeId = Constants.EMPTY_STRING;
            this.conditionId = Constants.EMPTY_STRING;
            this.dealOptionId = Constants.EMPTY_STRING;
            itemViewModel.holder.type_id = Constants.EMPTY_STRING;
            itemViewModel.holder.price_type_id= Constants.EMPTY_STRING;
            itemViewModel.holder.condition_id = Constants.EMPTY_STRING;
            itemViewModel.holder.deal_option_id = Constants.EMPTY_STRING;

            setSortingSelection(0);


            itemViewModel.holder.order_by = Constants.FILTERING_ADDED_DATE;
            itemViewModel.holder.order_type = Constants.FILTERING_DESC;

            //navigationController.navigateBackToHomeFeaturedFragmentFromFiltering(SpecialFilteringFragment.this.getActivity(), itemViewModel.holder);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViewModels() {
        itemViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemViewModel.class);
    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        bindingData();

    }

    private void bindingData() {

        binding.get().lowestPriceEditText.setText(itemViewModel.holder.min_price);
        binding.get().highestPriceEditText.setText(itemViewModel.holder.max_price);

        binding.get().typeTextView.setText(itemViewModel.holder.type_name);
        binding.get().priceTypeTextView.setText(itemViewModel.holder.price_type_name);
        binding.get().itemConditionTextView.setText(itemViewModel.holder.condition_name);
        binding.get().dealOptionTextView.setText(itemViewModel.holder.deal_option_name);

        this.typeId = itemViewModel.holder.type_id;
        this.priceTypeId = itemViewModel.holder.price_type_id;
        this.conditionId = itemViewModel.holder.condition_id;
        this.dealOptionId = itemViewModel.holder.deal_option_id;


    }

    @Override
    public void onDispatched() {

    }

    private void setSortingSelection(int index) {

        if (index == 0) {
            binding.get().recentButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baesline_access_time_black_24), null, getResources().getDrawable(R.drawable.baseline_check_green_24), null);

            itemViewModel.holder.order_by = Constants.FILTERING_ADDED_DATE;
            itemViewModel.holder.order_type = Constants.FILTERING_DESC;

        } else {
            binding.get().recentButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baesline_access_time_black_24), null, null, null);
        }

        if (index == 1) {
            binding.get().popularButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_graph_black_24), null, getResources().getDrawable(R.drawable.baseline_check_green_24), null);

            itemViewModel.holder.order_by = Constants.FILTERING_TRENDING;
            itemViewModel.holder.order_type = Constants.FILTERING_DESC;

        } else {
            binding.get().popularButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_graph_black_24), null, null, null);
        }

        if (index == 2) {
            binding.get().highestButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_price_up_black_24), null, getResources().getDrawable(R.drawable.baseline_check_green_24), null);

            itemViewModel.holder.order_by = Constants.FILTERING_NAME;
            itemViewModel.holder.order_type = Constants.FILTERING_ASC;

        } else {
            binding.get().highestButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_price_up_black_24), null, null, null);
        }

        if (index == 3) {
            binding.get().lowestButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_price_down_black_24), null, getResources().getDrawable(R.drawable.baseline_check_green_24), null);

            itemViewModel.holder.order_by = Constants.FILTERING_NAME;
            itemViewModel.holder.order_type = Constants.FILTERING_DESC;

        } else {
            binding.get().lowestButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_price_down_black_24), null, null, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE__SEARCH_VIEW_FRAGMENT && resultCode == Constants.RESULT_CODE__SEARCH_WITH_ITEM_TYPE) {

            this.typeId = data.getStringExtra(Constants.ITEM_TYPE_ID);
            binding.get().typeTextView.setText(data.getStringExtra(Constants.ITEM_TYPE_NAME));
            itemViewModel.holder.type_id = this.typeId;
        }
        else if (requestCode == Constants.REQUEST_CODE__SEARCH_VIEW_FRAGMENT && resultCode == Constants.RESULT_CODE__SEARCH_WITH_ITEM_PRICE_TYPE) {

            this.priceTypeId = data.getStringExtra(Constants.ITEM_PRICE_TYPE_ID);
            binding.get().priceTypeTextView.setText(data.getStringExtra(Constants.ITEM_PRICE_TYPE_NAME));
            itemViewModel.holder.price_type_id = this.priceTypeId;
        }
        else if (requestCode == Constants.REQUEST_CODE__SEARCH_VIEW_FRAGMENT && resultCode == Constants.RESULT_CODE__SEARCH_WITH_ITEM_CONDITION_TYPE) {

            this.conditionId = data.getStringExtra(Constants.ITEM_CONDITION_TYPE_ID);
            binding.get().itemConditionTextView.setText(data.getStringExtra(Constants.ITEM_CONDITION_TYPE_NAME));
            itemViewModel.holder.condition_id = this.conditionId;
        }
        else if (requestCode == Constants.REQUEST_CODE__SEARCH_VIEW_FRAGMENT && resultCode == Constants.RESULT_CODE__SEARCH_WITH_ITEM_OPTION_TYPE) {

            this.dealOptionId = data.getStringExtra(Constants.ITEM_OPTION_TYPE_ID);
            binding.get().dealOptionTextView.setText(data.getStringExtra(Constants.ITEM_OPTION_TYPE_NAME));
            itemViewModel.holder.deal_option_id = this.dealOptionId;
        }

    }
}
