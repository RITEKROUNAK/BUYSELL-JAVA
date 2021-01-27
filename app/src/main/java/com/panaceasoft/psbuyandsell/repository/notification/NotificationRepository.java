package com.panaceasoft.psbuyandsell.repository.notification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.panaceasoft.psbuyandsell.AppExecutors;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.api.ApiResponse;
import com.panaceasoft.psbuyandsell.api.PSApiService;
import com.panaceasoft.psbuyandsell.db.NotificationDao;
import com.panaceasoft.psbuyandsell.db.PSCoreDb;
import com.panaceasoft.psbuyandsell.repository.common.NetworkBoundResource;
import com.panaceasoft.psbuyandsell.repository.common.PSRepository;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewobject.ApiStatus;
import com.panaceasoft.psbuyandsell.viewobject.ChatHistory;
import com.panaceasoft.psbuyandsell.viewobject.Noti;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class NotificationRepository extends PSRepository {

    //region variable
    private final NotificationDao notificationDao;
    //end region

    //region constructor
    @Inject
    NotificationRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, NotificationDao notificationDao) {
        super(psApiService, appExecutors, db);
        this.notificationDao = notificationDao;
    }
    //end region

    //Get notification list
    public LiveData<Resource<List<Noti>>> getNotificationList(String apiKey, String userId, String limit, String offset, String deviceToken) {

        return new NetworkBoundResource<List<Noti>, List<Noti>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Noti> itemList) {
                Utils.psLog("SaveCallResult of getNotificationList.");

//                db.beginTransaction();
//
//                try {
//
//                    notificationDao.deleteAllNotificationList();
//
//                    notificationDao.insertAllNotificationList(itemList);
//
//                    db.setTransactionSuccessful();
//
//                } catch (Exception e) {
//                    Utils.psErrorLog("Error in doing transaction of recent notification list.", e);
//                } finally {
//                    db.endTransaction();
//                }

                try {
                    db.runInTransaction(() -> {
                        notificationDao.deleteAllNotificationList();

                        notificationDao.insertAllNotificationList(itemList);
                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Noti> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<Noti>> loadFromDb() {
                Utils.psLog("Load Recent notification From Db");
                return notificationDao.getAllNotificationList();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Noti>>> createCall() {
                return psApiService.getNotificationList(apiKey,
                        limit,
                        offset,
                        userId,
                        deviceToken);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getRecentNotificationList) : " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.notificationDao().deleteAllNotificationList()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }
        }.asLiveData();
    }


    public LiveData<Resource<Boolean>> getNextPageNotificationList(String userId, String deviceToken, String limit, String offset) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();
        LiveData<ApiResponse<List<Noti>>> apiResponse = psApiService.getNotificationList(Config.API_KEY, limit, offset, userId, deviceToken);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {

                    try {
                        db.runInTransaction(() -> {
                            if (response.body != null) {
                                db.notificationDao().insertAllNotificationList(response.body);
                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.success(true));

                });
            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, false));
            }

        });

        return statusLiveData;

    }


    //Get Product detail
    public LiveData<Resource<Noti>> getNotificationDetail(String apiKey, String notificationId) {

        return new NetworkBoundResource<Noti, Noti>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull Noti itemList) {
                Utils.psLog("SaveCallResult of recent products.");

//                db.beginTransaction();
//
//                try {
//
//                    notificationDao.deleteNotificationById(notificationId);
//
//                    notificationDao.insert(itemList);
//
//                    db.setTransactionSuccessful();
//
//                } catch (Exception e) {
//                    Utils.psErrorLog("Error in doing transaction of discount list.", e);
//                } finally {
//                    db.endTransaction();
//                }

                try {
                    db.runInTransaction(() -> {
                        notificationDao.deleteNotificationById(notificationId);

                        notificationDao.insert(itemList);
                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Noti data) {

                // Recent news always load from server
                return connectivity.isConnected();

            }

            @NonNull
            @Override
            protected LiveData<Noti> loadFromDb() {
                Utils.psLog("Load discount From Db");

                return notificationDao.getNotificationById(notificationId);

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Noti>> createCall() {
                Utils.psLog("Call API Service to get discount.");

                return psApiService.getNotificationDetail(apiKey, notificationId);

            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getDiscount) : " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.notificationDao().deleteNotificationById(notificationId)));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }

    //noti read post
    public LiveData<Resource<Boolean>> uploadNotiPostToServer(String noti_id, String userId, String device_token) {

        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            try {
                // Call the API Service
                Response<Noti> response;

                response = psApiService
                        .isReadNoti(Config.API_KEY, noti_id, userId, device_token).execute();

                // Wrap with APIResponse Class
                ApiResponse<Noti> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

//                    try {
//                        db.beginTransaction();
//
//                        if (apiResponse.body != null) {
//
//                            db.notificationDao().insert(response.body());
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

                    try {
                        db.runInTransaction(() -> {
                            if (apiResponse.body != null) {
                                db.notificationDao().insert(response.body());
                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.success(apiResponse.getNextPage() != null));

                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, false));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), false));
            }
        });

        return statusLiveData;
    }
    //endregion

    public LiveData<Resource<Boolean>> sendChatNoti(String itemId, String senderId, String receiverId, String message, String type) {
        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            Response<ApiStatus> response;

            try {
                response = psApiService.sendNotiForChat(Config.API_KEY, itemId, senderId, receiverId, message, type).execute();

                if (response.isSuccessful()) {
                    statusLiveData.postValue(Resource.success(true));
                } else {
                    statusLiveData.postValue(Resource.error("error", false));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return statusLiveData;
    }

    public LiveData<Resource<Boolean>> resetUnreadCount(String itemId, String buyerUserId, String sellerUserId, String type) {
        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            Response<ChatHistory> response;

            try {
                response = psApiService.resetUnreadCount(Config.API_KEY, itemId, buyerUserId, sellerUserId, type).execute();

                db.runInTransaction(() -> {

                    try {

                        if (response.body() != null) {

                            db.chatHistoryDao().insert(response.body());

                        }

                    } catch (Exception e) {
                        Utils.psErrorLog("Error in doing transaction of syncChatHistory.", e);
                    }
                });

                if (response.isSuccessful()) {
                    statusLiveData.postValue(Resource.success(true));
                } else {
                    statusLiveData.postValue(Resource.error("error", false));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return statusLiveData;
    }


}