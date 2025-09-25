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
                    cordova.getActivity().registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
                } else {
                    cordova.getActivity().registerReceiver(receiver, filter);
                }

                PluginResult registered = new PluginResult(PluginResult.Status.OK, "Receiver registered");
                registered.setKeepCallback(true);
                callbackContext.sendPluginResult(registered);
            }

            PluginResult keepAlive = new PluginResult(PluginResult.Status.NO_RESULT);
            keepAlive.setKeepCallback(true);
            callbackContext.sendPluginResult(keepAlive);
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
