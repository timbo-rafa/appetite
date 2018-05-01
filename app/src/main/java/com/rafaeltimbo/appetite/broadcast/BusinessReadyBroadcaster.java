package com.rafaeltimbo.appetite.broadcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.rafaeltimbo.appetite.model.Business;
import com.rafaeltimbo.appetite.utils.Constants;

public class BusinessReadyBroadcaster {
    private Context context;
    private static final String TAG = "Appetite.BusinessReadyBroadcaster";

    public BusinessReadyBroadcaster(Context context) {
        this.context = context;
    }

    public void sendBroadcastBusinessReady(Business business) {
        System.out.println(TAG +  ":" + business);
        Intent intent = new Intent();
        intent.setAction(Constants.BROADCAST_BUSINESS_READY);
        Bundle data = new Bundle();
        data.putParcelable(Constants.BUSINESS, business);
        intent.putExtras(data);
        context.sendBroadcast(intent);
    }
}
