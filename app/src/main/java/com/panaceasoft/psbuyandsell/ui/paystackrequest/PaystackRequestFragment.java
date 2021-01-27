package com.panaceasoft.psbuyandsell.ui.paystackrequest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentPaystackRequestBinding;

import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.ItemPaidHistoryViewModel.ItemPaidHistoryViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.user.UserViewModel;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import co.paystack.android.PaystackSdk;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PaystackRequestFragment extends PSFragment {

    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemPaidHistoryViewModel itemPaidHistoryViewModel;

    private UserViewModel userViewModel;

    private PSDialogMsg psDialogMsg;

    String paystackKey;

    private String payment_method_nonce;

    @VisibleForTesting
    private AutoClearedValue<FragmentPaystackRequestBinding> binding;

    private AutoClearedValue<ProgressDialog> prgDialog;

    //endregion
    String userEmail = "";

    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentPaystackRequestBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_paystack_request, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }


    @Override
    protected void initUIAndActions() {

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        // Init Dialog
        prgDialog = new AutoClearedValue<>(this, new ProgressDialog(getActivity()));
        //prgDialog.get().setMessage(getString(R.string.message__please_wait));

        prgDialog.get().setCancelable(false);

        //fadeIn Animation
        fadeIn(binding.get().getRoot());

        binding.get().PaystackButton.setOnClickListener(view -> {
            userEmail = binding.get().emailEditText.getText().toString();
            if (userEmail.equals("")) {
                psDialogMsg.showErrorDialog(getString(R.string.error_message__blank_email), getString(R.string.app__ok));
                psDialogMsg.show();
                return;
            }
            else{
                navigationController.navigateToPaystackActivity(PaystackRequestFragment.this.getActivity(), paystackKey, userEmail);
            }

        });

    }

    @Override
    protected void initViewModels() {
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);
        itemPaidHistoryViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemPaidHistoryViewModel.class);

    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        getIntentData();

        PaystackSdk.setPublicKey(paystackKey);

        //initialize sdk
        PaystackSdk.initialize(getApplicationContext());

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

        userViewModel.getLoginUser().observe(this, listResource -> {
            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (listResource != null && listResource.size() > 0) {
                Utils.psLog("Got Data");

                //fadeIn Animation
                fadeIn(binding.get().getRoot());
                userViewModel.user = listResource.get(0).user;
                binding.get().emailEditText.setText(userViewModel.user.userEmail);

            } else {
                //noinspection Constant Conditions
                Utils.psLog("Empty Data");
            }
        });

        try {
            if (getActivity() != null) {
                if (getActivity().getIntent().getExtras() != null) {
                    paystackKey = getActivity().getIntent().getExtras().getString(Constants.PAYSTACKKEY);
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("", e);
        }
    }

    public void sendData(String razorPaymentID) {
        itemPaidHistoryViewModel.setUploadItemPaidHistoryData(
                String.valueOf(itemPaidHistoryViewModel.itemId),
                itemPaidHistoryViewModel.amount,
                itemPaidHistoryViewModel.startDate,
                itemPaidHistoryViewModel.howmanyDay,
                Constants.PAYSTACK,
                payment_method_nonce,
                itemPaidHistoryViewModel.timeStamp,
                razorPaymentID,
                ""
        );
    }

    private void getIntentData() {
        try {
            if(getActivity() != null) {
                if(getActivity().getIntent().getExtras() != null){
                    paystackKey = getActivity().getIntent().getExtras().getString(Constants.PAYSTACKKEY);
                    itemPaidHistoryViewModel.itemId = getActivity().getIntent().getExtras().getString(Constants.ITEM_ID);
                    itemPaidHistoryViewModel.amount = getActivity().getIntent().getExtras().getString(Constants.PROMOTE_AMOUNT);
                    itemPaidHistoryViewModel.startDate = getActivity().getIntent().getExtras().getString(Constants.PROMOTE_START_DATE);
                    itemPaidHistoryViewModel.howmanyDay = getActivity().getIntent().getExtras().getString(Constants.PROMOTE_HOWMANY_DAY);
                    itemPaidHistoryViewModel.timeStamp = getActivity().getIntent().getExtras().getString(Constants.PROMOTE_START_TIME_STAMP);
                    Utils.psLog(itemPaidHistoryViewModel.itemId);
                    Utils.psLog(paystackKey);
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("",e);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE__PAYSTACK_REQUEST_ACTIVITY &&
                resultCode == Constants.RESULT_CODE__PAYSTACK_REQUEST_ACTIVITY) {
            if (getActivity() != null) {
                payment_method_nonce = data.getStringExtra(Constants.PAYMENT_TOKEN);
                sendData("");

                navigationController.navigatePaystackBackToPromoteFragment(getActivity(), payment_method_nonce);
                getActivity().finish();

            }

        }
    }

    //endregion
}
