package com.panaceasoft.psbuyandsell;

import com.panaceasoft.psbuyandsell.utils.Constants;

/**
 * Created by Panacea-Soft on 29/11/2019.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class Config {

    /**
     * AppVersion
     * For your app, you need to change according based on your app version
     */
    public static String APP_VERSION = "3.0";

    /**
     * APP Setting
     * Set false, your app is production
     * It will turn off the logging Process
     */
    public static boolean IS_DEVELOPMENT = true;

    /**
     * API URL
     * Change your backend url
     */
    public static final String APP_BASE_URL = "https://www.panacea-soft.com/buysell-admin";

    public static final String APP_API_URL = APP_BASE_URL + "/index.php/";
    public static final String APP_IMAGES_URL = APP_BASE_URL + "/uploads/";
    public static final String APP_IMAGES_THUMB_URL = APP_BASE_URL + "/uploads/thumbnail/";


    /**
     * API Key
     * If you change here, you need to update in server.
     */
    public static final String API_KEY = "teampsisthebest";


    /**
     * For default language change, please check
     * LanguageFragment for language code and country code
     * ..............................................................
     * Language             | Language Code     | Country Code
     * ..............................................................
     * "English"            | "en"              | ""
     * "Arabic"             | "ar"              | ""
     * "Chinese (Mandarin)" | "zh"              | ""
     * "French"             | "fr"              | ""
     * "German"             | "de"              | ""
     * "India (Hindi)"      | "hi"              | "rIN"
     * "Indonesian"         | "in"              | ""
     * "Italian"            | "it"              | ""
     * "Japanese"           | "ja"              | ""
     * "Korean"             | "ko"              | ""
     * "Malay"              | "ms"              | ""
     * "Portuguese"         | "pt"              | ""
     * "Russian"            | "ru"              | ""
     * "Spanish"            | "es"              | ""
     * "Thai"               | "th"              | ""
     * "Turkish"            | "tr"              | ""
     * ..............................................................
     */
    public static final String LANGUAGE_CODE = "en";
    public static final String DEFAULT_LANGUAGE_COUNTRY_CODE = "";
    public static final String DEFAULT_LANGUAGE = LANGUAGE_CODE;

    /**
     * Loading Limit Count Setting
     */
    public static final int API_SERVICE_CACHE_LIMIT = 5; // Minutes Cache

    public static int RATING_COUNT = 30;

    public static int ITEM_COUNT = 30;

    public static int LIST_CATEGORY_COUNT = 30;
    public static int LIST_FILTER_CATEGORY_COUNT = 30;
    public static int LIST_SEARCH_CATEGORY_COUNT = 30;
    public static int LIST_CONDITION_COUNT = 30;
    public static int LIST_CURRENCY_COUNT = 30;
    public static int LIST_DEAL_OPTION_COUNT = 30;
    public static int LIST_LOCATION_COUNT = 30;
    public static int LIST_PRICE_TYPE_COUNT = 30;
    public static int LIST_TYPE_COUNT = 30;
    public static int LIST_SEARCH_SUB_CAT_COUNT = 30;

    public static int LIST_NEW_FEED_COUNT = 30;

    public static int NOTI_LIST_COUNT = 30;

    public static int COMMENT_COUNT = 30;

    public static int LIST_NEW_FEED_COUNT_PAGER = 10; // cannot equal 15

    public static int HISTORY_COUNT = 30;

    public static int REPORTED_ITEM_COUNT = 30;

    public static int BLOCK_USER_COUNT = 30;

    public static int CHAT_HISTORY_COUNT = 30;

    public static int OFFER_LIST_COUNT = 30;

    public static int LIST_OFFLINE_COUNT = 30;

    public static int LOGIN_USER_APPROVED_ITEM_COUNT = 6;

    public static int LOGIN_USER_PENDING_ITEM_COUNT = 6;

    public static int LOGIN_USER_REJECTED_ITEM_COUNT = 6;

    public static int LOGIN_USER_DISABLED_ITEM_COUNT = 6;

    public static int PAID_ITEM_COUNT = 6;

    public static final String LIMIT_FROM_DB_COUNT = "10";

    /**
     * Price Format
     * Need to change according to your format that you need
     * E.g.
     * ",###.00"   => 2,555.00
     * "###.00"    => 2555.00
     * ".00"       => 2555.00
     * ",###"      => 2555
     * ",###,0"    => 2555.0
     */
    public static final String DECIMAL_PLACES_FORMAT = ",###.00";
    /**
     * Show currency at font or back
     * true => $ 2,555.00
     * false => 2,555,00 $
     */
    public static final boolean SYMBOL_SHOW_FRONT = true;


    /**
     * Region playstore
     */
    public static String PLAYSTORE_MARKET_URL_FIX = "market://details?id=";
    public static String PLAYSTORE_HTTP_URL_FIX = "http://play.google.com/store/apps/details?id=";

    /**
     * Image Cache and Loading
     */
    public static int IMAGE_CACHE_LIMIT = 250; // Mb
    public static boolean PRE_LOAD_FULL_IMAGE = true;


    /**
     * Admob Setting
     */
    public static final Boolean SHOW_ADMOB = true;


    /**
     * Firebase Configs
     */
    public static final String SUCCESSFULLY_DELETED = "deleted";
    public static final String SUCCESSFULLY_SAVED = "saved";
    public static final String CHAT = "Chat";
    public static final String MESSAGE = "Message";
    public static final String USER_PRESENCE = "User_Presence";
    public static final String CHAT_WITH = "Current_Chat_With";
    public static final String ACTIVE = "Online (Active)";
    public static final String INACTIVE = "Online (Inactive)";
    public static final String OFFLINE = "OFFLINE";
