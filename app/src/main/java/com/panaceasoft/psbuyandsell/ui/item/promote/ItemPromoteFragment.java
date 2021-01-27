package com.panaceasoft.psbuyandsell.ui.item.promote;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentItemPromoteEntryBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.ItemPaidHistoryViewModel.ItemPaidHistoryViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.apploading.PSAPPLoadingViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.paypal.PaypalViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.user.UserViewModel;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ItemPromoteFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

//region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private Calendar dateTime = Calendar.getInstance();
    private String startDate = Constants.ZERO;
    private String endDate = Constants.ZERO;
    private String clientTokenString;
    private String payment_method_nonce;
    private String paymentMethod;
    private String currencySymbol;
    private String oneDayPrice = Config.PROMOTE_DEFAULT_ONE_DAY_PRICE;
    private String dayEditTextString;
    private int firstChoicePrice;
    private int secondChoicePrice;
    private int thirdChoicePrice;
    private int fourthChoicePrice;
    private Date startingDate;
    private String endingDate = String.valueOf(Config.PROMOTE_FIRST_CHOICE_DAY_OR_DEFAULT_DAY);
    private PSDialogMsg psDialogMsg;
    private boolean chooseDay = false;
    private boolean customDay = false;
    private long startTimeStamp;
    private int amount;


    private PSAPPLoadingViewModel appLoadingViewModel;
    private PaypalViewModel paypalViewModel;
    private ItemPaidHistoryViewModel itemPaidHistoryViewModel;
    private UserViewModel userViewModel;


    @VisibleForTesting
    private AutoClearedValue<FragmentItemPromoteEntryBinding> binding;


