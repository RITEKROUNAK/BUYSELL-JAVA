package com.panaceasoft.psbuyandsell.viewobject.holder;

import com.panaceasoft.psbuyandsell.utils.Constants;

import java.io.Serializable;

public class ItemParameterHolder implements Serializable {

    public String keyword, cat_id, sub_cat_id,type_id,price_type_id,condition_id,deal_option_id,currency_id,location_id,
            type_name,price_type_name,condition_name,deal_option_name,currency_name,location_name,
            order_by, order_type, lat, lng, mapMiles,max_price, min_price,userId,isPaid, cityLat, cityLng, status;

    public ItemParameterHolder() {
        this.keyword = "";
        this.cat_id = "";
        this.sub_cat_id = "";
        this.type_id = "";
        this.price_type_id = "";
        this.condition_id = "";
        this.deal_option_id = "";
        this.currency_id = "";
        this.location_id = "";
        this.type_name = "";
        this.price_type_name = "";
        this.condition_name = "";
        this.deal_option_name = "";
        this.currency_name = "";
        this.location_name = "";
        this.order_by = Constants.FILTERING_ADDED_DATE;
        this.order_type = Constants.FILTERING_DESC;
        this.lat = "";
        this.lng = "";
        this.cityLat = "";
        this.cityLng = "";
        this.mapMiles = "";
        this.max_price = "";
        this.min_price = "";
        this.userId = "";
        this.isPaid = "";
        this.status = "1";

    }

    public ItemParameterHolder getPopularItem()
    {
        this.keyword = "";
        this.cat_id = "";
        this.sub_cat_id = "";
        this.type_id = "";
        this.price_type_id = "";
        this.condition_id = "";
        this.deal_option_id = "";
        this.currency_id = "";
        this.location_id = "";
        this.type_name = "";
        this.price_type_name = "";
        this.condition_name = "";
        this.deal_option_name = "";
        this.currency_name = "";
        this.location_name = "";
        this.order_by = Constants.FILTERING_TRENDING;
        this.order_type = Constants.FILTERING_DESC;
        this.lat = "";
        this.lng = "";
        this.cityLat = "";
        this.cityLng = "";
        this.mapMiles = "";
        this.max_price = "";
        this.min_price = "";
        this.userId = "";
        this.isPaid = "";
        this.status = "1";

        return this;
    }

    public ItemParameterHolder getRecentItem()
    {
        this.keyword = "";
        this.cat_id = "";
        this.sub_cat_id = "";
        this.type_id = "";
        this.price_type_id = "";
        this.condition_id = "";
        this.deal_option_id = "";
        this.currency_id = "";
        this.location_id = "";
        this.type_name = "";
        this.price_type_name = "";
        this.condition_name = "";
        this.deal_option_name = "";
        this.currency_name = "";
        this.location_name = "";
        this.order_by = Constants.FILTERING_ADDED_DATE;
        this.order_type = Constants.FILTERING_DESC;
        this.lat = "";
        this.lng = "";
        this.cityLat = "";
        this.cityLng = "";
        this.mapMiles = "";
        this.max_price = "";
        this.min_price = "";
        this.userId = "";
        this.isPaid =Constants.PAIDITEMFIRST;
        this.status = "1";

        return this;
    }

    public ItemParameterHolder getFeaturedItem()
    {
        this.keyword = "";
        this.cat_id = "";
        this.sub_cat_id = "";
        this.type_id = "";
        this.price_type_id = "";
        this.condition_id = "";
        this.deal_option_id = "";
        this.currency_id = "";
        this.location_id = "";
        this.type_name = "";
        this.price_type_name = "";
        this.condition_name = "";
        this.deal_option_name = "";
        this.currency_name = "";
        this.location_name = "";
        this.order_by = Constants.FILTERING_ADDED_DATE;
        this.order_type = Constants.FILTERING_DESC;
        this.lat = "";
        this.lng = "";
        this.cityLat = "";
        this.cityLng = "";
        this.mapMiles = "";
        this.max_price = "";
        this.min_price = "";
        this.userId = "";
        this.isPaid =Constants.ONLYPAIDITEM;
        this.status = "1";

        return this;
    }


    public String getItemMapKey(){

        String result = "";

        if (!keyword.isEmpty()) {
            result += keyword + ":";
        }

        if (!cat_id.isEmpty()) {
            result += cat_id + ":";
        }

        if (!sub_cat_id.isEmpty()) {
            result += sub_cat_id + ":";
        }

        if (!type_id.isEmpty()) {
            result += type_id + ":";
        }

        if (!price_type_id.isEmpty()) {
            result += price_type_id + ":";
        }

        if (!condition_id.isEmpty()) {
            result += condition_id + ":";
        }

        if (!deal_option_id.isEmpty()) {
            result += deal_option_id + ":";
        }

        if (!order_by.isEmpty()) {
            result += order_by + ":";
        }

        if (!order_type.isEmpty()) {
            result += order_type + ":";
        }

        if (!max_price.isEmpty()) {
            result += max_price + ":";
        }

        if (!min_price.isEmpty()) {
            result += min_price + ":";
        }

        if(!lat.isEmpty() && !lng.isEmpty() && !mapMiles.isEmpty()){
            result += "";
        }else {
            if (!location_id.isEmpty()) {
                result += location_id + ":";
            }
        }

        if (!lat.isEmpty()) {
            result += lat + ":";
        }

        if (!lng.isEmpty()) {
            result += lng + ":";
        }

        if (!mapMiles.isEmpty()) {
            result += mapMiles + ":";
        }

        if (!userId.isEmpty()) {
            result += userId + ":";
        }

        if (!isPaid.isEmpty()){
            result += isPaid + ":";
        }

        if (!status.isEmpty()){
            result += status + ":";
        }
        return result;
    }
}
