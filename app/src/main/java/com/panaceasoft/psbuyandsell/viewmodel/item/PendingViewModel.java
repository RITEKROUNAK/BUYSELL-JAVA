package com.panaceasoft.psbuyandsell.viewmodel.item;


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

public class PendingViewModel extends PSViewModel {

    private final LiveData<Resource<List<Item>>> itemListByKeyData;
    private final MutableLiveData<PendingViewModel.ItemTmpDataHolder> itemListByKeyObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageItemListByKeyData;
    private final MutableLiveData<PendingViewModel.ItemTmpDataHolder> nextPageItemListByKeyObj = new MutableLiveData<>();

    public ItemParameterHolder holder = new ItemParameterHolder();
    public String itemId = "";
    public String cityId = "";
    public String locationId = "";


    public String userId = "";


    @Inject
    PendingViewModel(ItemRepository repository) {

        itemListByKeyData = Transformations.switchMap(itemListByKeyObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getItemListByKey(obj.loginUserId, obj.limit, obj.offset, obj.itemParameterHolder);

        });

        nextPageItemListByKeyData = Transformations.switchMap(nextPageItemListByKeyObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNextPageProductListByKey(obj.itemParameterHolder, obj.loginUserId, obj.limit, obj.offset);

        });


    }

    public void setItemListByKeyObj(String loginUserId, String limit, String offset, ItemParameterHolder parameterHolder) {
        if (!isLoading) {
            ItemTmpDataHolder tmpDataHolder = new ItemTmpDataHolder(limit, offset, loginUserId, parameterHolder);

            this.itemListByKeyObj.setValue(tmpDataHolder);
            setLoadingState(true);

        }
    }

    public LiveData<Resource<List<Item>>> getItemListByKeyData() {
        return itemListByKeyData;
    }

    public void setNextPageItemListByKeyObj(String limit, String offset, String loginUserId, ItemParameterHolder parameterHolder) {

        if (!isLoading) {
            ItemTmpDataHolder tmpDataHolder = new ItemTmpDataHolder(limit, offset, loginUserId, parameterHolder);

            setLoadingState(true);

            this.nextPageItemListByKeyObj.setValue(tmpDataHolder);
        }
    }

    public LiveData<Resource<Boolean>> getNextPageItemListByKeyData() {
        return nextPageItemListByKeyData;
    }

    //endregion

    static class ItemTmpDataHolder {

        private String limit, offset, loginUserId;
        private ItemParameterHolder itemParameterHolder;

        ItemTmpDataHolder(String limit, String offset, String loginUserId, ItemParameterHolder itemParameterHolder) {
            this.limit = limit;
            this.offset = offset;
            this.loginUserId = loginUserId;
            this.itemParameterHolder = itemParameterHolder;
        }
    }

}

