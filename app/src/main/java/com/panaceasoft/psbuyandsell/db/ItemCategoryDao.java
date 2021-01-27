package com.panaceasoft.psbuyandsell.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.panaceasoft.psbuyandsell.viewobject.ItemCategory;

import java.util.List;

@Dao
public interface ItemCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemCategory itemCategory);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(ItemCategory itemCategory);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ItemCategory> cityCategories);

    @Query("DELETE FROM ItemCategory")
    void deleteAllCityCategory();

    @Query("DELETE FROM ItemCategory WHERE id = :id")
    void deleteCityCategoryById(String id);

    @Query("SELECT max(sorting) from ItemCategory ")
    int getMaxSortingByValue();

    @Query("SELECT * FROM ItemCategory  ORDER BY sorting")
    LiveData<List<ItemCategory>> getAllCityCategoryById();

    @Query("DELETE FROM ItemCategory WHERE id =:id")
    public abstract void deleteItemCategoryById(String id);



}
