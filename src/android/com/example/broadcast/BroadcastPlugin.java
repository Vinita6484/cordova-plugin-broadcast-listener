package com.example.broadcast;

import android.content.IntentFilter;
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
                cordova.getActivity().registerReceiver(receiver, filter);
            }

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

        return false; // Important: return false if action is unrecognized
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            cordova.getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
