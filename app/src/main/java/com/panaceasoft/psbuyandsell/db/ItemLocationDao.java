package com.panaceasoft.psbuyandsell.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.panaceasoft.psbuyandsell.viewobject.ItemLocation;

import java.util.List;

@Dao
public interface ItemLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemLocation itemLocation);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(ItemLocation itemLocation);

    @Query("DELETE FROM ItemLocation")
    void deleteAllItemLocation();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ItemLocation> itemLocationList);

    @Query("SELECT * FROM ItemLocation ")
    LiveData<List<ItemLocation>> getAllItemLocation();

//    @Query("SELECT * FROM ItemLocation WHERE catId=:catId")
//    LiveData<List<ItemLocation>> getSubCategoryList(String catId);
}
