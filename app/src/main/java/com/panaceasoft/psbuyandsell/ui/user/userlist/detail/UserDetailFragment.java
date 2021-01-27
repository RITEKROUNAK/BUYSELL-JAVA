package com.panaceasoft.psbuyandsell.ui.user.userlist.detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.MainActivity;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentUserDetailBinding;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.ui.item.adapter.ItemVerticalListAdapter;
import com.panaceasoft.psbuyandsell.ui.user.ProfileFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.PSDialogMsg;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewmodel.item.ItemViewModel;
import com.panaceasoft.psbuyandsell.viewmodel.user.UserViewModel;
import com.panaceasoft.psbuyandsell.viewobject.Item;
import com.panaceasoft.psbuyandsell.viewobject.User;
import com.panaceasoft.psbuyandsell.viewobject.common.Status;

import java.util.List;

public class UserDetailFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {


    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemViewModel itemViewModel;
    private UserViewModel userViewModel;
    public PSDialogMsg psDialogMsg;
    private static final int REQUEST_CALL = 1;


    @VisibleForTesting
    private AutoClearedValue<FragmentUserDetailBinding> binding;
    private AutoClearedValue<ItemVerticalListAdapter> adapter;

    //endregion

    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentUserDetailBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_detail, container, false, dataBindingComponent);


        binding = new AutoClearedValue<>(this, dataBinding);


        return binding.get().getRoot();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initUIAndActions() {


        if (getActivity() instanceof MainActivity) {
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.global__primary));
            ((MainActivity) getActivity()).updateMenuIconWhite();

        }

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        binding.get().userOwnItemList.setNestedScrollingEnabled(false);
        binding.get().seeAllTextView.setOnClickListener(view ->
                navigationController.navigateToItemListActivity(getActivity(), userViewModel.otherUserId,Constants.FLAGNOPAID, Constants.ONE, getString(R.string.profile__listing)));

        binding.get().followBtn.setOnClickListener(v -> userViewModel.setUserFollowPostObj(loginUserId, userViewModel.otherUserId));

        binding.get().phoneTextView.setOnClickListener(v -> {
            String number = binding.get().phoneTextView.getText().toString();
                if(!(number.trim().isEmpty() || number.trim().equals("-"))){
                    Utils.callPhone(this,number);
                }
        });

        binding.get().ratingBarInformation.setOnClickListener(v -> navigationController.navigateToRatingList(UserDetailFragment.this.getActivity(),binding.get().getUser().userId));
        binding.get().ratingBarInformation.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                navigationController.navigateToRatingList(UserDetailFragment.this.getActivity(),binding.get().getUser().userId);
            }
            return true;
        });

        binding.get().blockUserTextView.setOnClickListener(v -> {
            psDialogMsg.showConfirmDialog(UserDetailFragment.this.getString(R.string.item_detail__confirm_block_user), UserDetailFragment.this.getString(R.string.app__ok), UserDetailFragment.this.getString(R.string.message__cancel_close));
            psDialogMsg.show();

            psDialogMsg.okButton.setOnClickListener(v12 -> {
                itemViewModel.setBlockUserStatusObj(loginUserId, itemViewModel.holder.userId);

                psDialogMsg.cancel();

                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            psDialogMsg.cancelButton.setOnClickListener(v1 -> psDialogMsg.cancel());

            });
        }


    @Override
    protected void initViewModels() {
        itemViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemViewModel.class);
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);
    }

    @Override
    protected void initAdapters() {

        ItemVerticalListAdapter nvAdapter = new ItemVerticalListAdapter(dataBindingComponent,
                item -> navigationController.navigateToItemDetailActivity(UserDetailFragment.this.getActivity(), item.id), this);
        this.adapter = new AutoClearedValue<>(this, nvAdapter);
        binding.get().userOwnItemList.setAdapter(nvAdapter);
    }

    @Override
    protected void initData() {

        getBlockUserStatus();

        try {
            if (getActivity() != null) {
                if (getActivity().getIntent() != null) {
                    if (getActivity().getIntent().getExtras() != null) {
                        userViewModel.otherUserId = getActivity().getIntent().getExtras().getString(Constants.OTHER_USER_ID);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userViewModel.otherUserId != null) {
            if (userViewModel.otherUserId.equals(loginUserId) || loginUserId.equals("")) {

                binding.get().followBtn.setVisibility(View.GONE);
            } else {

                binding.get().followBtn.setVisibility(View.VISIBLE);
            }
        }

        if (userViewModel.otherUserId != null) {
            if (userViewModel.otherUserId.equals(loginUserId) || loginUserId.equals("")) {

                binding.get().blockUserTextView.setVisibility(View.GONE);
            } else {

                binding.get().blockUserTextView.setVisibility(View.VISIBLE);
            }
        }

        //User
        userViewModel.getOtherUser(loginUserId, userViewModel.otherUserId).observe(this, listResource -> {

            if (listResource != null) {

                Utils.psLog("Got Data" + listResource.message + listResource.toString());

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        if (listResource.data != null) {
                            //fadeIn Animation
                            fadeIn(binding.get().getRoot());

                            binding.get().setUser(listResource.data);
                            Utils.psLog("Photo : " + listResource.data.userProfilePhoto);

                            replaceUserData(listResource.data);

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

                            replaceUserData(listResource.data);

                            if (listResource.data.isFollowed != null) {

                                userViewModel.isFollowed = listResource.data.isFollowed;

                            }

                        }

                        break;
                    case ERROR:
                        // Error State

                        psDialogMsg.showErrorDialog(listResource.message, getString(R.string.app__ok));
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
        });

        //Item
        itemViewModel.holder.userId = userViewModel.otherUserId;

        itemViewModel.setItemListByKeyObj(loginUserId, String.valueOf(Config.LOGIN_USER_APPROVED_ITEM_COUNT), Constants.ZERO, itemViewModel.holder);

        itemViewModel.getItemListByKeyData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                itemReplaceData(listResource.data);

                            }

                        }

                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                itemReplaceData(listResource.data);
                            }
                        }

                        userViewModel.setLoadingState(false);

                        break;

                    case ERROR:
                        break;
                }
            }
        });

        //For user follow post
        userViewModel.getUserFollowPostData().observe(this, result -> {
            if (result != null) {
                if (result.status == Status.SUCCESS) {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                        userViewModel.setLoadingState(false);
                        userViewModel.getOtherUser(loginUserId, userViewModel.otherUserId);
                    }

                } else if (result.status == Status.ERROR) {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                        userViewModel.setLoadingState(false);
                    }
                }
            }
        });
    }

    @Override
    public void onDispatched() {

    }

    private void getBlockUserStatus() {

        itemViewModel.getBlockUserStatusData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {
                    case SUCCESS:

                        //add offer text
                        Toast.makeText(getContext(), "Success Block this user", Toast.LENGTH_SHORT).show();

                        break;

                    case ERROR:
                        Toast.makeText(getContext(), "Fail Block this user", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void replaceUserData(User user) {

        binding.get().joinedDateTextView.setText(user.addedDate);
        binding.get().nameTextView.setText(user.userName);
        binding.get().overAllRatingTextView.setText(user.overallRating);
        binding.get().ratingBarInformation.setRating(user.ratingDetails.totalRatingValue);
        String ratingCount = "( " + user.ratingCount + " )";
        binding.get().ratingCountTextView.setText(ratingCount);
        binding.get().phoneTextView.setText(user.userPhone.isEmpty() ? " - " : user.userPhone);

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
        if(user.isShowPhone.equals("1")) {
            binding.get().phoneTextView.setVisibility(View.VISIBLE);
            binding.get().phoneImageView.setVisibility(View.VISIBLE);
        } else {
            binding.get().phoneTextView.setVisibility(View.INVISIBLE);
            binding.get().phoneImageView.setVisibility(View.INVISIBLE);
        }

    }

    private void itemReplaceData(List<Item> itemList) {
        adapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

}
