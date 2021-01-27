package com.panaceasoft.psbuyandsell.ui.user.more;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.ads.AdRequest;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.MainActivity;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentMoreBinding;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.user.UserViewModel;
import com.panaceasoft.psbuyandsell.viewobject.holder.UserParameterHolder;

import java.net.MalformedURLException;
import java.net.URL;

public class MoreFragment extends PSFragment {
    //region Variables
    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    @VisibleForTesting
    private AutoClearedValue<FragmentMoreBinding> binding;

    private UserViewModel userViewModel;

    private PSDialogMsg psDialogMsg;

    private ConsentForm form;

    //endregion

    //region Override Methods
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentMoreBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.global__primary));
            
            ((MainActivity) getActivity()).updateToolbarIconColor(Color.WHITE);
            ((MainActivity) getActivity()).updateMenuIconWhite();
        }


        psDialogMsg = new PSDialogMsg(getActivity(), false);

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            binding.get().adView.loadAd(adRequest);
        } else {
            binding.get().adView.setVisibility(View.GONE);
        }


        binding.get().pendingPostsTextView.setText(binding.get().pendingPostsTextView.getText().toString());
        binding.get().pendingListTextView.setText(binding.get().pendingListTextView.getText().toString());

        binding.get().activePostsTextView.setText(binding.get().activePostsTextView.getText().toString());
        binding.get().activeListTextView.setText(binding.get().activeListTextView.getText().toString());

        binding.get().paidAdsTextView.setText(binding.get().paidAdsTextView.getText().toString());
        binding.get().promoteTextView.setText(binding.get().promoteTextView.getText().toString());

        binding.get().favouriteTextView.setText(binding.get().favouriteTextView.getText().toString());
        binding.get().favouritePostsTextView.setText(binding.get().favouritePostsTextView.getText().toString());

        binding.get().offerTextView.setText(binding.get().offerTextView.getText().toString());
        binding.get().offerListTextView.setText(binding.get().offerListTextView.getText().toString());

        binding.get().followerTextView.setText(binding.get().followerTextView.getText().toString());
        binding.get().followerListTextView.setText(binding.get().followerListTextView.getText().toString());

        binding.get().followingTextView.setText(binding.get().followingTextView.getText().toString());
        binding.get().followingListTextView.setText(binding.get().followingListTextView.getText().toString());

        binding.get().historyTextView.setText(binding.get().historyTextView.getText().toString());
        binding.get().historyListTextView.setText(binding.get().historyListTextView.getText().toString());

        binding.get().blockTextView.setText(binding.get().blockTextView.getText().toString());
        binding.get().blockListTextView.setText(binding.get().blockListTextView.getText().toString());

        binding.get().reportTextView.setText(binding.get().reportTextView.getText().toString());
        binding.get().reportListTextView.setText(binding.get().reportListTextView.getText().toString());

        binding.get().deactivateTextView.setText(binding.get().deactivateTextView.getText().toString());
        binding.get().deactivateRecoverTextView.setText(binding.get().deactivateRecoverTextView.getText().toString());

        binding.get().appSettingTitleTextView.setText(binding.get().appSettingTitleTextView.getText().toString());
        binding.get().appSettingTextView.setText(binding.get().appSettingTextView.getText().toString());

        binding.get().pendingPostsTextView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGNOPAID, Constants.ZERO, getString(R.string.profile__pending_listing)));
        binding.get().pendingListTextView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGNOPAID, Constants.ZERO, getString(R.string.profile__pending_listing)));
        binding.get().pendingImageView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGNOPAID, Constants.ZERO, getString(R.string.profile__pending_listing)));

        binding.get().activePostsTextView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGNOPAID, Constants.ONE, getString(R.string.profile__listing)));
        binding.get().activeListTextView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGNOPAID, Constants.ONE, getString(R.string.profile__listing)));
        binding.get().activeImageView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGNOPAID, Constants.ONE, getString(R.string.profile__listing)));

        binding.get().paidAdsTextView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGPAID, Constants.ONE, ""));
        binding.get().promoteTextView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGPAID, Constants.ONE, ""));
        binding.get().paidAdsImageView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGPAID, Constants.ONE, ""));

        binding.get().favouriteTextView.setOnClickListener(view -> navigationController.navigateToFavouriteActivity(getActivity()));
        binding.get().favouritePostsTextView.setOnClickListener(view -> navigationController.navigateToFavouriteActivity(getActivity()));
        binding.get().favouriteImageView.setOnClickListener(view -> navigationController.navigateToFavouriteActivity(getActivity()));

        binding.get().offerTextView.setOnClickListener(view -> navigationController.navigateToOfferList(getActivity()));
        binding.get().offerListTextView.setOnClickListener(view -> navigationController.navigateToOfferList(getActivity()));
        binding.get().offerImageView.setOnClickListener(view -> navigationController.navigateToOfferList(getActivity()));

        binding.get().followerTextView.setOnClickListener(view -> navigationController.navigateToUserListActivity(MoreFragment.this.getActivity(), new UserParameterHolder().getFollowerUsers()));
        binding.get().followerListTextView.setOnClickListener(view -> navigationController.navigateToUserListActivity(MoreFragment.this.getActivity(), new UserParameterHolder().getFollowerUsers()));
        binding.get().followerImageView.setOnClickListener(view -> navigationController.navigateToUserListActivity(MoreFragment.this.getActivity(), new UserParameterHolder().getFollowerUsers()));

        binding.get().followingTextView.setOnClickListener(view -> navigationController.navigateToUserListActivity(MoreFragment.this.getActivity(), new UserParameterHolder().getFollowingUsers()));
        binding.get().followingListTextView.setOnClickListener(view -> navigationController.navigateToUserListActivity(MoreFragment.this.getActivity(), new UserParameterHolder().getFollowingUsers()));
        binding.get().followingImageView.setOnClickListener(view -> navigationController.navigateToUserListActivity(MoreFragment.this.getActivity(), new UserParameterHolder().getFollowingUsers()));

        binding.get().historyTextView.setOnClickListener(view -> navigationController.navigateToHistoryList(getActivity()));
        binding.get().historyListTextView.setOnClickListener(view -> navigationController.navigateToHistoryList(getActivity()));
        binding.get().historyImageView.setOnClickListener(view -> navigationController.navigateToHistoryList(getActivity()));

        binding.get().blockTextView.setOnClickListener(view -> navigationController.navigateToBlockUserList(getActivity()));
        binding.get().blockListTextView.setOnClickListener(view -> navigationController.navigateToBlockUserList(getActivity()));
        binding.get().blockImageView.setOnClickListener(view -> navigationController.navigateToBlockUserList(getActivity()));

        binding.get().reportTextView.setOnClickListener(view -> navigationController.navigateToReportedItemList(getActivity()));
        binding.get().reportListTextView.setOnClickListener(view -> navigationController.navigateToReportedItemList(getActivity()));
        binding.get().reportImageView.setOnClickListener(view -> navigationController.navigateToReportedItemList(getActivity()));

        binding.get().appSettingTitleTextView.setOnClickListener(view -> navigationController.navigateToSettingActivity(getActivity()));
        binding.get().appSettingTextView.setOnClickListener(view -> navigationController.navigateToSettingActivity(getActivity()));
        binding.get().appSettingImageView.setOnClickListener(view -> navigationController.navigateToSettingActivity(getActivity()));

        binding.get().deactivateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psDialogMsg.showConfirmDialog(getString(R.string.profile__confirm_deactivate), getString(R.string.app__ok), getString(R.string.message__cancel_close));
                psDialogMsg.show();

                psDialogMsg.okButton.setOnClickListener(v12 -> {
                    if(getActivity() != null) {
                        navigationController.navigateBackToMoreFragment(getActivity());

                        if (!(getActivity() instanceof MainActivity)) {
                            getActivity().finish();
                        }
                    }

                    psDialogMsg.cancel();
                });

                psDialogMsg.cancelButton.setOnClickListener(v1 -> psDialogMsg.cancel());

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

        userViewModel.setUserObj(loginUserId);
        userViewModel.getLoginUser().observe(this, data -> {

            if (data != null) {

                if (data.size() > 0) {
                    userViewModel.user = data.get(0).user;
                }
            }

        });

    }


    class ClearCacheAsync extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (getActivity() != null) {
                Glide glide = Glide.get(getActivity().getApplicationContext());
                glide.clearDiskCache();
            }

            return true;
        }

    }

    private void collectConsent() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL(Config.POLICY_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }

        form = new ConsentForm.Builder(this.getContext(), privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.

                        Utils.psLog("Form loaded");

                        if (form != null) {
                            form.show();
                        }
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.

                        Utils.psLog("Form Opened");
                    }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        // Consent form was closed.

                        pref.edit().putString(Config.CONSENTSTATUS_CURRENT_STATUS, consentStatus.name()).apply();
                        pref.edit().putBoolean(Config.CONSENTSTATUS_IS_READY_KEY, true).apply();
                        Utils.psLog("Form Closed");
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        // Consent form error.

                        pref.edit().putBoolean(Config.CONSENTSTATUS_IS_READY_KEY, false).apply();
                        Utils.psLog("Form Error " + errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .build();

        form.load();

    }
}


