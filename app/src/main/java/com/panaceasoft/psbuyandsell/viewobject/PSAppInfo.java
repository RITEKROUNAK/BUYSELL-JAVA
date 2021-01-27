package com.panaceasoft.psbuyandsell.viewobject;


import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(primaryKeys = "id")
public class PSAppInfo {

    @NonNull
    public String id;

    @Embedded(prefix = "version_")
    @SerializedName("version")
    public PSAppVersion psAppVersion;

    @SerializedName("app_setting")
    @Embedded(prefix = "app_setting_")
    public PSAppSetting psAppSetting;

    @SerializedName("user_info")
    @Embedded(prefix = "user_info_")
    public PSUserInfo userInfo;

    @SerializedName("delete_history")
    @Ignore
    public List<DeletedObject> deletedObjects;

    @SerializedName("oneday")
    public String oneDay;

    @SerializedName("currency_symbol")
    public String currencySymbol;

    @SerializedName("currency_short_form")
    public String currencyShortForm;

    @SerializedName("stripe_publishable_key")
    public String stripePublishableKey;

    @SerializedName("paystack_key")
    public String payStackKey;

    @SerializedName("in_app_purchased_enabled")
    public String inAppPurchasedEnabled;

    @SerializedName("in_app_purchased_prd_id_android")
    public String inAppPurchasedPrdIdAndroid;

    @SerializedName("stripe_enabled")
    public String stripeEnabled;

    @SerializedName("paystack_enabled")
    public String payStackEnabled;

    @SerializedName("paypal_enabled")
    public String paypalEnabled;

    @SerializedName("razor_enabled")
    public String razorEnabled;

    @SerializedName("razor_key")
    public String razorKey;

    public PSAppInfo(@NonNull String id, PSAppVersion psAppVersion, PSAppSetting psAppSetting, PSUserInfo userInfo, String oneDay, String currencySymbol, String currencyShortForm, String stripePublishableKey, String payStackKey, String inAppPurchasedEnabled, String inAppPurchasedPrdIdAndroid, String payStackEnabled, String paypalEnabled, String stripeEnabled,String razorEnabled,String razorKey) {
        this.id = id;
        this.psAppVersion = psAppVersion;
        this.psAppSetting = psAppSetting;
        this.userInfo =userInfo;
        this.oneDay = oneDay;
        this.currencySymbol = currencySymbol;
        this.currencyShortForm = currencyShortForm;
        this.stripePublishableKey = stripePublishableKey;
        this.payStackKey = payStackKey;
        this.inAppPurchasedEnabled = inAppPurchasedEnabled;
        this.inAppPurchasedPrdIdAndroid = inAppPurchasedPrdIdAndroid;
        this.payStackEnabled = payStackEnabled;
        this.stripeEnabled = stripeEnabled;
        this.paypalEnabled = paypalEnabled;
        this.razorEnabled = razorEnabled;
        this.razorKey = razorKey;
    }
}
