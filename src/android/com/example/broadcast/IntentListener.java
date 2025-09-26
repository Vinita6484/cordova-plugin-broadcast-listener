package com.example.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

public class IntentListener extends BroadcastReceiver {
    private static final String TAG = "IntentListener";

    private CordovaInterface cordova;
    private CallbackContext callbackContext;
    private boolean isRegistered = false;

    public void setCordovaContext(CordovaInterface cordova, CallbackContext callbackContext) {
        this.cordova = cordova;
        this.callbackContext = callbackContext;
        Log.d(TAG, "Cordova context and callback set");
    }

    public void setRegistered(boolean registered) {
        this.isRegistered = registered;
        Log.d(TAG, "Receiver registration flag set to: " + registered);
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public CallbackContext getCallbackContext() {
        return callbackContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive triggered");

        if (callbackContext == null || cordova == null) {
            Log.w(TAG, "CallbackContext or CordovaInterface is null. Cannot send result to JavaScript.");
            return;
        }

        Log.d(TAG, "Processing intent on UI thread");
        cordova.getActivity().runOnUiThread(() -> {
            try {
                JSONObject result = new JSONObject();

                String actualAction = intent.getAction();
                result.put("action", actualAction);
                result.put("flags", intent.getFlags());
                Log.d(TAG, "Intent action: " + actualAction);
                Log.d(TAG, "Intent flags: " + intent.getFlags());

                if (intent.getData() != null) {
                    String dataUri = intent.getData().toString();
                    result.put("dataUri", dataUri);
                    Log.d(TAG, "Intent data URI: " + dataUri);
                }

                JSONObject extrasJson = new JSONObject();
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    for (String key : extras.keySet()) {
                        Object value = extras.get(key);
                        extrasJson.put(key, JSONObject.wrap(value));
                        Log.d(TAG, "Extra key: " + key + ", value: " + value);
                    }
                } else {
                    Log.d(TAG, "No extras found in intent");
                }

                result.put("extras", extrasJson);
                Log.d(TAG, "Final JSON payload: " + result.toString());

                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
                Log.d(TAG, "PluginResult sent to JavaScript");

            } catch (JSONException e) {
                Log.e(TAG, "Error packaging intent data into JSON: " + e.getMessage(), e);

                PluginResult errorResult = new PluginResult(PluginResult.Status.ERROR, "Failed to package intent data.");
                errorResult.setKeepCallback(true);
                callbackContext.sendPluginResult(errorResult);
                Log.d(TAG, "Error PluginResult sent to JavaScript");
            }
        });
    }
}
