package com.panaceasoft.psbuyandsell.api;

import androidx.lifecycle.LiveData;

import com.panaceasoft.psbuyandsell.viewobject.AboutUs;
import com.panaceasoft.psbuyandsell.viewobject.ApiStatus;
import com.panaceasoft.psbuyandsell.viewobject.BlockUser;
import com.panaceasoft.psbuyandsell.viewobject.Blog;
import com.panaceasoft.psbuyandsell.viewobject.ChatHistory;
import com.panaceasoft.psbuyandsell.viewobject.City;
import com.panaceasoft.psbuyandsell.viewobject.Image;
import com.panaceasoft.psbuyandsell.viewobject.Item;
import com.panaceasoft.psbuyandsell.viewobject.ItemCategory;
import com.panaceasoft.psbuyandsell.viewobject.ItemCondition;
import com.panaceasoft.psbuyandsell.viewobject.ItemCurrency;
import com.panaceasoft.psbuyandsell.viewobject.ItemDealOption;
import com.panaceasoft.psbuyandsell.viewobject.ItemLocation;
import com.panaceasoft.psbuyandsell.viewobject.ItemPaidHistory;
import com.panaceasoft.psbuyandsell.viewobject.ItemPriceType;
import com.panaceasoft.psbuyandsell.viewobject.ItemSubCategory;
import com.panaceasoft.psbuyandsell.viewobject.ItemType;
import com.panaceasoft.psbuyandsell.viewobject.Noti;
import com.panaceasoft.psbuyandsell.viewobject.Offer;
import com.panaceasoft.psbuyandsell.viewobject.OfflinePaymentMethodHeader;
import com.panaceasoft.psbuyandsell.viewobject.PSAppInfo;
import com.panaceasoft.psbuyandsell.viewobject.PSCount;
import com.panaceasoft.psbuyandsell.viewobject.Rating;
import com.panaceasoft.psbuyandsell.viewobject.ReportedItem;
import com.panaceasoft.psbuyandsell.viewobject.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * REST API access points
 */
public interface PSApiService {

    //region Get favourite product list

    @GET("rest/items/get_favourite/api_key/{API_KEY}/login_user_id/{login_user_id}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<Item>>> getFavouriteList(@Path("API_KEY") String apiKey,
                                                       @Path("login_user_id") String login_user_id,
                                                       @Path("limit") String limit,
                                                       @Path("offset") String offset);

    //endregion

    //region Post Favourite Product
    @FormUrlEncoded
    @POST("rest/favourites/press/api_key/{API_KEY}")
    Call<Item> setPostFavourite(
            @Path("API_KEY") String api_key,
            @Field("item_id") String itemId,
            @Field("user_id") String userId);

    //endregion

    // region Post Follow User
    @FormUrlEncoded
    @POST("rest/userfollows/add_follow/api_key/{API_KEY}")
    Call<User> setPostUserFollow(
            @Path("API_KEY") String api_key,
            @Field("user_id") String userId,
            @Field("followed_user_id") String followed_user_id);

    //endregion

    //region Get Product Detail

    @GET("rest/items/get/api_key/{API_KEY}/id/{id}/login_user_id/{login_user_id}")
    LiveData<ApiResponse<Item>> getItemDetail(@Path("API_KEY") String apiKey,
                                              @Path("id") String Id,
                                              @Path("login_user_id") String login_user_id);

    //endregion

    //region deleteItem
    @FormUrlEncoded
    @POST("rest/items/item_delete/api_key/{API_KEY}")
    Call<ApiStatus> deleteItem(@Path("API_KEY") String apiKey,
                               @Field("item_id") String item_id);

    //endregion

    //region deleteItem
    @FormUrlEncoded
    @POST("rest/users/user_delete/api_key/{API_KEY}")
    Call<ApiStatus> deleteUser(@Path("API_KEY") String apiKey,
                               @Field("user_id") String user_id);

    //endregion

    //region markSoldOutItem

