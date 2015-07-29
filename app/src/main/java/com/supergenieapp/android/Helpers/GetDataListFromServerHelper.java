package com.supergenieapp.android.Helpers;

import android.content.Context;

import com.supergenieapp.android.Database.DBDataSource;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.GenieApplication;
import com.supergenieapp.android.Objects.Categories;
import com.supergenieapp.android.Objects.Chat;
import com.supergenieapp.android.Objects.MessageValues;
import com.supergenieapp.android.Objects.Messages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Raviteja on 7/29/2015.
 */
public class GetDataListFromServerHelper {
    private Context context;
    private DBDataSource dbDataSource;

    public GetDataListFromServerHelper(Context context) {
        this.context = context;
        this.dbDataSource = GenieApplication.getInstance().getDBDataSource();
    }

    public ArrayList<Messages> parseJSONDataIntoChatObjectArray(String jsonString) {
        ArrayList<Categories> categoriesArrayList = dbDataSource.getAllCategories();
        ArrayList<Messages> messagesArrayList = new ArrayList<Messages>();
        ArrayList<Messages> messagesArrayListUnSynced = new ArrayList<Messages>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has("payload")) {
                System.out.println("onReceivedMessagesList " + jsonObject.getString("payload"));
                JSONObject payload = jsonObject.getJSONObject("payload");
                for (Categories cats : categoriesArrayList) {
                    if (payload.has(String.valueOf(cats.getId()))) {
                        System.out.println("onReceivedMessagesList " + payload.getString(String.valueOf(cats.getId())));
                        JSONArray catJSONArray = payload.getJSONArray(String.valueOf(cats.getId()));
                        Messages lastMessage = null;
                        for (int i = 0; i < catJSONArray.length(); i++) {
                            System.out.println(catJSONArray.get(i));
                            JSONObject chatMessage = catJSONArray.getJSONObject(i);
                            int status = 0, categoryId = 0, messageType = 0;
                            String id = "", url = "", text = "";
                            long created_at = 0, updated_at = 0;
                            double lng = 0, lat = 0;
                            int uid = 0, aid = 0;
                            int direction = DataFields.INCOMING;
                            if (chatMessage.has("cid")) {
                                categoryId = chatMessage.getInt("cid");
                            }
                            if (chatMessage.has("_id")) {
                                id = chatMessage.getString("_id");
                            }
                            if (chatMessage.has("uid")) {
                                uid = chatMessage.getInt("uid");
                            }
                            if (chatMessage.has("aid")) {
                                aid = chatMessage.getInt("aid");
                            }
                            if (chatMessage.has("message")) {
                                JSONObject message = chatMessage.getJSONObject("message");
                                if (message.has("category")) {
                                    messageType = message.getInt("category");
                                }
                                if (message.has("status")) {
                                    status = message.getInt("status");
                                }
                                if (message.has("sender_id")) {
                                    if (uid == message.getInt("sender_id")) {
                                        direction = DataFields.OUTGOING;
                                    } else if (aid == message.getInt("sender_id")) {
                                        direction = DataFields.INCOMING;
                                    }
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
                                messagesArrayList.add(messageObject);
                                if (i == 0) {
                                    lastMessage = messageObject;
                                }
                            }
                        }
                        messagesArrayList.addAll(getUnSyncedMsg(lastMessage, String.valueOf(cats.getId())));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messagesArrayList;
    }

    private ArrayList<Messages> getUnSyncedMsg(Messages lastMessage, String catId) {
        ArrayList<Messages> holdMessages = new ArrayList<>();
        if (lastMessage != null) {
            holdMessages = dbDataSource.getAllListBasedOnCategoryWithHideTime(catId, lastMessage.getCreatedAt());
        }
        return holdMessages;
    }
}