package com.panaceasoft.psbuyandsell.di;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.api.PSApiService;
import com.panaceasoft.psbuyandsell.db.AboutUsDao;
import com.panaceasoft.psbuyandsell.db.BlockUserDao;
import com.panaceasoft.psbuyandsell.db.BlogDao;
import com.panaceasoft.psbuyandsell.db.ChatHistoryDao;
import com.panaceasoft.psbuyandsell.db.CityDao;
import com.panaceasoft.psbuyandsell.db.CityMapDao;
import com.panaceasoft.psbuyandsell.db.DeletedObjectDao;
import com.panaceasoft.psbuyandsell.db.HistoryDao;
import com.panaceasoft.psbuyandsell.db.ImageDao;
import com.panaceasoft.psbuyandsell.db.ItemCategoryDao;
import com.panaceasoft.psbuyandsell.db.ItemCollectionHeaderDao;
import com.panaceasoft.psbuyandsell.db.ItemConditionDao;
import com.panaceasoft.psbuyandsell.db.ItemCurrencyDao;
import com.panaceasoft.psbuyandsell.db.ItemDao;
import com.panaceasoft.psbuyandsell.db.ItemDealOptionDao;
import com.panaceasoft.psbuyandsell.db.ItemLocationDao;
import com.panaceasoft.psbuyandsell.db.ItemMapDao;
import com.panaceasoft.psbuyandsell.db.ItemPaidHistoryDao;
import com.panaceasoft.psbuyandsell.db.ItemPriceTypeDao;
import com.panaceasoft.psbuyandsell.db.ItemSubCategoryDao;
import com.panaceasoft.psbuyandsell.db.ItemTypeDao;
import com.panaceasoft.psbuyandsell.db.MessageDao;
import com.panaceasoft.psbuyandsell.db.NotificationDao;
import com.panaceasoft.psbuyandsell.db.OfferDao;
import com.panaceasoft.psbuyandsell.db.OfflinePaymentDao;
import com.panaceasoft.psbuyandsell.db.PSAppInfoDao;
import com.panaceasoft.psbuyandsell.db.PSAppVersionDao;
import com.panaceasoft.psbuyandsell.db.PSCoreDb;
import com.panaceasoft.psbuyandsell.db.PSCountDao;
import com.panaceasoft.psbuyandsell.db.RatingDao;
import com.panaceasoft.psbuyandsell.db.ReportedItemDao;
import com.panaceasoft.psbuyandsell.db.UserDao;
import com.panaceasoft.psbuyandsell.db.UserMapDao;
import com.panaceasoft.psbuyandsell.utils.AppLanguage;
import com.panaceasoft.psbuyandsell.utils.Connectivity;
import com.panaceasoft.psbuyandsell.utils.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Panacea-Soft on 11/15/17.
 * Contact Email : teamps.is.cool@gmail.com
 */

@Module(includes = ViewModelModule.class)
class AppModule {

    @Singleton
    @Provides
    PSApiService providePSApiService() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        return new Retrofit.Builder()
                .baseUrl(Config.APP_API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(PSApiService.class);

    }

    @Singleton
    @Provides
    PSCoreDb provideDb(Application app) {
        return Room.databaseBuilder(app, PSCoreDb.class, "psmulticity.db")
                //.addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    Connectivity provideConnectivity(Application app) {
        return new Connectivity(app);
    }

    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences(Application app) {
        return PreferenceManager.getDefaultSharedPreferences(app.getApplicationContext());
    }

    @Singleton
    @Provides
    UserDao provideUserDao(PSCoreDb db) {
        return db.userDao();
    }

    @Singleton
    @Provides
    UserMapDao provideUserMapDao(PSCoreDb db) {
        return db.userMapDao();
    }

    @Singleton
    @Provides
    AppLanguage provideCurrentLanguage(SharedPreferences sharedPreferences) {
        return new AppLanguage(sharedPreferences);
    }

    @Singleton
    @Provides
    AboutUsDao provideAboutUsDao(PSCoreDb db) {
        return db.aboutUsDao();
    }

    @Singleton
    @Provides
    ImageDao provideImageDao(PSCoreDb db) {
        return db.imageDao();
    }