    @FormUrlEncoded
    @POST("rest/items/sold_out_from_itemdetails/api_key/{api_key}/login_user_id/{login_user_id}")
    LiveData<ApiResponse<Item>> markSoldOutItem(@Path("api_key") String apiKey,
                                                @Path("login_user_id") String login_user_id,
                                                @Field("item_id") String item_id);

    //endregion

    //region Get Image List

    @GET("rest/images/get/api_key/{API_KEY}/img_parent_id/{img_parent_id}/img_type/{img_type}")
    LiveData<ApiResponse<List<Image>>> getImageList(@Path("API_KEY") String apiKey,
                                                    @Path("img_parent_id") String img_parent_id,
                                                    @Path("img_type") String imageType);

    //endregion

    //region Notification

    //region Submit Notification Token
    @FormUrlEncoded
    @POST("rest/notis/register/api_key/{API_KEY}")
    Call<ApiStatus> rawRegisterNotiToken(@Path("API_KEY") String apiKey,
                                         @Field("platform_name") String platform,
                                         @Field("device_token") String deviceId,
                                         @Field("user_id") String userId);


    @FormUrlEncoded
    @POST("rest/notis/unregister/api_key/{API_KEY}")
    Call<ApiStatus> rawUnregisterNotiToken(@Path("API_KEY") String apiKey,
                                           @Field("platform_name") String platform,
                                           @Field("device_token") String deviceId,
                                           @Field("user_id") String userId);

    //endregion

    //region Get Notification List

    @FormUrlEncoded
    @POST("rest/noti_messages/all_notis/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<Noti>>> getNotificationList(@Path("API_KEY") String apiKey,
                                                          @Path("limit") String limit,
                                                          @Path("offset") String offset,
                                                          @Field("user_id") String userId,
                                                          @Field("device_token") String deviceToken);

    //endregion

    //region Get Notification detail

    @GET("rest/notis/get/api_key/{API_KEY}/id/{id}")
    LiveData<ApiResponse<Noti>> getNotificationDetail(@Path("API_KEY") String apiKey,
                                                      @Path("id") String id);

    //endregion

    //region Is Read Notificaiton
    @FormUrlEncoded
    @POST("rest/notis/is_read/api_key/{API_KEY}")
    Call<Noti> isReadNoti(
            @Path("API_KEY") String apiKey,
            @Field("noti_id") String noti_id,
            @Field("user_id") String userId,
            @Field("device_token") String device_token);

    //endregion

    //region POST Upload Image
    @Multipart
    @POST("rest/images/upload/api_key/{API_KEY}")
    LiveData<ApiResponse<User>> doUploadImage(@Path("API_KEY") String apiKey,
                                              @Part("user_id") RequestBody userId,
                                              @Part("file") RequestBody name,
                                              @Part MultipartBody.Part file,
                                              @Part("platform_name") RequestBody platformName);
    //endregion

    //region POST User for Login
    @FormUrlEncoded
    @POST("rest/users/login/api_key/{API_KEY}")
    LiveData<ApiResponse<User>> postUserLogin(@Path("API_KEY") String apiKey,
                                              @Field("user_email") String userEmail,
                                              @Field("user_password") String userPassword,
                                              @Field("device_token") String deviceToken);
    //endregion

    //region POST User for Register

    @FormUrlEncoded
    @POST("rest/users/facebook_register/api_key/{API_KEY}")
    Call<User> postFBUser(@Path("API_KEY") String apiKey,
                          @Field("facebook_id") String facebookId,
                          @Field("user_name") String userName,
                          @Field("user_email") String userEmail,
                          @Field("profile_img_id") String profilePhotoUrl,
                          @Field("device_token") String deviceToken);

    @FormUrlEncoded
    @POST("rest/users/add/api_key/{API_KEY}")
    Call<User> postUser(@Path("API_KEY") String apiKey,
                        @Field("user_name") String userName,
                        @Field("user_email") String userEmail,
                        @Field("user_password") String userPassword,
                        @Field("device_token") String deviceToken);
    //endregion

    //region POST Forgot Password
    @FormUrlEncoded
    @POST("rest/users/reset/api_key/{API_KEY}")
    LiveData<ApiResponse<ApiStatus>> postForgotPassword(@Path("API_KEY") String apiKey,
                                                        @Field("user_email") String userEmail);
    //endregion

