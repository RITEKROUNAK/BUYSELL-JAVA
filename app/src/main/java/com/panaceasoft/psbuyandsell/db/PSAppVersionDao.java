package com.panaceasoft.psbuyandsell.db;

import com.panaceasoft.psbuyandsell.viewobject.PSAppVersion;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface PSAppVersionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PSAppVersion psAppVersion);

    @Query("DELETE FROM PSAppVersion")
    void deleteAll();
}
