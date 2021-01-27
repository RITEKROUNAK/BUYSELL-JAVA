package com.panaceasoft.psbuyandsell.ui.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentUserFbRegisterBinding;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.user.UserViewModel;
import com.panaceasoft.psbuyandsell.viewobject.User;

import org.json.JSONException;

import java.util.Arrays;

/**
 * UserFBRegisterFragment
 */
public class UserFBRegisterFragment extends PSFragment {


    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private UserViewModel userViewModel;

    private PSDialogMsg psDialogMsg;

    @VisibleForTesting
    private AutoClearedValue<FragmentUserFbRegisterBinding> binding;

    private AutoClearedValue<ProgressDialog> prgDialog;

    private CallbackManager callbackManager;

    //endregion


    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        FacebookSdk.sdkInitialize(getContext());

        callbackManager = CallbackManager.Factory.create();

        // Inflate the layout for this fragment
        FragmentUserFbRegisterBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_fb_register, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }


    @Override
    protected void initUIAndActions() {

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        binding.get().fbRegisterTitleTextView.setText(getString(R.string.fb_register__welcome, getString(R.string.app__app_name)));
        binding.get().fbRegisterDescTextView.setText(getString(R.string.fb_register__desc));

//        Glide.with(this).load(R.drawable.fb_register_bg).apply(new RequestOptions()
//                .placeholder(R.drawable.default_image)
//                .centerCrop()
//                .dontAnimate()
//                .dontTransform()).into(binding.get().bgImageView);

        dataBindingComponent.getFragmentBindingAdapters().bindFullImageDrawable(binding.get().bgImageView, getResources().getDrawable(R.drawable.fb_register_bg));

//        // Init Dialog
        prgDialog = new AutoClearedValue<>(this, new ProgressDialog(getActivity()));
        //prgDialog.get().setMessage(getString(R.string.message__please_wait));

        prgDialog.get().setMessage((Utils.getSpannableString(getContext(), getString(R.string.message__please_wait), Utils.Fonts.MM_FONT)));
        prgDialog.get().setCancelable(false);

        String token = pref.getString(Constants.NOTI_TOKEN, Constants.USER_NO_DEVICE_TOKEN);

        binding.get().tmpLogin.setOnClickListener(view -> userViewModel.setUserLogin(new User(
                "",
                "",
                "",
                "",
                "",
                "",
                "admin@mokets.com",
                "admin@mokets.com",
                "",
                "",
                "",
                "admin",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                token,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                null)));

    }


    @Override
    protected void initViewModels() {
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);
    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        String token = pref.getString(Constants.NOTI_TOKEN, Constants.USER_NO_DEVICE_TOKEN);

        binding.get().loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d("aaaaaaa", loginResult.getAccessToken().toString());

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        (object, response) -> {

                            String name = "";
                            String email = "";
                            String id = "";
                            String imageURL = "";
                            try {
                                if (object != null) {

                                    name = object.getString(Constants.FB_NAME_KEY);

                                }
                                //link.setText(object.getString("link"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                if (object != null) {

                                    email = object.getString(Constants.FB_EMAIL_KEK);

                                }
                                //link.setText(object.getString("link"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                if (object != null) {

                                    id = object.getString(Constants.FB_ID_KEY);

                                }
                                //link.setText(object.getString("link"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if (!id.equals("")) {
                                prgDialog.get().show();
                                userViewModel.registerFBUser(id, name, email, imageURL, token);
                            }

                        });
                Bundle parameters = new Bundle();
                parameters.putString(Constants.FB_FIELDS, Constants.FB_EMAILNAMEID);
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

                Utils.psLog("OnCancel.");
            }

            @Override
            public void onError(FacebookException e) {
                Utils.psLog("OnError.");
            }


        });


        userViewModel.getRegisterFBUserData().observe(this, listResource -> {

            if (listResource != null) {

                Utils.psLog("Got Data" + listResource.message + listResource.toString());

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        prgDialog.get().show();

                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {
                            try {

                                Utils.addUserLoginData(pref,listResource.data.user,listResource.data.userId);
                                Utils.navigateAfterUserLogin(getActivity(),navigationController);


                            } catch (NullPointerException ne) {
                                Utils.psErrorLog("Null Pointer Exception.", ne);
                            } catch (Exception e) {
                                Utils.psErrorLog("Error in getting notification flag data.", e);
                            }

                            userViewModel.isLoading = false;
                            prgDialog.get().cancel();

                        }

                        break;
                    case ERROR:
                        // Error State

                        userViewModel.isLoading = false;
                        prgDialog.get().cancel();

                        psDialogMsg.showErrorDialog(listResource.message, getString(R.string.app__ok));
                        psDialogMsg.show();

                        break;
                    default:
                        // Default
                        //userViewModel.isLoading = false;
                        //prgDialog.get().cancel();
                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }

        });

        // Tmp Later to delete
        userViewModel.getUserLoginStatus().observe(this, listResource -> {

            if (listResource != null) {

                Utils.psLog("Got Data" + listResource.message + listResource.toString());

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {
                            try {

                                Utils.addUserLoginData(pref,listResource.data.user,listResource.data.userId);
                                Utils.navigateAfterUserLogin(getActivity(),navigationController);


                            } catch (NullPointerException ne) {
                                Utils.psErrorLog("Null Pointer Exception.", ne);
                            } catch (Exception e) {
                                Utils.psErrorLog("Error in getting notification flag data.", e);
                            }

                            userViewModel.setLoadingState(false);

                        }

                        break;
                    case ERROR:
                        // Error State

                        psDialogMsg.showErrorDialog(listResource.message, getString(R.string.app__ok));
                        psDialogMsg.show();

                        userViewModel.setLoadingState(false);

                        break;
                    default:
                        // Default

                        userViewModel.setLoadingState(false);

                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //endregion

}