    //region PUT User for User Update
    @FormUrlEncoded
    @POST("rest/users/profile_update/api_key/{API_KEY}")
    LiveData<ApiResponse<ApiStatus>> putUser(@Path("API_KEY") String apiKey,
                                             @Field("user_id") String loginUserId,
                                             @Field("user_name") String userName,
                                             @Field("user_email") String userEmail,
                                             @Field("user_phone") String userPhone,
                                             @Field("user_address") String user_address,
                                             @Field("city") String city,
                                             @Field("user_about_me") String userAboutMe,
                                             @Field("is_show_email") String isShowEmail,
                                             @Field("is_show_phone") String isShowPhone,
                                             @Field("device_token") String device_token
    );

    //endregion

    //region PUT for Password Update
    @FormUrlEncoded
    @POST("rest/users/password_update/api_key/{API_KEY}")
    LiveData<ApiResponse<ApiStatus>> postPasswordUpdate(@Path("API_KEY") String apiKey,
                                                        @Field("user_id") String loginUserId,
                                                        @Field("user_password") String password);
    //endregion

    //endregion


    //region About Us

    @GET("rest/abouts/get/api_key/{API_KEY}")
    LiveData<ApiResponse<List<AboutUs>>> getAboutUs(@Path("API_KEY") String apiKey);

    //endregion


    //region Contact Us

    @FormUrlEncoded
    @POST("rest/contacts/add/api_key/{API_KEY}")
    Call<ApiStatus> rawPostContact(@Path("API_KEY") String apiKey,
                                   @Field("contact_name") String contactName,
                                   @Field("contact_email") String contactEmail,
                                   @Field("contact_message") String contactMessage,
                                   @Field("contact_phone") String contactPhone);

    //endregion


    //region GET SubCategory List

    @GET("rest/subcategories/get/api_key/{API_KEY}")
    LiveData<ApiResponse<List<ItemSubCategory>>> getAllSubCategoryList(@Path("API_KEY") String apiKey);

    @GET("rest/subcategories/get/api_key/{API_KEY}/cat_id/{cat_id}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<ItemSubCategory>>> getSubCategoryList(@Path("API_KEY") String apiKey,
                                                                    @Path("cat_id") String catId,
                                                                    @Path("limit") String limit,
                                                                    @Path("offset") String offset);

    @GET("rest/subcategories/get/api_key/{API_KEY}/limit/{limit}/offset/{offset}/cat_id/{cat_id}")
    LiveData<ApiResponse<List<ItemSubCategory>>> getSubCategoryListWithCatId(@Path("API_KEY") String apiKey,
                                                                             @Path("cat_id") String catId,
                                                                             @Path("limit") String limit,
                                                                             @Path("offset") String offset);

    //endregion

    //region Delete Shop list by date

    @FormUrlEncoded
    @POST("rest/appinfo/get_delete_history/api_key/{API_KEY}")
    Call<PSAppInfo> getDeletedHistory(
            @Path("API_KEY") String apiKey,
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Field("user_id") String user_id);

    //endregion

    //region Get All Rating List
    @FormUrlEncoded
    @POST("rest/rates/rating_user/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<Rating>>> getAllRatingList(@Path("API_KEY") String apiKey,
                                                         @Field("user_id") String itemId,
                                                         @Path("limit") String limit,
                                                         @Path("offset") String offset);

    //endregion

    //region Post Rating

    @FormUrlEncoded
    @POST("rest/rates/add_rating/api_key/{API_KEY}")
    Call<Rating> postRating(
            @Path("API_KEY") String api_key,
            @Field("from_user_id") String fromUserId,
            @Field("to_user_id") String toUserId,
            @Field("rating") String rating,
            @Field("title") String title,
            @Field("description") String description);

    //endregion

    //endregion


    //region Touch Count

    @FormUrlEncoded
    @POST("rest/touches/touch_item/api_key/{API_KEY}")
    Call<ApiStatus> setrawPostTouchCount(
            @Path("API_KEY") String apiKey,
            @Field("item_id") String itemId,
            @Field("user_id") String userId);

