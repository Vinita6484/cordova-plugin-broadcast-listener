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
            // Send debug alert to JS: Broadcast received
            PluginResult debugLog = new PluginResult(PluginResult.Status.OK, "Broadcast received");
            debugLog.setKeepCallback(true);
            callbackContext.sendPluginResult(debugLog);

            // Extract actual payload
            String data = intent.getStringExtra("com.symbol.datawedge.data_string");
            PluginResult result = new PluginResult(PluginResult.Status.OK, data);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
        }
    }
}
