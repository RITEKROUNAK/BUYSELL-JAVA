package com.panaceasoft.psbuyandsell.ui.user;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentUserRegisterBinding;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.user.UserViewModel;
import com.panaceasoft.psbuyandsell.viewobject.User;

import java.util.List;

import okhttp3.internal.Util;

/**
 * UserRegisterFragment
 */
public class UserRegisterFragment extends PSFragment {


    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private UserViewModel userViewModel;

    private PSDialogMsg psDialogMsg;



    @VisibleForTesting
    private AutoClearedValue<FragmentUserRegisterBinding> binding;

    private AutoClearedValue<ProgressDialog> prgDialog;

    //endregion
    private FirebaseAuth mAuth;
    FirebaseUser user;
    String userEmail = "";

    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentUserRegisterBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_register, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }

    private void handleFirebaseAuthError(String email){
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful()){
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethod = result.getSignInMethods();

                    Utils.psLog("SignInMethod  ="+ signInMethod);
                    if(signInMethod.contains(Constants.EMAILAUTH)){
                        psDialogMsg.showErrorDialog("["+email+"]"+getString(R.string.login__auth_email),getString(R.string.app__ok));
                        psDialogMsg.show();
                    }else if(signInMethod.contains(Constants.GOOGLEAUTH)){
                        psDialogMsg.showErrorDialog("["+email+"]"+getString(R.string.login__auth_google),getString(R.string.app__ok));
                        psDialogMsg.show();
                    }else if(signInMethod.contains(Constants.FACEBOOKAUTH)){
                        psDialogMsg.showErrorDialog("["+email+"]"+getString(R.string.login__auth_facebook),getString(R.string.app__ok));
                        psDialogMsg.show();
                    }
                }
            }
        });
    }

    public FirebaseUser createUserWithEmailAndPassword(String email, String password){
        try{
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(UserRegisterFragment.this.getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Utils.psLog("createUserWithEmail:success");
                                user = mAuth.getCurrentUser();
                                UserRegisterFragment.this.registerUser();
                            }else {
                                signInWithEmailAndPassword(email, password);
                            }
                        }
                    });
        }catch (Exception exception){
            Utils.psLog("***** Error Exception");

            psDialogMsg.showErrorDialog(getString(R.string.login__exception_error),getString(R.string.app__ok));
            psDialogMsg.show();

        }
        return  user;
    }

    private FirebaseUser signInWithEmailAndPassword(String email, String password){

        try {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(UserRegisterFragment.this.getActivity(), new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Utils.psLog("signInWithEmail:success");
                        user = mAuth.getCurrentUser();
                        UserRegisterFragment.this.registerUser();
                    } else {
                        Utils.psLog("Fail");

                        if(!email.equals(Constants.DEFAULTEMAIL)) {

                            createUserWithEmailAndPassword(Constants.DEFAULTEMAIL, Constants.DEFAULTPASSWORD);
                        }else {
                            // Error
                            handleFirebaseAuthError(binding.get().emailEditText.getText().toString().trim());
                        }



                    }

                }
            });
        }catch (Exception e){
            Utils.psLog("signInWithEmail:failure");
            psDialogMsg.showWarningDialog(getString(R.string.login__failure_error), getString(R.string.app__ok));
            psDialogMsg.show();

        }
