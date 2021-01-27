package com.panaceasoft.psbuyandsell.viewmodel.chathistory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.panaceasoft.psbuyandsell.repository.chathistory.ChatHistoryRepository;
import com.panaceasoft.psbuyandsell.utils.AbsentLiveData;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.common.PSViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ChatHistory;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.holder.ChatHistoryParameterHolder;

import java.util.List;

import javax.inject.Inject;

public class ChatHistoryViewModel extends PSViewModel {

    //region Variables

    private final LiveData<Resource<List<ChatHistory>>> chatHistoryListData;
    private MutableLiveData<ChatHistoryViewModel.TmpDataHolder> chatHistoryListObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageChatHistoryListData;
    private MutableLiveData<TmpDataHolder> nextPageChatHistoryListObj = new MutableLiveData<>();

    private final LiveData<Resource<ChatHistory>> chatHistoryData;
    private MutableLiveData<ChatHistoryTmpHolder> chatHistoryObj = new MutableLiveData<>();

    public ChatHistoryParameterHolder holder = new ChatHistoryParameterHolder();

    public ChatHistory chatHistory;

    //endregion

    //region Constructors

    @Inject
    ChatHistoryViewModel(ChatHistoryRepository repository) {

        Utils.psLog("ChatHistoryViewModel");

        chatHistoryListData = Transformations.switchMap(chatHistoryListObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            Utils.psLog("ChatHistoryViewModel : chatHistoryListData");
            return repository.getChatHistoryList(obj.userId, obj.chatHistoryParameterHolder, obj.limit, obj.offset);
        });

        nextPageChatHistoryListData = Transformations.switchMap(nextPageChatHistoryListObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            Utils.psLog("ChatHistoryViewModel : nextPageChatHistoryListData");
            return repository.getNextPageChatHistoryList(obj.userId, obj.chatHistoryParameterHolder, obj.limit, obj.offset);
        });

        chatHistoryData = Transformations.switchMap(chatHistoryObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            Utils.psLog("ChatHistoryViewModel : chatHistoryData");
            return repository.getChatHistory(obj.itemId, obj.buyerUserId, obj.sellerUserId);
        });

    }

    //endregion

    public void setChatHistoryListObj(String userId, ChatHistoryParameterHolder chatHistoryParameterHolder, String limit, String offset) {
        if (!isLoading) {
            TmpDataHolder tmpDataHolder = new TmpDataHolder();
            tmpDataHolder.offset = offset;
            tmpDataHolder.limit = limit;
            tmpDataHolder.userId = userId;
            tmpDataHolder.chatHistoryParameterHolder = chatHistoryParameterHolder;

            chatHistoryListObj.setValue(tmpDataHolder);

            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<List<ChatHistory>>> getChatHistoryListData() {
        return chatHistoryListData;
    }

    //Get Latest Category Next Page
    public void setNextPageChatHistoryFromSellerObj(String userId, ChatHistoryParameterHolder chatHistoryParameterHolder, String limit, String offset) {

        if (!isLoading) {
            TmpDataHolder tmpDataHolder = new TmpDataHolder();
            tmpDataHolder.offset = offset;
            tmpDataHolder.limit = limit;
            tmpDataHolder.userId = userId;
            tmpDataHolder.chatHistoryParameterHolder = chatHistoryParameterHolder;
            nextPageChatHistoryListObj.setValue(tmpDataHolder);

            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getNextPageChatHistoryListData() {
        return nextPageChatHistoryListData;
    }

    public void setChatHistoryObj(String itemId, String buyerUserId, String sellerUserId) {
        if (!isLoading) {

            chatHistoryObj.setValue(new ChatHistoryTmpHolder(itemId, buyerUserId, sellerUserId));

            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<ChatHistory>> getChatHistoryData() {
        return chatHistoryData;
    }

    class TmpDataHolder {
        String limit = "";
        String offset = "";
        String userId = "";
        ChatHistoryParameterHolder chatHistoryParameterHolder;
    }

    class ChatHistoryTmpHolder {
        String itemId;
        String buyerUserId;
        String sellerUserId;

        ChatHistoryTmpHolder(String itemId, String buyerUserId, String sellerUserId) {
            this.itemId = itemId;
            this.buyerUserId = buyerUserId;
            this.sellerUserId = sellerUserId;
        }
    }
}
