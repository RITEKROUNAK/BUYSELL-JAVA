package com.panaceasoft.psbuyandsell.viewmodel.blockuser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.panaceasoft.psbuyandsell.repository.blockuser.BlockUserRepository;
import com.panaceasoft.psbuyandsell.utils.AbsentLiveData;
import com.panaceasoft.psbuyandsell.viewmodel.common.PSViewModel;
import com.panaceasoft.psbuyandsell.viewobject.BlockUser;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;

public class BlockUserViewModel  extends PSViewModel {

    private final LiveData<Resource<List<BlockUser>>> blockUserData;
    private MutableLiveData<BlockUserViewModel.TmpDataHolder> blockUserObj = new MutableLiveData<>();


    private final LiveData<Resource<Boolean>> nextPageblockUserData;
    private MutableLiveData<BlockUserViewModel.TmpDataHolder> nextPageblockUserObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> unblockUserStatusData;
    private MutableLiveData<BlockUserViewModel.UnblockUserTmpDataHolder> unblockUserStatusObj = new MutableLiveData<>();



    @Inject
    BlockUserViewModel(BlockUserRepository repository) {
        blockUserData = Transformations.switchMap(blockUserObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getBlockUserList(obj.limit, obj.offset, obj.loginUserId);
        });

        nextPageblockUserData = Transformations.switchMap(nextPageblockUserObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNextPageBlockUserList(obj.limit, obj.offset, obj.loginUserId);

        });

        unblockUserStatusData = Transformations.switchMap(unblockUserStatusObj,obj ->{
            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.unblockUser(obj.loginUserId,obj.userId);
        });
    }

        public void setBlockUserObj(String limit, String offset,String loginUserId) {
            BlockUserViewModel.TmpDataHolder tmpDataHolder = new BlockUserViewModel.TmpDataHolder(limit, offset,loginUserId);

            this.blockUserObj.setValue(tmpDataHolder);
        }

        public LiveData<Resource<List<BlockUser>>> getBlockUserData(){return blockUserData ;}


        public void setNextPageBlockUserObj(String limit, String offset,String loginUserId) {
            BlockUserViewModel.TmpDataHolder tmpDataHolder = new BlockUserViewModel.TmpDataHolder(limit, offset,loginUserId);

            this.nextPageblockUserObj.setValue(tmpDataHolder);
        }

        public LiveData<Resource<Boolean>> getNextPageBlockUserData() {
            return nextPageblockUserData;
        }

        public  void  setUnblockUserStatusObj(String loginUserId,String userId){
        UnblockUserTmpDataHolder tmpDataHolder = new UnblockUserTmpDataHolder(loginUserId,userId);
        this.unblockUserStatusObj.setValue(tmpDataHolder);
        }

    public LiveData<Resource<Boolean>> getUnblockUserStatusData() {
        return unblockUserStatusData;
    }

        class UnblockUserTmpDataHolder{
        private String loginUserId,userId;

        public UnblockUserTmpDataHolder(String loginUserId,String userId){
            this.loginUserId =loginUserId;
            this.userId = userId;
        }
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
