package com.panaceasoft.psbuyandsell.viewmodel.clearalldata;

import com.panaceasoft.psbuyandsell.repository.clearpackage.ClearPackageRepository;
import com.panaceasoft.psbuyandsell.utils.AbsentLiveData;
import com.panaceasoft.psbuyandsell.viewmodel.common.PSViewModel;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class ClearAllDataViewModel extends PSViewModel {

    private final LiveData<Resource<Boolean>> deleteAllDataData;
    private MutableLiveData<Boolean> deleteAllDataObj = new MutableLiveData<>();


    @Inject
    public ClearAllDataViewModel(ClearPackageRepository repository) {

        deleteAllDataData = Transformations.switchMap(deleteAllDataObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.clearAllTheData();
        });
    }

    public void setDeleteAllDataObj() {

        this.deleteAllDataObj.setValue(true);
    }

    public LiveData<Resource<Boolean>> getDeleteAllDataData() {
        return deleteAllDataData;
    }


}
