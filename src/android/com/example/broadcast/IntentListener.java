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
            sendAlert("❌ CallbackContext is null — cannot send result to JavaScript");
            return;
        }

        String expectedAction = "com.example.broadcast.SCAN";
        String actualAction = intent.getAction();

        sendAlert("📡 Broadcast received");
        sendAlert("Action: " + actualAction);

        if (!expectedAction.equals(actualAction)) {
            sendAlert("⚠️ Action mismatch: expected '" + expectedAction + "', but received '" + actualAction + "'");
        }

        sendAlert("Flags: " + intent.getFlags());

        if (intent.getData() != null) {
            sendAlert("Data URI: " + intent.getData().toString());
        }

        Set<String> categories = intent.getCategories();
        if (categories != null) {
            for (String category : categories) {
                sendAlert("Category: " + category);
            }
        }

        Bundle extras = intent.getExtras();
        if (extras != null && !extras.isEmpty()) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                sendAlert("Extra - " + key + ": " + String.valueOf(value));
            }
        } else {
            sendAlert("No extras found in intent");
        }
    }

    private void sendAlert(String message) {
        PluginResult result = new PluginResult(PluginResult.Status.OK, message);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }
}
