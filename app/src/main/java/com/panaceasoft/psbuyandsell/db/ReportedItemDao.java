package com.panaceasoft.psbuyandsell.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.panaceasoft.psbuyandsell.viewobject.ReportedItem;

import java.util.List;

@Dao
public abstract class ReportedItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(ReportedItem reportedItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertALL(List<ReportedItem> reportedItem);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(ReportedItem reportedItem);


    @Query("SELECT * FROM ReportedItem ORDER BY addedDate desc limit :limit")
    public abstract LiveData<List<ReportedItem>> getAllReportedItemListData(String limit);


    @Query("DELETE FROM ReportedItem WHERE id =:id")
    public abstract void deleteReportedItemById(String id);

    @Query("DELETE FROM ReportedItem")
    public abstract void deleteReportedItem();
}
