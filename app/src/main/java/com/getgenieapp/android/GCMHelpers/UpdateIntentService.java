package com.getgenieapp.android.GCMHelpers;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.GenieApplication;
import com.getgenieapp.android.R;
import com.getgenieapp.android.SecurePreferences.SecurePreferences;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public UpdateIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final SecurePreferences securePreferences = GenieApplication.getInstance().getSecurePrefs();

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(this);
                final String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, "GCM Registration Token: " + token);

                sendRegistrationToServer(token, new onTokenUpdate() {
                    @Override
                    public void onUpdate(boolean status, boolean verify) {
                        try {
                            // Subscribe to topic channels
                            subscribeTopics(token);
                        } catch (Exception e) {
                            Log.d(TAG, "Failed to complete token refresh", e);
                        }
                        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
                        registrationComplete.putExtra("status", status);
                        registrationComplete.putExtra("verify", verify);
                        LocalBroadcastManager.getInstance(UpdateIntentService.this).sendBroadcast(registrationComplete);
                    }
                });


            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }
    }

    int i = 0;

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token, final onTokenUpdate onTokenUpdate) {
        if (i < 2) {
            final SharedPreferences sharedPreferences = GenieApplication.getInstance().getSecurePrefs();
            if (sharedPreferences.getString(DataFields.TOKEN, null) != null) {
                JSONObject json = new JSONObject();
                try {
                    json.put("gcm_token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, DataFields.getServerUrl() + DataFields.UPDATEGCMURL, "", new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        try {
                            onTokenUpdate.onUpdate(response.has("gcm_updated"), response.getBoolean("verified"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        i++;
                        sendRegistrationToServer(token, onTokenUpdate);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        System.out.println(sharedPreferences.getString(DataFields.TOKEN, ""));
                        params.put("x-access-token", sharedPreferences.getString(DataFields.TOKEN, ""));
                        return params;
                    }
                };
                GenieApplication.getInstance().addToRequestQueue(req);

            }
        }
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

    private interface onTokenUpdate {
        public void onUpdate(boolean status, boolean verify);
    }
}

