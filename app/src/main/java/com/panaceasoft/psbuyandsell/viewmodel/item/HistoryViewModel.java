package com.panaceasoft.psbuyandsell.viewmodel.item;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.panaceasoft.psbuyandsell.repository.item.ItemRepository;
import com.panaceasoft.psbuyandsell.utils.AbsentLiveData;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.common.PSViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ItemHistory;

import java.util.List;

import javax.inject.Inject;

public class HistoryViewModel extends PSViewModel {
    private final LiveData<List<ItemHistory>> historyListData;
    private MutableLiveData<HistoryViewModel.TmpDataHolder> historyListObj = new MutableLiveData<>();


    @Inject
    HistoryViewModel(ItemRepository itemRepository) {
        //  basket List

        historyListData = Transformations.switchMap(historyListObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("get basket");
            return itemRepository.getAllHistoryList(obj.offset);
        });


    }
    //endregion
    //region Getter And Setter for basket List

    public void setHistoryItemListObj(String offset) {
        HistoryViewModel.TmpDataHolder tmpDataHolder = new HistoryViewModel.TmpDataHolder();
        tmpDataHolder.offset = offset;
        historyListObj.setValue(tmpDataHolder);
    }

    public LiveData<List<ItemHistory>> getAllHistoryItemList() {
        return historyListData;
    }

    //endregion


    //region Holder
    class TmpDataHolder {
        public int id = 0;
        public String productId = "";
        public String loginUserId = "";
        public String offset = "";
        public Boolean isConnected = false;
    }
//endregion
}