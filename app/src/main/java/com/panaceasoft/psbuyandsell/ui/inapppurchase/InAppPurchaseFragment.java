package com.panaceasoft.psbuyandsell.ui.inapppurchase;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.MainActivity;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentInAppPurchaseBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.ui.inapppurchase.adapter.InAppPurchaseAdapter;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.ItemPaidHistoryViewModel.ItemPaidHistoryViewModel;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class InAppPurchaseFragment extends PSFragment implements PurchasesUpdatedListener, DataBoundListAdapter.DiffUtilDispatchedInterface {
    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemPaidHistoryViewModel itemPaidHistoryViewModel;

    @VisibleForTesting
    private AutoClearedValue<FragmentInAppPurchaseBinding> binding;
    private AutoClearedValue<InAppPurchaseAdapter> inAppPurchaseAdapter;
    private PSDialogMsg psDialogMsg;
    private Calendar dateTime = Calendar.getInstance();
    private long startTimeStamp;
    private Date startingDate;
    private static BillingClient billingClient;
    private String amount;
    ConsumeResponseListener listener;
    Map<String, String> dayMap = new HashMap<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentInAppPurchaseBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_in_app_purchase, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        setupBillingClient();

        return binding.get().getRoot();
    }

    @Override
    public void onDispatched() {

        LinearLayoutManager layoutManager = (LinearLayoutManager)
                binding.get().productListRecyclerView.getLayoutManager();

        if (layoutManager != null) {
            layoutManager.scrollToPosition(0);
        }
    }

    public static class BillingClientSetup {
        private static BillingClient instance;

        public static BillingClient getInstance(Context context, PurchasesUpdatedListener listener) {
            return  instance == null ? setupBillingClient(context, listener): instance;
        }

        private static BillingClient setupBillingClient(Context context, PurchasesUpdatedListener listener) {
            billingClient = BillingClient.newBuilder(context)
                    .enablePendingPurchases()
                    .setListener(listener)
                    .build();
            return billingClient;
        }
    }

    List<String> getIdAndDayList(String idAndDayString) {
        final List<String> idList = new ArrayList<>();
        dayMap = new HashMap<>();

        if (idAndDayString != null && idAndDayString.contains("##")) {
            final String[] idAndDayList = idAndDayString.split("##");

            for (String idAndDay : idAndDayList) {
                if (idAndDay != null && idAndDay.contains("@@")) {
                    final String[] idAndDaySplit = idAndDay.split("@@");
                    idList.add(idAndDaySplit[0]);
                    dayMap.put(idAndDaySplit[0], idAndDaySplit[1]);
                }
            }

        }

        return idList ;

    }

    private void setupBillingClient (){
        listener = (billingResult, s) -> {
            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                Toast.makeText(getContext(), "3 Ok!", Toast.LENGTH_SHORT).show();
        };

        billingClient = BillingClientSetup.getInstance(getContext(), this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if( billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(getContext(), "Success to connect billing", Toast.LENGTH_SHORT).show();
                    List<String> skus = new ArrayList<>();

                    skus = getIdAndDayList(itemPaidHistoryViewModel.inAppPurchasedPrdIdAndroid);

                    SkuDetailsParams params = SkuDetailsParams.newBuilder()
                            .setSkusList(skus)
                            .setType(BillingClient.SkuType.INAPP)
                            .build();

                    billingClient.querySkuDetailsAsync(params, (billingResult1, list) -> {
                        if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                            replaceInAppPurchasedData(list);

                        } else {
                            Toast.makeText(getContext(), "Error code:" + billingResult1.getResponseCode(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Error code:"+ billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(getContext(), "Yor are disconnect from Billing Service", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void initUIAndActions() {

        psDialogMsg = new PSDialogMsg(this.getActivity(), false);

        binding.get().startDateDataTextView.setOnClickListener(view -> InAppPurchaseFragment.this.openDatePicker(binding.get().startDateDataTextView));


        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setToolbarText(((MainActivity) getActivity()).binding.toolbar, getString(R.string.item_promote__purchase_promotion_packages));
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.global__primary));
            ((MainActivity) getActivity()).updateMenuIconWhite();
            ((MainActivity) getActivity()).updateToolbarIconColor(Color.WHITE);
            ((MainActivity) getActivity()).refreshPSCount();
        }

    }

    @Override
    protected void initViewModels() {
        itemPaidHistoryViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemPaidHistoryViewModel.class);
    }

    @Override
    protected void initAdapters() {
        InAppPurchaseAdapter inAppPurchaseAdapter = new InAppPurchaseAdapter(dataBindingComponent,
                skuDetails -> {
                    if (startingDate == null || startingDate.equals("")) {
                        psDialogMsg.showWarningDialog(InAppPurchaseFragment.this.getString(R.string.item_promote__warning_for_start_date), InAppPurchaseFragment.this.getString(R.string.app__ok));
                        psDialogMsg.show();
                    } else {

                        BillingFlowParams billingBuilder =
                                BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build();

                        int response = billingClient.launchBillingFlow((Activity) Objects.requireNonNull(getContext()), billingBuilder).
                                getResponseCode();

                        amount = skuDetails.getPrice();

                        switch (response) {
                            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                            Toast.makeText(getContext(), "BILLING_UNAVAILABLE", Toast.LENGTH_LONG).show();
                            break;

                            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                                Toast.makeText(getContext(), "DEVELOPER_ERROR", Toast.LENGTH_LONG).show();
                                break;

                            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                                Toast.makeText(getContext(), "FEATURE_NOT_SUPPORTED", Toast.LENGTH_LONG).show();
                                break;

                            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                                Toast.makeText(getContext(), "ITEM_ALREADY_OWNED", Toast.LENGTH_LONG).show();
                                break;

                            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                                Toast.makeText(getContext(), "SERVICE_DISCONNECTED", Toast.LENGTH_LONG).show();
                                break;

                            case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                                Toast.makeText(getContext(), "SERVICE_TIMEOUT", Toast.LENGTH_LONG).show();
                                break;

                            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                                Toast.makeText(getContext(), "ITEM_UNAVAILABLE", Toast.LENGTH_LONG).show();
                                break;

                            default:
                                break;

                        }
                    }
                });

        this.inAppPurchaseAdapter = new AutoClearedValue<>(this, inAppPurchaseAdapter);
        binding.get().productListRecyclerView.setAdapter(inAppPurchaseAdapter);
    }

    @Override
    protected void initData() {

        getIntentData();

        itemPaidHistoryViewModel.getUploadItemPaidHistoryData().observe(this, result -> {
            if (result != null) {
                if (result.status == Status.SUCCESS) {

                    psDialogMsg.showSuccessDialog(getString(R.string.item_promote__success_message), getString(R.string.app__ok));
                    psDialogMsg.show();

                    itemPaidHistoryViewModel.setLoadingState(false);
                    psDialogMsg.okButton.setOnClickListener(view -> {
                        psDialogMsg.cancel();
                        if (Config.CLOSE_ENTRY_AFTER_SUBMIT) {
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
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

    private void openDatePicker(TextView editText) {

        DatePickerDialog.OnDateSetListener datePickerDialog = (view, year, monthOfYear, dayOfMonth) -> {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            InAppPurchaseFragment.this.updateDate(editText);
        };

        if (getContext() != null) {
            DatePickerDialog dialog = new DatePickerDialog(getContext(), datePickerDialog, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
            dialog.show();
        }
    }

    private void updateDate(TextView editText) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
        String shortTimeStr = sdf.format(dateTime.getTime());
        editText.setText(shortTimeStr);

        try {
            long millis = new Date().getTime();
            Date todayDate = Utils.getDateCurrentTimeZone(millis);

            Date userChoiceDate = sdf.parse(shortTimeStr);
            Utils.psLog("User Choice Date " + userChoiceDate);
            Utils.psLog("Today Date " + todayDate);

            if (DateUtils.isToday(userChoiceDate.getTime())) {

                Calendar cal = Calendar.getInstance();
                cal.setTime(userChoiceDate);
                cal.add(Calendar.MINUTE, 30);
                Utils.psLog("Today Date fddddd " + cal.getTime());

                startTimeStamp = Utils.getTimeStamp(cal.getTime());

            } else {
                Calendar pickedUpCalendar = Calendar.getInstance();
                pickedUpCalendar.setTime(userChoiceDate);
                int year = pickedUpCalendar.get(Calendar.YEAR);
                int month = pickedUpCalendar.get(Calendar.MONTH);
                int day = pickedUpCalendar.get(Calendar.DAY_OF_MONTH);

                pickedUpCalendar.set(year, month, day, 0, 0, 0);

                startTimeStamp = Utils.getTimeStamp(pickedUpCalendar.getTime());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            startingDate = sdf.parse(shortTimeStr);
            Utils.psLog(String.format("Starting Date Time is : %s", String.valueOf(startingDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        }
//        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
//            // Handle an error caused by a user cancelling the purchase flow.
//        } else {
//            // Handle any other error codes.
//        }
    }

    private void getIntentData() {
        try {
            if(getActivity() != null) {
                if(getActivity().getIntent().getExtras() != null){
                    itemPaidHistoryViewModel.itemId = getActivity().getIntent().getExtras().getString(Constants.ITEM_ID);
                    itemPaidHistoryViewModel.inAppPurchasedPrdIdAndroid = getActivity().getIntent().getExtras().getString(Constants.IN_APP_PURCHASED_PRD_ID_ANDROID);
                    Utils.psLog(itemPaidHistoryViewModel.itemId);
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("",e);
        }
    }

    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    int responseCode = billingResult.getResponseCode();
                    String debugMessage = billingResult.getDebugMessage();
                });

                itemPaidHistoryViewModel.setUploadItemPaidHistoryData(
                        String.valueOf(itemPaidHistoryViewModel.itemId),
                        amount,
                        binding.get().startDateDataTextView.getText().toString(),
                        dayMap.get(purchase.getSku()),
                        Constants.IN_APP_PURCHASE,
                        "",
                        String.valueOf(startTimeStamp),
                        "",
                        purchase.getOrderId()
                );
            }
        }
    }

    private void replaceInAppPurchasedData(List<SkuDetails> skuDetails) {
        inAppPurchaseAdapter.get().replace(skuDetails);
        binding.get().executePendingBindings();

    }
}


