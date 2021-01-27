package com.panaceasoft.psbuyandsell.repository.blockuser;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.panaceasoft.psbuyandsell.AppExecutors;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.api.ApiResponse;
import com.panaceasoft.psbuyandsell.api.PSApiService;
import com.panaceasoft.psbuyandsell.db.BlockUserDao;
import com.panaceasoft.psbuyandsell.db.PSCoreDb;
import com.panaceasoft.psbuyandsell.repository.common.NetworkBoundResource;
import com.panaceasoft.psbuyandsell.repository.common.PSRepository;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewobject.ApiStatus;
import com.panaceasoft.psbuyandsell.viewobject.BlockUser;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class BlockUserRepository extends PSRepository {

    private final BlockUserDao blockUserDao;

    @Inject
    BlockUserRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, BlockUserDao blockUserDao) {
        super(psApiService, appExecutors, db);
        this.blockUserDao = blockUserDao;
    }

    public LiveData<Resource<List<BlockUser>>> getBlockUserList(String limit, String offset, String loginUserId) {

        return new NetworkBoundResource<List<BlockUser>, List<BlockUser>>(appExecutors) {



            @Override
            protected void saveCallResult(@NonNull List<BlockUser> item) {
                Utils.psLog("SaveCallResult of related products.");

                try {
                    db.runInTransaction(() -> {
                        db.blockUserDao().deleteBlockUser();

                        db.blockUserDao().insertALL(item);




                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<BlockUser> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<BlockUser>> loadFromDb() {
                return db.blockUserDao().getAllBlockUserListData(limit);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<BlockUser>>> createCall() {
                Utils.psLog("Call API Service to get related.");
                return psApiService.getBlockUser(Config.API_KEY,limit, offset, Utils.checkUserId(loginUserId));
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getRelated) : " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.blockUserDao().deleteBlockUser()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }


    public LiveData<Resource<Boolean>> unblockUser(String loginUserId,String userId){
        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            Response<ApiStatus> response;

            try {
                response = psApiService.UnBlockuser(Config.API_KEY, Utils.checkUserId(loginUserId),userId).execute();

                if (response.isSuccessful()) {
                    appExecutors.diskIO().execute(() -> {

                        try {
                            db.runInTransaction(() -> {

                                db.blockUserDao().deleteUserData(userId);
                                db.blockUserDao().getUserData(userId);


                            });

                        } catch (NullPointerException ne) {
                            Utils.psErrorLog("Null Pointer Exception : ", ne);
                        } catch (Exception e) {
                            Utils.psErrorLog("Exception : ", e);
                        }

                        statusLiveData.postValue(Resource.success(true));


                    });

                } else {
                    statusLiveData.postValue(Resource.error("error", false));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return statusLiveData;
    }

    public LiveData<Resource<Boolean>> getNextPageBlockUserList(String limit, String offset, String loginUserId) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<List<BlockUser>>> apiResponse = psApiService.getBlockUser(Config.API_KEY, limit, offset, Utils.checkUserId(loginUserId));

        statusLiveData.addSource(apiResponse, new Observer<ApiResponse<List<BlockUser>>>() {
            @Override
            public void onChanged(ApiResponse<List<BlockUser>> response) {

                statusLiveData.removeSource(apiResponse);

                //noinspection Constant Conditions
                if (response.isSuccessful()) {

                    appExecutors.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                db.runInTransaction(() -> {
                                    db.blockUserDao().insertALL(response.body);
                                });
                            } catch (Exception ex) {
                                Utils.psErrorLog("Error at ", ex);
                            }

                            statusLiveData.postValue(Resource.success(true));
                        }
                    });

                } else {
                    statusLiveData.postValue(Resource.error(response.errorMessage, null));
                }
            }
        });

        return statusLiveData;
    }
}



