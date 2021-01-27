package com.panaceasoft.psbuyandsell.ui.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.MainActivity;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentProfileBinding;
import com.panaceasoft.psbuyandsell.ui.city.selectedcity.SelectedCityFragment;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.ui.item.adapter.ItemHorizontalListAdapter;
import com.panaceasoft.psbuyandsell.ui.item.detail.ItemFragment;
import com.panaceasoft.psbuyandsell.ui.item.promote.adapter.ItemPromoteHorizontalListAdapter;
import com.panaceasoft.psbuyandsell.ui.user.userlist.detail.UserDetailFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.ItemPaidHistoryViewModel.ItemPaidHistoryViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.item.DisabledViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.item.ItemViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.item.PendingViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.item.RecentItemViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.item.RejectedViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.user.UserViewModel;
import com.panaceasoft.psbuyandsell.viewobject.Item;
import com.panaceasoft.psbuyandsell.viewobject.ItemPaidHistory;
import com.panaceasoft.psbuyandsell.viewobject.User;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.holder.UserParameterHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ProfileFragment
 */
public class ProfileFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {


    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemViewModel itemViewModel;
    private DisabledViewModel disabledViewModel;
    private RejectedViewModel rejectedViewModel;
    private PendingViewModel pendingViewModel;
    private UserViewModel userViewModel;
    private ItemPaidHistoryViewModel itemPaidHistoryViewModel;
    private RecentItemViewModel recentItemViewModel;
    public PSDialogMsg psDialogMsg;
    private boolean layoutDone = false;
    private int loadingCount = 0;

    @VisibleForTesting
    private AutoClearedValue<FragmentProfileBinding> binding;
    private AutoClearedValue<ItemHorizontalListAdapter> adapter;
    private AutoClearedValue<ItemHorizontalListAdapter> pendingAdapter;
    private AutoClearedValue<ItemHorizontalListAdapter> rejectedAdapter;
    private AutoClearedValue<ItemHorizontalListAdapter> disabledAdapter;
    private AutoClearedValue<ItemPromoteHorizontalListAdapter> itemPromoteHorizontalListAdapter;


