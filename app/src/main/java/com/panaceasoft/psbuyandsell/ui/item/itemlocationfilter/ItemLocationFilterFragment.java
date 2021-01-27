package com.panaceasoft.psbuyandsell.ui.item.itemlocationfilter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.MainActivity;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentItemLocationFilterBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;

import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.itemlocation.ItemLocationViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ItemLocation;


import java.util.List;

public class ItemLocationFilterFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private ItemLocationViewModel itemLocationViewModel;
    public Intent intent = new Intent();
    private ItemLocation itemLocation;
    private boolean searchKeywordOnFocus = false;

    @VisibleForTesting
    private AutoClearedValue<FragmentItemLocationFilterBinding> binding;
    private AutoClearedValue<List<ItemLocation>> lastItemLocationData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentItemLocationFilterBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_location_filter, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();



    }

    @Override
    public void onDispatched() {

    }

    @Override
    protected void initUIAndActions() {

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.layout__primary_background));
            ((MainActivity) getActivity()).updateToolbarIconColor(Color.GRAY);
            ((MainActivity) getActivity()).updateMenuIconGrey();
            ((MainActivity) getActivity()).refreshPSCount();
        }


        binding.get().keywordEditText.setOnFocusChangeListener((v, hasFocus) -> {

            searchKeywordOnFocus = hasFocus;
            Utils.psLog("Focus " + hasFocus);
        });
        binding.get().keywordEditText.setOnKeyListener((v, keyCode, event) -> {

            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                itemLocationViewModel.keyword = binding.get().keywordEditText.getText().toString();

                binding.get().keywordEditText.clearFocus();
                searchKeywordOnFocus = false;

                itemLocationViewModel.loadingDirection = Utils.LoadingDirection.top;

                // reset productViewModel.offset
                itemLocationViewModel.offset = 0;

                // reset productViewModel.forceEndLoading
                itemLocationViewModel.forceEndLoading = false;

                // update live data
                itemLocationViewModel.setItemLocationListObj(String.valueOf(Config.LIST_LOCATION_COUNT),
                        String.valueOf(itemLocationViewModel.offset),
                        loginUserId,
                        itemLocationViewModel.keyword,
                        itemLocationViewModel.orderBy,
                        itemLocationViewModel.orderType);


                Utils.psLog("Down");

                return false;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {

                Utils.psLog("Up");
            }
            return false;
        });



        binding.get().filterButton.setOnClickListener(v -> {


            if (binding.get().defaultRadioButton.isChecked()) {
                itemLocationViewModel.orderBy = Constants.SEARCH_CITY_DEFAULT_ORDERING;

            }else {
                itemLocationViewModel.orderBy = Constants.SEARCH_CITY_ADDED_DATE;
            }

            if (binding.get().ascRadioButton.isChecked()) {
                itemLocationViewModel.orderType = Constants.SEARCH_CITY_ASCE;

            }else {
                itemLocationViewModel.orderType = Constants.SEARCH_CITY_DESC;
            }


            if (getActivity() != null) {
                navigationController.navigateBackToItemLocationFilterFragment(getActivity(),
                        binding.get().keywordEditText.getText().toString(),
                        itemLocationViewModel.orderBy, itemLocationViewModel.orderType);
                getActivity().finish();

            }

        });


    }


    @Override
    protected void initViewModels() {
        itemLocationViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemLocationViewModel.class);
    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            itemLocationViewModel.keyword = intent.getStringExtra(Constants.SEARCH_CITY_INTENT_KEYWORD);
            itemLocationViewModel.orderType = intent.getStringExtra(Constants.SEARCH_CITY_INTENT_ORDER_TYPE);
            itemLocationViewModel.orderBy = intent.getStringExtra(Constants.SEARCH_CITY_INTENT_ORDER_BY);

            binding.get().keywordEditText.setText(itemLocationViewModel.keyword);

            if (itemLocationViewModel.orderBy.equals(Constants.SEARCH_CITY_DEFAULT_ORDERING)){
               binding.get().defaultRadioButton.setChecked(true);
              binding.get().latestDateRadioButton.setChecked(false);
            }else {
                binding.get().latestDateRadioButton.setChecked(true);
                binding.get().defaultRadioButton.setChecked(false);

            }

            if (itemLocationViewModel.orderType.equals(Constants.SEARCH_CITY_ASCE)){
                binding.get().ascRadioButton.setChecked(true);
                binding.get().descRadioButton.setChecked(false);
            }else {
                binding.get().descRadioButton.setChecked(true);
                binding.get().ascRadioButton.setChecked(false);

            }

        }
    }


}



