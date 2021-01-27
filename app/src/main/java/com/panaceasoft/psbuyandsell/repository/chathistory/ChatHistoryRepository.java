package com.panaceasoft.psbuyandsell.repository.chathistory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.panaceasoft.psbuyandsell.AppExecutors;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.api.ApiResponse;
import com.panaceasoft.psbuyandsell.api.PSApiService;
import com.panaceasoft.psbuyandsell.db.ChatHistoryDao;
import com.panaceasoft.psbuyandsell.db.PSCoreDb;
import com.panaceasoft.psbuyandsell.repository.common.NetworkBoundResource;
import com.panaceasoft.psbuyandsell.repository.common.PSRepository;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewobject.ChatHistory;
import com.panaceasoft.psbuyandsell.viewobject.ChatHistoryMap;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.holder.ChatHistoryParameterHolder;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.MapKey;

@Singleton
public class ChatHistoryRepository extends PSRepository {
    private ChatHistoryDao chatHistoryDao;

    @Inject
    ChatHistoryRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, ChatHistoryDao chatHistoryDao) {
        super(psApiService, appExecutors, db);

        Utils.psLog("Inside ChatHistoryRepository");

        this.chatHistoryDao = chatHistoryDao;
    }

    public LiveData<Resource<ChatHistory>> getChatHistory(String itemId, String buyerUserId, String sellerUserId) {
        return new NetworkBoundResource<ChatHistory, ChatHistory>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull ChatHistory chatHistory) {

                Utils.psLog("SaveCallResult of getChatHistoryList.");


                db.runInTransaction(() -> {

                    try {

                        db.chatHistoryDao().deleteChatHistory(itemId, buyerUserId, sellerUserId);

                        chatHistoryDao.insert(chatHistory);

                    } catch (Exception e) {
                        Utils.psErrorLog("Error in doing transaction of getChatHistory.", e);
                    }
                });

            }

            @Override
            protected boolean shouldFetch(@Nullable ChatHistory data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<ChatHistory> loadFromDb() {
                Utils.psLog("Load ChatHistory From Db");
                return chatHistoryDao.getChatHistory(itemId, buyerUserId, sellerUserId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ChatHistory>> createCall() {
                return psApiService.getChatHistory(Config.API_KEY, itemId, buyerUserId, sellerUserId);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of " + code);
                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.chatHistoryDao().deleteChatHistory(itemId, buyerUserId, sellerUserId)));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }

    public LiveData<Resource<List<ChatHistory>>> getChatHistoryList(String userId, ChatHistoryParameterHolder chatHistoryParameterHolder, String limit, String offset) {
        return new NetworkBoundResource<List<ChatHistory>, List<ChatHistory>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<ChatHistory> chatHistoryList) {

                Utils.psLog("SaveCallResult of getChatHistoryList.");


                db.runInTransaction(() -> {

                    try {

                        String mapKey = chatHistoryParameterHolder.getMapKey();

                        chatHistoryDao.deleteByMapKey(mapKey);

                        chatHistoryDao.insertAll(chatHistoryList);

                        String dateTime = Utils.getDateTime();

                        for (int i = 0; i < chatHistoryList.size(); i++) {
                            chatHistoryDao.insert(new ChatHistoryMap(mapKey + chatHistoryList.get(i).id, mapKey, chatHistoryList.get(i).id, i + 1, dateTime));
                        }

                    } catch (Exception e) {
                        Utils.psErrorLog("Error in doing transaction of getChatHistoryList.", e);
                    }
                });

            }

            @Override
            protected boolean shouldFetch(@Nullable List<ChatHistory> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<ChatHistory>> loadFromDb() {
                Utils.psLog("Load ChatHistory From Db");
                String mapKey = chatHistoryParameterHolder.getMapKey();
                return chatHistoryDao.getChatHistoryByKey(mapKey);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ChatHistory>>> createCall() {
                return psApiService.getChatHistoryList(Config.API_KEY, userId, chatHistoryParameterHolder.returnType, limit, offset);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> {
                            String mapKey = chatHistoryParameterHolder.getMapKey();

                            chatHistoryDao.deleteByMapKey(mapKey);
                        }));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> getNextPageChatHistoryList(String userId, ChatHistoryParameterHolder chatHistoryParameterHolder, String limit, String offset) {
        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();
        LiveData<ApiResponse<List<ChatHistory>>> apiResponse = psApiService.getChatHistoryList(Config.API_KEY, userId, chatHistoryParameterHolder.returnType, limit, offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful() && response.body != null) {

                appExecutors.diskIO().execute(() -> db.runInTransaction(() -> {

                    try {

                        chatHistoryDao.insertAll(response.body);

                        int finalIndex = db.itemMapDao().getMaxSortingByValue(chatHistoryParameterHolder.getMapKey());

                        int startIndex = finalIndex + 1;

                        String mapKey = chatHistoryParameterHolder.getMapKey();
                        String dateTime = Utils.getDateTime();

                        for (int i = 0; i < response.body.size(); i++) {
                            chatHistoryDao.insert(new ChatHistoryMap(mapKey + response.body.get(i).id, mapKey, response.body.get(i).id, startIndex + i, dateTime));
                        }

                        statusLiveData.postValue(Resource.success(true));

                    } catch (Exception e) {
                        statusLiveData.postValue(Resource.error(response.errorMessage, null));
                        Utils.psErrorLog("Error in doing transaction of getChatHistoryList.", e);
                    }
                }));

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }

        });

        return statusLiveData;
    }

}
