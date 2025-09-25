package com.example.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;

import java.util.Set;

public class IntentListener extends BroadcastReceiver {
    public static CallbackContext callbackContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Alert: Broadcast received
        PluginResult received = new PluginResult(PluginResult.Status.OK, "Broadcast received");
        received.setKeepCallback(true);
        callbackContext.sendPluginResult(received);

        // Alert: Intent action
        String action = intent.getAction();
        PluginResult actionResult = new PluginResult(PluginResult.Status.OK, "Action: " + action);
        actionResult.setKeepCallback(true);
        callbackContext.sendPluginResult(actionResult);

        // Alert: Intent flags
        PluginResult flagsResult = new PluginResult(PluginResult.Status.OK, "Flags: " + intent.getFlags());
        flagsResult.setKeepCallback(true);
        callbackContext.sendPluginResult(flagsResult);

        // Alert: Intent data URI
        if (intent.getData() != null) {
            PluginResult dataUriResult = new PluginResult(PluginResult.Status.OK, "Data URI: " + intent.getData().toString());
            dataUriResult.setKeepCallback(true);
            callbackContext.sendPluginResult(dataUriResult);
        }

        // Alert: Intent categories
        Set<String> categories = intent.getCategories();
        if (categories != null) {
            for (String category : categories) {
                PluginResult categoryResult = new PluginResult(PluginResult.Status.OK, "Category: " + category);
                categoryResult.setKeepCallback(true);
                callbackContext.sendPluginResult(categoryResult);
            }
        }

        // Alert: All extras
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                PluginResult extraResult = new PluginResult(PluginResult.Status.OK, key + ": " + String.valueOf(value));
                extraResult.setKeepCallback(true);
                callbackContext.sendPluginResult(extraResult);
            }
        }

        // Alert: Specific DataWedge payload
        String data = intent.getStringExtra("com.symbol.datawedge.data_string");
        if (data != null) {
            PluginResult payload = new PluginResult(PluginResult.Status.OK, "DataWedge: " + data);
            payload.setKeepCallback(true);
            callbackContext.sendPluginResult(payload);
        } else {
            PluginResult noData = new PluginResult(PluginResult.Status.OK, "No DataWedge string found");
            noData.setKeepCallback(true);
            callbackContext.sendPluginResult(noData);
        }
    }
}