//        FirebaseUser firebaseUser = user;
        return  user;
    }


    @Override
    protected void initUIAndActions() {

        dataBindingComponent.getFragmentBindingAdapters().bindFullImageDrawable(binding.get().bgImageView, getResources().getDrawable(R.drawable.login_app_bg));

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        // Init Dialog
        prgDialog = new AutoClearedValue<>(this, new ProgressDialog(getActivity()));
        //prgDialog.get().setMessage(getString(R.string.message__please_wait));

        prgDialog.get().setMessage((Utils.getSpannableString(getContext(), getString(R.string.message__please_wait), Utils.Fonts.MM_FONT)));
        prgDialog.get().setCancelable(false);
        mAuth = FirebaseAuth.getInstance();

        //fadeIn Animation
        fadeIn(binding.get().getRoot());

        binding.get().loginButton.setOnClickListener(view -> {

            if (connectivity.isConnected()) {

                Utils.navigateAfterLogin(UserRegisterFragment.this.getActivity(), navigationController);

            } else {


                psDialogMsg.showWarningDialog(getString(R.string.no_internet_error), getString(R.string.app__ok));

                psDialogMsg.show();
            }

        });

        binding.get().policyAndPrivacyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.get().policyAndPrivacyCheckBox.isChecked()) {
                    navigationController.navigateToPrivacyPolicyActivity(getActivity());
                    userViewModel.checkFlag = true;
                } else {
                    userViewModel.checkFlag = false;
                }
            }
        });


        binding.get().registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userViewModel.checkFlag) {
                    userEmail = binding.get().emailEditText.getText().toString().trim();
                    userViewModel.isLoading = true;
                    updateRegisterBtnStatus();

                    // userEmail and userEmail is correct
                    // This is needed for firebase login
                    // no need to change to password
                    createUserWithEmailAndPassword(userEmail, userEmail);
                } else {

                    psDialogMsg.showWarningDialog(getString(R.string.error_message__to_check_agreement), getString(R.string.app__ok));
                    psDialogMsg.show();

              }

            }
        });


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

        bindingData();

        userViewModel.getRegisterUser().observe(this, listResource -> {

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

                            if (listResource.data.status.equals(Constants.TWO)) {

                                if (getActivity() != null) {

                                    Utils.registerUserLoginData(pref, listResource.data, binding.get().passwordEditText.getText().toString());
                                    Utils.navigateAfterUserRegister(getActivity(), navigationController);

                                }

                                userViewModel.isLoading = false;
                                prgDialog.get().cancel();
                                updateRegisterBtnStatus();
                            }else {
                                Utils.updateUserLoginData(pref,listResource.data);
                                Utils.navigateAfterUserLogin(getActivity(),navigationController);
                            }

                        }

                        break;
                    case ERROR:
                        // Error State

                        psDialogMsg.showWarningDialog(listResource.message, getString(R.string.app__ok));
                        binding.get().registerButton.setText(getResources().getString(R.string.register__register));
                        psDialogMsg.show();

                        userViewModel.isLoading = false;
                        prgDialog.get().cancel();

                        break;
                    default:
                        // Default
                        userViewModel.isLoading = false;
                        prgDialog.get().cancel();
                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }
        });
    }

    private void bindingData() {

        if (!userEmailToVerify.isEmpty()) {
            binding.get().emailEditText.setText(userEmailToVerify);
        }
        if (!userPasswordToVerify.isEmpty()) {
            binding.get().passwordEditText.setText(userPasswordToVerify);
        }
        if (!userNameToVerify.isEmpty()) {
            binding.get().nameEditText.setText(userNameToVerify);
        }
    }

    //endregion


    //region Private Methods

    private void updateRegisterBtnStatus() {
        if (userViewModel.isLoading) {
            binding.get().registerButton.setText(getResources().getString(R.string.message__loading));
        } else {
            binding.get().registerButton.setText(getResources().getString(R.string.register__register));
        }
    }

    private void registerUser() {

        Utils.hideKeyboard(getActivity());

        String userName = binding.get().nameEditText.getText().toString().trim();
        if (userName.equals("")) {

            psDialogMsg.showWarningDialog(getString(R.string.error_message__blank_name), getString(R.string.app__ok));

            psDialogMsg.show();
            return;
        }

        userEmail = binding.get().emailEditText.getText().toString().trim();
        if (userEmail.equals("")) {

            psDialogMsg.showWarningDialog(getString(R.string.error_message__blank_email), getString(R.string.app__ok));

            psDialogMsg.show();
            return;
        }

        String userPassword = binding.get().passwordEditText.getText().toString().trim();
        if (userPassword.equals("")) {

            psDialogMsg.showWarningDialog(getString(R.string.error_message__blank_password), getString(R.string.app__ok));

            psDialogMsg.show();
            return;
        }




        String token = pref.getString(Constants.NOTI_TOKEN, Constants.USER_NO_DEVICE_TOKEN);

        userViewModel.setRegisterUser(new User(
                "",
                "",
                "",
                "",
                "",
                "",
                userName,
                userEmail,
                "",
                "",
                "",
                userPassword,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "", token, "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));
    }

    //endregion

}

