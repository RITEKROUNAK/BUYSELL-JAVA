package com.panaceasoft.psbuyandsell.viewmodel.itemlocation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.panaceasoft.psbuyandsell.repository.itemlocation.ItemLocationRepository;
import com.panaceasoft.psbuyandsell.utils.AbsentLiveData;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.common.PSViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ItemLocation;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.holder.CategoryParameterHolder;

import java.util.List;

import javax.inject.Inject;

public class ItemLocationViewModel extends PSViewModel {


    //region Variables

    private final LiveData<Resource<List<ItemLocation>>> itemTypeListData;
    private MutableLiveData<ItemLocationViewModel.TmpDataHolder> itemTypeListObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageLoadingStateData;
    private MutableLiveData<ItemLocationViewModel.TmpDataHolder> nextPageLoadingStateObj = new MutableLiveData<>();

    public CategoryParameterHolder categoryParameterHolder = new CategoryParameterHolder();

    public String keyword, orderType = Constants.SEARCH_CITY_DESC, orderBy = Constants.SEARCH_CITY_DEFAULT_ORDERING;

    //endregion

    //region Constructors

    @Inject
    ItemLocationViewModel(ItemLocationRepository repository) {

        Utils.psLog("ItemLocationViewModel");

        itemTypeListData = Transformations.switchMap(itemTypeListObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            Utils.psLog("ItemLocationViewModel : categories");
            return repository.getItemLocationList(obj.limit, obj.offset,obj.loginUserId,obj.keyword,obj.orderBy,obj.orderType);
        });

        nextPageLoadingStateData = Transformations.switchMap(nextPageLoadingStateObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            Utils.psLog("Category List.");
            return repository.getNextPageLocationList(obj.limit, obj.offset,obj.loginUserId,obj.keyword,obj.orderBy,obj.orderType);
        });

    }

    //endregion

    public void setItemLocationListObj(String limit, String offset,String loginUserId,String keyword,String orderBy,String orderType) {
        if (!isLoading) {
            TmpDataHolder tmpDataHolder = new TmpDataHolder();
            tmpDataHolder.offset = offset;
            tmpDataHolder.limit = limit;
            tmpDataHolder.loginUserId=loginUserId;
            tmpDataHolder.keyword=keyword;
            tmpDataHolder.orderBy=orderBy;
            tmpDataHolder.orderType=orderType;
            itemTypeListObj.setValue(tmpDataHolder);

            // start loading
            setLoadingState(true);
        }
    }


    public LiveData<Resource<List<ItemLocation>>> getItemLocationListData() {
        return itemTypeListData;
    }



    public void setNextPageLoadingStateObj(String limit, String offset) {

        if (!isLoading) {
            ItemLocationViewModel.TmpDataHolder tmpDataHolder = new ItemLocationViewModel.TmpDataHolder();
            tmpDataHolder.offset = offset;
            tmpDataHolder.limit = limit;
            nextPageLoadingStateObj.setValue(tmpDataHolder);

            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getNextPageLoadingStateData() {
        return nextPageLoadingStateData;
    }

    class TmpDataHolder {
        public String limit = "";
        public String offset = "";
        public String cityId = "";
        public String loginUserId="";
        public String keyword="";
        public String orderBy="";
        public String orderType="";
    }
}
