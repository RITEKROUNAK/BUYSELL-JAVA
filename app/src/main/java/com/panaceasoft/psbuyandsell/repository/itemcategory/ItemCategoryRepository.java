package com.panaceasoft.psbuyandsell.repository.itemcategory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.panaceasoft.psbuyandsell.AppExecutors;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.api.ApiResponse;
import com.panaceasoft.psbuyandsell.api.PSApiService;
import com.panaceasoft.psbuyandsell.db.ItemCategoryDao;
import com.panaceasoft.psbuyandsell.db.PSCoreDb;
import com.panaceasoft.psbuyandsell.repository.common.NetworkBoundResource;
import com.panaceasoft.psbuyandsell.repository.common.PSRepository;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewobject.ItemCategory;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Panacea-Soft on 11/25/17.
 * Contact Email : teamps.is.cool@gmail.com
 */

@Singleton
public class ItemCategoryRepository extends PSRepository {


    //region Variables

    private ItemCategoryDao itemCategoryDao;

    //endregion


    //region Constructor

    @Inject
    ItemCategoryRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, ItemCategoryDao itemCategoryDao) {
        super(psApiService, appExecutors, db);

        Utils.psLog("Inside CityCategoryRepository");

        this.itemCategoryDao = itemCategoryDao;
    }

    //endregion


    //region Category Repository Functions for ViewModel

    /**
     * Load Category List
     */

    public LiveData<Resource<List<ItemCategory>>> getAllSearchCityCategory(String limit, String offset) {

        return new NetworkBoundResource<List<ItemCategory>, List<ItemCategory>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<ItemCategory> item) {
                Utils.psLog("SaveCallResult of getAllCategoriesWithUserId");

                try {
                    db.runInTransaction(() -> {
                        db.itemCategoryDao().deleteAllCityCategory();

                        for (int i = 0; i < item.size(); i++) {
                            itemCategoryDao.insert(new ItemCategory(i + 1, item.get(i).id, item.get(i).name, item.get(i).ordering, item.get(i).status, item.get(i).addedDate, item.get(i).updatedDate,
                                    item.get(i).addedUserId, item.get(i).cityId, item.get(i).updatedUserId, item.get(i).updatedFlag, item.get(i).addedDateStr, item.get(i).defaultPhoto, item.get(i).defaultIcon));
                        }
                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }


            @Override
            protected boolean shouldFetch(@Nullable List<ItemCategory> data) {

                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<ItemCategory>> loadFromDb() {

                Utils.psLog("Load From Db (All Categories)");

                return itemCategoryDao.getAllCityCategoryById();

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ItemCategory>>> createCall() {
                Utils.psLog("Call Get All Categories webservice.");

                return psApiService.getCityCategory(Config.API_KEY, limit, offset);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of About Us " + code);
                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.itemCategoryDao().deleteAllCityCategory()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> getNextSearchCityCategory(String limit, String offset) {
        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();
        LiveData<ApiResponse<List<ItemCategory>>> apiResponse = psApiService.getCityCategory(Config.API_KEY, limit, offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {

                    try {
                        db.runInTransaction(() -> {
                            if (response.body != null) {

                                int finalIndex = itemCategoryDao.getMaxSortingByValue();

                                int startIndex = finalIndex + 1;

                                for (int i = 0; i < response.body.size(); i++) {
                                    itemCategoryDao.insert(new ItemCategory(startIndex + i, response.body.get(i).id, response.body.get(i).name, response.body.get(i).ordering, response.body.get(i).status, response.body.get(i).addedDate, response.body.get(i).updatedDate,
                                            response.body.get(i).addedUserId, response.body.get(i).cityId, response.body.get(i).updatedUserId, response.body.get(i).updatedFlag, response.body.get(i).addedDateStr, response.body.get(i).defaultPhoto, response.body.get(i).defaultIcon));
                                }

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
