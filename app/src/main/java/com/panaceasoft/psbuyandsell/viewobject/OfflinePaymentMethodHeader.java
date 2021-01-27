package com.panaceasoft.psbuyandsell.viewobject;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(primaryKeys = "id")
public class OfflinePaymentMethodHeader {

    @NonNull
    public String id;

    @SerializedName("message")
    public final String message;

    @Ignore
    @SerializedName("offline_payment")
    public List<OfflinePayment> offlinePayment;
    
    public OfflinePaymentMethodHeader(@NonNull String id, String message) {
        this.id = id;
        this.message = message;
    }
}
