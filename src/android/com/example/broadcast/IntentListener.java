package com.example.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

public class IntentListener extends BroadcastReceiver {
    public static CallbackContext callbackContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (callbackContext != null) {
            String data = intent.getStringExtra("com.symbol.datawedge.data_string"); // Adjust key as needed
            PluginResult result = new PluginResult(PluginResult.Status.OK, data);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
        }
    }
}
