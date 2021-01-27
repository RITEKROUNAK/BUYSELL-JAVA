package com.panaceasoft.psbuyandsell.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.panaceasoft.psbuyandsell.viewobject.Offer;
import com.panaceasoft.psbuyandsell.viewobject.OfferMap;

import java.util.List;

@Dao
public interface OfferDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Offer offer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Offer> offerList);

    @Query("DELETE FROM Offer")
    void deleteAll();

    @Query("DELETE FROM Offer WHERE id = :id")
    void deleteItemById(String id);

//    @Query("SELECT * FROM Offer ch WHERE ch.id = :id LIMIT 1")
//    LiveData<Offer> getOfferListById(String id);

//    @Query("SELECT * FROM Offer ch WHERE ch.item_id = :itemId AND ch.buyer_userId = :buyerUserId AND ch.seller_userId = :sellerUserId LIMIT 1")
//    LiveData<Offer> getOfferList(String itemId, String buyerUserId, String sellerUserId);

    @Query("SELECT ch.* FROM Offer ch, OfferMap chm WHERE ch.id = chm.chatHistoryId AND chm.mapKey = :value ORDER BY chm.sorting asc")
    LiveData<List<Offer>> getOfferListByKey (String value);

//    @Query("DELETE FROM Offer WHERE item_id = :itemId AND buyer_userId = :buyerUserId AND seller_userId = :sellerUserId")
//    void deleteOfferList(String itemId, String buyerUserId, String sellerUserId);

// OfferList Map

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OfferMap offerMap);

    @Query("DELETE FROM OfferMap WHERE mapKey = :key")
    void deleteByMapKey(String key);

    @Query("SELECT max(sorting) from OfferMap WHERE mapKey = :value")
    int getMaxSortingByValue(String value);

    @Query("SELECT * FROM OfferMap")
    List<OfferMap> getAll();

//    @Query("DELETE FROM OfferMap")
//    void deleteAllOfferListMap();
}