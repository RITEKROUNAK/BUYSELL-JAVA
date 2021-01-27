package com.panaceasoft.psbuyandsell.ui.apploading;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.login.LoginManager;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.MainActivity;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentAppLoadingBinding;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.apploading.PSAPPLoadingViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.clearalldata.ClearAllDataViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.item.ItemViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.user.UserViewModel;
import com.panaceasoft.psbuyandsell.viewobject.Item;
import com.panaceasoft.psbuyandsell.viewobject.PSAppInfo;
import com.panaceasoft.psbuyandsell.viewobject.UserLogin;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AppLoadingFragment extends PSFragment {


    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private PSDialogMsg psDialogMsg;
    private String startDate = Constants.ZERO;
    private String endDate = Constants.ZERO;

    private PSAPPLoadingViewModel appLoadingViewModel;
    private ClearAllDataViewModel clearAllDataViewModel;
    private UserViewModel userViewModel;
    private String itemId = "";

    //endregion Variables

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAppLoadingBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_app_loading, container, false, dataBindingComponent);
        AutoClearedValue<FragmentAppLoadingBinding> binding = new AutoClearedValue<>(this, dataBinding);

        if(getActivity() != null) {
            Intent intent = getActivity().getIntent();
            String data = intent.getDataString();
            if (data != null) {
                String[] spPath= data.split("=");
                itemId = spPath[1];
                //hh
            }
        }

        return binding.get().getRoot();
    }


    @Override
    protected void initUIAndActions() {

        psDialogMsg = new PSDialogMsg(getActivity(), false);

//        if (force_update) {
//            navigationController.navigateToForceUpdateActivity(this.getActivity(), force_update_title, force_update_msg);
//        }
    }

    @Override
    protected void initViewModels() {
        appLoadingViewModel = new ViewModelProvider(this, viewModelFactory).get(PSAPPLoadingViewModel.class);
        clearAllDataViewModel = new ViewModelProvider(this, viewModelFactory).get(ClearAllDataViewModel.class);
        userViewModel = new ViewModelProvider(this,viewModelFactory).get(UserViewModel.class);

    }

    @Override
    protected void initAdapters() {
    }

    @Override
    protected void initData() {

        if (connectivity.isConnected()) {
            if (startDate.equals(Constants.ZERO)) {

                startDate = getDateTime();
                Utils.setDatesToShared(startDate, endDate, pref);
            }

            endDate = getDateTime();
            appLoadingViewModel.setDeleteHistoryObj(startDate, endDate,loginUserId);

        } else {
            if (!selected_location_id.isEmpty()) {
                navigationController.navigateToMainActivity(getActivity(), selected_location_id, selected_location_name, itemId,selectedLat, selectedLng);

            } else {
                navigationController.navigateToLocationActivity(getActivity(), Constants.LOCATION_NOT_CLEAR_ICON, Constants.EMPTY_STRING,itemId);
            }

            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (getActivity() != null) {
                getActivity().finish();
            }
        }

        appLoadingViewModel.getDeleteHistoryData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {

                    case SUCCESS:

                        if (result.data != null) {

                            switch (result.data.userInfo.userStatus) {
                                case Constants.USER_STATUS__DELECTED:
                                    AppLoadingFragment.this.logout();
                                    showErrorDialog(result.data, getString(R.string.error_message__user_deleted));
                                    break;
                                case Constants.USER_STATUS__BANNED:
                                    AppLoadingFragment.this.logout();
                                    showErrorDialog(result.data, getString(R.string.error_message__user_banned));
                                    break;
                                case Constants.USER_STATUS__UNPUBLISHED:
                                    AppLoadingFragment.this.logout();
                                    showErrorDialog(result.data, getString(R.string.error_message__user_unpublished));
                                    break;
                                default:
                                    //default
                                    appLoadingViewModel.psAppInfo = result.data;
                                    checkVersionNumber(result.data);
                                    startDate = endDate;
                                    break;
                            }

                        }
                        break;

                    case ERROR:

                        break;
                }
            }

        });


        clearAllDataViewModel.getDeleteAllDataData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {

                    case ERROR:

                        break;

                    case SUCCESS:

                        checkForceUpdate(appLoadingViewModel.psAppInfo);
                        break;
                }
            }
        });

        userViewModel.getLoginUser().observe( this, new Observer<List<UserLogin>>() {
            @Override
            public void onChanged(List<UserLogin> data) {
                if (data != null) {
                    if (data.size() > 0) {
                        userViewModel.user = data.get(0).user;
                    }
                }
            }
        });

    }



    public void showErrorDialog(PSAppInfo psAppInfo, String message){
        psDialogMsg.showErrorDialog(message, getString(R.string.app__ok));
        psDialogMsg.show();

        psDialogMsg.okButton.setOnClickListener(view -> {
            psDialogMsg.cancel();
            appLoadingViewModel.psAppInfo = psAppInfo;
            checkVersionNumber(psAppInfo);
            startDate = endDate;
        });

    }

    private void logout() {
        userViewModel.deleteUserLogin(userViewModel.user).observe(this, status -> {
            if (status != null) {

                LoginManager.getInstance().logOut();
            }
        });
    }

    private void checkForceUpdate(PSAppInfo psAppInfo) {
        if (psAppInfo.psAppVersion.versionForceUpdate.equals(Constants.ONE)) {

            pref.edit().putString(Constants.APPINFO_PREF_VERSION_NO, psAppInfo.psAppVersion.versionNo).apply();
            pref.edit().putBoolean(Constants.APPINFO_PREF_FORCE_UPDATE, true).apply();
            pref.edit().putString(Constants.APPINFO_FORCE_UPDATE_TITLE, psAppInfo.psAppVersion.versionTitle).apply();
            pref.edit().putString(Constants.APPINFO_FORCE_UPDATE_MSG, psAppInfo.psAppVersion.versionMessage).apply();

            navigationController.navigateToForceUpdateActivity(this.getActivity(), psAppInfo.psAppVersion.versionTitle, psAppInfo.psAppVersion.versionMessage);
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else if (psAppInfo.psAppVersion.versionForceUpdate.equals(Constants.ZERO)) {

            pref.edit().putBoolean(Constants.APPINFO_PREF_FORCE_UPDATE, false).apply();

            psDialogMsg.showAppInfoDialog(getString(R.string.update), getString(R.string.app__cancel), psAppInfo.psAppVersion.versionTitle, psAppInfo.psAppVersion.versionMessage);
            ShowDialog();
        }
    }

    private void checkVersionNumber(PSAppInfo psAppInfo) {
        if (!Config.APP_VERSION.equals(psAppInfo.psAppVersion.versionNo)) {

            if (psAppInfo.psAppVersion.versionNeedClearData.equals(Constants.ONE)) {
                psDialogMsg.cancel();
                clearAllDataViewModel.setDeleteAllDataObj();
            } else {
                checkForceUpdate(appLoadingViewModel.psAppInfo);
            }

        } else {
            pref.edit().putBoolean(Constants.APPINFO_PREF_FORCE_UPDATE, false).apply();
            if (!selected_location_id.isEmpty()) {
                navigationController.navigateToMainActivity(getActivity(), selected_location_id, selected_location_name,itemId, selectedLat, selectedLng);
            } else {
                navigationController.navigateToLocationActivity(getActivity(), Constants.LOCATION_NOT_CLEAR_ICON, Constants.EMPTY_STRING,itemId);
            }
            if (getActivity() != null) {
                getActivity().finish();
            }
        }

    }

    private void ShowDialog() {
        psDialogMsg.show();

        psDialogMsg.okButton.setOnClickListener(v -> {
            psDialogMsg.cancel();

            if (!selected_location_id.isEmpty()) {
                navigationController.navigateToMainActivity(getActivity(), selected_location_id, selected_location_name,itemId, selectedLat, selectedLng);
            } else {
                navigationController.navigateToLocationActivity(getActivity(), Constants.LOCATION_NOT_CLEAR_ICON, Constants.EMPTY_STRING,itemId);
            }


            if (getActivity() != null) {
                navigationController.navigateToPlayStore(AppLoadingFragment.this.getActivity());

                getActivity().finish();
            }

        });

        psDialogMsg.cancelButton.setOnClickListener(v -> {
            psDialogMsg.cancel();
            if (!selected_location_id.isEmpty()) {
                navigationController.navigateToMainActivity(getActivity(), selected_location_id, selected_location_name,itemId, selectedLat, selectedLng);
            } else {
                navigationController.navigateToLocationActivity(getActivity(), Constants.LOCATION_NOT_CLEAR_ICON, Constants.EMPTY_STRING,itemId);
            }
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        psDialogMsg.getDialog().setOnCancelListener(dialog -> {
            if (!selected_location_id.isEmpty()) {
                navigationController.navigateToMainActivity(getActivity(), selected_location_id, selected_location_name, itemId,selectedLat, selectedLng);
            } else {
                navigationController.navigateToLocationActivity(getActivity(), Constants.LOCATION_NOT_CLEAR_ICON, Constants.EMPTY_STRING,itemId);
            }
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }


    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onResume() {

        super.onResume();
    }
}
