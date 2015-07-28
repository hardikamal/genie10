package com.supergenieapp.android.GCMHelpers;
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

import com.supergenieapp.android.Activities.SplashScreenActivity;
import com.supergenieapp.android.Database.DBDataSource;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Extras.GetDate;
import com.supergenieapp.android.Extras.NotificationHandler;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.GenieApplication;
import com.supergenieapp.android.Objects.Categories;
import com.supergenieapp.android.Objects.Chat;
import com.supergenieapp.android.Objects.MessageValues;
import com.supergenieapp.android.Objects.Messages;
import com.supergenieapp.android.R;
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
        } else if (data.containsKey("msg")) {
            int status = 0, categoryId = 0, messageType = 0;
            String id = "", url = "", text = "";
            long created_at = 0, updated_at = 0;
            double lng = 0, lat = 0;
            int direction = DataFields.OUTGOING;
            try {
                JSONObject jsonObject = new JSONObject(data.getString("msg"));
                if (jsonObject.has("cid")) {
                    categoryId = jsonObject.getInt("cid");
                }
                if (jsonObject.has("_id")) {
                    id = jsonObject.getString("_id");
                }
                if (jsonObject.has("chat")) {
                    JSONObject chat = jsonObject.getJSONObject("chat");
                    if (chat.has("message")) {
                        JSONObject message = chat.getJSONObject("message");
                        if (message.has("category")) {
                            messageType = message.getInt("category");
                        }
                        if (message.has("status")) {
                            status = message.getInt("status");
                        }
                        if (message.has("sender_id")) {
                            direction = DataFields.INCOMING;
                        }
                        if (message.has("created_at")) {
                            created_at = message.getLong("created_at");
                        }
                        if (message.has("updated_at")) {
                            updated_at = message.getLong("updated_at");
                        }
                        if (message.has("category_value")) {
                            JSONObject category_value = message.getJSONObject("category_value");
                            if (messageType == DataFields.TEXT) {
                                if (category_value.has("text"))
                                    text = category_value.getString("text");
                            } else if (messageType == DataFields.LOCATION) {
                                if (category_value.has("lng"))
                                    lng = category_value.getDouble("lng");
                                if (category_value.has("lat"))
                                    lat = category_value.getDouble("lat");
                                if (category_value.has("address"))
                                    text = category_value.getString("address");
                            } else if (messageType == DataFields.IMAGE) {
                                if (category_value.has("caption"))
                                    text = category_value.getString("caption");
                                if (category_value.has("url"))
                                    url = category_value.getString("url");
                            } else if (messageType == DataFields.PAYNOW) {
                                text = category_value.toString();
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Chat chat = null;
            if (messageType == DataFields.TEXT || messageType == DataFields.PAYNOW) {
                chat = new Chat(id, categoryId, direction, status, created_at, updated_at, messageType, text);
            } else if (messageType == DataFields.LOCATION) {
                chat = new Chat(id, categoryId, direction, status, created_at, updated_at, messageType, text, lng, lat);
            } else if (messageType == DataFields.IMAGE) {
                chat = new Chat(id, categoryId, direction, status, created_at, updated_at, messageType, url, text);
            }

            if (chat != null) {
                MessageValues messageValues = null;
                if (chat.getType() == DataFields.TEXT) {
                    messageValues = new MessageValues(DataFields.TEXT, chat.getText());
                }
                if (chat.getType() == DataFields.LOCATION) {
                    messageValues = new MessageValues(DataFields.LOCATION, chat.getText(), chat.getLng(), chat.getLat());
                }
                if (chat.getType() == DataFields.IMAGE) {
                    messageValues = new MessageValues(DataFields.IMAGE, chat.getUrl(), chat.getText());
                }
                if (chat.getType() == DataFields.PAYNOW) {
                    messageValues = new MessageValues(DataFields.PAYNOW, chat.getText());
                }
                Messages messageObject = new Messages(chat.getId(), chat.getType(), chat.getCategory_Id(), messageValues, chat.getStatus(), chat.getCreated_at(), chat.getUpdated_at(), direction);
                DBDataSource dbDataSource = new DBDataSource(this);
                dbDataSource.addNormal(messageObject);

                Categories categories = dbDataSource.getCategories(messageObject.getCategory());
                if (categories != null)
                    dbDataSource.UpdateCatNotification(messageObject.getCategory(), categories.getNotification_count() + 1);

                new NotificationHandler(this).updateNotification(DataFields.NotificationId, new GetDate().getCurrentTime() + " : " + showMessage(messageValues), messageObject.getCategory());
            }
        } else {
            Log.v("GCM PUSH", data.toString());
            sendNotification("Unhandled Push Notification Received");
        }
    }

    private String showMessage(MessageValues messageValues) {
        if (messageValues.get_id() == DataFields.TEXT) {
            return messageValues.getText();
        }
        if (messageValues.get_id() == DataFields.LOCATION) {
            return messageValues.getText();
        }
        if (messageValues.get_id() == DataFields.IMAGE) {
            return getString(R.string.imagereceived);
        }
        if (messageValues.get_id() == DataFields.PAYNOW) {
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
                .setSmallIcon(R.drawable.supergenie_512)
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

