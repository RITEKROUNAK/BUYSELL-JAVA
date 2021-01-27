package com.panaceasoft.psbuyandsell.viewobject;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "id")
public class ItemType {

    @NonNull
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public final String name;

    @SerializedName("status")
    public final String status;

    @SerializedName("added_date")
    public final String addedDate;

    @SerializedName("is_empty_object")
    public final String is_empty_object;

    public ItemType(@NonNull String id, String name, String status, String addedDate, String is_empty_object) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.addedDate = addedDate;
        this.is_empty_object = is_empty_object;
    }
}
