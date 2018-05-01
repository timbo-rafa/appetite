package com.rafaeltimbo.appetite.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.rafaeltimbo.appetite.utils.Constants;

public class RangeUpdateReceiver extends BroadcastReceiver {
    private String TAG = "Appetite.RangeUpdateReceiver";
    private RangeUpdateReceiver.OnReceive customOnReceive;

    public interface OnReceive {
        void onReceive();
    }

    public RangeUpdateReceiver( RangeUpdateReceiver.OnReceive customOnReceive ){
        this.customOnReceive = customOnReceive;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle data = intent.getExtras();

        if (Constants.BROADCAST_RANGE_UPDATE.equals(action)) {
            if (this.customOnReceive != null) {
                this.customOnReceive.onReceive();
            }

        } else {
            System.out.println(TAG + " Nothing to do for action " + action);
        }

    }

    public IntentFilter getIntentFilter() {
        System.out.println(TAG + " registering service state change receiver...");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_RANGE_UPDATE);
        return intentFilter;
    }
}
