package com.panaceasoft.psbuyandsell.viewobject;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "id")
public class PSUserInfo {

    @NonNull
    public String id;

    @SerializedName("user_status")
    public final String userStatus;


    public PSUserInfo(@NonNull String id, String userStatus) {
        this.id = id;
        this.userStatus = userStatus;

    }
}
