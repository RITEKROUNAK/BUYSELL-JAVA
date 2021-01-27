package com.panaceasoft.psbuyandsell.viewobject;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "id")
public class OfflinePayment {

    @NonNull
    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

    @SerializedName("status")
    public String status;

    @SerializedName("added_date")
    public String added_date;

    @SerializedName("added_user_id")
    public String added_user_id;

    @SerializedName("updated_date")
    public String updated_date;

    @SerializedName("updated_user_id")
    public String updated_user_id;

    @Embedded(prefix = "default_icon_")
    @SerializedName("default_icon")
    public Image defaultIcon;

    public OfflinePayment(@NonNull String id, String title,String description,String status,String added_date,String added_user_id,
                          String updated_date,String updated_user_id,Image defaultIcon){

        this.id = id;
        this.title = title;
        this.description = description;
        this.status =status;
        this.added_date = added_date;
        this.added_user_id = added_user_id;
        this.updated_date = updated_date;
        this.updated_user_id = updated_user_id;
        this.defaultIcon = defaultIcon;
    }


}