    //endregion


    //region News|Blog

    @GET("rest/feeds/get/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<Blog>>> getAllNewsFeed(@Path("API_KEY") String api_key,
                                                     @Path("limit") String limit,
                                                     @Path("offset") String offset);

    @GET("rest/feeds/get/api_key/{API_KEY}/id/{id}")
    LiveData<ApiResponse<Blog>> getNewsById(@Path("API_KEY") String api_key,
                                            @Path("id") String id);

    //endregion

    //region SearchCity
    @FormUrlEncoded
    @POST("rest/cities/search/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<City>>> searchCity(
            @Path("API_KEY") String API_KEY,
            @Path("limit") String limit,
            @Path("offset") String offset,
            @Field("id") String id,
            @Field("keyword") String keyword,
            @Field("is_featured") String is_featured,
            @Field("order_by") String order_by,
            @Field("order_type") String order_type);



    //endregion

    //region SearchCity
    @FormUrlEncoded
    @POST("rest/userfollows/search/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<User>>> searchUser(
            @Path("API_KEY") String API_KEY,
            @Path("limit") String limit,
            @Path("offset") String offset,
            @Field("user_name") String user_name,
            @Field("overall_rating") String overall_rating,
            @Field("return_types") String return_types,
            @Field("login_user_id") String login_user_id,
            @Field("id") String id
    );


    //endregion

    //region SearchCity
    @FormUrlEncoded
    @POST("rest/userfollows/search/api_key/{API_KEY}")
    LiveData<ApiResponse<User>> getUserDetail(
            @Path("API_KEY") String API_KEY,
            @Field("user_name") String user_name,
            @Field("overall_rating") String overall_rating,
            @Field("return_types") String return_types,
            @Field("login_user_id") String login_user_id,
            @Field("id") String id
    );


    //endregion

    //region SearchItem
    @FormUrlEncoded
    @POST("rest/items/search/api_key/{API_KEY}/limit/{limit}/offset/{offset}/login_user_id/{login_user_id}")
    LiveData<ApiResponse<List<Item>>> searchItem(
            @Path("API_KEY") String API_KEY,
            @Path("limit") String limit,
            @Path("offset") String offset,
            @Path("login_user_id") String login_user_id,
            @Field("searchterm") String searchterm,
            @Field("cat_id") String cat_id,
            @Field("sub_cat_id") String sub_cat_id,
            @Field("order_by") String order_by,
            @Field("order_type") String order_type,
            @Field("item_type_id") String item_type_id,
            @Field("item_price_type_id") String item_price_type_id,
            @Field("item_currency_id") String item_currency_id,
            @Field("item_location_id") String item_location_id,
            @Field("deal_option_id") String deal_option_id,
            @Field("condition_of_item_id") String condition_of_item_id,
            @Field("max_price") String max_price,
            @Field("min_price") String min_price,
            @Field("brand") String brand,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("miles") String miles,
            @Field("added_user_id") String userId,
            @Field("is_paid") String isPaid,
            @Field("status") String status
    );

    //endregion

    //region item image upload

    @FormUrlEncoded
    @POST("rest/items/add/api_key/{API_KEY}")
    Call<Item> itemUpload(
            @Path("API_KEY") String API_KEY,
            @Field("cat_id") String cat_id,
            @Field("sub_cat_id") String sub_cat_id,
            @Field("item_type_id") String item_type_id,
            @Field("item_price_type_id") String item_price_type_id,
            @Field("item_currency_id") String item_currency_id,
            @Field("condition_of_item_id") String condition_of_item_id,
            @Field("item_location_id") String item_location_id,
            @Field("deal_option_remark") String deal_option_remark,
            @Field("description") String description,
            @Field("highlight_info") String highlight_info,
            @Field("price") String price,
            @Field("deal_option_id") String deal_option_id,
            @Field("brand") String brand,
            @Field("business_mode") String business_mode,
            @Field("is_sold_out") String is_sold_out,
            @Field("title") String title,
            @Field("address") String address,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("id") String id,
            @Field("added_user_id") String user_id

    );

