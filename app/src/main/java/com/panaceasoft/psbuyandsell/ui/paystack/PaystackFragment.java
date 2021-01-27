package com.panaceasoft.psbuyandsell.ui.paystack;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;

import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentPaystackBinding;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;


import org.json.JSONException;

import java.util.Calendar;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PaystackFragment extends PSFragment {
    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ProgressDialog progressDialog;
    private PSDialogMsg psDialogMsg;
    private String paystackKey;
    private Charge charge;
    private String userEmail = "";

    @VisibleForTesting
    private AutoClearedValue<FragmentPaystackBinding> binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        FragmentPaystackBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_paystack, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);


        return binding.get().getRoot();

    }

    @Override
    protected void initUIAndActions() {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Getting Token");
        progressDialog.setCancelable(false);

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        binding.get().PayStackButton.setOnClickListener(v -> {
            if(binding.get().CardNumberEditText.getText().toString().isEmpty()){
                psDialogMsg.showWarningDialog(getString(R.string.paystack__choose_card_number), getString(R.string.app__ok));
                psDialogMsg.show();
            }else if(binding.get().ExpiredMMEditText.getText().toString().isEmpty() ||
                    binding.get().ExpiredYYEditText.getText().toString().isEmpty()){
                psDialogMsg.showWarningDialog(getString(R.string.paystack__choose_expired_date), getString(R.string.app__ok));
                psDialogMsg.show();
            }else if(binding.get().CvcEditText.getText().toString().isEmpty()){
                psDialogMsg.showWarningDialog(getString(R.string.paystack__choose_cvc), getString(R.string.app__ok));
                psDialogMsg.show();
            }else if(binding.get().CardNameEditText.getText().toString().isEmpty()){
                psDialogMsg.showWarningDialog(getString(R.string.paystack__card_holder_name), getString(R.string.app__ok));
                psDialogMsg.show();
            }else {
                startAFreshCharge();
            }

        });

        binding.get().CardNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int len=s.toString().length();

                if (before == 0 && (len == 4 || len == 9 || len == 14 || len == 19 || len == 24 || len == 29)) {
                    s = s + " ";
                    binding.get().CardNumberEditText.setText(s);
                    binding.get().CardNumberEditText.setSelection(len + 1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

    }

    private void startAFreshCharge() {
        // initialize the charge
        charge = new Charge();
        charge.setCard(loadCardFromForm());

        progressDialog.setMessage(getString(R.string.paystack__performing_trans));
        progressDialog.show();

            // Set transaction params directly in app (note that these params
            // are only used if an access_code is not set. In debug mode,
            // setting them after setting an access code would throw an exception

            charge.setAmount(2000);
//            charge.setEmail("customer@email.com");
            charge.setEmail(userEmail);
            charge.setReference("ChargedFromAndroid_" + Calendar.getInstance().getTimeInMillis());
            try {
                charge.putCustomField("Charged From", "Android SDK");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chargeCard();

    }

    private Card loadCardFromForm() {
        //validate fields
        Card card;
        String cardNum = binding.get().CardNumberEditText.getText().toString().replace(" ", "");
        String cvc = binding.get().CvcEditText.getText().toString();
        String cardMonth = binding.get().ExpiredMMEditText.getText().toString();
        String cardYear = binding.get().ExpiredYYEditText.getText().toString();
        String cardName = binding.get().CardNameEditText.getText().toString();

        //build card object with ONLY the number, update the other fields later
        card = new Card.Builder(cardNum, Integer.parseInt(cardMonth), Integer.parseInt(cardYear),cvc).build();
        card.setName(cardName);
        return card;
    }

    private void chargeCard() {

        PaystackSdk.chargeCard(getActivity(), charge, new Paystack.TransactionCallback() {
            // This is called only after transaction is successful

            @Override
            public void onSuccess(Transaction transaction) {
                progressDialog.cancel();

                close(transaction.getReference());

            }

            // This is called only before requesting OTP
            // Save reference so you may send to server if
            // error occurs with OTP
            // No need to dismiss dialog
            @Override
            public void beforeValidate (Transaction transaction) {
                Toast.makeText(getActivity(), transaction.getReference(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                if (error instanceof ExpiredAccessCodeException) {
                    startAFreshCharge();
                    chargeCard();
                    return;
                }else{
                    psDialogMsg.showErrorDialog(error.toString(), getString(R.string.app__ok));
                    psDialogMsg.show();
                }

                progressDialog.cancel();
            }

        });
    }

    @Override
    protected void initViewModels() {
//        psappLoadingViewModel = new ViewModelProvider(this,viewModelFactory).get(PSAPPLoadingViewModel.class);
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

    public void close(String token) {

        if (getActivity() != null) {
            navigationController.navigatePaystackBackToPayStackRequestFragment(getActivity(), token);

            getActivity().finish();

        }
    }

    private void getIntentData() {
        try {
            if (getActivity() != null) {
                if (getActivity().getIntent().getExtras() != null){
                    paystackKey = getActivity().getIntent().getExtras().getString(Constants.PAYSTACKKEY);
                    userEmail = getActivity().getIntent().getExtras().getString(Constants.USER_EMAIL);
                    Utils.psLog(paystackKey);
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("",e);
        }
    }
}
