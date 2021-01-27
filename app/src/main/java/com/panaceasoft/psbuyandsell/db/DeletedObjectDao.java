package com.panaceasoft.psbuyandsell.db;

import com.panaceasoft.psbuyandsell.viewobject.DeletedObject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DeletedObjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DeletedObject deletedObject);

    @Query("DELETE FROM DeletedObject")
    void deleteAll();
}
