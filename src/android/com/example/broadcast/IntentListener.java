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

import java.util.Set;

public class IntentListener extends BroadcastReceiver {
    private static final String TAG = "IntentListener";
    
    // FIX 1: Make references non-static and private
    private CordovaInterface cordova;
    private CallbackContext callbackContext;
    private boolean isRegistered = false; // Used by the main plugin class

    // Setters injected by BroadcastPlugin.java
    public void setCordovaContext(CordovaInterface cordova, CallbackContext callbackContext) {
        this.cordova = cordova;
        this.callbackContext = callbackContext;
    }

    public void setRegistered(boolean registered) {
        this.isRegistered = registered;
    }
    
    public boolean isRegistered() {
        return isRegistered;
    }
    
    public CallbackContext getCallbackContext() {
        return callbackContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check for null context/callback immediately
        if (callbackContext == null || cordova == null) {
            Log.w(TAG, "CallbackContext is null. Cannot send result to JavaScript.");
            return;
        }

        // FIX 2: All Cordova results MUST run on the UI thread
        cordova.getActivity().runOnUiThread(() -> {
            try {
                // FIX 3: Process Intent data into a structured JSON object
                JSONObject result = new JSONObject();
                
                String actualAction = intent.getAction();
                result.put("action", actualAction);
                result.put("flags", intent.getFlags());
                
                if (intent.getData() != null) {
                    result.put("dataUri", intent.getData().toString());
                }

                JSONObject extrasJson = new JSONObject();
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    for (String key : extras.keySet()) {
                        Object value = extras.get(key);
                        // JSONObject.wrap handles basic types and converts them safely
                        extrasJson.put(key, JSONObject.wrap(value)); 
                    }
                }
                result.put("extras", extrasJson);
                
                // Send the structured JSON object back to JavaScript
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
                pluginResult.setKeepCallback(true); // Keep the connection open
                callbackContext.sendPluginResult(pluginResult);

            } catch (JSONException e) {
                Log.e(TAG, "Error packaging intent data into JSON: " + e.getMessage());
                
                // Send an error result if JSON creation fails
                PluginResult errorResult = new PluginResult(PluginResult.Status.ERROR, "Failed to package intent data.");
                errorResult.setKeepCallback(true);
                callbackContext.sendPluginResult(errorResult);
            }
        });
    }
}