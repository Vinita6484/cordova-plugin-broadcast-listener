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
        if (callbackContext == null) {
            PluginResult error = new PluginResult(PluginResult.Status.ERROR, "CallbackContext is null");
            error.setKeepCallback(true);
            return;
        }


        // ✅ Alert: Broadcast received
        sendAlert("Broadcast received");

        // ✅ Alert: Intent action
        String action = intent.getAction();
        sendAlert("Action: " + action);

        // ✅ Alert: Intent flags
        sendAlert("Flags: " + intent.getFlags());

        // ✅ Alert: Intent data URI
        if (intent.getData() != null) {
            sendAlert("Data URI: " + intent.getData().toString());
        }

        // ✅ Alert: Intent categories
        Set<String> categories = intent.getCategories();
        if (categories != null) {
            for (String category : categories) {
                sendAlert("Category: " + category);
            }
        }

        // ✅ Alert: All extras
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                sendAlert("Extra - " + key + ": " + String.valueOf(value));
            }
        }

        // ✅ Alert: Specific DataWedge payload
        String data = intent.getStringExtra("com.symbol.datawedge.data_string");
        if (data != null) {
            sendAlert("DataWedge: " + data);
        } else {
            sendAlert("No DataWedge string found");
        }
    }

    // 🔧 Helper method to send alerts to JS
    private void sendAlert(String message) {
        PluginResult result = new PluginResult(PluginResult.Status.OK, message);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }
}
