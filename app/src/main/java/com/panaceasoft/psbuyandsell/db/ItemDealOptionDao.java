package com.panaceasoft.psbuyandsell.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.panaceasoft.psbuyandsell.viewobject.ItemDealOption;

import java.util.List;

@Dao
public interface ItemDealOptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemDealOption itemDealOption);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(ItemDealOption itemDealOption);

    @Query("DELETE FROM ItemDealOption")
    void deleteAllItemDealOption();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ItemDealOption> itemDealOptionList);

    @Query("SELECT * FROM ItemDealOption ORDER BY addedDate DESC")
    LiveData<List<ItemDealOption>> getAllItemDealOption();

//    @Query("SELECT * FROM ItemDealOption WHERE catId=:catId")
//    LiveData<List<ItemDealOption>> getSubCategoryList(String catId);
}
