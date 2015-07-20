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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.getgenieapp.android.Activities.SplashScreenActivity;
import com.getgenieapp.android.Database.DBDataSource;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.GetDate;
import com.getgenieapp.android.Extras.NotificationHandler;
import com.getgenieapp.android.Extras.Utils;
import com.getgenieapp.android.GenieApplication;
import com.getgenieapp.android.Objects.Chat;
import com.getgenieapp.android.Objects.MessageValues;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;
import com.google.android.gms.gcm.GcmListenerService;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        MixpanelAPI mixpanel =
                MixpanelAPI.getInstance(this, getString(R.string.mixpanel));
        mixpanel.getPeople().identify(new Utils(this).getDeviceSerialNumber());
        mixpanel.getPeople().initPushHandling(getString(R.string.projectId));

        if (data.containsKey("alertmsg")) {
            try {
                JSONObject jsonObject = new JSONObject(data.getString("alertmsg"));
                new NotificationHandler(this).notification(DataFields.ALERTMSG, jsonObject.getString("alert"));
                GenieApplication.getInstance().getSecurePrefs().edit().putBoolean("agent", true).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (data.containsKey("msg")) {
            int cid = 0;
            int aid = 0;
            int category = 0;
            String text = "";
            int status = 0;
            int sender_id = 0;
            long created_at = 0;
            long updated_at = 0;
            String id = "";
            double lng = 0;
            double lat = 0;
            String url = "";
            try {
                JSONObject jsonObject = new JSONObject(data.getString("msg"));
                if (jsonObject.has("cid")) {
                    cid = jsonObject.getInt("cid");
                }
                if (jsonObject.has("_id")) {
                    id = jsonObject.getString("_id");
                }
                if (jsonObject.has("chat")) {
                    JSONObject chat = jsonObject.getJSONObject("chat");
                    if (chat.has("aid")) {
                        aid = chat.getInt("aid");
                    }
                    if (chat.has("message")) {
                        JSONObject message = chat.getJSONObject("message");
                        if (message.has("category")) {
                            category = message.getInt("category");
                        }
                        if (message.has("status")) {
                            status = message.getInt("status");
                        }
                        if (message.has("sender_id")) {
                            sender_id = message.getInt("sender_id");
                        }
                        if (message.has("created_at")) {
                            created_at = message.getLong("created_at");
                        }
                        if (message.has("updated_at")) {
                            updated_at = message.getLong("updated_at");
                        }
                        if (message.has("category_value")) {
                            JSONObject category_value = message.getJSONObject("category_value");
                            if (category == 1) {
                                if (category_value.has("text"))
                                    text = category_value.getString("text");
                            } else if (category == 2) {
                                if (category_value.has("lng"))
                                    lng = category_value.getDouble("lng");
                                if (category_value.has("lat"))
                                    lat = category_value.getDouble("lat");
                                if (category_value.has("text"))
                                    text = category_value.getString("text");
                            } else if (category == 3) {
                                if (category_value.has("text"))
                                    text = category_value.getString("text");
                                if (category_value.has("url"))
                                    url = category_value.getString("url");
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Chat chat = new Chat(cid, aid, category, text, status, sender_id, created_at, updated_at, id, lng, lat, url);
            MessageValues messageValues = null;
            if (chat.getCategory() == 1) {
                messageValues = new MessageValues(chat.getCategory(), chat.getText());
            }
            if (chat.getCategory() == 2) {
                messageValues = new MessageValues(chat.getCategory(), chat.getText(), chat.getLng(), chat.getLat());
            }
            if (chat.getCategory() == 3) {
                messageValues = new MessageValues(chat.getCategory(), chat.getUrl(), chat.getText());
            }
            if (chat.getCategory() == 5) {
                messageValues = new MessageValues(chat.getCategory(), chat.getText());
            }

            Messages messageObject = new Messages(chat.getId(), chat.getAid(), chat.getSender_id(), chat.getCategory(), chat.getCid(), messageValues, chat.getStatus(), chat.getCreated_at(), chat.getUpdated_at(), 1);
            DBDataSource dbDataSource = new DBDataSource(this);
            dbDataSource.addNormal(messageObject);
            Log.d(TAG, "From: " + from);
            Log.d(TAG, "Messages: " + data.toString());
            dbDataSource.UpdateCatNotification(chat.getCid(), dbDataSource.getCategories(chat.getCid()).getNotification_count() + 1);

            /**
             * Production applications would usually process the message here.
             * Eg: - Syncing with server.
             *     - Store message in local database.
             *     - Update UI.
             */

            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */
            new NotificationHandler(this).updateNotification(DataFields.NotificationId, getString(R.string.newmessagereceived), new GetDate().getCurrentTime() + " : " + showMessage(messageValues), chat.getCid());
        } else {
            Log.v("GCM PUSH", data.toString());
            sendNotification("Unhandled Push Notification Received");
        }
    }

    private String showMessage(MessageValues messageValues) {
        if (messageValues.get_id() == 1) {
            return messageValues.getText();
        }
        if (messageValues.get_id() == 2) {
            return messageValues.getText();
        }
        if (messageValues.get_id() == 3) {
            return getString(R.string.imagereceived);
        }
        if (messageValues.get_id() == 5) {
            return getString(R.string.paynow);
        }
        return getString(R.string.imagereceived);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.genie_android_icons_97x97)
                .setContentTitle("SuperGenie GCM Messages")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(10, notificationBuilder.build());
    }
}