    @Multipart
    @POST("rest/images/upload_item/api_key/{API_KEY}")
    LiveData<ApiResponse<Image>> itemImageUpload(@Path("API_KEY") String apiKey,
                                                 @Part("item_id") RequestBody item_id,
                                                 @Part("img_id") RequestBody img_id,
                                                 @Part MultipartBody.Part file);

    //endregion

    //item from follower
    @GET("rest/items/get_item_by_followuser/api_key/{API_KEY}/login_user_id/{login_user_id}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<Item>>> getItemListFromFollower(
            @Path("API_KEY") String API_KEY,
            @Path("login_user_id") String login_user_id,
            @Path("limit") String limit,
            @Path("offset") String offset);
    //endregion

    //region ItemCategory

    @GET("rest/categories/get/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<ItemCategory>>> getCityCategory(
            @Path("API_KEY") String API_KEY,
            @Path("limit") String limit,
            @Path("offset") String offset

    );

    //region ItemCategory

    //region ItemCurrencyType

    @GET("rest/currencies/get/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<ItemCurrency>>> getItemCurrencyTypeList(
            @Path("API_KEY") String API_KEY,
            @Path("limit") String limit,
            @Path("offset") String offset
    );

    //endregion

    //region ItemPriceType

    @GET("rest/pricetypes/get/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<ItemPriceType>>> getItemPriceTypeList(
            @Path("API_KEY") String API_KEY,
            @Path("limit") String limit,
            @Path("offset") String offset
    );

    //endregion

    //region ItemType

    @GET("rest/itemtypes/get/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<ItemType>>> getItemTypeList(

            @Path("API_KEY") String API_KEY,
            @Path("limit") String limit,
            @Path("offset") String offset
    );

    //endregion

    //region ItemCondition

    @GET("rest/conditions/get/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<ItemCondition>>> getItemConditionTypeList(

            @Path("API_KEY") String API_KEY,
            @Path("limit") String limit,
            @Path("offset") String offset
    );

    //region reporteditem

    @GET("rest/items/get_reported_item_by_loginuser/api_key/{API_KEY}/limit/{limit}/offset/{offset}/login_user_id/{loginUserId}")
    LiveData<ApiResponse<List<ReportedItem>>> getReportedItem(
            @Path("API_KEY") String API_KEY,
            @Path("limit") String limit,
            @Path("offset") String offset,
            @Path("loginUserId") String loginUserId
    );
    //endregion

    //region blockuser

    @GET("rest/users/get_blocked_user_by_loginuser/api_key/{API_KEY}/limit/{limit}/offset/{offset}/login_user_id/{loginUserId}")
    LiveData<ApiResponse<List<BlockUser>>> getBlockUser(
            @Path("API_KEY") String API_KEY,
            @Path("limit") String limit,
            @Path("offset") String offset,
            @Path("loginUserId") String loginUserId
    );

    // region unblockuser
    @FormUrlEncoded
    @POST("rest/blockusers/unblock/api_key/{API_KEY}")
    Call<ApiStatus> UnBlockuser(
            @Path("API_KEY") String API_KEY,
            @Field("from_block_user_id") String fromblockuserid,
            @Field("to_block_user_id") String toblockuserid

    );

    //region ItemLocation

//    @GET("rest/itemlocations/get/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
//    LiveData<ApiResponse<List<ItemLocation>>> getItemLocationList(
//
//            @Path("API_KEY") String API_KEY,
//            @Path("limit") String limit,
//            @Path("offset") String offset
//    );

    @FormUrlEncoded
    @POST("rest/itemlocations/search/api_key/{API_KEY}/limit/{limit}/offset/{offset}/login_user_id/{loginUserId}")
    LiveData<ApiResponse<List<ItemLocation>>> getItemLocationList(
            @Path("API_KEY") String API_KEY,
            @Path("limit") String limit,
            @Path("offset") String offset,
            @Path("loginUserId") String loginUserId,
            @Field("keyword") String keyword,
            @Field("order_by") String order_by,
            @Field("order_type")String  order_type
    );

    //endregion

    //region ItemDealOption

