package com.panaceasoft.psbuyandsell.ui.offlinepayment;

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
import com.google.android.gms.ads.AdRequest;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentOfflineBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.ui.offlinepayment.adapter.OfflinePaymentAdapter;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.ItemPaidHistoryViewModel.ItemPaidHistoryViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.offlinepayment.OfflinePaymentViewModel;
import com.panaceasoft.psbuyandsell.viewobject.OfflinePayment;
import com.panaceasoft.psbuyandsell.viewobject.OfflinePaymentMethodHeader;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;


import java.util.List;

public  class OfflinePaymentHeaderListFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface{

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private OfflinePaymentViewModel offlinePaymentViewModel;
    public OfflinePaymentAdapter nvAdapter;
    private ItemPaidHistoryViewModel itemPaidHistoryViewModel;
    private PSDialogMsg psDialogMsg;

    @VisibleForTesting
    private AutoClearedValue<FragmentOfflineBinding> binding;
    private AutoClearedValue<OfflinePaymentAdapter> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        FragmentOfflineBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_offline, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

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

        psDialogMsg = new PSDialogMsg(this.getActivity(), false);

        binding.get().offlinePaymentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                if (layoutManager != null) {

                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();

                    if (lastPosition == adapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !offlinePaymentViewModel.forceEndLoading) {

                            int limit = Config.LIST_PRICE_TYPE_COUNT;

                            offlinePaymentViewModel.offset = offlinePaymentViewModel.offset + limit;

                            //offlinePaymentViewModel.setNextPageLoadingStateObj( String.valueOf(limit), String.valueOf(offlinePaymentViewModel.offset));

                            offlinePaymentViewModel.setLoadingState(true);
                        }
                    }
                }
            }
        });


      binding.get().payofflineButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              itemPaidHistoryViewModel.setUploadItemPaidHistoryData(
                      String.valueOf(itemPaidHistoryViewModel.itemId),
                      itemPaidHistoryViewModel.amount,
                      itemPaidHistoryViewModel.startDate,
                      itemPaidHistoryViewModel.howmanyDay,
                      Constants.OFFLINE,
                      "",
                      itemPaidHistoryViewModel.timeStamp,
                      "",
                      ""
              );
          }
      });

    }

    private void getIntentData() {
        try {
            if (getActivity() != null) {
                if (getActivity().getIntent().getExtras() != null) {
                    itemPaidHistoryViewModel.itemId = getActivity().getIntent().getExtras().getString(Constants.ITEM_ID);
                    itemPaidHistoryViewModel.amount = getActivity().getIntent().getExtras().getString(Constants.PROMOTE_AMOUNT);
                    itemPaidHistoryViewModel.startDate = getActivity().getIntent().getExtras().getString(Constants.PROMOTE_START_DATE);
                    itemPaidHistoryViewModel.howmanyDay = getActivity().getIntent().getExtras().getString(Constants.PROMOTE_HOWMANY_DAY);
                    itemPaidHistoryViewModel.timeStamp = getActivity().getIntent().getExtras().getString(Constants.PROMOTE_START_TIME_STAMP);
                    Utils.psLog(itemPaidHistoryViewModel.itemId);
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("", e);
        }
    }

    @Override
    public void onDispatched() {
        if (offlinePaymentViewModel.loadingDirection == Utils.LoadingDirection.top) {

            if (binding.get().offlinePaymentRecyclerView != null) {

                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        binding.get().offlinePaymentRecyclerView.getLayoutManager();

                if (layoutManager != null) {
                    layoutManager.scrollToPosition(0);
                }
            }
        }
    }


    @Override
    protected void initViewModels() {
        offlinePaymentViewModel = new ViewModelProvider(this, viewModelFactory).get(OfflinePaymentViewModel.class);
        itemPaidHistoryViewModel = new  ViewModelProvider(this,viewModelFactory).get(ItemPaidHistoryViewModel.class);
    }

    @Override
    protected void initAdapters() {

        nvAdapter = new OfflinePaymentAdapter(dataBindingComponent, offlinePaymentMethod -> {
        },this);

        this.adapter = new AutoClearedValue<>(this, nvAdapter);
        binding.get().offlinePaymentRecyclerView.setAdapter(nvAdapter);

    }

    @Override
    protected void initData() {

        getIntentData();

        offlinePaymentViewModel.setOfflinePaymentHeaderListObj(String.valueOf(Config.LIST_OFFLINE_COUNT),String.valueOf(offlinePaymentViewModel.offset));

        LiveData<Resource<OfflinePaymentMethodHeader>> news = offlinePaymentViewModel.getOfflinePaymentHeaderData();

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

                                // Update Message
                                binding.get().optionTextView.setText(listResource.data.message); //listResource.data.message;

                                // Update the data
                                replaceData(listResource.data.offlinePayment);

                            }

                            break;

                        case SUCCESS:
                            // Success State
                            // Data are from Server

                            if (listResource.data != null) {

                                // Update Message
                                binding.get().optionTextView.setText(listResource.data.message);

                                // Update the data
                                replaceData(listResource.data.offlinePayment);
                            }

                            offlinePaymentViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            offlinePaymentViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                    if (offlinePaymentViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        offlinePaymentViewModel.forceEndLoading = true;
                    }
                }
            });

            itemPaidHistoryViewModel.getUploadItemPaidHistoryData().observe(OfflinePaymentHeaderListFragment.this, result -> {
                if (result != null) {
                    if (result.status == Status.SUCCESS) {

                        psDialogMsg.showSuccessDialog(getString(R.string.item_promote__success_message), getString(R.string.app__ok));
                        psDialogMsg.show();

                        itemPaidHistoryViewModel.setLoadingState(false);
                        psDialogMsg.okButton.setOnClickListener(view -> {
                            psDialogMsg.cancel();
                                if (getActivity() != null) {
                                    getActivity().finish();
                                }
                        });

                    } else if (result.status == Status.ERROR) {

                        psDialogMsg.showErrorDialog(result.message, getString(R.string.app__ok));
                        psDialogMsg.show();

                        itemPaidHistoryViewModel.setLoadingState(false);
                    }
                }
            });

        }

//        offlinePaymentViewModel.getNextPageLoadingStateData().observe(this, state -> {
//            if (state != null) {
//                if (state.status == Status.ERROR) {
//                    Utils.psLog("Next Page State : " + state.data);
//
//                    offlinePaymentViewModel.setLoadingState(false);
//                    offlinePaymentViewModel.forceEndLoading = true;
//                }
//            }
//        });

        offlinePaymentViewModel.getLoadingState().observe(this, loadingState -> {
            binding.get().setLoadingMore(offlinePaymentViewModel.isLoading);

        });
    }

    private void replaceData(List<OfflinePayment> offlinePaymentList) {

       adapter.get().replace(offlinePaymentList);
        binding.get().executePendingBindings();

    }

}
