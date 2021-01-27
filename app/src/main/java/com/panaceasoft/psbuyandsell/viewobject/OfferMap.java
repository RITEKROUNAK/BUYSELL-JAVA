package com.panaceasoft.psbuyandsell.viewobject;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = "id")
public class OfferMap {

    @NonNull
    public final String id;

    public final String mapKey;

    public final String chatHistoryId;

    public final int sorting;

    public final String addedDate;

    public OfferMap(@NonNull String id, String mapKey, String chatHistoryId, int sorting, String addedDate) {

        this.id = id;
        this.mapKey = mapKey;
        this.chatHistoryId= chatHistoryId;
        this.sorting = sorting;
        this.addedDate = addedDate;
    }

}
