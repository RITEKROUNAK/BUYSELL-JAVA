package com.panaceasoft.psbuyandsell.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.panaceasoft.psbuyandsell.viewobject.BlockUser;
import com.panaceasoft.psbuyandsell.viewobject.User;


import java.util.List;

@Dao
public abstract class BlockUserDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(BlockUser blockUser);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertALL(List<BlockUser> blockUser);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(BlockUser blockUser);



    @Query("SELECT * FROM BlockUser ORDER BY addedDate desc limit :limit")
    public abstract LiveData<List<BlockUser>> getAllBlockUserListData(String limit);

    @Query("SELECT * FROM BlockUser WHERE userId = :userId  ORDER BY addedDate desc")
    public abstract   LiveData<List<BlockUser>>  getUserData(String userId);

    @Query("DELETE FROM BlockUser WHERE userId = :userId")
    public abstract void  deleteUserData(String userId);

    @Query("DELETE FROM BlockUser")
    public abstract void deleteBlockUser();
}
