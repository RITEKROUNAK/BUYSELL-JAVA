package com.panaceasoft.psbuyandsell.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.panaceasoft.psbuyandsell.viewobject.OfflinePayment;
import com.panaceasoft.psbuyandsell.viewobject.OfflinePaymentMethodHeader;
import java.util.List;

@Dao
public abstract class OfflinePaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public  abstract void insertOfflinePaymentHeader (OfflinePaymentMethodHeader offlinePaymentMethodHeader);

    @Query("SELECT * FROM OfflinePaymentMethodHeader")
    public  abstract LiveData<OfflinePaymentMethodHeader> getAll();

    @Query("SELECT * FROM OfflinePaymentMethodHeader LIMIT 1")
    public abstract OfflinePaymentMethodHeader getOfflinePaymentMethodHeader();

    @Query("SELECT * FROM OfflinePayment")
    public abstract List<OfflinePayment> getAllOfflinePaymentList();

    @Query("DELETE FROM OfflinePaymentMethodHeader")
    public  abstract  void  deleteAllOfflinePaymentMethodHeader();

    public OfflinePaymentMethodHeader getOfflinePaymentMethodHeaderAndPaymentList(){
        OfflinePaymentMethodHeader offlinePaymentMethodHeader = getOfflinePaymentMethodHeader();

        if(offlinePaymentMethodHeader != null ) {
            offlinePaymentMethodHeader.offlinePayment = getAllOfflinePaymentList();
        }
        return  offlinePaymentMethodHeader;
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public  abstract void insert(OfflinePayment offlinePayment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public  abstract void insertAllOfflinePayment(List<OfflinePayment> offlinePaymentList);

    @Query("DELETE FROM OfflinePayment")
    public abstract void deleteAllOfflinePayment();





}
