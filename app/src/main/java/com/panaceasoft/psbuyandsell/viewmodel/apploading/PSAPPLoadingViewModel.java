package com.panaceasoft.psbuyandsell.viewmodel.apploading;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.panaceasoft.psbuyandsell.repository.apploading.AppLoadingRepository;
import com.panaceasoft.psbuyandsell.utils.AbsentLiveData;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.common.PSViewModel;
import com.panaceasoft.psbuyandsell.viewobject.PSAppInfo;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import javax.inject.Inject;

public class PSAPPLoadingViewModel extends PSViewModel {

    private final LiveData<Resource<PSAppInfo>> deleteHistoryData;
    private MutableLiveData<TmpDataHolder> deleteHistoryObj = new MutableLiveData<>();
    public PSAppInfo psAppInfo;
    public String stripePublishableKey;
    public String payStackKey;
    public String currencyShortForm;
    public String inAppPurchasedPrdIdAndroid;
    public String appSettingLat;
    public String appSettingLng;

    @Inject
    PSAPPLoadingViewModel(AppLoadingRepository repository) {
        deleteHistoryData = Transformations.switchMap(deleteHistoryObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("PSAppInfoViewModel");
            return repository.deleteTheSpecificObjects(obj.startDate, obj.endDate,obj.user_id);
        });
    }

    public void setDeleteHistoryObj(String startDate, String endDate,String user_id) {

        TmpDataHolder tmpDataHolder = new TmpDataHolder(startDate, endDate,user_id);

        this.deleteHistoryObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<PSAppInfo>> getDeleteHistoryData() {
        return deleteHistoryData;
    }

    class TmpDataHolder {
        String startDate, endDate, user_id;

        private TmpDataHolder(String startDate, String endDate,String user_id) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.user_id=user_id;
        }
    }

}

