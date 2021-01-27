package com.panaceasoft.psbuyandsell.ui.item.reporteditem;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.MainActivity;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentReportedItemBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.ui.item.reporteditem.adapter.ReportedItemAdapter;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.reporteditem.ReportedItemViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ReportedItem;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import java.util.List;

public class ReportedItemFragment  extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ReportedItemViewModel reportedItemViewModel;

    @VisibleForTesting
    private AutoClearedValue<FragmentReportedItemBinding> binding;
    private AutoClearedValue<ReportedItemAdapter> reportedItemAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentReportedItemBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reported_item, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }


    @Override
    public void onDispatched() {
        if (reportedItemViewModel.loadingDirection == Utils.LoadingDirection.bottom) {

            if (binding.get().reportedItemListRecyclerView != null) {

                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        binding.get().reportedItemListRecyclerView.getLayoutManager();

                if (layoutManager != null) {
                    layoutManager.scrollToPosition(0);
                }
            }
        }
    }

    @Override
    protected void initUIAndActions() {

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setToolbarText(((MainActivity) getActivity()).binding.toolbar, getString(R.string.menu__report_item));
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.global__primary));
            ((MainActivity) getActivity()).updateMenuIconWhite();
            ((MainActivity) getActivity()).updateToolbarIconColor(Color.WHITE);
            ((MainActivity) getActivity()).refreshPSCount();
        }



        binding.get().reportedItemListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                if (layoutManager != null) {

                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();
                    if (lastPosition == reportedItemAdapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !reportedItemViewModel.forceEndLoading) {

                            reportedItemViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                            int limit = Config.REPORTED_ITEM_COUNT;
                            reportedItemViewModel.offset = reportedItemViewModel.offset + limit;
                            reportedItemViewModel.setLoadingState(true);
                            reportedItemViewModel.setNextPagereportedObj(String.valueOf(Config.REPORTED_ITEM_COUNT),
                                    String.valueOf(reportedItemViewModel.offset),loginUserId);
                        }
                    }
                }
            }
        });

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            reportedItemViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset productViewModel.offset

            reportedItemViewModel.offset = 0;

            // reset productViewModel.forceEndLoading
            reportedItemViewModel.forceEndLoading = false;

            reportedItemViewModel.setReportedObj(String.valueOf(Config.REPORTED_ITEM_COUNT), String.valueOf(reportedItemViewModel.offset),loginUserId);

            // update live data

        });
    }

    @Override
    protected void initViewModels() {
        reportedItemViewModel = new ViewModelProvider(this,viewModelFactory).get(ReportedItemViewModel.class);

    }

    @Override
    protected void initAdapters() {
        ReportedItemAdapter reportedItemAdapter = new ReportedItemAdapter(dataBindingComponent,
                reportedItem -> navigationController.navigateToItemDetailFromHistoryListOnly(ReportedItemFragment.this.getActivity(), reportedItem.id, reportedItem.title));
        this.reportedItemAdapter = new AutoClearedValue<>(this, reportedItemAdapter);
        binding.get().reportedItemListRecyclerView.setAdapter(reportedItemAdapter);

    }

    @Override
    protected void initData() {
        reportedItemViewModel.setReportedObj(String.valueOf(Config.REPORTED_ITEM_COUNT),String.valueOf(reportedItemViewModel.offset),loginUserId);
        reportedItemViewModel.getReportedData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {
                    case SUCCESS:
                        replaceItemReportedData(result.data);
                        reportedItemViewModel.setLoadingState(false);
                        break;

                    case LOADING:
                        replaceItemReportedData(result.data);
                        break;

                    case ERROR:

                        reportedItemViewModel.setLoadingState(false);
                        break;
                }
            }

        });

        reportedItemViewModel.getNextPagereportedData().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {

                    reportedItemViewModel.setLoadingState(false);
                    reportedItemViewModel.forceEndLoading = true;
                }
            }
        });


        reportedItemViewModel.getLoadingState().observe(this, loadingState -> {

            binding.get().setLoadingMore(reportedItemViewModel.isLoading);

            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }

        });

    }

    private void replaceItemReportedData(List<ReportedItem> reportedItems) {
        reportedItemAdapter.get().replace(reportedItems);
        binding.get().executePendingBindings();

    }
}
