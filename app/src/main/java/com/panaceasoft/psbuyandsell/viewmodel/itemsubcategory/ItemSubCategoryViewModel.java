package com.panaceasoft.psbuyandsell.viewmodel.itemsubcategory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.repository.itemsubcategory.ItemSubCategoryRepository;
import com.panaceasoft.psbuyandsell.utils.AbsentLiveData;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.common.PSViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ItemSubCategory;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;

public class ItemSubCategoryViewModel extends PSViewModel {

    private LiveData<Resource<List<ItemSubCategory>>> allSubCategoryListData;
    private MutableLiveData<TmpDataHolder> allSubCategoryListObj = new MutableLiveData<>();

    private LiveData<Resource<List<ItemSubCategory>>> subCategoryListData;
    private MutableLiveData<TmpDataHolder> subCategoryListObj = new MutableLiveData<>();

    private LiveData<Resource<Boolean>> nextPageLoadingStateData;
    private MutableLiveData<TmpDataHolder> nextPageLoadingStateObj = new MutableLiveData<>();

    private final LiveData<Resource<List<ItemSubCategory>>> subCategoryListByCatIdData;
    private MutableLiveData<ListByCatIdTmpDataHolder> subCategoryListByCatIdObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageSubCategoryListByCatIdData;
    private MutableLiveData<ListByCatIdTmpDataHolder> nextPageSubCategoryListByCatIdObj = new MutableLiveData<>();

    public String catId = "";

    @Inject
    ItemSubCategoryViewModel(ItemSubCategoryRepository repository) {
        Utils.psLog("Inside SubCategoryViewModel");

        allSubCategoryListData = Transformations.switchMap(allSubCategoryListObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("allSubCategoryListData");
            return repository.getAllItemSubCategoryList(Config.API_KEY);
        });

        subCategoryListData = Transformations.switchMap(subCategoryListObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("subCategoryListData");
            return repository.getSubCategoryList(Config.API_KEY,obj.catId, obj.limit, obj.offset);
        });

        nextPageLoadingStateData = Transformations.switchMap(nextPageLoadingStateObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("nextPageLoadingStateData");
            return repository.getNextPageSubCategory(obj.catId, obj.limit, obj.offset);
        });

        subCategoryListByCatIdData = Transformations.switchMap(subCategoryListByCatIdObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            Utils.psLog("ItemCategoryViewModel : categories");
            return repository.getSubCategoriesWithCatId(obj.offset, obj.catId);
        });

        nextPageSubCategoryListByCatIdData = Transformations.switchMap(nextPageSubCategoryListByCatIdObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            Utils.psLog("Category List.");
            return repository.getNextPageSubCategoriesWithCatId(obj.limit, obj.offset, obj.catId);
        });
    }

    //list by cat id
    public void setSubCategoryListByCatIdObj(String catId, String limit, String offset){

        ListByCatIdTmpDataHolder tmpDataHolder = new ListByCatIdTmpDataHolder(limit, offset,catId);

        subCategoryListByCatIdObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<List<ItemSubCategory>>> getSubCategoryListByCatIdData()
    {
        return subCategoryListByCatIdData;
    }

    public void setNextPageSubCategoryListByCatIdObj( String limit, String offset, String catId)
    {
        ListByCatIdTmpDataHolder tmpDataHolder = new ListByCatIdTmpDataHolder(limit, offset,  catId);

        nextPageSubCategoryListByCatIdObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<Boolean>> getNextPageSubCategoryListByCatIdData() {
        return nextPageSubCategoryListByCatIdData;
    }
    //endregion

    public void setAllSubCategoryListObj() {
        if (!isLoading) {
            TmpDataHolder tmpDataHolder = new TmpDataHolder();
            allSubCategoryListObj.setValue(tmpDataHolder);

            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<List<ItemSubCategory>>> getAllSubCategoryListData() {
        return allSubCategoryListData;
    }


    public void setSubCategoryListData(String catId, String limit, String offset) {
        if (!isLoading) {
            TmpDataHolder tmpDataHolder = new TmpDataHolder();
            tmpDataHolder.catId = catId;
            tmpDataHolder.limit = limit;
            tmpDataHolder.offset = offset;
            subCategoryListObj.setValue(tmpDataHolder);

            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<List<ItemSubCategory>>> getSubCategoryListData() {
        return subCategoryListData;
    }

    public void setNextPageLoadingStateObj(String catId, String limit, String offset) {

        if (!isLoading) {
            TmpDataHolder tmpDataHolder = new TmpDataHolder();
            tmpDataHolder.catId = catId;
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
        public String loginUserId = "";
        public String offset = "";
        public String limit = "";
        public String catId = "";
        public Boolean isConnected = false;


    }

    class ListByCatIdTmpDataHolder {
        public String limit = "";
        public String offset = "";
        public String catId = "";

        public ListByCatIdTmpDataHolder(String limit, String offset,String catId) {
            this.limit = limit;
            this.offset = offset;
            this.catId = catId;
        }
    }

}
