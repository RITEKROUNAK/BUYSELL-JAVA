package com.panaceasoft.psbuyandsell.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.panaceasoft.psbuyandsell.viewobject.ItemMap;

import java.util.List;

@Dao
public interface ItemMapDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemMap itemMap);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<ItemMap> itemMap);

    @Query("DELETE FROM ItemMap WHERE mapKey = :key")
    void deleteByMapKey(String key);

    @Query("DELETE FROM ItemMap WHERE mapKey = :key AND itemId = :itemId")
    void deleteByMapKeyAndItemId(String key, String itemId);

    @Query("DELETE FROM ItemMap WHERE mapKey = :key AND addedUserId = :addedUserId")
    void deleteByMapKeyAndUserId(String key, String addedUserId);

    @Query("SELECT max(sorting) from ItemMap WHERE mapKey = :value ")
    int getMaxSortingByValue(String value);

    @Query("SELECT * FROM ItemMap")
    List<ItemMap> getAll();

    @Query("SELECT * FROM ItemMap WHERE itemId = :id")
    List<ItemMap> getItemListByItemId(String id);

    @Query("DELETE FROM ItemMap")
    void deleteAll();
}