    @Singleton
    @Provides
    ItemCurrencyDao provideItemCurrencyDao(PSCoreDb db) {
        return db.itemCurrencyDao();
    }

    @Singleton
    @Provides
    ItemTypeDao provideItemTypeDao(PSCoreDb db) {
        return db.itemTypeDao();
    }

    @Singleton
    @Provides
    ItemPriceTypeDao provideItemPriceTypeDao(PSCoreDb db) {
        return db.itemPriceTypeDao();
    }

    @Singleton
    @Provides
    HistoryDao provideHistoryDao(PSCoreDb db) {
        return db.historyDao();
    }

    @Singleton
    @Provides
    RatingDao provideRatingDao(PSCoreDb db) {
        return db.ratingDao();
    }

    @Singleton
    @Provides
    ItemDealOptionDao provideItemDealOptionDao(PSCoreDb db) {
        return db.itemDealOptionDao();
    }

    @Singleton
    @Provides
    ItemConditionDao provideItemConditionDao(PSCoreDb db) {
        return db.itemConditionDao();
    }

    @Singleton
    @Provides
    ItemLocationDao provideItemLocationDao(PSCoreDb db) {
        return db.itemLocationDao();
    }

    @Singleton
    @Provides
    NotificationDao provideNotificationDao(PSCoreDb db) {
        return db.notificationDao();
    }

    @Singleton
    @Provides
    BlogDao provideNewsFeedDao(PSCoreDb db) {
        return db.blogDao();
    }

    @Singleton
    @Provides
    PSAppInfoDao providePSAppInfoDao(PSCoreDb db) {
        return db.psAppInfoDao();
    }

    @Singleton
    @Provides
    PSAppVersionDao providePSAppVersionDao(PSCoreDb db) {
        return db.psAppVersionDao();
    }

    @Singleton
    @Provides
    DeletedObjectDao provideDeletedObjectDao(PSCoreDb db) {
        return db.deletedObjectDao();
    }

    @Singleton
    @Provides
    CityDao provideCityDao(PSCoreDb db) {
        return db.cityDao();
    }

    @Singleton
    @Provides
    CityMapDao provideCityMapDao(PSCoreDb db) {
        return db.cityMapDao();
    }

    @Singleton
    @Provides
    ItemDao provideItemDao(PSCoreDb db) {
        return db.itemDao();
    }

    @Singleton
    @Provides
    ItemMapDao provideItemMapDao(PSCoreDb db) {
        return db.itemMapDao();
    }

    @Singleton
    @Provides
    ItemCategoryDao provideCityCategoryDao(PSCoreDb db) {
        return db.itemCategoryDao();
    }

    @Singleton
    @Provides
    ItemCollectionHeaderDao provideItemCollectionHeaderDao(PSCoreDb db) {
        return db.itemCollectionHeaderDao();
    }

    @Singleton
    @Provides
    ItemSubCategoryDao provideItemSubCategoryDao(PSCoreDb db) {
        return db.itemSubCategoryDao();
    }

    @Singleton
    @Provides
    ChatHistoryDao provideChatHistoryDao(PSCoreDb db) {
        return db.chatHistoryDao();
    }

    @Singleton
    @Provides
    OfferDao provideOfferListDao(PSCoreDb db){
        return db.offerDao();
    }

    @Singleton
    @Provides
    ReportedItemDao provideReportedItemDao(PSCoreDb db){
        return db.reportedItemDao();
    }

    @Singleton
    @Provides
    BlockUserDao provideBlockUserDao(PSCoreDb db){
        return db.blockUserDao();
    }

    @Singleton
    @Provides
    OfflinePaymentDao provideOfflinePaymentDao(PSCoreDb db){
        return db.offlinePaymentDao();
    }

    @Singleton
    @Provides
    MessageDao provideMessageDao(PSCoreDb db) {
        return db.messageDao();
    }

    @Singleton
    @Provides
    PSCountDao providePSCountDao(PSCoreDb db) {
        return db.psCountDao();
    }

    @Singleton
    @Provides
    ItemPaidHistoryDao provideItemPaidHistoryDao(PSCoreDb db) {
        return db.itemPaidHistoryDao();
    }
}