    @GET("rest/options/get/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<ItemDealOption>>> getItemDealOptionList(

            @Path("API_KEY") String API_KEY,
            @Path("limit") String limit,
            @Path("offset") String offset
    );

    //endregion

    //region getUserById

    @GET("rest/users/get/api_key/{API_KEY}/id/{id}")
    LiveData<ApiResponse<User>> getUserById(@Path("API_KEY") String apiKey,
                                            @Path("id") String Id);

    @FormUrlEncoded
    @POST("rest/notis/send_chat_noti/api_key/{API_KEY}")
    Call<ApiStatus> sendNotiForChat(
            @Path("API_KEY") String API_KEY,
            @Field("item_id") String item_id,
            @Field("buyer_user_id") String buyerUserId,
            @Field("seller_user_id") String sellerUserId,
            @Field("message") String message,
            @Field("type") String type);

    //report item
    @FormUrlEncoded
    @POST("rest/itemreports/add/api_key/{API_KEY}")
    Call<ApiStatus> reportItem(
            @Path("API_KEY") String API_KEY,
            @Field("item_id") String item_id,
            @Field("reported_user_id") String reported_user_id);

    //block user
    @FormUrlEncoded
    @POST("rest/blockusers/add/api_key/{API_KEY}")
    Call<ApiStatus> blockUser(
            @Path("API_KEY") String API_KEY,
            @Field("from_block_user_id") String loginUserId,
            @Field("to_block_user_id") String addedUserId);

    //logout user
    @FormUrlEncoded
    @POST("rest/users/logout/api_key/{API_KEY}")
    Call<ApiStatus> logoutUser(
            @Path("API_KEY") String API_KEY,
            @Field("user_id") String user_id);


    @FormUrlEncoded
    @POST("rest/chats/reset_count/api_key/{API_KEY}")
    Call<ChatHistory> resetUnreadCount(
            @Path("API_KEY") String API_KEY,
            @Field("item_id") String item_id,
            @Field("buyer_user_id") String buyerUserId,
            @Field("seller_user_id") String sellerUserId,
            @Field("type") String type);

    @FormUrlEncoded
    @POST("rest/chats/add/api_key/{API_KEY}")
    Call<ChatHistory> syncChatHistory(
            @Path("API_KEY") String API_KEY,
            @Field("item_id") String item_id,
            @Field("buyer_user_id") String buyer_user_id,
            @Field("seller_user_id") String seller_user_id,
            @Field("type") String type

    );

    @FormUrlEncoded
    @POST("rest/chats/item_sold_out/api_key/{API_KEY}")
    Call<ChatHistory> sellItem(
            @Path("API_KEY") String API_KEY,
            @Field("item_id") String item_id,
            @Field("buyer_user_id") String buyer_user_id,
            @Field("seller_user_id") String seller_user_id
    );

    @FormUrlEncoded
    @POST("rest/chats/update_price/api_key/{API_KEY}")
    Call<ChatHistory> updateNegoPrice(
            @Path("API_KEY") String API_KEY,
            @Field("item_id") String item_id,
            @Field("buyer_user_id") String buyer_user_id,
            @Field("seller_user_id") String seller_user_id,
            @Field("nego_price") String nego_price,
            @Field("type") String type

    );

