package com.panaceasoft.psbuyandsell.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.panaceasoft.psbuyandsell.viewobject.ItemSubCategory;

import java.util.List;

@Dao
public interface ItemSubCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemSubCategory subCategory);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(ItemSubCategory subCategory);

    @Query("DELETE FROM ItemSubCategory")
    void deleteAllSubCategory();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ItemSubCategory> subCategories);

    @Query("SELECT * FROM ItemSubCategory ORDER BY addedDate DESC")
    LiveData<List<ItemSubCategory>> getAllSubCategory();

    @Query("SELECT * FROM ItemSubCategory WHERE catId=:catId")
    LiveData<List<ItemSubCategory>> getSubCategoryList(String catId);

}
