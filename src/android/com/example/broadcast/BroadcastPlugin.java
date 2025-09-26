package com.example.broadcast;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import org.apache.cordova.*;
import org.json.JSONArray;

public class BroadcastPlugin extends CordovaPlugin {
    private static final String TAG = "BroadcastPlugin";
    private IntentListener receiver;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        Log.d(TAG, "execute called with action: " + action);

        if ("startListening".equals(action)) {
            Log.d(TAG, "startListening invoked");

            if (receiver == null) {
                receiver = new IntentListener();
                Log.d(TAG, "Created new IntentListener instance");
            }

            receiver.setCordovaContext(this.cordova, callbackContext);

            if (!receiver.isRegistered()) {
                Log.d(TAG, "Receiver not registered yet, proceeding to register");

                IntentFilter filter = new IntentFilter("com.example.broadcast.SCAN");
                Context context = cordova.getActivity().getApplicationContext();

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
                        Log.d(TAG, "Receiver registered with RECEIVER_EXPORTED");
                    } else {
                        context.registerReceiver(receiver, filter);
                        Log.d(TAG, "Receiver registered normally");
                    }

                    receiver.setRegistered(true);
                    Log.d(TAG, "Receiver registration flag set to true");

                    PluginResult registered = new PluginResult(PluginResult.Status.NO_RESULT);
                    registered.setKeepCallback(true);
                    callbackContext.sendPluginResult(registered);
                    Log.d(TAG, "Callback kept alive after registration");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to register receiver: " + e.getMessage(), e);
                    callbackContext.error("Failed to register receiver: " + e.getMessage());
                }
            } else {
                Log.d(TAG, "Receiver already registered, keeping callback alive");
                PluginResult keepAlive = new PluginResult(PluginResult.Status.NO_RESULT);
                keepAlive.setKeepCallback(true);
                callbackContext.sendPluginResult(keepAlive);
            }
            return true;
        }

        if ("stopListening".equals(action)) {
            Log.d(TAG, "stopListening invoked");

            if (receiver != null && receiver.isRegistered()) {
                Context context = cordova.getActivity().getApplicationContext();
                context.unregisterReceiver(receiver);
                receiver.setRegistered(false);
                Log.d(TAG, "Receiver unregistered");
            }

            if (receiver != null && receiver.getCallbackContext() != null) {
                receiver.getCallbackContext().success("Stopped listening");
                Log.d(TAG, "CallbackContext notified of stop");
            }

            return true;
        }

        Log.w(TAG, "Unknown action received: " + action);
        return false;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");

        if (receiver != null && receiver.isRegistered()) {
            Context context = cordova.getActivity().getApplicationContext();
            context.unregisterReceiver(receiver);
            receiver.setRegistered(false);
            Log.d(TAG, "Receiver unregistered during onDestroy");
        }

        if (receiver != null && receiver.getCallbackContext() != null) {
            receiver.getCallbackContext().success("Plugin destroyed.");
            Log.d(TAG, "CallbackContext notified of plugin destruction");
        }
    }
}