    @FormUrlEncoded
    @POST("rest/chats/update_accept/api_key/{API_KEY}")
    Call<ChatHistory> acceptedOffer(
            @Path("API_KEY") String API_KEY,
            @Field("item_id") String item_id,
            @Field("buyer_user_id") String buyer_user_id,
            @Field("seller_user_id") String seller_user_id,
            @Field("nego_price") String nego_price,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST("rest/chats/get_chat_history/api_key/{API_KEY}")
    LiveData<ApiResponse<ChatHistory>> getChatHistory(
            @Path("API_KEY") String API_KEY,
            @Field("item_id") String itemId,
            @Field("buyer_user_id") String buyerUserId,
            @Field("seller_user_id") String sellerUserId
    );

    @FormUrlEncoded
    @POST("rest/users/phone_register/api_key/{API_KEY}")
    Call<User> postPhoneLogin(
            @Path("API_KEY") String API_KEY,
            @Field("phone_id") String phoneId,
            @Field("user_name") String userName,
            @Field("user_phone") String userPhone,
            @Field("device_token") String deviceToken
    );

    @FormUrlEncoded
    @POST("rest/users/google_register/api_key/{API_KEY}")
    Call<User> postGoogleLogin(
            @Path("API_KEY") String API_KEY,
            @Field("google_id") String googleId,
            @Field("user_name") String userName,
            @Field("user_email") String userEmail,
            @Field("profile_photo_url") String profilePhotoUrl,
            @Field("device_token") String deviceToken
    );

    @FormUrlEncoded
    @POST("rest/chat_items/get_buyer_seller_list/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<ChatHistory>>> getChatHistoryList(
            @Path("API_KEY") String API_KEY,
            @Field("user_id") String user_id,
            @Field("return_type") String type,
            @Path("limit") String limit,
            @Path("offset") String offset);


    @FormUrlEncoded
    @POST("rest/chats/offer_list/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<Offer>>> getOfferList (
            @Path("API_KEY") String API_KEY,
            @Field("user_id") String user_id,
            @Field("return_type") String return_types,
            @Path("limit") String limit,
            @Path("offset") String offset);


    @Multipart
    @POST("rest/images/chat_image_upload/api_key/{API_KEY}")
    LiveData<ApiResponse<Image>> chatImageUpload(@Path("API_KEY") String apiKey,
                                                 @Part("sender_id") RequestBody userId,
                                                 @Part MultipartBody.Part file,
                                                 @Part("buyer_user_id") RequestBody buyer_user_id,
                                                 @Part("seller_user_id") RequestBody seller_user_id,
                                                 @Part("item_id") RequestBody item_id,
                                                 @Part("type") RequestBody type
    );

    @FormUrlEncoded
    @POST("rest/users/verify/api_key/{API_KEY}")
    Call<User> verifyEmail(
            @Path("API_KEY") String API_KEY,
            @Field("user_id") String userId,
            @Field("code") String code);


    @FormUrlEncoded
    @POST("rest/users/request_code/api_key/{API_KEY}")
    Call<ApiStatus> resentCodeAgain(
            @Path("API_KEY") String API_KEY,
            @Field("user_email") String user_email
    );

    @FormUrlEncoded
    @POST("rest/users/unread_count/api_key/{API_KEY}")
    LiveData<ApiResponse<PSCount>> postGetAllCount(
            @Path("API_KEY") String API_KEY,
            @Field("user_id") String userId,
            @Field("device_token") String deviceToken
    );


    //region Paypal

    @GET("rest/paypal/get_token/api_key/{API_KEY}")
    Call<ApiStatus> getPaypalToken(@Path("API_KEY") String apiKey);

    //endregion


    //region upload item paid history
    @FormUrlEncoded
    @POST("rest/paid_items/add/api_key/{API_KEY}")
    Call<ItemPaidHistory> uploadItemPaidHistory(
            @Path("API_KEY") String API_KEY,
            @Field("item_id") String itemId,
            @Field("amount") String amount,
            @Field("start_date") String startDate,
            @Field("how_many_day") String howManyDay,
            @Field("payment_method") String paymentMethod,
            @Field("payment_method_nonce") String paymentMethodNonce,
            @Field("start_timestamp") String startTimeStamp,
            @Field("razor_id") String razorId);

    // endregion


    //offline payment

    @GET("rest/offline_payments/get_offline_payment/api_key/{API_KEY}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<OfflinePaymentMethodHeader>> getOfflinePayment(
            @Path("API_KEY") String apiKey,
            @Path("limit") String limit,
            @Path("offset") String offset
    );

    //region get paid ad

    @GET("rest/paid_items/get/api_key/{API_KEY}/login_user_id/{login_user_id}/limit/{limit}/offset/{offset}")
    LiveData<ApiResponse<List<ItemPaidHistory>>> getPaidAd(
            @Path("API_KEY") String apiKey,
            @Path("login_user_id") String login_user_id,
            @Path("limit") String limit,
            @Path("offset") String offset
    );



    //endregion
}