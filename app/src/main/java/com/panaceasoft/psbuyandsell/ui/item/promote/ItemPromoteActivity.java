package com.panaceasoft.psbuyandsell.ui.item.promote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.databinding.ActivityItemPromoteEntryBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSAppCompactActivity;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.MyContextWrapper;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewobject.User;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import javax.inject.Inject;

public class ItemPromoteActivity extends PSAppCompactActivity implements PaymentResultListener {

    PSFragment fragment;
    User user;

    @Inject
    SharedPreferences pref;

    //region Override Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityItemPromoteEntryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_item_promote_entry);

        // Init all UI
        initUI(binding);

        //razor
        Checkout.preload(getApplicationContext());

    }

    public void startPayment(String currency,int amount) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", user.userName);
            options.put("description", "");
            options.put("currency", currency);//currency code

            options.put("amount", Utils.round( amount * 100, 2));

            JSONObject preFill = new JSONObject();
            preFill.put("email", user.userEmail);
            preFill.put("contact", user.userPhone);

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        this.user = user;
    }

    public User getCurrentUser() {
        return this.user;
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String CURRENT_LANG_CODE = preferences.getString(Constants.LANGUAGE_CODE, Config.DEFAULT_LANGUAGE);
        String CURRENT_LANG_COUNTRY_CODE = preferences.getString(Constants.LANGUAGE_COUNTRY_CODE, Config.DEFAULT_LANGUAGE_COUNTRY_CODE);

        super.attachBaseContext(MyContextWrapper.wrap(newBase, CURRENT_LANG_CODE, CURRENT_LANG_COUNTRY_CODE, true));
    }

    //endregion


    //region Private Methods

    private void initUI(ActivityItemPromoteEntryBinding binding) {

        // Toolbar
        initToolbar(binding.toolbar, getString(R.string.item_promote__promote_entry));

        fragment = new ItemPromoteFragment();

        // setup Fragment
        setupFragment(fragment);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPaymentSuccess(String razorPaymentID) {
        ((ItemPromoteFragment) fragment).sendData(razorPaymentID);
    }

    @Override
    public void onPaymentError(int code, String response) {

    }
}