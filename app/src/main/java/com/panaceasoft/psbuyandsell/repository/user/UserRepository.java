package com.panaceasoft.psbuyandsell.repository.user;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.panaceasoft.psbuyandsell.AppExecutors;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.api.ApiResponse;
import com.panaceasoft.psbuyandsell.api.PSApiService;
import com.panaceasoft.psbuyandsell.db.PSCoreDb;
import com.panaceasoft.psbuyandsell.db.UserDao;
import com.panaceasoft.psbuyandsell.repository.common.NetworkBoundResource;
import com.panaceasoft.psbuyandsell.repository.common.PSRepository;
import com.panaceasoft.psbuyandsell.utils.AbsentLiveData;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Utils;
import com.panaceasoft.psbuyandsell.viewobject.ApiStatus;
import com.panaceasoft.psbuyandsell.viewobject.User;
import com.panaceasoft.psbuyandsell.viewobject.UserLogin;
import com.panaceasoft.psbuyandsell.viewobject.UserMap;
import com.panaceasoft.psbuyandsell.viewobject.common.Resource;
import com.panaceasoft.psbuyandsell.viewobject.holder.UserParameterHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Created by Panacea-Soft on 11/17/17.
 * Contact Email : teamps.is.cool@gmail.com
 */

@Singleton
public class UserRepository extends PSRepository {


    //region Variables

    private final UserDao userDao;
    private String isSelected;

    //endregion


    //region Constructor

