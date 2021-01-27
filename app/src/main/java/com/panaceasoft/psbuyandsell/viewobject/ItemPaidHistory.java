package com.panaceasoft.psbuyandsell.viewobject;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "id")
public class ItemPaidHistory {

    @NonNull
    @SerializedName("id")
    public final String id;

    @SerializedName("item_id")
    public  String itemId;

    @SerializedName("start_date")
    public String startDate;

    @SerializedName("end_date")
    public String endDate;

    @SerializedName("amount")
    public String amount;

    @SerializedName("payment_method")
    public String paymentMethod;

    @SerializedName("trans_code")
    public String transCode;

    @SerializedName("status")
    public String status;

    @SerializedName("added_date")
    public String addedDate;

    @SerializedName("added_user_id")
    public String addedUserId;

    @SerializedName("updated_date")
    public String updatedDate;

    @SerializedName("updated_user_id")
    public String updatedUserId;

    @SerializedName("updated_flag")
    public String updatedFlag;

    @SerializedName("added_date_str")
    public String addedDateStreet;

    @SerializedName("paid_status")
    public String paidStatus;

    @SerializedName("item")
    @Embedded(prefix = "item_")
    public final Item item;

    public ItemPaidHistory(@NonNull String id, String itemId, String startDate, String endDate, String amount,
                           String paymentMethod, String transCode, String status, String addedDate, String addedUserId,
                           String updatedDate, String updatedUserId, String updatedFlag, String addedDateStreet, String paidStatus, Item item) {
        this.id = id;
        this.itemId = itemId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transCode = transCode;
        this.addedDate = addedDate;
        this.addedUserId = addedUserId;
        this.updatedDate = updatedDate;
        this.updatedUserId = updatedUserId;
        this.updatedFlag = updatedFlag;
        this.paidStatus = paidStatus;
        this.item = item;
        this.status = status;
        this.addedDateStreet = addedDateStreet;
    }
}
