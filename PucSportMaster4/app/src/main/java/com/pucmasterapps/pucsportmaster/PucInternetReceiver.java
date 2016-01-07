package com.pucmasterapps.pucsportmaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PucInternetReceiver extends BroadcastReceiver {
    private static final String TAG = "internet receiver";

    public PucInternetReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       Log.i(TAG, "Ste offline");
    }
}
