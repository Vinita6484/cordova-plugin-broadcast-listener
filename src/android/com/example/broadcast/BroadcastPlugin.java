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
            // CRITICAL FIX: Set the non-static callback on the receiver instance
            if (receiver == null) {
                receiver = new IntentListener();
            }
            // 1. Pass a reference to the main plugin instance and the callback context
            receiver.setCordovaContext(this.cordova, callbackContext); 

            if (!receiver.isRegistered()) {
                IntentFilter filter = new IntentFilter("com.example.broadcast.SCAN");

                // FIX: Use Application Context for safety
                Context context = cordova.getActivity().getApplicationContext(); 

                // Use a private field or check in receiver to see if it's registered
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
                    } else {
                        context.registerReceiver(receiver, filter);
                    }
                    receiver.setRegistered(true);

                    // Send initial success and keep callback alive
                    PluginResult registered = new PluginResult(PluginResult.Status.NO_RESULT);
                    registered.setKeepCallback(true); 
                    callbackContext.sendPluginResult(registered);
                } catch (Exception e) {
                    callbackContext.error("Failed to register receiver: " + e.getMessage());
                }
            } else {
                // If already registered, just keep the callback alive (might update it)
                PluginResult keepAlive = new PluginResult(PluginResult.Status.NO_RESULT);
                keepAlive.setKeepCallback(true);
                callbackContext.sendPluginResult(keepAlive);
            }
            return true;
        }

        if ("stopListening".equals(action)) {
            if (receiver != null && receiver.isRegistered()) {
                // FIX: Use Application Context for consistency
                Context context = cordova.getActivity().getApplicationContext(); 
                context.unregisterReceiver(receiver);
                receiver.setRegistered(false);
            }
            // IMPORTANT: End the callback when stopping
            if (receiver != null && receiver.getCallbackContext() != null) {
                receiver.getCallbackContext().success("Stopped listening");
            }
            // receiver = null; // Can optionally null out the receiver here
            return true;
        }

        return false;
    }

    @Override
    public void onDestroy() {
        if (receiver != null && receiver.isRegistered()) {
            // FIX: Use Application Context for consistency
            Context context = cordova.getActivity().getApplicationContext(); 
            context.unregisterReceiver(receiver);
            receiver.setRegistered(false);
        }
        // IMPORTANT: Clean up the callback if the plugin is destroyed
        if (receiver != null && receiver.getCallbackContext() != null) {
            receiver.getCallbackContext().success("Plugin destroyed.");
        }
    }
}