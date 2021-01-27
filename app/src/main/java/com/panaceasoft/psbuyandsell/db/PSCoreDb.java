package com.panaceasoft.psbuyandsell.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.panaceasoft.psbuyandsell.db.common.Converters;
import com.panaceasoft.psbuyandsell.viewobject.AboutUs;
import com.panaceasoft.psbuyandsell.viewobject.BlockUser;
import com.panaceasoft.psbuyandsell.viewobject.Blog;
import com.panaceasoft.psbuyandsell.viewobject.ChatHistory;
import com.panaceasoft.psbuyandsell.viewobject.ChatHistoryMap;
import com.panaceasoft.psbuyandsell.viewobject.City;
import com.panaceasoft.psbuyandsell.viewobject.CityMap;
import com.panaceasoft.psbuyandsell.viewobject.DeletedObject;
import com.panaceasoft.psbuyandsell.viewobject.Image;
import com.panaceasoft.psbuyandsell.viewobject.Item;
import com.panaceasoft.psbuyandsell.viewobject.ItemCategory;
import com.panaceasoft.psbuyandsell.viewobject.ItemCollection;
import com.panaceasoft.psbuyandsell.viewobject.ItemCollectionHeader;
import com.panaceasoft.psbuyandsell.viewobject.ItemCondition;
import com.panaceasoft.psbuyandsell.viewobject.ItemCurrency;
import com.panaceasoft.psbuyandsell.viewobject.ItemDealOption;
import com.panaceasoft.psbuyandsell.viewobject.ItemFavourite;
import com.panaceasoft.psbuyandsell.viewobject.ItemFromFollower;
import com.panaceasoft.psbuyandsell.viewobject.ItemHistory;
import com.panaceasoft.psbuyandsell.viewobject.ItemLocation;
import com.panaceasoft.psbuyandsell.viewobject.ItemMap;
import com.panaceasoft.psbuyandsell.viewobject.ItemPaidHistory;
import com.panaceasoft.psbuyandsell.viewobject.ItemPriceType;
import com.panaceasoft.psbuyandsell.viewobject.ItemSpecs;
import com.panaceasoft.psbuyandsell.viewobject.ItemSubCategory;
import com.panaceasoft.psbuyandsell.viewobject.ItemType;
import com.panaceasoft.psbuyandsell.viewobject.Noti;
import com.panaceasoft.psbuyandsell.viewobject.Offer;
import com.panaceasoft.psbuyandsell.viewobject.OfferMap;
import com.panaceasoft.psbuyandsell.viewobject.OfflinePayment;
import com.panaceasoft.psbuyandsell.viewobject.OfflinePaymentMethodHeader;
import com.panaceasoft.psbuyandsell.viewobject.PSAppInfo;
import com.panaceasoft.psbuyandsell.viewobject.PSAppSetting;
import com.panaceasoft.psbuyandsell.viewobject.PSAppVersion;
import com.panaceasoft.psbuyandsell.viewobject.PSCount;
import com.panaceasoft.psbuyandsell.viewobject.Rating;
import com.panaceasoft.psbuyandsell.viewobject.ReportedItem;
import com.panaceasoft.psbuyandsell.viewobject.User;
import com.panaceasoft.psbuyandsell.viewobject.UserLogin;
import com.panaceasoft.psbuyandsell.viewobject.UserMap;
import com.panaceasoft.psbuyandsell.viewobject.messageHolder.Message;


/**
 * Created by Panacea-Soft on 11/20/17.
 * Contact Email : teamps.is.cool@gmail.com
 */

@Database(entities = {
        Image.class,
        User.class,
        UserLogin.class,
        AboutUs.class,
        ItemFavourite.class,
        Noti.class,
        ItemHistory.class,
        Blog.class,
        Rating.class,
        PSAppInfo.class,
        PSAppVersion.class,
        DeletedObject.class,
        City.class,
        CityMap.class,
        Item.class,
        ItemMap.class,
        ItemCategory.class,
        ItemCollectionHeader.class,
        ItemCollection.class,
        ItemSubCategory.class,
        ItemSpecs.class,
        ItemCurrency.class,
        ItemPriceType.class,
        ItemType.class,
        ItemLocation.class,
        ItemDealOption.class,
        ItemCondition.class,
        ItemFromFollower.class,
        Message.class,
        ChatHistory.class,
        ChatHistoryMap.class,
        Offer.class,
        OfferMap.class,
        PSAppSetting.class,
        UserMap.class,
        PSCount.class,
        ItemPaidHistory.class,
        OfflinePaymentMethodHeader.class,
        OfflinePayment.class,
        ReportedItem.class,
        BlockUser.class

}, version = 12, exportSchema = false)
// app version 3.0 = db version 12
// app version 2.9 = db version 11
// app version 2.8 = db version 10
// app version 2.7 = db version 9
// app version 2.6 = db version 8
// app version 2.5 = db version 7
// app version 2.4 = db version 7
// app version 2.3 = db version 6
// app version 2.2 = db version 6
// app version 2.1 = db version 6
// app version 2.0 = db version 6
// app version 1.9 = db version 6
// app version 1.8 = db version 5
// app version 1.7 = db version 4
// app version 1.6 = db version 4
// app version 1.5 = db version 4
// app version 1.4 = db version 3
// app version 1.3 = db version 3
// app version 1.2 = db version 2
// app version 1.0 = db version 1


@TypeConverters({Converters.class})

public abstract class PSCoreDb extends RoomDatabase {

    abstract public UserDao userDao();

    abstract public UserMapDao userMapDao();

    abstract public HistoryDao historyDao();

    abstract public SpecsDao specsDao();

    abstract public AboutUsDao aboutUsDao();

    abstract public ImageDao imageDao();

    abstract public ItemDealOptionDao itemDealOptionDao();

    abstract public ItemConditionDao itemConditionDao();

    abstract public ItemLocationDao itemLocationDao();

    abstract public ItemCurrencyDao itemCurrencyDao();

    abstract public ItemPriceTypeDao itemPriceTypeDao();

    abstract public OfflinePaymentDao offlinePaymentDao();

    abstract public ItemTypeDao itemTypeDao();

    abstract public RatingDao ratingDao();

    abstract public NotificationDao notificationDao();

    abstract public BlogDao blogDao();

    abstract public PSAppInfoDao psAppInfoDao();

    abstract public PSAppVersionDao psAppVersionDao();

    abstract public DeletedObjectDao deletedObjectDao();

    abstract public CityDao cityDao();

    abstract public CityMapDao cityMapDao();

    abstract public ItemDao itemDao();

    abstract public ItemMapDao itemMapDao();

    abstract public ItemCategoryDao itemCategoryDao();

    abstract public ItemCollectionHeaderDao itemCollectionHeaderDao();

    abstract public ItemSubCategoryDao itemSubCategoryDao();

    abstract public ChatHistoryDao chatHistoryDao();

    abstract public OfferDao offerDao();

    abstract public MessageDao messageDao();

    abstract public PSCountDao psCountDao();

    abstract public ItemPaidHistoryDao itemPaidHistoryDao();

    abstract public ReportedItemDao reportedItemDao();

    abstract public BlockUserDao blockUserDao();


//    /**
//     * Migrate from:
//     * version 1 - using Room
//     * to
//     * version 2 - using Room where the {@link } has an extra field: addedDateStr
//     */
//    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE news "
//                    + " ADD COLUMN addedDateStr INTEGER NOT NULL DEFAULT 0");
//        }
//    };

    /* More migration write here */
}