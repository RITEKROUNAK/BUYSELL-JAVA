package com.panaceasoft.psbuyandsell.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.panaceasoft.psbuyandsell.viewobject.UserMap;

import java.util.List;

@Dao
public interface UserMapDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserMap itemMap);

    @Query("DELETE FROM UserMap WHERE mapKey = :key")
    void deleteUsersByMapKey(String key);

    @Query("DELETE FROM UserMap WHERE mapKey = :key AND userId = :userId")
    void deleteByMapKeyAndUserId(String key, String userId);

    @Query("SELECT max(sorting) from UserMap WHERE mapKey = :value ")
    int getMaxSortingByValue(String value);

    @Query("SELECT * FROM UserMap")
    List<UserMap> getAll();

    @Query("DELETE FROM UserMap")
    void deleteAll();

}
