package com.panaceasoft.psbuyandsell.viewmodel.itemfromfollower;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.panaceasoft.psbuyandsell.repository.item.ItemRepository;
import com.panaceasoft.psbuyandsell.utils.AbsentLiveData;
import com.panaceasoft.psbuyandsell.viewmodel.common.PSViewModel;
import com.panaceasoft.psbuyandsell.viewobject.Item;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.holder.ItemParameterHolder;

import java.util.List;

import javax.inject.Inject;

public class ItemFromFollowerViewModel extends PSViewModel {

    private final LiveData<Resource<List<Item>>> itemFromFollowerListData;
    private final MutableLiveData<ItemFromFollowerViewModel.ItemTmpDataHolder> itemFromFollowerListObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageitemFromFollowerListData;
    private final MutableLiveData<ItemFromFollowerViewModel.ItemTmpDataHolder> nextPageitemFromFollowerListObj = new MutableLiveData<>();

    public ItemParameterHolder holder = new ItemParameterHolder();
    public String lat = "";
    public String lng = "";

    public String itemId = "";
    public String cityId = "";

    @Inject
    ItemFromFollowerViewModel(ItemRepository repository)
    {

        itemFromFollowerListData = Transformations.switchMap(itemFromFollowerListObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getItemFromFollowerList(obj.loginUserId, obj.limit, obj.offset);

        });

        nextPageitemFromFollowerListData = Transformations.switchMap(nextPageitemFromFollowerListObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNextPageItemFromFollowerList(obj.loginUserId, obj.limit, obj.offset);

        });

    }

    //region getItemList

    public void setItemFromFollowerListObj(String loginUserId, String limit, String offset) {

        ItemTmpDataHolder tmpDataHolder = new ItemTmpDataHolder(limit, offset, loginUserId);

        this.itemFromFollowerListObj.setValue(tmpDataHolder);

    }

    public LiveData<Resource<List<Item>>> getItemFromFollowerListData() {
        return itemFromFollowerListData;
    }

    public void setNextPageItemFromFollowerListObj( String loginUserId,String limit, String offset) {

        if(!isLoading)
        {
            ItemTmpDataHolder tmpDataHolder = new ItemTmpDataHolder(limit, offset, loginUserId);

            setLoadingState(true);

            this.nextPageitemFromFollowerListObj.setValue(tmpDataHolder);
        }
    }

    public LiveData<Resource<Boolean>> getNextPageItemFromFollowerListData() {
        return nextPageitemFromFollowerListData;
    }

    //endregion

    private class ItemTmpDataHolder {

        private String limit, offset, loginUserId;

        private ItemTmpDataHolder(String limit, String offset, String loginUserId) {
            this.limit = limit;
            this.offset = offset;
            this.loginUserId = loginUserId;
        }
    }

    //region Holder
    class TmpDataHolder {
        public String offset = "";
        public String itemId = "";
        public String userId = "";
        public String cityId = "";
        public Boolean isConnected = false;
    }
    //endregion

}