//    public static final String FROMUSERID ="FROMUSERID";
//    public static final String TOUSERID ="TOUSERID";


    /**
     * GDPR Configs
     */
    public static String CONSENTSTATUS_PERSONALIZED = "PERSONALIZED";
    public static String CONSENTSTATUS_NON_PERSONALIZED = "NON_PERSONALIZED";
    public static String CONSENTSTATUS_UNKNOWN = "UNKNOWN";
    public static String CONSENTSTATUS_CURRENT_STATUS = "UNKNOWN";
    public static String CONSENTSTATUS_IS_READY_KEY = "CONSENTSTATUS_IS_READY";

    /**
     * Policy Url
     */
    public static String POLICY_URL = "http://www.panacea-soft.com/policy/policy.html";

    /**
     * URI Authority File
     */
    public static String AUTHORITYFILE = ".fileprovider";

    /**
     * Facebook login Config
     */
    public static boolean ENABLE_FACEBOOK_LOGIN = true;

    /**
     * Google login Config
     */
    public static boolean ENABLE_GOOGLE_LOGIN = true;

    /**
     * Phone login Config
     */
    public static boolean ENABLE_PHONE_LOGIN = true;

    /**
     * Show SubCategory
     */
    public static boolean SHOW_SUBCATEGORY = true;

    /**
     * New item upload setting
     */
    public static boolean CLOSE_ENTRY_AFTER_SUBMIT = true;

    /**
     * Default Camera Config
     * <p>
     * Constants.DEFAULT_CAMERA   // Build In Default Camera
     * Constants.CUSTOM_CAMERA    // Custom Camera
     */
    public static String CAMERA_CONFIG = Constants.DEFAULT_CAMERA;

    /**
     * Error Codes
     */
    public static int ERROR_CODE_10001 = 10001; // Totally No Record
    public static int ERROR_CODE_10002 = 10002; // No More Record at pagination

    /**
     * Compress Image
     */
    public static boolean isCompressImage = true;
    public static float uploadImageHeight = 1024;
    public static float uploadImageWidth = 1024;
    public static float profileImageHeight = 512;
    public static float profileImageWidth = 512;
    public static float chatImageHeight = 650;
    public static float chatImageWidth = 650;

    /**
     * Promote Item
     */
    public static int PROMOTE_FIRST_CHOICE_DAY_OR_DEFAULT_DAY = 7;
    public static int PROMOTE_SECOND_CHOICE_DAY = 14;
    public static int PROMOTE_THIRD_CHOICE_DAY = 30;
    public static int PROMOTE_FOURTH_CHOICE_DAY = 60;
    public static String PROMOTE_DEFAULT_ONE_DAY_PRICE = "10";

    /**
     * Razor
     */
    public static boolean isRazorSupportMultiCurrency = false;
    public static String defaultRazorCurrency = "INR";


    /**
     * Free Price
     */
    public static String freePrice = "$ .00";
}