    //endregion


    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentProfileBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initUIAndActions() {

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setToolbarText(((MainActivity) getActivity()).binding.toolbar, getString(R.string.menu__profile));
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.global__primary));
            ((MainActivity) getActivity()).updateMenuIconWhite();
            ((MainActivity) getActivity()).updateToolbarIconColor(Color.WHITE);
            ((MainActivity) getActivity()).refreshPSCount();
        }

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        binding.get().approvedListingRecyclerView.setNestedScrollingEnabled(false);
        binding.get().pendingRecyclerView.setNestedScrollingEnabled(false);
        binding.get().rejectedRecyclerView.setNestedScrollingEnabled(false);
        binding.get().disabledRecyclerView.setNestedScrollingEnabled(false);
        binding.get().editTextView.setOnClickListener(view -> navigationController.navigateToProfileEditActivity(getActivity()));
        binding.get().seeAllTextView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGNOPAID, Constants.ONE, getString(R.string.profile__listing)));
        binding.get().pendingSeeAllTextView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGNOPAID, Constants.ZERO, getString(R.string.profile__pending_listing)));
        binding.get().rejectedSeeAllTextView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGNOPAID, Constants.THREE, getString(R.string.profile__rejected_listing)));
        binding.get().disabledSeeAllTextView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGNOPAID, Constants.TWO, getString(R.string.profile__disabled_listing)));
        binding.get().favouriteTextView.setOnClickListener(view -> navigationController.navigateToFavouriteActivity(getActivity()));
        binding.get().heartImageView.setOnClickListener(view -> navigationController.navigateToFavouriteActivity(getActivity()));
        binding.get().moreTextView.setOnClickListener(view -> navigationController.navigateToMoreActivity(getActivity(), loginUserName));
        binding.get().followingUserTextView.setOnClickListener(view -> navigationController.navigateToUserListActivity(ProfileFragment.this.getActivity(), new UserParameterHolder().getFollowingUsers()));
        binding.get().followUserTextView.setOnClickListener(view -> navigationController.navigateToUserListActivity(ProfileFragment.this.getActivity(), new UserParameterHolder().getFollowerUsers()));
        binding.get().deactivateTextView.setOnClickListener(v -> {
            psDialogMsg.showConfirmDialog(getString(R.string.profile__confirm_deactivate), getString(R.string.app__ok), getString(R.string.message__cancel_close));
            psDialogMsg.show();

            psDialogMsg.okButton.setOnClickListener(v12 -> {
                userViewModel.setDeleteUserObj(loginUserId);

                psDialogMsg.cancel();
            });

            psDialogMsg.cancelButton.setOnClickListener(v1 -> psDialogMsg.cancel());

        });


        binding.get().paidAdViewAllTextView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, Constants.FLAGPAID,Constants.ONE, ""));

        binding.get().addItemButton.setOnClickListener(v -> {

            Utils.navigateOnUserVerificationActivity(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController, new Utils.NavigateOnUserVerificationActivityCallback() {
                @Override
                public void onSuccess() {
                    navigationController.navigateToItemEntryActivity(getActivity(), Constants.ADD_NEW_ITEM, recentItemViewModel.locationId, recentItemViewModel.locationName);
                }
            });

        });

        binding.get().ratingBarInformation.setOnClickListener(v -> navigationController.navigateToRatingList(ProfileFragment.this.getActivity(),binding.get().getUser().userId));
        binding.get().ratingBarInformation.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                navigationController.navigateToRatingList(ProfileFragment.this.getActivity(),binding.get().getUser().userId);
            }
            return true;
        });

        getIntentData();
    }

    @Override
    protected void initViewModels() {
        itemViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemViewModel.class);
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);
        itemPaidHistoryViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemPaidHistoryViewModel.class);
        disabledViewModel = new ViewModelProvider(this, viewModelFactory).get(DisabledViewModel.class);
        rejectedViewModel = new ViewModelProvider(this, viewModelFactory).get(RejectedViewModel.class);
        pendingViewModel = new ViewModelProvider(this, viewModelFactory).get(PendingViewModel.class);
        recentItemViewModel = new ViewModelProvider(this, viewModelFactory).get(RecentItemViewModel.class);

    }

    @Override
    protected void initAdapters() {

        ItemHorizontalListAdapter nvAdapter = new ItemHorizontalListAdapter(dataBindingComponent, item -> navigationController.navigateToItemDetailActivity(ProfileFragment.this.getActivity(), item.id), this);
        this.adapter = new AutoClearedValue<>(this, nvAdapter);
        binding.get().approvedListingRecyclerView.setAdapter(nvAdapter);

        ItemHorizontalListAdapter pendingAdapter = new ItemHorizontalListAdapter(dataBindingComponent, item -> navigationController.navigateToItemDetailActivity(ProfileFragment.this.getActivity(), item.id), this);
        this.pendingAdapter = new AutoClearedValue<>(this, pendingAdapter);
        binding.get().pendingRecyclerView.setAdapter(pendingAdapter);

        ItemHorizontalListAdapter rejectedAdapter = new ItemHorizontalListAdapter(dataBindingComponent, item -> navigationController.navigateToItemDetailActivity(ProfileFragment.this.getActivity(), item.id), this);
        this.rejectedAdapter = new AutoClearedValue<>(this, rejectedAdapter);
        binding.get().rejectedRecyclerView.setAdapter(rejectedAdapter);

        ItemHorizontalListAdapter disabledAdapter = new ItemHorizontalListAdapter(dataBindingComponent, item -> navigationController.navigateToItemDetailActivity(ProfileFragment.this.getActivity(), item.id), this);
        this.disabledAdapter = new AutoClearedValue<>(this, disabledAdapter);
        binding.get().disabledRecyclerView.setAdapter(disabledAdapter);


        ItemPromoteHorizontalListAdapter itemPromoteAdapter = new ItemPromoteHorizontalListAdapter(dataBindingComponent, itemPaidHistory -> navigationController.navigateToItemDetailActivity(ProfileFragment.this.getActivity(), itemPaidHistory.item.id), this);
        this.itemPromoteHorizontalListAdapter = new AutoClearedValue<>(this, itemPromoteAdapter);
        binding.get().userPaidItemRecyclerView.setAdapter(itemPromoteAdapter);

    }

    @Override
    protected void initData() {

        userViewModel.getLoginUser().observe(this, data -> {

            if (data != null) {

                if (data.size() > 0) {
                    userViewModel.user = data.get(0).user;
                }
            }

        });

        //User
        userViewModel.setUserObj(loginUserId);
        userViewModel.getUserData().observe(this, new Observer<Resource<User>>() {
            @Override
            public void onChanged(Resource<User> listResource) {

                if (listResource != null) {

                    Utils.psLog("Got Data" + listResource.message + listResource.toString());

                    switch (listResource.status) {
                        case LOADING:
                            // Loading State
                            // Data are from Local DB

                            if (listResource.data != null) {
                                //fadeIn Animation
                                ProfileFragment.this.fadeIn(binding.get().getRoot());

                                binding.get().setUser(listResource.data);
                                Utils.psLog("Photo : " + listResource.data.userProfilePhoto);

                                ProfileFragment.this.replaceUserData(listResource.data);


                            }

                            break;
                        case SUCCESS:
                            // Success State
                            // Data are from Server

                            if (listResource.data != null) {

                                //fadeIn Animation
                                //fadeIn(binding.get().getRoot());

                                binding.get().setUser(listResource.data);
                                Utils.psLog("Photo : " + listResource.data.userProfilePhoto);

                                ProfileFragment.this.replaceUserData(listResource.data);
                            }

                            break;
                        case ERROR:
                            // Error State

                            psDialogMsg.showErrorDialog(listResource.message, ProfileFragment.this.getString(R.string.app__ok));
                            psDialogMsg.show();

                            userViewModel.isLoading = false;

                            break;
                        default:
                            // Default
                            userViewModel.isLoading = false;

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                }

                // we don't need any null checks here for the SubCategoryAdapter since LiveData guarantees that
                // it won't call us if fragment is stopped or not started.
                if (listResource != null && listResource.data != null) {
                    Utils.psLog("Got Data");


                } else {
                    //noinspection Constant Conditions
                    Utils.psLog("Empty Data");

                }
            }
        });

        //delete user
        userViewModel.getDeleteUserStatus().observe(this, result -> {

            if (result != null) {
                switch (result.status) {
                    case SUCCESS:

                        //add offer text
                        Toast.makeText(getContext(), "Success Delete user", Toast.LENGTH_SHORT).show();

                        logout();

                        break;

                    case ERROR:
                        Toast.makeText(getContext(), "Fail Delete this user", Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        });

        //Approved Item Listing
        itemViewModel.holder.userId = loginUserId;
        itemViewModel.holder.status = Constants.ONE;

        itemViewModel.setItemListByKeyObj(Utils.checkUserId(loginUserId),
                String.valueOf(Config.LOGIN_USER_APPROVED_ITEM_COUNT), Constants.ZERO, itemViewModel.holder);

        itemViewModel.getItemListByKeyData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                hideApprovedList(false);
                                itemReplaceData(listResource.data);
                            }else {
                                hideApprovedList(true);
                            }
                            itemViewModel.setLoadingState(false);
                        }

                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                itemReplaceData(listResource.data);
                            }

                        }

                        break;

                    case ERROR:

                        hideApprovedList(true);
                        itemViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        //end

        //pending listing
        pendingViewModel.holder.userId = loginUserId;
        pendingViewModel.holder.status = Constants.ZERO;

        pendingViewModel.setItemListByKeyObj(Utils.checkUserId(loginUserId),
                String.valueOf(Config.LOGIN_USER_PENDING_ITEM_COUNT), Constants.ZERO, pendingViewModel.holder);

        pendingViewModel.getItemListByKeyData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                hidePendingList(false);
                                replacePendingListData(listResource.data);
                            }else {
                                hidePendingList(true);
                            }
                            pendingViewModel.setLoadingState(false);
                        }

                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replacePendingListData(listResource.data);
                            }

                        }

                        break;

                    case ERROR:

                        hidePendingList(true);
                        pendingViewModel.setLoadingState(false);
                        break;
                }
            }
        });
        // end

        //rejected listing
        rejectedViewModel.holder.userId = loginUserId;
        rejectedViewModel.holder.status = Constants.THREE;

        rejectedViewModel.setItemListByKeyObj(Utils.checkUserId(loginUserId),
                String.valueOf(Config.LOGIN_USER_REJECTED_ITEM_COUNT), Constants.ZERO, rejectedViewModel.holder);

        rejectedViewModel.getItemListByKeyData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                hideRejectedList(false);
                                replaceRejectedListData(listResource.data);
                            }else{
                                hideRejectedList(true);
                            }
                            rejectedViewModel.setLoadingState(false);
                        }

                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replaceRejectedListData(listResource.data);
                            }
                        }

                        break;

                    case ERROR:

                        hideRejectedList(true);
                        rejectedViewModel.setLoadingState(false);
                        break;
                }
            }
        });
        // end

        //disabled listing

        disabledViewModel.holder.userId = loginUserId;
        disabledViewModel.holder.status = Constants.TWO;

        disabledViewModel.setItemListByKeyObj(Utils.checkUserId(loginUserId),
                String.valueOf(Config.LOGIN_USER_DISABLED_ITEM_COUNT), Constants.ZERO, disabledViewModel.holder);

        disabledViewModel.getItemListByKeyData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                hideDisabledList(false);
                                replaceDisabledListData(listResource.data);
                            }else {
                                hideDisabledList(true);
                            }
                            disabledViewModel.setLoadingState(false);
                        }

                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replaceDisabledListData(listResource.data);
                            }

                        }

                        break;

                    case ERROR:

                        hideDisabledList(true);
                        disabledViewModel.setLoadingState(false);
                        break;
                }
            }
        });
        // end

        itemViewModel.getItemListFromDbByKeyData().observe(this, listResource -> {

            if (listResource != null) {

                itemReplaceData(listResource);

            }
        });

        // Get Paid Item History List
        itemPaidHistoryViewModel.setPaidItemHistory(Utils.checkUserId(loginUserId), String.valueOf(Config.PAID_ITEM_COUNT), String.valueOf(itemPaidHistoryViewModel.offset));

        itemPaidHistoryViewModel.getPaidItemHistory().observe(this, result -> {

            if (result != null) {
                switch (result.status) {
                    case SUCCESS:

                        if (result.data != null) {
                            hidePaidItemList(false);
                            replacePaidItemHistoryList(result.data);
                        }else {
                            hidePaidItemList(true);
                        }
                        itemPaidHistoryViewModel.setLoadingState(false);
                        break;

                    case LOADING:

                        if (result.data != null) {
                            replacePaidItemHistoryList(result.data);
                        }

                        break;
                    case ERROR:
                        hidePaidItemList(true);
                        itemPaidHistoryViewModel.setLoadingState(false);
                        break;

                        default:
                            break;
                }
            }

        });

    }

    private void hidePaidItemList(boolean isTrue) {
        if(isTrue) {
            binding.get().paidAdTextView.setVisibility(View.GONE);
            binding.get().paidAdViewAllTextView.setVisibility(View.GONE);
            binding.get().userPaidItemRecyclerView.setVisibility(View.GONE);
        }else {
            binding.get().paidAdTextView.setVisibility(View.VISIBLE);
            binding.get().paidAdViewAllTextView.setVisibility(View.VISIBLE);
            binding.get().userPaidItemRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void hideApprovedList(boolean isTrue) {
        if(isTrue) {
            binding.get().approvedListingRecyclerView.setVisibility(View.GONE);
            binding.get().historyTextView.setVisibility(View.GONE);
            binding.get().seeAllTextView.setVisibility(View.GONE);
        }else {
            binding.get().approvedListingRecyclerView.setVisibility(View.VISIBLE);
            binding.get().historyTextView.setVisibility(View.VISIBLE);
            binding.get().seeAllTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hidePendingList(boolean isTrue) {
        if(isTrue) {
            binding.get().pendingTitleTextView.setVisibility(View.GONE);
            binding.get().pendingSeeAllTextView.setVisibility(View.GONE);
            binding.get().pendingRecyclerView.setVisibility(View.GONE);
        }else {
            binding.get().pendingTitleTextView.setVisibility(View.VISIBLE);
            binding.get().pendingSeeAllTextView.setVisibility(View.VISIBLE);
            binding.get().pendingRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void hideRejectedList(boolean isTrue) {
        if(isTrue) {
            binding.get().rejectedTitleTextView.setVisibility(View.GONE);
            binding.get().rejectedSeeAllTextView.setVisibility(View.GONE);
            binding.get().rejectedRecyclerView.setVisibility(View.GONE);
        }else {
            binding.get().rejectedTitleTextView.setVisibility(View.VISIBLE);
            binding.get().rejectedSeeAllTextView.setVisibility(View.VISIBLE);
            binding.get().rejectedRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void hideDisabledList(boolean isTrue) {
        if(isTrue) {
            binding.get().disabledTitleTextView.setVisibility(View.GONE);
            binding.get().disabledSeeAllTextView.setVisibility(View.GONE);
            binding.get().disabledRecyclerView.setVisibility(View.GONE);
        }else{
            binding.get().disabledTitleTextView.setVisibility(View.VISIBLE);
            binding.get().disabledSeeAllTextView.setVisibility(View.VISIBLE);
            binding.get().disabledRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void getIntentData() {

        if (getActivity() != null) {

            recentItemViewModel.locationId = selected_location_id;
            recentItemViewModel.locationName = selected_location_name;
            recentItemViewModel.locationLat = selectedLat;
            recentItemViewModel.locationLng = selectedLng;

            recentItemViewModel.recentItemParameterHolder.location_id = recentItemViewModel.locationId;

        }
    }

    private void logout() {

        if ((MainActivity) getActivity() != null) {
            ((MainActivity) getActivity()).hideBottomNavigation();

            userViewModel.deleteUserLogin(userViewModel.user).observe(this, status -> {
                if (status != null) {
//                    this.menuId = 0;

                    ((MainActivity) getActivity()).setToolbarText(((MainActivity) getActivity()).binding.toolbar, getString(R.string.app__app_name));

                    ((MainActivity) getActivity()).isLogout = true;

                    FacebookSdk.sdkInitialize(((MainActivity) getActivity()).getApplicationContext());
                    LoginManager.getInstance().logOut();

                    if (getFragmentManager() != null) {
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                }
            });
        }
    }

    @Override
    public void onDispatched() {

    }


    @SuppressLint("ClickableViewAccessibility")
    private void replaceUserData(User user) {

        binding.get().editTextView.setText(binding.get().editTextView.getText().toString());
//        binding.get().userNotificatinTextView.setText(binding.get().userNotificatinTextView.getText().toString());
//        binding.get().userHistoryTextView.setText(binding.get().userHistoryTextView.getText().toString());
        binding.get().favouriteTextView.setText(binding.get().favouriteTextView.getText().toString());
        binding.get().moreTextView.setText(binding.get().moreTextView.getText().toString());
        binding.get().historyTextView.setText(binding.get().historyTextView.getText().toString());
        binding.get().seeAllTextView.setText(binding.get().seeAllTextView.getText().toString());
        binding.get().joinedDateTitleTextView.setText(binding.get().joinedDateTitleTextView.getText().toString());

        String strCurrentDate = user.addedDate;
        java.text.SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date inputDate ;
        try {
            inputDate = inputFormat.parse(strCurrentDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MM yyyy HH:mm:ss", Locale.US);
            if(inputDate != null) {
                String outputDateString = outputFormat.format(inputDate.getTime());
                binding.get().joinedDateTextView.setText(outputDateString);// user.addedDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        binding.get().nameTextView.setText(user.userName);
        binding.get().overAllRatingTextView.setText(user.overallRating);
        binding.get().ratingBarInformation.setRating(user.ratingDetails.totalRatingValue);

        String ratingCount = "( " + user.ratingCount + " )";
        String followerCount = getString(R.string.profile__followers) + " ( " + user.followerCount + " )";
        String followingCount = getString(R.string.profile__following) + " ( " + user.followingCount + " )";

        binding.get().ratingCountTextView.setText(ratingCount);
        binding.get().followUserTextView.setText(followerCount);
        binding.get().followingUserTextView.setText(followingCount);

        if (user.emailVerify.equals("1")) {
            binding.get().emailImage.setVisibility(View.VISIBLE);
        } else {
            binding.get().emailImage.setVisibility(View.GONE);
        }

        if (user.facebookVerify.equals("1")) {
            binding.get().facebookImage.setVisibility(View.VISIBLE);
        } else {
            binding.get().facebookImage.setVisibility(View.GONE);
        }

        if (user.phoneVerify.equals("1")) {
            binding.get().phoneImage.setVisibility(View.VISIBLE);
        } else {
            binding.get().phoneImage.setVisibility(View.GONE);
        }

        if (user.googleVerify.equals("1")) {
            binding.get().googleImage.setVisibility(View.VISIBLE);
        } else {
            binding.get().googleImage.setVisibility(View.GONE);
        }


    }

    private void itemReplaceData(List<Item> itemList) {
        adapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replacePendingListData(List<Item> itemList) {
        pendingAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replaceRejectedListData(List<Item> itemList) {
        rejectedAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replaceDisabledListData(List<Item> itemList) {
        disabledAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }
    private void replacePaidItemHistoryList(List<ItemPaidHistory> itemPaidHistories) {
        this.itemPromoteHorizontalListAdapter.get().replace(itemPaidHistories);
        binding.get().executePendingBindings();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE__PROFILE_FRAGMENT
                && resultCode == Constants.RESULT_CODE__LOGOUT_ACTIVATED) {
            if (getActivity() instanceof MainActivity) {
                userViewModel.setDeleteUserObj(loginUserId);
                ((MainActivity) getActivity()).setToolbarText(((MainActivity) getActivity()).binding.toolbar, getString(R.string.profile__title));
                //navigationController.navigateToUserFBRegister((MainActivity) getActivity());

               // navigationController.navigateToUserLogin((MainActivity) getActivity());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        userViewModel.setUserObj(loginUserId);
        itemViewModel.setItemListByKeyObj(Utils.checkUserId(loginUserId), String.valueOf(Config.LOGIN_USER_APPROVED_ITEM_COUNT), Constants.ZERO, itemViewModel.holder);

    }
}
