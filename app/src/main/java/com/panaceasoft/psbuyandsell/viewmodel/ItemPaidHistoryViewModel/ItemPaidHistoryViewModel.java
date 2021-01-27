package com.panaceasoft.psbuyandsell.viewmodel.ItemPaidHistoryViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.panaceasoft.psbuyandsell.repository.itempaidhistory.ItemPaidHistoryRepository;
import com.panaceasoft.psbuyandsell.utils.AbsentLiveData;
import com.panaceasoft.psbuyandsell.viewmodel.common.PSViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ItemPaidHistory;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;

public class ItemPaidHistoryViewModel extends PSViewModel {

    public String itemId;
    public String amount;
    public String startDate;
    public String howmanyDay;
    public String timeStamp;
    public String inAppPurchasedPrdIdAndroid;

    // Upload Paid Ad
    private final LiveData<Resource<ItemPaidHistory>> sendItemPaidHistoryData;
    private MutableLiveData<ItemPaidHistoryViewModel.TmpDataHolder> sendItemPaidHistoryObj = new MutableLiveData<>();


    // Get Paid Ad
    private final LiveData<Resource<List<ItemPaidHistory>>>itemPaidHistoryData;
    private MutableLiveData<ItemPaidHistoryViewModel.TmpDataHolder> itemPaidHistoryObj = new MutableLiveData<>();

    // Get Next Page Paid Ad
    private final LiveData<Resource<Boolean>> nextPageItemPaidHistoryData;
    private MutableLiveData<ItemPaidHistoryViewModel.TmpDataHolder> nextPageItemPaidHistoryObj = new MutableLiveData<>();


    @Inject
    ItemPaidHistoryViewModel(ItemPaidHistoryRepository itemPaidHistoryRepository) {

        sendItemPaidHistoryData = Transformations.switchMap(sendItemPaidHistoryObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }
            return itemPaidHistoryRepository.uploadItemPaidToServer(obj.itemId, obj.amount, obj.startDate, obj.howManyDay, obj.paymentMethod, obj.paymentMethodNonce,obj.startTimeStamp, obj.razorId);
        });


        itemPaidHistoryData = Transformations.switchMap(itemPaidHistoryObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return itemPaidHistoryRepository.getItemPaidHistoryList(obj.loginUserId,obj.limit, obj.offset);

        });

        nextPageItemPaidHistoryData = Transformations.switchMap(nextPageItemPaidHistoryObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return itemPaidHistoryRepository.getNextPageItemPaidHistoryList(obj.loginUserId, obj.limit, obj.offset);

        });
    }

    // region Getter And Setter for Item Paid History Upload

    public void setUploadItemPaidHistoryData(String itemId, String amount, String startDate, String howManyDay, String paymentMethod, String paymentMethodNonce, String startTimeStamp, String razorId, String purchasedId) {

        TmpDataHolder tmpDataHolder = new TmpDataHolder();
        tmpDataHolder.itemId = itemId;
        tmpDataHolder.amount = amount;
        tmpDataHolder.startDate =startDate;
        tmpDataHolder.howManyDay = howManyDay;
        tmpDataHolder.paymentMethod = paymentMethod;
        tmpDataHolder.paymentMethodNonce = paymentMethodNonce;
        tmpDataHolder.startTimeStamp = startTimeStamp;
        tmpDataHolder.razorId = razorId;
        tmpDataHolder.purchasedId = purchasedId;
        sendItemPaidHistoryObj.setValue(tmpDataHolder);

    }

    public LiveData<Resource<ItemPaidHistory>> getUploadItemPaidHistoryData() {
        return sendItemPaidHistoryData;
    }



    // endregion

    // region getter and setter for get item paid history
    public void setPaidItemHistory(String loginUserId, String limit, String offset) {
        if(!isLoading){
       TmpDataHolder tmpDataHolder = new  TmpDataHolder();
        tmpDataHolder.loginUserId = loginUserId;
        tmpDataHolder.limit = limit;
        tmpDataHolder.offset = offset;

        this.itemPaidHistoryObj.setValue(tmpDataHolder);
        }
    }

    public LiveData<Resource<List<ItemPaidHistory>>> getPaidItemHistory() {
        return itemPaidHistoryData;
    }
    // endregion


    //region getter and setter for get item paid history next page

    public void setNextPagePaidItemHistory(String loginUserId, String limit, String offset) {
        TmpDataHolder tmpDataHolder = new TmpDataHolder();
        tmpDataHolder.loginUserId = loginUserId;
        tmpDataHolder.limit = limit;
        tmpDataHolder.offset = offset;

        this.nextPageItemPaidHistoryObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<Boolean>> getNextPagePaidItemHistory() {
        return nextPageItemPaidHistoryData;
    }


    //endregion
    class TmpDataHolder {
        public String itemId = "";
        public String amount = "";
        public String startDate = "";
        public String howManyDay = "";
        public String paymentMethod = "";
        public String paymentMethodNonce = "";
        public String loginUserId = "";
        public String limit = "";
        public String offset = "";
        public String startTimeStamp = "";
        public String razorId = "";
        public String purchasedId = "";
    }


}
