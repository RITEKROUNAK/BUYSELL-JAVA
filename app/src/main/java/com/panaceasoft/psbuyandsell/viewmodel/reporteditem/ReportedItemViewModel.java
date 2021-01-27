package com.panaceasoft.psbuyandsell.viewmodel.reporteditem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.panaceasoft.psbuyandsell.repository.reporteditem.ReportedItemRepository;
import com.panaceasoft.psbuyandsell.utils.AbsentLiveData;
import com.panaceasoft.psbuyandsell.viewmodel.common.PSViewModel;
import com.panaceasoft.psbuyandsell.viewobject.ReportedItem;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;

public class ReportedItemViewModel extends PSViewModel {

    private final LiveData<Resource<List<ReportedItem>>> reportedData;
    private MutableLiveData<ReportedItemViewModel.TmpDataHolder> reportedObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPagereportedData;
    private MutableLiveData<ReportedItemViewModel.TmpDataHolder> nextPagereportedObj = new MutableLiveData<>();

    @Inject
    ReportedItemViewModel(ReportedItemRepository repository){
        reportedData = Transformations.switchMap(reportedObj, obj -> {
            if (obj == null){
                return AbsentLiveData.create();
            }
            return  repository.getReportedItemList(obj.limit,obj.offset,obj.loginUserId);
        });

        nextPagereportedData = Transformations.switchMap(nextPagereportedObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNextPageReportedItemList(obj.limit,obj.offset,obj.loginUserId);

        });



    }

    public void setReportedObj(String limit, String offset,String loginUserId) {
      TmpDataHolder tmpDataHolder = new TmpDataHolder(limit, offset,loginUserId);

        this.reportedObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<List<ReportedItem>>> getReportedData(){return  reportedData;}


    public void setNextPagereportedObj(String limit, String offset,String loginUserId) {
      TmpDataHolder tmpDataHolder = new TmpDataHolder(limit, offset,loginUserId);

        this.nextPagereportedObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<Boolean>> getNextPagereportedData() {
        return nextPagereportedData;
    }

    class TmpDataHolder {

        String  limit, offset,loginUserId;

        public TmpDataHolder(String limit, String offset,String loginUserId) {
            this.limit = limit;
            this.offset = offset;
            this.loginUserId =loginUserId;
        }
    }
}
