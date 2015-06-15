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

import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.GenieJSON;
import com.getgenieapp.android.GenieApplication;
import com.getgenieapp.android.R;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONObject;

import java.io.IOException;

public class UpdateIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public UpdateIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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
                    public void onUpdate(boolean status) {
                        try {
                            // Subscribe to topic channels
                            subscribeTopics(token);

                            // You should store a boolean that indicates whether the generated token has been
                            // sent to your server. If the boolean is false, send the token to your server,
                            // otherwise your server should have already received the token.
                            if (status)
                                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                            else
                                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
                            // [END register_for_gcm]
                            // Notify UI that registration has completed, so the progress indicator can be hidden.
                        } catch (Exception e) {
                            Log.d(TAG, "Failed to complete token refresh", e);
                            // If an exception happens while fetching the new token or updating our registration data
                            // on a third-party server, this ensures that we'll attempt the update at a later time.
                            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
                        }
                        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
                        LocalBroadcastManager.getInstance(UpdateIntentService.this).sendBroadcast(registrationComplete);
                    }
                });


            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, onTokenUpdate onUpdate) {
        SharedPreferences sharedPreferences = GenieApplication.getInstance().getSecurePrefs();
        if (sharedPreferences.getString(DataFields.TOKEN, null) != null) {
            JSONObject json = new GenieJSON(this);

//            Ion.with(this)
//                    .load(DataFields.getServerUrl() + DataFields.UPDATEGCMURL)
//                    .setJsonObjectBody((JsonObject) new JsonParser().parse(json.toString()))
//                    .asJsonObject()
//                    .setCallback(new FutureCallback<JsonObject>() {
//                        @Override
//                        public void onCompleted(Exception e, JsonObject result) {
//
//                        }
//                    });

            // ToDo add volley
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
        public void onUpdate(boolean status);
    }
}

