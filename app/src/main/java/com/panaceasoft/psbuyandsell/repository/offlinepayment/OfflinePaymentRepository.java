package com.panaceasoft.psbuyandsell.repository.offlinepayment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.panaceasoft.psbuyandsell.AppExecutors;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.api.ApiResponse;
import com.panaceasoft.psbuyandsell.api.PSApiService;
import com.panaceasoft.psbuyandsell.db.OfflinePaymentDao;
import com.panaceasoft.psbuyandsell.db.PSCoreDb;
import com.panaceasoft.psbuyandsell.repository.common.NetworkBoundResource;
import com.panaceasoft.psbuyandsell.repository.common.PSRepository;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewobject.OfflinePaymentMethodHeader;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import javax.inject.Inject;

public class OfflinePaymentRepository extends PSRepository {

    private OfflinePaymentDao offlinePaymentDao;

    @Inject
    OfflinePaymentRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, OfflinePaymentDao offlinePaymentDao) {
        super(psApiService, appExecutors, db);

        this.offlinePaymentDao = offlinePaymentDao;
    }


    public LiveData<Resource<OfflinePaymentMethodHeader>> getOfflinePaymentHeaderList(String apiKey, String limit, String offset) {
        return new NetworkBoundResource<OfflinePaymentMethodHeader, OfflinePaymentMethodHeader>(appExecutors) {


            @Override
            protected void saveCallResult(@NonNull OfflinePaymentMethodHeader offlinePaymentMethodHeader) {
                db.beginTransaction();

                try {

                    offlinePaymentMethodHeader.id = "1";

                    offlinePaymentDao.deleteAllOfflinePaymentMethodHeader();

                    offlinePaymentDao.insertOfflinePaymentHeader(offlinePaymentMethodHeader);

                    offlinePaymentDao.deleteAllOfflinePayment();

                    if (offlinePaymentMethodHeader.offlinePayment != null) {

                        offlinePaymentDao.insertAllOfflinePayment(offlinePaymentMethodHeader.offlinePayment);

                    }

                    db.setTransactionSuccessful();

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of getOfflinePaymentHeaderList.", e);

                } finally {
                    db.endTransaction();
                }
            }


            @Override
            protected boolean shouldFetch(@Nullable OfflinePaymentMethodHeader data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<OfflinePaymentMethodHeader> loadFromDb() {
                MutableLiveData<OfflinePaymentMethodHeader> offlinePaymentMethodHeaderMutableLiveData = new MutableLiveData<>();
                appExecutors.diskIO().execute(() -> {
                    OfflinePaymentMethodHeader offlinePaymentMethodHeader = offlinePaymentDao.getOfflinePaymentMethodHeaderAndPaymentList();
                    appExecutors.mainThread().execute(() -> offlinePaymentMethodHeaderMutableLiveData.setValue(offlinePaymentMethodHeader) );
                });
                return offlinePaymentMethodHeaderMutableLiveData;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<OfflinePaymentMethodHeader>> createCall() {
                return psApiService.getOfflinePayment(apiKey, limit, offset);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of About Us");

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.itemPriceTypeDao().deleteAllItemPriceType()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }


        }.asLiveData();
    }

//
//    public LiveData<Resource<Boolean>> getNextPageOfflinePaymentMethodList(String limit, String offset) {
//
//        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();
//
//        LiveData<ApiResponse<OfflinePaymentMethodHeader>> apiResponse = psApiService.getOfflinePayment(Config.API_KEY, limit, offset);
//
//        statusLiveData.addSource(apiResponse, response -> {
//
//            statusLiveData.removeSource(apiResponse);
//
//            //noinspection Constant Conditions
//            if (response.isSuccessful()) {
//
//                appExecutors.diskIO().execute(() -> {
//
//                    try {
//                        db.beginTransaction();
//
//                        if (response.body != null) {
//
//                            offlinePaymentDao.deleteAll();
////                            offlinePaymentDao.insert(response.body);
//
//
////                            for (OfflinePaymentMethodHeader header : response.body.offlinePayment) {
////
////                                offlinePaymentDao.deleteAllBasedOnOfflinePaymentId(header.id);
////                                if (header.offlinePayment != null) {
////                                    for (OfflinePayment offline : header.offlinePayment) {
////                                        offlinePaymentDao.insert(new OfflinePaymentMethod(header.id, offline.id));
////                                    }
////                                }
////                            }
//
//                        }
//
//                        db.setTransactionSuccessful();
//                    } catch (NullPointerException ne) {
//                        Utils.psErrorLog("Null Pointer Exception : ", ne);
//                    } catch (Exception e) {
//                        Utils.psErrorLog("Exception : ", e);
//                    } finally {
//                        db.endTransaction();
//                    }
//
//
//                    statusLiveData.postValue(Resource.success(true));
//                });
//
//            } else {
//                statusLiveData.postValue(Resource.error(response.errorMessage, null));
//            }
//        });
//
//        return statusLiveData;
//    }


}
