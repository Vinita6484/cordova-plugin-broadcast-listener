package com.example.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

public class IntentListener extends BroadcastReceiver {
    public static CallbackContext callbackContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (callbackContext != null) {
            try {
                JSONObject result = new JSONObject();
                result.put("action", intent.getAction());
                result.put("data", intent.getStringExtra("com.symbol.datawedge.data_string")); // Zebra scan key
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            } catch (Exception e) {
                callbackContext.error("Error: " + e.getMessage());
            }
        }
    }
}