    @Inject
    UserRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, UserDao userDao) {
        super(psApiService, appExecutors, db);

        this.userDao = userDao;
    }

    //endregion


    //region User Repository Functions for ViewModel

    /**
     * Function to login
     *
     * @param apiKey   APIKey to access web services
     * @param email    User Email
     * @param password User Password
     * @return Login User Data
     */
    public LiveData<Resource<UserLogin>> doLogin(String apiKey, String email, String password, String deviceToken) {

        Utils.psLog("Do Login : " + email + " & " + password);

        return new NetworkBoundResource<UserLogin, User>(appExecutors) {

            String userId = "";

            @Override
            protected void saveCallResult(@NonNull User user) {
                Utils.psLog("SaveCallResult of doLogin.");

                try {
                    db.runInTransaction(() -> {
                        // set User id
                        userId = user.userId;

                        // clear user login data
                        userDao.deleteUserLogin();

                        // insert user data
                        userDao.insert(user);

                        // insert user login
                        UserLogin userLogin = new UserLogin(userId, true, user);
                        userDao.insert(userLogin);

                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable UserLogin data) {
                // for user login, always should fetch
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<UserLogin> loadFromDb() {
                Utils.psLog("Load User Login data from database.");
                if (userId == null || userId.equals("")) {
                    return AbsentLiveData.create();
                }

                return userDao.getUserLoginData(userId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                Utils.psLog("Call API Service to do user login.");
                return psApiService.postUserLogin(apiKey, email, password, deviceToken);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed in doLogin.");
            }
        }.asLiveData();
    }

    /**
     * Function to get User Login Data.
     *
     * @return UserLogin Data.
     */
    public LiveData<List<UserLogin>> getLoginUser() {

        Utils.psLog("Get Login User");

        return userDao.getUserLoginData();
    }

    /**
     * Function to get Login User
     *
     * @param apiKey APIKey to access to API Service
     * @param userId User Id to fetch
     * @return Login User
     */
    public LiveData<Resource<User>> getLoginUser(String apiKey, String userId) {

        return new NetworkBoundResource<User, User>(appExecutors) {


            @Override
            protected void saveCallResult(@NonNull User user) {
                Utils.psLog("SaveCallResult of doLogin.");

                try {
                    db.runInTransaction(() -> {
                        // clear user login data
                        userDao.deleteUserLogin();

                        // insert user data
                        userDao.insert(user);

                        // insert user login
                        UserLogin userLogin = new UserLogin(userId, true, user);
                        userDao.insert(userLogin);

                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable User data) {
                // for user login, always should fetch
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<User> loadFromDb() {
                Utils.psLog("Load User Login data from database.");

                return userDao.getUserData(userId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                Utils.psLog("Call API Service to do user login.");
                return psApiService.getUserDetail(apiKey, Constants.EMPTY_STRING,
                        Constants.EMPTY_STRING, Constants.EMPTY_STRING, Constants.EMPTY_STRING, userId);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed in doLogin.");
            }
        }.asLiveData();
    }

    /**
     * Function to get User
     *
     * @param apiKey APIKey to access to API Service
     * @param userId User Id to fetch
     * @return Login User
     */

    public LiveData<Resource<User>> getOtherUser(String apiKey, String userId, String otherUserId) {

        return new NetworkBoundResource<User, User>(appExecutors) {


            @Override
            protected void saveCallResult(@NonNull User user) {
                Utils.psLog("SaveCallResult of doLogin.");

                try {
                    db.runInTransaction(() -> {
                        // clear user login data
                        userDao.deleteUser();

                        // insert user data
                        userDao.insert(user);
                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable User data) {
                // for user login, always should fetch
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<User> loadFromDb() {
                Utils.psLog("Load User Login data from database.");

                return userDao.getUserData(otherUserId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                Utils.psLog("Call API Service to do user login.");
                return psApiService.getUserDetail(apiKey, Constants.EMPTY_STRING,
                        Constants.EMPTY_STRING, Constants.EMPTY_STRING, userId, otherUserId);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed in doLogin.");
            }
        }.asLiveData();
    }

    /**
     * Function to register new user.
     *
     * @param apiKey   APIKey to access web services
     * @param userName User Name
     * @param email    User Email
     * @param password User Password
     * @return Login User Data
     */
    public LiveData<Resource<User>> registerUser(String apiKey, String userName, String email, String password, String deviceToken) {

        final MutableLiveData<Resource<User>> statusLiveData = new MutableLiveData<>(); // To update the status to the listener


        appExecutors.networkIO().execute(() -> {

            try {

                // Call the API Service
                Response<User> response = psApiService.postUser(apiKey, userName, email, password, deviceToken).execute();


                // Wrap with APIResponse Class
                ApiResponse<User> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

                    try {
                        db.runInTransaction(() -> {
                            if (apiResponse.body != null) {

                                // clear user login data
                                userDao.deleteUserLogin();

                                // insert user data
                                userDao.insert(apiResponse.body);

                                statusLiveData.postValue(Resource.success(response.body()));
                            }

                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), null));
            }

        });
        return statusLiveData;

    }

    /**
     * Function to register FB user.
     *
     * @param apiKey   APIKey to access web services
     * @param userName User Name
     * @param email    User Email
     * @param imageUrl Image URL
     * @return Login User Data
     */
    public LiveData<Resource<UserLogin>> registerFBUser(String apiKey, String fbId, String userName, String email, String imageUrl, String deviceToken) {

        final MutableLiveData<Resource<UserLogin>> statusLiveData = new MutableLiveData<>(); // To update the status to the listener

        appExecutors.networkIO().execute(() -> {

            try {

                // Call the API Service
                Response<User> response = psApiService
                        .postFBUser(apiKey, fbId, userName, email, imageUrl, deviceToken).execute();


                // Wrap with APIResponse Class
                ApiResponse<User> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

                    try {
                        db.runInTransaction(() -> {

                            if (apiResponse.body != null) {
                                // set User id
                                String userId = apiResponse.body.userId;

                                // clear user login data
                                userDao.deleteUserLogin();

                                // insert user data
                                userDao.insert(apiResponse.body);

                                // insert user login
                                UserLogin userLogin = new UserLogin(userId, true, apiResponse.body);
                                userDao.insert(userLogin);

                                statusLiveData.postValue(Resource.success(userLogin));
                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), null));
            }

        });
        return statusLiveData;

    }

    public LiveData<Resource<UserLogin>> postPhoneLogin(String apiKey, String phoneId, String userName, String userPhone, String deviceToken) {

        final MutableLiveData<Resource<UserLogin>> statusLiveData = new MutableLiveData<>(); // To update the status to the listener

        appExecutors.networkIO().execute(() -> {

            try {

                // Call the API Service
                Response<User> response = psApiService
                        .postPhoneLogin(apiKey, phoneId, userName, userPhone, deviceToken).execute();


                // Wrap with APIResponse Class
                ApiResponse<User> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

                    try {
                        db.beginTransaction();

                        if (apiResponse.body != null) {
                            // set User id
                            String userId = apiResponse.body.userId;

                            // clear user login data
                            userDao.deleteUserLogin();

                            // insert user data
                            userDao.insert(apiResponse.body);

                            // insert user login
                            UserLogin userLogin = new UserLogin(userId, true, apiResponse.body);
                            userDao.insert(userLogin);

                            db.setTransactionSuccessful();

                            statusLiveData.postValue(Resource.success(userLogin));
                        }

                    } catch (NullPointerException ne) {
                        Utils.psErrorLog("Null Pointer Exception : ", ne);
                    } catch (Exception e) {
                        Utils.psErrorLog("Exception : ", e);
                    } finally {
                        db.endTransaction();
                    }

                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), null));
            }

        });
        return statusLiveData;

    }

    public LiveData<Resource<UserLogin>> postGoogleLogin(String apiKey, String googleId, String userName, String userEmail, String profilePhotoUrl, String deviceToken) {

        final MutableLiveData<Resource<UserLogin>> statusLiveData = new MutableLiveData<>(); // To update the status to the listener

        appExecutors.networkIO().execute(() -> {

            try {

                // Call the API Service
                Response<User> response = psApiService
                        .postGoogleLogin(apiKey, googleId, userName, userEmail, profilePhotoUrl, deviceToken).execute();


                // Wrap with APIResponse Class
                ApiResponse<User> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

                    try {
                        db.beginTransaction();

                        if (apiResponse.body != null) {
                            // set User id
                            String userId = apiResponse.body.userId;

                            // clear user login data
                            userDao.deleteUserLogin();

                            // insert user data
                            userDao.insert(apiResponse.body);

                            // insert user login
                            UserLogin userLogin = new UserLogin(userId, true, apiResponse.body);
                            userDao.insert(userLogin);

                            db.setTransactionSuccessful();

                            statusLiveData.postValue(Resource.success(userLogin));
                        }

                    } catch (NullPointerException ne) {
                        Utils.psErrorLog("Null Pointer Exception : ", ne);
                    } catch (Exception e) {
                        Utils.psErrorLog("Exception : ", e);
                    } finally {
                        db.endTransaction();
                    }

                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), null));
            }

        });
        return statusLiveData;

    }

    /**
     * Function to update user.
     *
     * @param apiKey APIKey to access web services
     * @param user   User Data to update.
     * @return Status of Request.
     */
    public LiveData<Resource<ApiStatus>> updateUser(String apiKey, User user) {

        return new NetworkBoundResource<ApiStatus, ApiStatus>(appExecutors) {

            String userId = "";
            private ApiStatus resultsDb;

            @Override
            protected void saveCallResult(@NonNull ApiStatus apiStatus) {
                Utils.psLog("SaveCallResult of update user.");

                try {
                    db.runInTransaction(() -> {
                        if (apiStatus.status.equals("success")) {

                            // set User id
                            userId = user.userId;

                            // update user data
                            userDao.update(user);

                            // update user login
                            UserLogin userLogin = new UserLogin(userId, true, user);
                            userDao.update(userLogin);

                        }
                        resultsDb = apiStatus;
                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable ApiStatus data) {
                // for user update, always should fetch
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<ApiStatus> loadFromDb() {
                if (userId == null || userId.equals("")) {
                    return AbsentLiveData.create();
                }

                return new LiveData<ApiStatus>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(resultsDb);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ApiStatus>> createCall() {
                Utils.psLog("Call API Service to update user.");
                return psApiService.putUser(apiKey, user.userId, user.userName, user.userEmail, user.userPhone, user.userAddress, user.city, user.userAboutMe, user.isShowEmail, user.isShowPhone, user.deviceToken);
            }

            @Override
            protected void onFetchFailed(int code, String message) {

                Utils.psLog("Fetch Failed (updateUser)." + message);
            }
        }.asLiveData();
    }

    /**
     * Function to request forgot password
     *
     * @param apiKey APIKey to access web services
     * @param email  User Email
     * @return Status Of request.
     */
    public LiveData<Resource<ApiStatus>> forgotPassword(String apiKey, String email) {

        return new NetworkBoundResource<ApiStatus, ApiStatus>(appExecutors) {

            private ApiStatus resultsDb;

            @Override
            protected void saveCallResult(@NonNull ApiStatus apiStatus) {

                Utils.psLog("SaveCallResult of forgotPassword");

                resultsDb = apiStatus;

            }

            @Override
            protected boolean shouldFetch(@Nullable ApiStatus data) {
                // for forgot password, always should fetch
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<ApiStatus> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                }

                return new LiveData<ApiStatus>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(resultsDb);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ApiStatus>> createCall() {
                Utils.psLog("Call API Service to Request Forgot Password.");
                return psApiService.postForgotPassword(apiKey, email);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of forgot Password.");
            }
        }.asLiveData();
    }

    /**
     * Function to Password Update
     *
     * @param apiKey      APIKey to access web services
     * @param loginUserId Current Login User Id
     * @param password    New Password
     * @return Status of Request.
     */
    public LiveData<Resource<ApiStatus>> passwordUpdate(String apiKey, String loginUserId, String password) {

        return new NetworkBoundResource<ApiStatus, ApiStatus>(appExecutors) {

            private ApiStatus resultsDb;

            @Override
            protected void saveCallResult(@NonNull ApiStatus apiStatus) {

                Utils.psLog("SaveCallResult of passwordUpdate");
                resultsDb = apiStatus;

            }

            @Override
            protected boolean shouldFetch(@Nullable ApiStatus data) {
                // for passwordUpdate, always should fetch
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<ApiStatus> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                }

                return new LiveData<ApiStatus>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(resultsDb);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ApiStatus>> createCall() {
                Utils.psLog("Call API Service to update password.");
                return psApiService.postPasswordUpdate(apiKey, loginUserId, password);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of password update.");
            }
        }.asLiveData();
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }

    private byte[] getImageData(Context context, Uri uri, String path) {
        byte[] inputData = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            InputStream iStream = null;
            try {
                iStream = context.getContentResolver().openInputStream(Objects.requireNonNull(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                inputData = getBytes(iStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {

            inputData = readBytesFromFile(path);

        }

        return inputData;

    }

    /**
     * Upload image ( Used in profile image upload now)
     *
     * @param filePath file path of selected image.
     * @param userId   user id to set image.
     * @param platform current platform ( " android " )
     * @return User
     */
    public LiveData<Resource<User>> uploadImage(Context context, String filePath, Uri uri, String userId, String platform, ContentResolver contentResolver) {

//        //Init File
//        File file = new File(filePath);
//        RequestBody requestFile =
//                RequestBody.create(MediaType.parse("multipart/form-data"), getImageData(context, uri, filePath));
//
//        // MultipartBody.Part is used to send also the actual file news_title
//        MultipartBody.Part body =
//                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        //Init File
        MultipartBody.Part body = null;
        RequestBody fullName = null;
        if (!filePath.equals("")) {
            File file = new File(filePath);
            if(Config.isCompressImage){
                byte[] compressedFile = Utils.compressImage(file, uri, contentResolver, Config.profileImageHeight, Config.profileImageWidth);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), compressedFile);

                // MultipartBody.Part is used to send also the actual file news_title
                body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            }else{
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);

                // MultipartBody.Part is used to send also the actual file news_title
                body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            }
            fullName =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), file.getName());
        }

        // add another part within the multipart request
        RequestBody platformRB =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), platform);

        RequestBody useIdRB =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), userId);

        MultipartBody.Part finalBody = body;
        RequestBody finalFullName = fullName;
        return new NetworkBoundResource<User, User>(appExecutors) {

            // Temp ResultType To Return
            private User resultsDb;
            String userId = "";

            @Override
            protected void saveCallResult(@NonNull User user) {
                Utils.psLog("SaveCallResult");

                try {
                    db.runInTransaction(() -> {

                        // set User id
                        userId = user.userId;

                        // update user data
                        userDao.update(user);

                        // update user login
                        UserLogin userLogin = new UserLogin(userId, true, user);
                        userDao.update(userLogin);

                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }

                resultsDb = user;
            }

            @Override
            protected boolean shouldFetch(@Nullable User data) {
                // Image upload should always connect to server.
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<User> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<User>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                Utils.psLog("Call API Service to upload image.");

                return psApiService.doUploadImage(Config.API_KEY, useIdRB, finalFullName, finalBody, platformRB);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of uploading image.");
            }
        }.asLiveData();
    }


    //endregion

    public LiveData<Resource<User>> getUserById(String id) {

        final MediatorLiveData<Resource<User>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<User>> apiResponse = psApiService.getUserById(Config.API_KEY, id);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> statusLiveData.postValue(Resource.successWithMsg("success", response.body)));

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }
        });

        return statusLiveData;

    }


    public LiveData<Resource<UserLogin>> verificationCodeForUser(String userId, String code) {

        final MutableLiveData<Resource<UserLogin>> statusLiveData = new MutableLiveData<>();


        appExecutors.networkIO().execute(() -> {

            try {

                // Call the API Service
                Response<User> response = psApiService.verifyEmail(Config.API_KEY, userId, code).execute();


                // Wrap with APIResponse Class
                ApiResponse<User> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

                    try {
                        db.runInTransaction(() -> {
                            if (apiResponse.body != null) {
                                // set User id
                                String user_id = apiResponse.body.userId;

                                // clear user login data
                                userDao.deleteUserLogin();

                                // insert user data
                                userDao.insert(apiResponse.body);

                                // insert user login
                                UserLogin userLogin = new UserLogin(user_id, true, apiResponse.body);
                                userDao.insert(userLogin);

                                statusLiveData.postValue(Resource.success(userLogin));
                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), null));
            }

        });
        return statusLiveData;

    }

    //region delete user post

    public LiveData<Resource<Boolean>> deletePostUser(String userId) {

        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            Response<ApiStatus> response;

            try {
                response = psApiService.deleteUser(
                        Config.API_KEY, userId).execute();

                ApiResponse<ApiStatus> apiResponse = new ApiResponse<>(response);

                if (apiResponse.isSuccessful()) {
                    statusLiveData.postValue(Resource.success(true));
                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, false));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), false));
            }

        });

        return statusLiveData;
    }

    //region logout delete user post

    public LiveData<Resource<Boolean>> logoutUser(String userId) {

        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            Response<ApiStatus> response;

            try {
                response = psApiService.logoutUser(
                        Config.API_KEY, userId).execute();

                ApiResponse<ApiStatus> apiResponse = new ApiResponse<>(response);

                if (apiResponse.isSuccessful()) {
                    statusLiveData.postValue(Resource.success(true));

                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, false));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), false));
            }

        });

        return statusLiveData;
    }


    //endregion

    public LiveData<Resource<Boolean>> resentCodeForUser(String userEmail) {
        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            Response<ApiStatus> response;

            try {
                response = psApiService.resentCodeAgain(Config.API_KEY, userEmail).execute();


                if (response.isSuccessful()) {
                    statusLiveData.postValue(Resource.success(true));
                } else {
                    statusLiveData.postValue(Resource.error("error", false));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return statusLiveData;
    }

    //region Get User List

    public LiveData<Resource<List<User>>> getUserListByKey(String loginUserId, String limit, String offset, UserParameterHolder userParameterHolder) {

        return new NetworkBoundResource<List<User>, List<User>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<User> userList) {
                Utils.psLog("SaveCallResult of getProductListByKey.");

                try {
                    db.runInTransaction(() -> {
                        String mapKey = userParameterHolder.getUserMapKey();

                        db.userMapDao().deleteUsersByMapKey(mapKey);

                        userDao.insertAll(userList);

                        String dateTime = Utils.getDateTime();

                        for (int i = 0; i < userList.size(); i++) {
                            db.userMapDao().insert(new UserMap(mapKey + userList.get(i).userId, mapKey, userList.get(i).userId, i + 1, dateTime));
                        }

                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<User> data) {

                // Recent news always load from server
                return connectivity.isConnected();

            }

            @NonNull
            @Override
            protected LiveData<List<User>> loadFromDb() {
                Utils.psLog("Load getProductListByKey From Db");
                String mapKey = userParameterHolder.getUserMapKey();

                return userDao.getUserByKey(mapKey);

            }


            @NonNull
            @Override
            protected LiveData<ApiResponse<List<User>>> createCall() {
                Utils.psLog("Call API Service to getProductListByKey.");

                return psApiService.searchUser(Config.API_KEY, limit, offset, userParameterHolder.user_name, userParameterHolder.overall_rating,
                        userParameterHolder.return_types, loginUserId, userParameterHolder.otherUserId);

            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getUserListByKey) : " + message);
            }

        }.asLiveData();

    }

    public LiveData<Resource<Boolean>> getNextPageUserListByKey(UserParameterHolder userParameterHolder, String loginUserId, String limit, String offset) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<List<User>>> apiResponse = psApiService.searchUser(Config.API_KEY, limit, offset, userParameterHolder.user_name, userParameterHolder.overall_rating,
                userParameterHolder.return_types, loginUserId, userParameterHolder.otherUserId);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                if (response.body != null) {
                    appExecutors.diskIO().execute(() -> {

                        try {
                            db.runInTransaction(() -> {
                                userDao.insertAll(response.body);

                                int finalIndex = db.userMapDao().getMaxSortingByValue(userParameterHolder.getUserMapKey());

                                int startIndex = finalIndex + 1;

                                String mapKey = userParameterHolder.getUserMapKey();
                                String dateTime = Utils.getDateTime();

                                for (int i = 0; i < response.body.size(); i++) {
                                    db.userMapDao().insert(new UserMap(mapKey + response.body.get(i).userId, mapKey, response.body.get(i).userId, startIndex + i, dateTime));
                                }
                            });
                        } catch (Exception ex) {
                            Utils.psErrorLog("Error at ", ex);
                        }

                        statusLiveData.postValue(Resource.success(true));
                    });
                } else {
                    statusLiveData.postValue(Resource.error(response.errorMessage, null));
                }

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }
        });

        return statusLiveData;

    }

    //endregion

    //rating post
    public LiveData<Resource<Boolean>> uploadUserFollowPostToServer(String userId, String followedUserId) {

        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            try {

                try {
                    db.runInTransaction(() -> {
                        isSelected = userDao.selectUserFollowById(userId);
                        if (isSelected.equals(Constants.ONE)) {
                            userDao.updateUserFollowById(userId, Constants.ZERO);
                        } else {
                            userDao.updateUserFollowById(userId, Constants.ONE);
                        }
                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }

                // Call the API Service
                Response<User> response;

                response = psApiService.setPostUserFollow(Config.API_KEY, userId, followedUserId).execute();

                // Wrap with APIResponse Class
                ApiResponse<User> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

                    try {
                        db.runInTransaction(() -> {
                            if (apiResponse.body != null) {
                                userDao.insert(apiResponse.body);

                                UserParameterHolder userParameterHolder = new UserParameterHolder().getFollowingUsers();
                                userParameterHolder.login_user_id = userId;
                                String mapKey = userParameterHolder.getUserMapKey();


//                            UserParameterHolder userParameterHolder = new UserParameterHolder().getFollowingUsers();
//                            userParameterHolder.login_user_id = userId;
//                            String mapKey = userParameterHolder.getUserMapKey();

                            if (apiResponse.body.isFollowed.equals(Constants.ZERO)) {
                                //delete data of unfollow user
                                db.itemDao().deleteAllItemFromFollowerByUserId(followedUserId);

//                                if (apiResponse.body.isFollowed.equals(Constants.ZERO)) {
//                                    //delete data of unfollow user
//                                    db.itemDao().deleteAllItemFromFollowerByUserId(followedUserId);

                                    db.userMapDao().deleteUsersByMapKey(mapKey);


//                                } else {
//                                    String dateTime = Utils.getDateTime();

                            } else {
                                String dateTime = Utils.getDateTime();

                                db.userMapDao().insert(new UserMap(mapKey + userId, mapKey, userId, 1, dateTime));

                                }


                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.success(apiResponse.getNextPage() != null));

                } else {

                    try {
                        db.runInTransaction(() -> {
                            isSelected = userDao.selectUserFollowById(userId);
                            if (isSelected.equals(Constants.ONE)) {
                                userDao.updateUserFollowById(userId, Constants.ZERO);
                            } else {
                                userDao.updateUserFollowById(userId, Constants.ONE);
                            }
                        });
                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }

                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, false));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), false));
            }
        });

        return statusLiveData;
    }
    //endregion

}
