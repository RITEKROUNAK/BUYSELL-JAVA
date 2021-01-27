package com.panaceasoft.psbuyandsell.repository.itempaidhistory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.panaceasoft.psbuyandsell.AppExecutors;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.api.ApiResponse;
import com.panaceasoft.psbuyandsell.api.PSApiService;
import com.panaceasoft.psbuyandsell.db.ItemPaidHistoryDao;
import com.panaceasoft.psbuyandsell.db.PSCoreDb;
import com.panaceasoft.psbuyandsell.repository.common.NetworkBoundResource;
import com.panaceasoft.psbuyandsell.repository.common.PSRepository;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewobject.ItemPaidHistory;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class ItemPaidHistoryRepository extends PSRepository {

    private final ItemPaidHistoryDao itemPaidHistoryDao;


    @Inject
    protected ItemPaidHistoryRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, ItemPaidHistoryDao itemPaidHistoryDao) {
        super(psApiService, appExecutors, db);
        this.itemPaidHistoryDao = itemPaidHistoryDao;
    }

    //region paid ad upload
    public LiveData<Resource<ItemPaidHistory>> uploadItemPaidToServer(String itemId, String amount, String startDate, String howManyDay, String paymentMethod, String paymentMethodNonce, String startTimeStamp,String razorId) {

        final MutableLiveData<Resource<ItemPaidHistory>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            Response<ItemPaidHistory> response;

            try {
                response = psApiService.uploadItemPaidHistory(Config.API_KEY,itemId,amount,startDate,howManyDay,paymentMethod,paymentMethodNonce,startTimeStamp,razorId).execute();

                ApiResponse<ItemPaidHistory> apiResponse = new ApiResponse<>(response);

                if (apiResponse.isSuccessful()) {
                    statusLiveData.postValue(Resource.success(apiResponse.body));
                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), null));
            }

        });

        return statusLiveData;

    }

    //region Get paid ad

    public LiveData<Resource<List<ItemPaidHistory>>> getItemPaidHistoryList(String loginUserId, String limit, String offset) {

        return new NetworkBoundResource<List<ItemPaidHistory>, List<ItemPaidHistory>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<ItemPaidHistory> paidListList) {
                Utils.psLog("SaveCallResult of related products.");

                try {
                    db.runInTransaction(() -> {
                        itemPaidHistoryDao.deleteAll();
                        itemPaidHistoryDao.insertAll(paidListList);
                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ItemPaidHistory> data) {

                // Recent news always load from server
                return connectivity.isConnected();

            }

            @NonNull
            @Override
            protected LiveData<List<ItemPaidHistory>> loadFromDb() {
                Utils.psLog("Load related From Db");
                if(limit.equals(String.valueOf(Config.PAID_ITEM_COUNT))){
                    return db.itemPaidHistoryDao().getAllItemPaidByLimit(limit,loginUserId);
                }
                return db.itemPaidHistoryDao().getAllItemPaid(loginUserId);

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ItemPaidHistory>>> createCall() {
                Utils.psLog("Call API Service to get related.");

                return psApiService.getPaidAd(Config.API_KEY, Utils.checkUserId(loginUserId), limit, offset);


            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getRelated) : " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.itemPaidHistoryDao().deleteAll()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }


    //Next page for item paid history

    public LiveData<Resource<Boolean>> getNextPageItemPaidHistoryList(String loginUserId, String limit, String offset) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<List<ItemPaidHistory>>> apiResponse = psApiService.getPaidAd(Config.API_KEY,loginUserId, limit, offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {

                    try {
                        db.runInTransaction(() -> {
                            db.itemPaidHistoryDao().insertAll(response.body);
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.success(true));
                });

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }
        });

        return statusLiveData;

    }

}
