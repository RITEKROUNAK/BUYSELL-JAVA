package com.panaceasoft.psbuyandsell.utils;

import android.content.Intent;

/**
 * Created by Panacea-Soft on 8/11/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class FirebaseInstanceIDService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {

        super.onNewToken(token);
        Utils.psLog("token : " + token);

        Intent in = new Intent();
        in.putExtra("message",token);
        in.setAction("NOW");

    }

}
