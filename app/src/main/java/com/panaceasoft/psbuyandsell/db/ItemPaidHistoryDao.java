package com.panaceasoft.psbuyandsell.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.panaceasoft.psbuyandsell.viewobject.ItemPaidHistory;

import java.util.List;

@Dao
public interface ItemPaidHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ItemPaidHistory> paidHistory);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemPaidHistory itemPaidHistory);

    @Query("SELECT * FROM ItemPaidHistory WHERE id = :id")
    LiveData<ItemPaidHistory> getItemPaidById(String id);

    @Query("SELECT * FROM ItemPaidHistory WHERE item_user_userId = :userId ORDER BY addedDate desc")
    LiveData<List<ItemPaidHistory>> getAllItemPaid(String userId);

    @Query("SELECT * FROM ItemPaidHistory WHERE item_user_userId = :userId ORDER BY addedDate desc limit :limit")
    LiveData<List<ItemPaidHistory>> getAllItemPaidByLimit(String limit, String userId);

    @Query("DELETE FROM ItemPaidHistory")
    void deleteAll();
}
