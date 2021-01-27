package com.panaceasoft.psbuyandsell.repository.itempricetype;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.panaceasoft.psbuyandsell.AppExecutors;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.api.ApiResponse;
import com.panaceasoft.psbuyandsell.api.PSApiService;
import com.panaceasoft.psbuyandsell.db.ItemPriceTypeDao;
import com.panaceasoft.psbuyandsell.db.PSCoreDb;
import com.panaceasoft.psbuyandsell.repository.common.NetworkBoundResource;
import com.panaceasoft.psbuyandsell.repository.common.PSRepository;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewobject.ItemPriceType;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ItemPriceTypeRepository extends PSRepository {
    private ItemPriceTypeDao itemPriceTypeDao;

    @Inject
    ItemPriceTypeRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, ItemPriceTypeDao itemPriceTypeDao) {

        super(psApiService, appExecutors, db);
        this.itemPriceTypeDao = itemPriceTypeDao;
    }

    public LiveData<Resource<List<ItemPriceType>>> getAllItemPriceTypeList(String limit, String offset) {

        return new NetworkBoundResource<List<ItemPriceType>, List<ItemPriceType>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<ItemPriceType> itemTypeList) {
                Utils.psLog("SaveCallResult of getAllCategoriesWithUserId");

                try {
                    db.runInTransaction(() -> {
                        itemPriceTypeDao.deleteAllItemPriceType();

                        itemPriceTypeDao.insertAll(itemTypeList);

                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }


            @Override
            protected boolean shouldFetch(@Nullable List<ItemPriceType> data) {

                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<ItemPriceType>> loadFromDb() {

                Utils.psLog("Load From Db (All Categories)");

                return itemPriceTypeDao.getAllItemPriceType();

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ItemPriceType>>> createCall() {
                Utils.psLog("Call Get All Categories webservice.");

                return psApiService.getItemPriceTypeList(Config.API_KEY, limit, offset);
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

    public LiveData<Resource<Boolean>> getNextPageItemPriceList( String limit, String offset) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();
        LiveData<ApiResponse<List<ItemPriceType>>> apiResponse = psApiService.getItemPriceTypeList(Config.API_KEY, limit, offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {

                    try {
                        db.runInTransaction(() -> {
                            if (apiResponse.getValue() != null) {

                                itemPriceTypeDao.insertAll(apiResponse.getValue().body);
                            }
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