//endregion

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentItemPromoteEntryBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_promote_entry, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);


        return binding.get().getRoot();


    }

    @Override
    public void onDispatched() {

    }

    @Override
    protected void initUIAndActions() {

        psDialogMsg = new PSDialogMsg(this.getActivity(), false);

        binding.get().firstChoiceCountTextView.setText(String.format("%s%s", Config.PROMOTE_FIRST_CHOICE_DAY_OR_DEFAULT_DAY, getString(R.string.item_promote__days)));

        binding.get().secondChoiceCountTextView.setText(String.format("%s%s", Config.PROMOTE_SECOND_CHOICE_DAY, getString(R.string.item_promote__days)));

        binding.get().thirdChoiceCountTextView.setText(String.format("%s%s", Config.PROMOTE_THIRD_CHOICE_DAY, getString(R.string.item_promote__days)));

        binding.get().fourthChoiceCountTextView.setText(String.format("%s%s", Config.PROMOTE_FOURTH_CHOICE_DAY, getString(R.string.item_promote__days)));

        binding.get().firstChoiceTitleTextView.setText(String.format("%s%s%s%s%s", getString(R.string.item_promote__promote_for), " ", Config.PROMOTE_FIRST_CHOICE_DAY_OR_DEFAULT_DAY, " ", getString(R.string.item_promote__c_days)));

        binding.get().secondChoiceTitleTextView.setText(String.format("%s%s%s%s%s", getString(R.string.item_promote__promote_for), " ", Config.PROMOTE_SECOND_CHOICE_DAY, " ", getString(R.string.item_promote__c_days)));

        binding.get().thirdChoiceTitleTextView.setText(String.format("%s%s%s%s%s", getString(R.string.item_promote__promote_for), " ", Config.PROMOTE_THIRD_CHOICE_DAY, " ", getString(R.string.item_promote__c_days)));

        binding.get().fourthChoiceTitleTextView.setText(String.format("%s%s%s%s%s", getString(R.string.item_promote__promote_for), " ", Config.PROMOTE_FOURTH_CHOICE_DAY, " ", getString(R.string.item_promote__c_days)));

        binding.get().firstChoicePriceTextView.setText(String.valueOf(firstChoicePrice));

        binding.get().thirdChoicePriceTextView.setText(String.valueOf(thirdChoicePrice));

        binding.get().secondChoicePriceTextView.setText(String.valueOf(secondChoicePrice));

        binding.get().fourthChoicePriceTextView.setText(String.valueOf(fourthChoicePrice));

        binding.get().startDateDataTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemPromoteFragment.this.openDatePicker(binding.get().startDateDataTextView);
            }
        });

        binding.get().dayDataEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.get().customSelectView.setVisibility(View.VISIBLE);
                binding.get().fourthChoiceSelectView.setVisibility(View.GONE);
                binding.get().thirdChoiceSelectView.setVisibility(View.GONE);
                binding.get().secondChoiceSelectView.setVisibility(View.GONE);
                binding.get().firstChoiceSelectView.setVisibility(View.GONE);
                chooseDay = false;
                customDay = true;

            }
        });

        binding.get().customPromoteConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.get().customSelectView.setVisibility(View.VISIBLE);
                binding.get().fourthChoiceSelectView.setVisibility(View.GONE);
                binding.get().thirdChoiceSelectView.setVisibility(View.GONE);
                binding.get().secondChoiceSelectView.setVisibility(View.GONE);
                binding.get().firstChoiceSelectView.setVisibility(View.GONE);
                chooseDay = false;
                customDay = true;
            }
        });

        binding.get().dayDataEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                dayEditTextString = binding.get().dayDataEditText.getText().toString();

                if (!dayEditTextString.equals("")) {
                    amount = Integer.parseInt(oneDayPrice) * Integer.parseInt(dayEditTextString);
                    binding.get().customPriceTextView.setText(String.format("%s%s", currencySymbol, amount));
                    chooseDay = true;
                    endingDate = dayEditTextString;
                } else {
                    binding.get().customPriceTextView.setText(String.format("%s%s", currencySymbol, "0.00"));
                    chooseDay = false;

                }

            }
        });
        binding.get().firstChoiceConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.get().firstChoiceSelectView.setVisibility(View.VISIBLE);
                binding.get().thirdChoiceSelectView.setVisibility(View.GONE);
                binding.get().secondChoiceSelectView.setVisibility(View.GONE);
                binding.get().fourthChoiceSelectView.setVisibility(View.GONE);
                binding.get().customSelectView.setVisibility(View.GONE);
                amount = firstChoicePrice;
                endingDate = String.valueOf(Config.PROMOTE_FIRST_CHOICE_DAY_OR_DEFAULT_DAY);
                chooseDay = true;
                customDay = false;
            }
        });

        binding.get().secondChoiceConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.get().secondChoiceSelectView.setVisibility(View.VISIBLE);
                binding.get().thirdChoiceSelectView.setVisibility(View.GONE);
                binding.get().firstChoiceSelectView.setVisibility(View.GONE);
                binding.get().fourthChoiceSelectView.setVisibility(View.GONE);
                binding.get().customSelectView.setVisibility(View.GONE);
                amount = secondChoicePrice;

                endingDate = String.valueOf(Config.PROMOTE_SECOND_CHOICE_DAY);
                chooseDay = true;
                customDay = false;
            }
        });

        binding.get().thirdChoiceConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.get().thirdChoiceSelectView.setVisibility(View.VISIBLE);
                binding.get().firstChoiceSelectView.setVisibility(View.GONE);
                binding.get().secondChoiceSelectView.setVisibility(View.GONE);
                binding.get().fourthChoiceSelectView.setVisibility(View.GONE);
                binding.get().customSelectView.setVisibility(View.GONE);
                amount = thirdChoicePrice;

                endingDate = String.valueOf(Config.PROMOTE_THIRD_CHOICE_DAY);
                chooseDay = true;
                customDay = false;
            }
        });


        binding.get().fourthChoiceConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.get().fourthChoiceSelectView.setVisibility(View.VISIBLE);
                binding.get().thirdChoiceSelectView.setVisibility(View.GONE);
                binding.get().secondChoiceSelectView.setVisibility(View.GONE);
                binding.get().firstChoiceSelectView.setVisibility(View.GONE);
                binding.get().customSelectView.setVisibility(View.GONE);
                amount = fourthChoicePrice;

                endingDate = String.valueOf(Config.PROMOTE_FOURTH_CHOICE_DAY);
                chooseDay = true;
                customDay = false;
            }
        });

        binding.get().paypelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = Constants.PAYPAL;
                if (startingDate == null || startingDate.equals("")) {
                    psDialogMsg.showWarningDialog(ItemPromoteFragment.this.getString(R.string.item_promote__warning_for_start_date), ItemPromoteFragment.this.getString(R.string.app__ok));
                    psDialogMsg.show();
                }
                else if(customDay){
                    if( binding.get().dayDataEditText.getText().toString().equals("")){
                        psDialogMsg.showWarningDialog(ItemPromoteFragment.this.getString(R.string.item_promote__enter_day), ItemPromoteFragment.this.getString(R.string.app__ok));
                        psDialogMsg.show();
                    }else{
                        amount = Integer.parseInt(oneDayPrice) * Integer.parseInt(binding.get().dayDataEditText.getText().toString());
                        paypalViewModel.setPaypalTokenObj();
                    }
                }
                else if (!chooseDay) {
                    amount = firstChoicePrice;
                    paypalViewModel.setPaypalTokenObj();
                } else {
                    paypalViewModel.setPaypalTokenObj();
                }

            }
        });

        binding.get().stripeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = Constants.STRIPE;
                if (startingDate == null || startingDate.equals("")) {
                    psDialogMsg.showWarningDialog(ItemPromoteFragment.this.getString(R.string.item_promote__warning_for_start_date), ItemPromoteFragment.this.getString(R.string.app__ok));
                    psDialogMsg.show();
                } else if(customDay){
                    if( binding.get().dayDataEditText.getText().toString().equals("")){
                        psDialogMsg.showWarningDialog(ItemPromoteFragment.this.getString(R.string.item_promote__enter_day), ItemPromoteFragment.this.getString(R.string.app__ok));
                        psDialogMsg.show();
                    }else{
                        amount = Integer.parseInt(oneDayPrice) * Integer.parseInt(binding.get().dayDataEditText.getText().toString());
                        navigationController.navigateToStripeActivity(ItemPromoteFragment.this.getActivity(), appLoadingViewModel.stripePublishableKey);
                    }
                }
                else if (!chooseDay) {
                        amount = firstChoicePrice;
                        navigationController.navigateToStripeActivity(ItemPromoteFragment.this.getActivity(), appLoadingViewModel.stripePublishableKey);
                }
                else {
                    navigationController.navigateToStripeActivity(ItemPromoteFragment.this.getActivity(), appLoadingViewModel.stripePublishableKey);
                }
            }
        });

        binding.get().payStackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = Constants.PAYSTACK;
                if (startingDate == null || startingDate.equals("")) {
                    psDialogMsg.showWarningDialog(ItemPromoteFragment.this.getString(R.string.item_promote__warning_for_start_date), ItemPromoteFragment.this.getString(R.string.app__ok));
                    psDialogMsg.show();

                } else if(customDay) {
                    if( binding.get().dayDataEditText.getText().toString().equals("")){
                        psDialogMsg.showWarningDialog(ItemPromoteFragment.this.getString(R.string.item_promote__enter_day), ItemPromoteFragment.this.getString(R.string.app__ok));
                        psDialogMsg.show();
                    } else {
                        amount = Integer.parseInt(oneDayPrice) * Integer.parseInt(binding.get().dayDataEditText.getText().toString());
                        navigationController.navigateToPaystackRequestActivity(ItemPromoteFragment.this.getActivity(), appLoadingViewModel.payStackKey,
                                itemPaidHistoryViewModel.itemId,String.valueOf(amount),
                                binding.get().startDateDataTextView.getText().toString(),
                                endingDate,
                                String.valueOf(startTimeStamp));
                    }
                }
                else if (!chooseDay) {
                    amount = firstChoicePrice;
                    navigationController.navigateToPaystackRequestActivity(ItemPromoteFragment.this.getActivity(), appLoadingViewModel.payStackKey,
                            itemPaidHistoryViewModel.itemId,String.valueOf(amount),
                            binding.get().startDateDataTextView.getText().toString(),
                            endingDate,
                            String.valueOf(startTimeStamp));
                }
                else {
                    navigationController.navigateToPaystackRequestActivity(ItemPromoteFragment.this.getActivity(), appLoadingViewModel.payStackKey,
                            itemPaidHistoryViewModel.itemId,String.valueOf(amount),
                            binding.get().startDateDataTextView.getText().toString(),
                            endingDate,
                            String.valueOf(startTimeStamp));
                }
            }
        });

        binding.get().offlinePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    paymentMethod = Constants.OFFLINE;

                    if (startingDate == null || startingDate.equals("")) {
                        psDialogMsg.showWarningDialog(ItemPromoteFragment.this.getString(R.string.item_promote__warning_for_start_date), ItemPromoteFragment.this.getString(R.string.app__ok));
                        psDialogMsg.show();
                    } else if (customDay) {
                        if (binding.get().dayDataEditText.getText().toString().equals("")) {
                            psDialogMsg.showWarningDialog(ItemPromoteFragment.this.getString(R.string.item_promote__enter_day), ItemPromoteFragment.this.getString(R.string.app__ok));
                            psDialogMsg.show();
                        } else {
                            amount = Integer.parseInt(oneDayPrice) * Integer.parseInt(binding.get().dayDataEditText.getText().toString());
                            navigationController.navigateToOfflinePaymentActivity(
                                    ItemPromoteFragment.this.getActivity(),itemPaidHistoryViewModel.itemId,String.valueOf(amount),
                                    binding.get().startDateDataTextView.getText().toString(),
                                    endingDate,
                                    String.valueOf(startTimeStamp));

                            if (getActivity() !=null) {
                                getActivity().finish();
                            }
                        }
                    } else if (!chooseDay) {
                        amount = firstChoicePrice;
                        navigationController.navigateToOfflinePaymentActivity(
                                ItemPromoteFragment.this.getActivity(),itemPaidHistoryViewModel.itemId,String.valueOf(amount),
                                binding.get().startDateDataTextView.getText().toString(),
                                endingDate,
                                String.valueOf(startTimeStamp));
                        if (getActivity() !=null) {
                            getActivity().finish();
                        }
                    } else {
                        navigationController.navigateToOfflinePaymentActivity(
                                ItemPromoteFragment.this.getActivity(),itemPaidHistoryViewModel.itemId,String.valueOf(amount),
                                binding.get().startDateDataTextView.getText().toString(),
                                endingDate,
                                String.valueOf(startTimeStamp));
                        if (getActivity() !=null) {
                            getActivity().finish();
                        }
                    }
                }
        });

        binding.get().razorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = Constants.RAZOR;
                if (startingDate == null || startingDate.equals("")) {
                    psDialogMsg.showWarningDialog(ItemPromoteFragment.this.getString(R.string.item_promote__warning_for_start_date), ItemPromoteFragment.this.getString(R.string.app__ok));
                    psDialogMsg.show();
                } else if (customDay) {
                    if (binding.get().dayDataEditText.getText().toString().equals("")) {
                        psDialogMsg.showWarningDialog(ItemPromoteFragment.this.getString(R.string.item_promote__enter_day), ItemPromoteFragment.this.getString(R.string.app__ok));
                        psDialogMsg.show();
                    } else {
                        amount = Integer.parseInt(oneDayPrice) * Integer.parseInt(binding.get().dayDataEditText.getText().toString());
                        ItemPromoteFragment.this.callRazor();
                    }
                } else if (!chooseDay) {
                    amount = firstChoicePrice;
                    ItemPromoteFragment.this.callRazor();
                } else {
                    ItemPromoteFragment.this.callRazor();
                }
            }
        });

    }

    private void  callRazor(){
        if(getActivity() != null) {
            if (Config.isRazorSupportMultiCurrency) {
                ((ItemPromoteActivity) getActivity()).startPayment(appLoadingViewModel.currencyShortForm, amount);
            } else {
                ((ItemPromoteActivity) getActivity()).startPayment(Config.defaultRazorCurrency, amount);
            }
        }
    }

    @Override
    protected void initViewModels() {
        appLoadingViewModel = new ViewModelProvider(this, viewModelFactory).get(PSAPPLoadingViewModel.class);
        paypalViewModel = new ViewModelProvider(this, viewModelFactory).get(PaypalViewModel.class);
        itemPaidHistoryViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemPaidHistoryViewModel.class);
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);

    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        getIntentData();

        userViewModel.getLoginUser().observe(this, listResource -> {
            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (listResource != null && listResource.size() > 0) {

                //fadeIn Animation
                ItemPromoteFragment.this.fadeIn(binding.get().getRoot());

                userViewModel.user = listResource.get(0).user;
                if (getActivity() != null) {

                    ((ItemPromoteActivity) ItemPromoteFragment.this.getActivity()).setCurrentUser(userViewModel.user);
                    ItemPromoteFragment.this.initUIAndActions();
                }

            }
        });

        if (connectivity.isConnected()) {
            if (startDate.equals(Constants.ZERO)) {

                startDate = getDateTime();
                Utils.setDatesToShared(startDate, endDate, pref);
            }

            endDate = getDateTime();
            appLoadingViewModel.setDeleteHistoryObj(startDate, endDate,loginUserId);

        } else {
            if (!selected_location_id.isEmpty()) {
                navigationController.navigateToMainActivity(getActivity(), selected_location_id, selected_location_name,itemPaidHistoryViewModel.itemId, selectedLat, selectedLng);

            } else {
                navigationController.navigateToLocationActivity(getActivity(), Constants.LOCATION_NOT_CLEAR_ICON, Constants.EMPTY_STRING,itemPaidHistoryViewModel.itemId);
            }

            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (getActivity() != null) {
                getActivity().finish();
            }
        }

        appLoadingViewModel.getDeleteHistoryData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {

                    case SUCCESS:

                        if (result.data != null) {

                            if (result.data.paypalEnabled.equals(Constants.CHECKPAYPALENABLE)) {
                                binding.get().paypelButton.setVisibility(View.VISIBLE);
                            } else {
                                binding.get().paypelButton.setVisibility(View.GONE);
                            }

                            if(result.data.stripeEnabled.equals(Constants.CHECKSTRIPEENABLE)){
                                binding.get().stripeButton.setVisibility(View.VISIBLE);
                            } else{
                                binding.get().stripeButton.setVisibility(View.GONE);
                            }

                            if(result.data.payStackEnabled.equals(Constants.CHECKPAYSTACKENABLE)){
                                binding.get().payStackButton.setVisibility(View.VISIBLE);
                            } else{
                                binding.get().payStackButton.setVisibility(View.GONE);
                            }

                            if(result.data.razorEnabled.equals(Constants.ONE)){
                                binding.get().razorButton.setVisibility(View.VISIBLE);
                            } else{
                                binding.get().razorButton.setVisibility(View.GONE);
                            }

                            appLoadingViewModel.psAppInfo = result.data;
                            oneDayPrice = result.data.oneDay;
                            currencySymbol = result.data.currencySymbol;
                            appLoadingViewModel.stripePublishableKey = result.data.stripePublishableKey;
                            appLoadingViewModel.payStackKey = result.data.payStackKey;
                            appLoadingViewModel.currencyShortForm = result.data.currencyShortForm;
                            CalculateOneDayPrice(oneDayPrice);
                            binding.get().firstChoicePriceTextView.setText(String.format("%s%s", result.data.currencySymbol, String.valueOf(firstChoicePrice)));
                            binding.get().secondChoicePriceTextView.setText(String.format("%s%s", result.data.currencySymbol, String.valueOf(secondChoicePrice)));
                            binding.get().thirdChoicePriceTextView.setText(String.format("%s%s", result.data.currencySymbol, String.valueOf(thirdChoicePrice)));
                            binding.get().fourthChoicePriceTextView.setText(String.format("%s%s", result.data.currencySymbol, String.valueOf(fourthChoicePrice)));
                            binding.get().customPriceTextView.setText(String.format("%s%s", result.data.currencySymbol, getString(R.string.item_promote__default_custom_price)));
                        }
                        break;

                    case ERROR:
                        Utils.psLog("Error in itemPromote Fragment");
                        break;
                }
            }

        });


        paypalViewModel.getPaypalTokenData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {

                    case SUCCESS:
                        clientTokenString = result.message;
                        Utils.psLog("Receive form sever paypal token string :  " + clientTokenString);

                        onBrainTreeSubmit();

                        break;

                    case ERROR:

                        Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        });


        itemPaidHistoryViewModel.getUploadItemPaidHistoryData().observe(this, result -> {
            if (result != null) {
                if (result.status == Status.SUCCESS) {

                    psDialogMsg.showSuccessDialog(getString(R.string.item_promote__success_message), getString(R.string.app__ok));
                    psDialogMsg.show();

                    itemPaidHistoryViewModel.setLoadingState(false);
                    psDialogMsg.okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            psDialogMsg.cancel();
                            if (Config.CLOSE_ENTRY_AFTER_SUBMIT) {
                                if (getActivity() != null) {
                                    getActivity().finish();
                                }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Utils.psLog(String.valueOf(requestCode));


        if (requestCode == Constants.REQUEST_CODE__PAYPAL) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                if (result.getPaymentMethodNonce() != null) {
                    onPaymentMethodNonceCreated(result.getPaymentMethodNonce());
                }
            }
        } else if (requestCode == Constants.REQUEST_CODE__STRIPE_ACTIVITY && resultCode == Constants.RESULT_CODE__STRIPE_ACTIVITY) {
            if (this.getActivity() != null) {

                payment_method_nonce = data.getStringExtra(Constants.PAYMENT_TOKEN);
                sendData("");

            }
        }
    }

    private void onBrainTreeSubmit() {
        if (getActivity() != null) {
            DropInRequest dropInRequest = new DropInRequest()
                    .clientToken(clientTokenString);
            this.getActivity().startActivityForResult(dropInRequest.getIntent(this.getActivity()), Constants.REQUEST_CODE__PAYPAL);
        }
    }

    private void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

        payment_method_nonce = paymentMethodNonce.getNonce();
        Utils.psLog("Payment Method Nonce " + payment_method_nonce);
        sendData("");
    }

    private void openDatePicker(TextView editText) {

        DatePickerDialog.OnDateSetListener datePickerDialog = (view, year, monthOfYear, dayOfMonth) -> {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            ItemPromoteFragment.this.updateDate(editText);
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

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void sendData(String razorPaymentID) {
        itemPaidHistoryViewModel.setUploadItemPaidHistoryData(
                itemPaidHistoryViewModel.itemId,
                String.valueOf(amount),
                binding.get().startDateDataTextView.getText().toString(),
                endingDate,
                paymentMethod,
                payment_method_nonce,
                String.valueOf(startTimeStamp),
                razorPaymentID,
                ""
        );
    }


    private void getIntentData() {
        try {
            if (getActivity() != null) {
                if (getActivity().getIntent().getExtras() != null) {
                    itemPaidHistoryViewModel.itemId = getActivity().getIntent().getExtras().getString(Constants.ITEM_ID);
                    Utils.psLog(itemPaidHistoryViewModel.itemId);
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("", e);
        }
    }

    private void CalculateOneDayPrice(String price) {
        int oneDayAmount = Integer.parseInt(price);
        firstChoicePrice = oneDayAmount * Config.PROMOTE_FIRST_CHOICE_DAY_OR_DEFAULT_DAY;
        secondChoicePrice = oneDayAmount * Config.PROMOTE_SECOND_CHOICE_DAY;
        thirdChoicePrice = oneDayAmount * Config.PROMOTE_THIRD_CHOICE_DAY;
        fourthChoicePrice = oneDayAmount * Config.PROMOTE_FOURTH_CHOICE_DAY;

    }

}
