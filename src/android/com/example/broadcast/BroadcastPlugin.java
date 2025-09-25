package com.example.broadcast;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import org.apache.cordova.*;
import org.json.JSONArray;

public class BroadcastPlugin extends CordovaPlugin {
    private IntentListener receiver;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if ("startListening".equals(action)) {
            IntentListener.callbackContext = callbackContext;

            if (receiver == null) {
                receiver = new IntentListener();
                IntentFilter filter = new IntentFilter("com.zebra.broadcast.SCAN");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    cordova.getActivity().registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
                } else {
                    cordova.getActivity().registerReceiver(receiver, filter);
                }

                // Send alert to JS: Receiver registered
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "Receiver registered");
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }

            // Keep callback alive for future broadcasts
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }

        if ("stopListening".equals(action)) {
            if (receiver != null) {
                cordova.getActivity().unregisterReceiver(receiver);
                receiver = null;
            }
            callbackContext.success("Stopped listening");
            return true;
        }

        return false;
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            cordova.getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
