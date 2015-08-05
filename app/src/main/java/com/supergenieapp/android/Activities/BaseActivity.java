package com.supergenieapp.android.Activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Extras.NotificationHandler;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.Fragments.ChatFragment;
import com.supergenieapp.android.Fragments.MainFragment;
import com.supergenieapp.android.Fragments.PaymentFragment;
import com.supergenieapp.android.GenieBaseActivity;
import com.supergenieapp.android.Objects.Categories;
import com.supergenieapp.android.Objects.Chat;
import com.supergenieapp.android.Objects.MessageValues;
import com.supergenieapp.android.Objects.Messages;
import com.supergenieapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BaseActivity extends GenieBaseActivity implements MainFragment.onSelect, TextToSpeech.OnInitListener, RecognitionListener {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.screen)
    LinearLayout screen;
    private static Socket mSocket;
    private static Categories categorie_selected = null;
    private HashMap<String, Object> mixpanelDataAdd = new HashMap<>();
    long startRxBytes = 0;
    long startTxBytes = 0;
    private TextToSpeech tts;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.inject(this);
        if (!sharedPreferences.getBoolean("isMuted", true)) {
            tts = new TextToSpeech(this, this);
        }

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        mSocket = genieApplication.getSocket();
        getWindow().setBackgroundDrawableResource(R.drawable.wallpaper_wallpaper);
        if (getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("page").equalsIgnoreCase("categories")) {
                mixpanelDataAdd.put("Page", "Load Categories");
                setSupportActionBar(mToolbar);
                startFragmentFromRight(R.id.body, new MainFragment());
                mToolbar.setLogo(R.drawable.genie_logo);
            } else if (getIntent().getStringExtra("page").contains("message")) {
                mixpanelDataAdd.put("Page", "Go to Chat Screen");
                int id = sharedPreferences.getInt("catid", 0);
                mixpanelDataAdd.put("Page", "Go to Category " + id);
                new NotificationHandler(this).resetNotification();
                if (id == 0) {
                    mixpanelDataAdd.put("Page", "Load Categories Multiple Messages");
                    mixPanelBuild("Multiple Category Notification");
                    setSupportActionBar(mToolbar);
                    startFragmentFromRight(R.id.body, new MainFragment());
                    mToolbar.setLogo(R.drawable.genie_logo);
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mToolbar.setTitle("");
                } else {
                    sharedPreferences.edit().putInt("catid", 0).apply();
                    categorie_selected = dbDataSource.getCategories(id);
                    if (categorie_selected != null) {
                        mixpanelDataAdd.put("Page", "Open Category " + categorie_selected.getName());
                        mixPanelBuild(categorie_selected.getName() + " Opened from notifications");
                        setSupportActionBar(mToolbar);
                        ActionBar actionBar = getSupportActionBar();
                        if (actionBar != null) {
                            actionBar.setHomeButtonEnabled(true);
                            actionBar.setDisplayHomeAsUpEnabled(true);
                            actionBar.setTitle(categorie_selected.getName());
                        }
                        mToolbar.setLogo(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
                        mToolbar.setTitle(categorie_selected.getName());
                        mToolbar.setBackgroundColor(Color.parseColor(categorie_selected.getBg_color()));
                        onReceive(categorie_selected);
                    }
                }
            } else {
                mixpanelDataAdd.put("Page", "Load Categories");
                setSupportActionBar(mToolbar);
                startFragmentFromRight(R.id.body, new MainFragment());
                mToolbar.setLogo(R.drawable.genie_logo);
            }
        }
    }

    @Override
    public void onBackPressed() {
        mixpanelDataAdd.put("Button Pressed", "Back");
        FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof PaymentFragment) {
                mixpanelDataAdd.put("Back from", "Payment Fragment");
                setSupportActionBar(mToolbar);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setHomeButtonEnabled(true);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }
                mToolbar.setLogo(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
                mToolbar.setTitle(categorie_selected.getName());
                mToolbar.setBackgroundColor(Color.parseColor(categorie_selected.getBg_color()));
                onReceiveFromLeft(categorie_selected);
//                if (fragmentManager.getBackStackEntryCount() > 0) {
//                    fragmentManager.popBackStack();
//                }
            } else if (fragment != null && fragment.isVisible() && fragment instanceof ChatFragment) {
                mixpanelDataAdd.put("Back from", "Chat Fragment");
                goBack();
            } else if (fragment != null && fragment.isVisible() && fragment instanceof MainFragment) {
                mixpanelDataAdd.put("Back from", "Main Fragment");
                fragmentManager.popBackStack();
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new NotificationHandler(this).cancelNotification(DataFields.ALERTMSG);
        mixpanelDataAdd.put("Activity", "Resumed");
        logging.LogV("Socket Checking to on");
        if (!mSocket.connected() && genieApplication.getSocket().connected()) {
            mixpanelDataAdd.put("Socket", "Opened");
            logging.LogV("Socket Opened");
            mSocket.on("reset connection", reset_connection);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.on("agents offline", agentOffline);
            mSocket.on("agents online", agentOnline);
            mSocket.on("earlier messages", loadMoreMessages);
            mSocket.on("incoming agent message", onMessageReceived);
            mSocket.on("typing", onTyping);
            mSocket.on("server_error", onServerError);
            mSocket.on("got category chats", onReceivedMessagesList);
            genieApplication.connectToSocket();
        }

        FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof MainFragment) {
                ((MainFragment) fragment).refreshData();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mixPanelTimerStart(BaseActivity.class.getName());
        startRxBytes = TrafficStats.getUidRxBytes(getApplicationInfo().uid);
        startTxBytes = TrafficStats.getUidTxBytes(getApplicationInfo().uid);
        logging.LogI("On Start");
    }

    @Override
    protected void onDestroy() {
        logging.LogI("On Destroy");
        if (startRxBytes != 0 && startTxBytes != 0) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Received Bytes ", TrafficStats.getUidRxBytes(getApplicationInfo().uid) - startRxBytes);
                jsonObject.put("Transmited Bytes ", TrafficStats.getUidTxBytes(getApplicationInfo().uid) - startTxBytes);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mixPanelBuildJSON("Data Usage " + BaseActivity.class.getName(), jsonObject);
        }
        mixPanelTimerStop(BaseActivity.class.getName());
        mixPanelBuildHashMap("General Run " + BaseActivity.class.getName(), mixpanelDataAdd);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        new NotificationHandler(this).cancelNotification(DataFields.ALERTMSG);
        mixpanelDataAdd.put("Activity", "Paused");
        logging.LogV("Socket Checking to off");
        mixpanelDataAdd.put("Socket", "Closed");
        logging.LogV("Socket Closed");
        genieApplication.disconnectToSocket();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("incoming agent message", onMessageReceived);
        mSocket.off("agents offline", agentOffline);
        mSocket.off(Socket.EVENT_RECONNECT, onReconnect);
        mSocket.off("agents online", agentOnline);
        mSocket.off("earlier messages", loadMoreMessages);
        mSocket.off("typing", onTyping);
        mSocket.off("server_error", onServerError);
        mSocket.off("reset connection", reset_connection);
        mSocket.off("got category chats", onReceivedMessagesList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(requestCode);

        FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof ChatFragment && data != null) {
                mixpanelDataAdd.put("Location", "Activity Returned");
                if (requestCode == DataFields.LOCATIONRESULT) {
                    ((ChatFragment) fragment).onActivityResultLocation(data);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mixpanelDataAdd.put("Pressed", "Home/Back Menu");
                mixPanelBuild("Home/Back Menu Pressed");
                onBackPressed();
                return true;
            case R.id.action_profile:
                mixpanelDataAdd.put("Pressed", "Profile Menu");
                mixPanelBuild("Profile Menu Pressed");
                startActivity(new Intent(BaseActivity.this, UserProfileActivity.class));
                return true;
            case R.id.action_previous_orders:
                mixpanelDataAdd.put("Pressed", "Previous Orders Menu");
                mixPanelBuild("Previous Orders Menu Pressed");
                startActivity(new Intent(this, OrderDetailsActivity.class));
                return true;
            case R.id.action_share:
                mixpanelDataAdd.put("Pressed", "Share Menu");
                mixPanelBuild("Profile Share Pressed");
                String shareBody = getString(R.string.bodytext);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.trygenie));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.shareus)));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Socket getSocket() {
        return mSocket;
    }

    private void goBack() {
        mixpanelDataAdd.put("Opened", "Main Fragment");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setLogo(R.drawable.genie_logo);
        mToolbar.setTitle("");
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("refresh", true);
        mainFragment.setArguments(bundle);
        startFragmentFromLeft(R.id.body, mainFragment);
    }

    public void onClick(Categories categories) {
        mixpanelDataAdd.put("Opened", "Category " + categories.getName());
        mixPanelBuild(categories.getName() + " Selected from categories Screen");
        categorie_selected = categories;
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", categorie_selected.getId());
        bundle.putString("color", categorie_selected.getBg_color());
        bundle.putLong("hide_time", categorie_selected.getHide_chats_time());
        bundle.putString("url", categorie_selected.getImage_url());
        chatFragment.setArguments(bundle);
        startFragmentFromRight(R.id.body, chatFragment);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mToolbar.setLogo(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        mToolbar.setTitle(categorie_selected.getName());

        mToolbar.setBackgroundColor(Color.parseColor(categorie_selected.getBg_color()));
    }

    public void onReceive(Categories categories) {
        categorie_selected = categories;
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", categorie_selected.getId());
        bundle.putInt("position", DataFields.position);
        bundle.putString("color", categorie_selected.getBg_color());
        bundle.putLong("hide_time", categorie_selected.getHide_chats_time());
        bundle.putString("url", categorie_selected.getImage_url());
        chatFragment.setArguments(bundle);
        startFragmentFromRight(R.id.body, chatFragment);
    }

    public void onReceiveFromLeft(Categories categories) {
        mixpanelDataAdd.put("Opened", "Category " + categories.getName() + " From Payments");
        categorie_selected = categories;
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", categorie_selected.getId());
        bundle.putInt("position", DataFields.position);
        bundle.putString("color", categorie_selected.getBg_color());
        bundle.putLong("hide_time", categorie_selected.getHide_chats_time());
        bundle.putString("url", categorie_selected.getImage_url());
        chatFragment.setArguments(bundle);
        startFragmentFromLeft(R.id.body, chatFragment);
    }

    public void setTyping() {
        if (categorie_selected != null) {
            FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible() && fragment instanceof ChatFragment) {
                    setSupportActionBar(mToolbar);

                    ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setHomeButtonEnabled(true);
                        actionBar.setDisplayHomeAsUpEnabled(true);
                        actionBar.setTitle(categorie_selected.getName());
                    }

                    mToolbar.setLogo(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

                    mToolbar.setTitle(categorie_selected.getName());
                    mToolbar.setSubtitleTextAppearance(this, R.style.subText);

                    mToolbar.setBackgroundColor(Color.parseColor(categorie_selected.getBg_color()));
                    mToolbar.setSubtitle(getString(R.string.agentistyping));
                }
            }
        }
    }

    private Emitter.Listener reset_connection = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mixPanelBuild("Socket Reset Connection");
            mixpanelDataAdd.put("Socket", "Reset Connection");
//            mSocket.emit("register user", sharedPreferences.getString(DataFields.TOKEN, null));
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("token", sharedPreferences.getString(DataFields.TOKEN, null));
                jsonObject.put("timestamp", Utils.getCurrentTimeMillis());
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            System.out.println("JSON for get all " + jsonObject.toString());
            if (mSocket.connected() && genieApplication.getSocket().connected())
                genieApplication.getSocket().emit("register user", jsonObject);
            else {
                Crouton.makeText(BaseActivity.this,
                        getString(R.string.serverconnectionerror), Style.ALERT, R.id.body).show();
            }
            // changed from get category chats to register users
            setChatStatus(true);
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mixpanelDataAdd.put("Socket", "Connection Error");
                    mixPanelBuild("Socket Connection Error");
                    Crouton.makeText(BaseActivity.this,
                            getString(R.string.onconnectionerror), Style.ALERT, R.id.body).show();
                    setChatStatus(false);
                }
            });
        }
    };

    private void setChatStatus(boolean status) {
        FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof ChatFragment) {
                if (status) {
                    mixpanelDataAdd.put("Chat", "Enable");
                    ((ChatFragment) fragment).setEnable();
                } else {
                    mixpanelDataAdd.put("Chat", "Disable");
                    ((ChatFragment) fragment).setDisable();
                }
            }
        }
    }

    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mixpanelDataAdd.put("Message", "Received" + args[0]);
                    setChatStatus(true);
                    logging.LogV(args[0].toString());
                    int status = 0, categoryId = 0, messageType = 0;
                    String id = "", url = "", text = "";
                    long created_at = 0, updated_at = 0;
                    double lng = 0, lat = 0;
                    int direction = DataFields.OUTGOING;

                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());
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
                                    } else if (messageType == DataFields.PAYASCOD) {
                                        if (category_value.has("text"))
                                            text = category_value.getString("text");
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Chat chat = null;
                    if (messageType == DataFields.TEXT || messageType == DataFields.PAYASCOD) {
                        chat = new Chat(id, categoryId, direction, status, Utils.getCurrentTimeMillis(), updated_at, messageType, text);
                    } else if (messageType == DataFields.LOCATION) {
                        chat = new Chat(id, categoryId, direction, status, Utils.getCurrentTimeMillis(), updated_at, messageType, text, lng, lat);
                    } else if (messageType == DataFields.IMAGE) {
                        chat = new Chat(id, categoryId, direction, status, Utils.getCurrentTimeMillis(), updated_at, messageType, url, text);
                    } else if (messageType == DataFields.PAYNOW) {
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
                        if (chat.getType() == DataFields.PAYASCOD) {
                            messageValues = new MessageValues(DataFields.PAYASCOD, chat.getText());
                        }
                        final Messages messageObject = new Messages(chat.getId(), chat.getType(), chat.getCategory_Id(), messageValues, chat.getStatus(), chat.getCreated_at(), chat.getUpdated_at(), direction);
                        dbDataSource.addNormal(messageObject);

                        if (chat.getType() == DataFields.IMAGE) {
                            final String urlLocal = messageValues.getUrl();
                            if (messageValues.getUrl().matches("data:image.*base64.*")) {
                                String base_64_source = messageValues.getUrl().replaceAll("data:image.*base64", "");
                                byte[] data = Base64.decode(base_64_source, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                if (bitmap != null) {
                                    FileOutputStream out = null;
                                    try {
                                        out = new FileOutputStream(DataFields.TempFolder + "/" + utils.hashString(urlLocal));
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        try {
                                            if (out != null) {
                                                out.close();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                postToChat(messageObject);
                            } else {
                                imageLoader.get(messageValues.getUrl(), new ImageLoader.ImageListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        postToChat(messageObject);
                                    }

                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        if (response != null && response.getBitmap() != null) {
                                            FileOutputStream out = null;
                                            try {
                                                out = new FileOutputStream(DataFields.TempFolder + "/" + utils.hashString(urlLocal));
                                                response.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            } finally {
                                                try {
                                                    if (out != null) {
                                                        out.close();
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            postToChat(messageObject);
                                        }
                                    }
                                });
                            }
                        } else {
                            postToChat(messageObject);
                        }
                    }
                }
            });
        }
    };

    private void postToChat(Messages messageObject) {
        mBus.post(messageObject);
        FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof MainFragment) {
                mixpanelDataAdd.put("Message", "Received in category Screen");
                mixPanelBuild("Notification Set from category page");
                Crouton.cancelAllCroutons();
                Categories categories = dbDataSource.getCategories(messageObject.getCategory());
                if (categories != null) {
                    Crouton.makeText(BaseActivity.this, getString(R.string.newmessagereceivedin) + categories.getName(), Style.CONFIRM, R.id.body).show();
                    dbDataSource.UpdateCatNotification(messageObject.getCategory(), categories.getNotification_count() + 1);
                }
                ((MainFragment) fragment).refreshData();
            } else if (fragment != null && fragment.isVisible() && fragment instanceof PaymentFragment) {
                mixpanelDataAdd.put("Message", "Received in category Screen");
                mixPanelBuild("Notification Set from category page");
                Crouton.cancelAllCroutons();
                Categories categories = dbDataSource.getCategories(messageObject.getCategory());
                if (categories != null)
                    dbDataSource.UpdateCatNotification(messageObject.getCategory(), categories.getNotification_count() + 1);
                System.out.println(dbDataSource.getCategories(messageObject.getCategory()).getNotification_count());
                if (categories != null) {
                    Crouton.makeText(BaseActivity.this, getString(R.string.newmessagereceivedin) + categories.getName(), Style.CONFIRM, R.id.body).show();
                    dbDataSource.UpdateCatNotification(messageObject.getCategory(), categories.getNotification_count() + 1);
                }
            }
        }
    }

    private Emitter.Listener agentOffline = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mixpanelDataAdd.put("Socket", "Agent Offline");
                    mixPanelBuild("Agent Offline");
                    new NotificationHandler(BaseActivity.this).cancelNotification(DataFields.ALERTMSG);
                    logging.LogV(args[0].toString());
                    Crouton.makeText(BaseActivity.this, getString(R.string.isoffline), Style.ALERT, R.id.body).show();
                    sharedPreferences.edit().putBoolean("agent", false).apply();
                    setChatStatus(false);
                }
            });
        }
    };

    private Emitter.Listener agentOnline = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mixpanelDataAdd.put("Socket", "Agent Online");
                    mixPanelBuild("Agent Online");
                    logging.LogV(args[0].toString());
                    sharedPreferences.edit().putBoolean("agent", true).apply();
                    setChatStatus(true);
                    new NotificationHandler(BaseActivity.this).cancelNotification(DataFields.ALERTMSG);
                }
            });
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mixpanelDataAdd.put("Socket", "Set Typing");
                    setTyping();
                }
            });
        }
    };

    private Emitter.Listener onReceivedMessagesList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("onReceivedMessagesList " + args[0]);
                    if (sharedPreferences.getBoolean("syncwithserver", true) || syncTimeDiff(sharedPreferences.getLong("syncwithservertime", 0))) {
                        sharedPreferences.edit().putBoolean("syncwithserver", false).apply();
                        sharedPreferences.edit().putLong("syncwithservertime", System.currentTimeMillis()).apply();
                        try {
                            JSONObject jsonObject = new JSONObject(args[0].toString());
                            if (jsonObject.has("payload")) {
                                System.out.println("onReceivedMessagesList " + jsonObject.getString("payload"));
                                JSONObject payload = jsonObject.getJSONObject("payload");
                                ArrayList<Categories> categoriesArrayList = dbDataSource.getAllCategories();
                                ArrayList<Messages> messagesArrayList = new ArrayList<Messages>();
                                ArrayList<Messages> messagesArrayListUnSynced = new ArrayList<Messages>();
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
                                                    } else if (messageType == DataFields.PAYASCOD) {
                                                        if (category_value.has("text"))
                                                            text = category_value.getString("text");
                                                    }
                                                }
                                            }
                                            Chat chat = null;
                                            if (messageType == DataFields.TEXT || messageType == DataFields.PAYNOW || messageType == DataFields.PAYASCOD) {
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
                                                if (chat.getType() == DataFields.PAYASCOD) {
                                                    messageValues = new MessageValues(DataFields.PAYASCOD, chat.getText());
                                                }
                                                Messages messageObject = new Messages(chat.getId(), chat.getType(), chat.getCategory_Id(), messageValues, chat.getStatus(), chat.getCreated_at(), chat.getUpdated_at(), direction);
                                                messagesArrayList.add(messageObject);
                                                if (i == 0) {
                                                    lastMessage = messageObject;
                                                }
                                            }
                                        }
                                        if (lastMessage != null) {
                                            ArrayList<Messages> holdMessages = dbDataSource.getAllListBasedOnCategoryWithHideTime(String.valueOf(cats.getId()), lastMessage.getCreatedAt());
                                            for (Messages msg : holdMessages) {
                                                if (msg.getMessageType() == DataFields.LOCATION) {
                                                    messagesArrayListUnSynced.add(msg);
                                                }
                                            }
                                        }
                                    }
                                }
                                messagesArrayList.addAll(messagesArrayListUnSynced);
                                dbDataSource.cleanTable();
                                dbDataSource.addFast(messagesArrayList);
                                for (Categories categories : dbDataSource.getAllCategories()) {
                                    System.out.println("Get Hide Time : " + categories.getName() + " " + categories.getNotification_count());
                                    if (categories.getNotification_count() != 0)
                                        dbDataSource.UpdateMessages(categories.getId(), categories.getHide_chats_time());
                                }
                                FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
                                List<Fragment> fragments = fragmentManager.getFragments();
                                for (Fragment fragment : fragments) {
                                    if (fragment != null && fragment.isVisible() && fragment instanceof ChatFragment) {
                                        ((ChatFragment) fragment).getCustomChatAdapter().notifyDataSetChanged();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    private boolean syncTimeDiff(long syncwithservertime) {
        return ((System.currentTimeMillis() - syncwithservertime) > 60 * 60 * 1000);
    }

    private Emitter.Listener onServerError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mixpanelDataAdd.put("Socket", "Server Error");
                    mixPanelBuild("Socket Server Error");
                }
            });
        }
    };

    private Emitter.Listener onReconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mixpanelDataAdd.put("Socket", "Server Reconnect");
                    mixPanelBuild("Socket Server Reconnect");
                    setChatStatus(true);
                }
            });
        }
    };

    private Emitter.Listener loadMoreMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Previous " + args[0]);
                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());
                        if (jsonObject.has("messages")) {
                            System.out.println("Previous " + jsonObject.getString("messages"));
                            ArrayList<Messages> messagesArrayList = new ArrayList<Messages>();
                            JSONArray catJSONArray = jsonObject.getJSONArray("messages");
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
                                        } else if (messageType == DataFields.PAYASCOD) {
                                            if (category_value.has("text"))
                                                text = category_value.getString("text");
                                        }
                                    }

                                }
                                Chat chat = null;
                                if (messageType == DataFields.TEXT || messageType == DataFields.PAYNOW || messageType == DataFields.PAYASCOD) {
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
                                    if (chat.getType() == DataFields.PAYASCOD) {
                                        messageValues = new MessageValues(DataFields.PAYASCOD, chat.getText());
                                    }
                                    Messages messageObject = new Messages(chat.getId(), chat.getType(), chat.getCategory_Id(), messageValues, chat.getStatus(), chat.getCreated_at(), chat.getUpdated_at(), direction);
                                    messagesArrayList.add(messageObject);
                                }
                            }
                            if (messagesArrayList.size() > 0) {
                                messagesArrayList = validateList(messagesArrayList);
                            }
                            dbDataSource.addFast(messagesArrayList);
                            FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
                            List<Fragment> fragments = fragmentManager.getFragments();
                            for (Fragment fragment : fragments) {
                                if (fragment != null && fragment.isVisible() && fragment instanceof ChatFragment) {
                                    if (messagesArrayList.size() != 0) {
                                        ((ChatFragment) fragment).displayMessages(true, DataFields.NoScroll);
                                    } else {
                                        ((ChatFragment) fragment).displayMessages(false, DataFields.NoScroll);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private ArrayList<Messages> validateList(ArrayList<Messages> messagesArrayList) {
        ArrayList<Messages> finalList = new ArrayList<>();
        ArrayList<Messages> messagesFromDB = dbDataSource.getAllListBasedOnCategory(String.valueOf(messagesArrayList.get(0).getCategory()));
        if (messagesFromDB.size() > 0) {
            for (Messages messages : messagesArrayList) {
                if (!messagesFromDB.contains(messages)) {
                    finalList.add(messages);
                }
            }
        }
        return finalList;
    }

    public void sendLoadMoreMessagesCall(JSONObject jsonObject) {
        if (mSocket.connected() && genieApplication.getSocket().connected())
            genieApplication.getSocket().emit("load earlier messages", jsonObject);
    }

    public void shoyCODAlert(String costToPay) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true)
                .setMessage(getString(R.string.wouldyouliketopay) + costToPay + getString(R.string.viacod))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        emitPayCodMessage();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert.show();
    }

    private void emitPayCodMessage() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject category_value = new JSONObject();
            category_value.put("cod", true);
            JSONObject msg = new JSONObject();
            msg.put("category", DataFields.PAYASCOD);
            msg.put("category_value", category_value);
            msg.put("created_at", Utils.getCurrentTimeMillis());
            jsonObject.put("msg", msg);
            jsonObject.put("cid", categorie_selected.getId());

            System.out.println(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mSocket.connected() && genieApplication.getSocket().connected())
            genieApplication.getSocket().emit("user message", jsonObject);
    }

    public void emitPayOnline(long created_at, String action) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("created_at", created_at);
            jsonObject.put("button_clicked", action);
            jsonObject.put("cid", categorie_selected.getId());

            System.out.println(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mSocket.connected() && genieApplication.getSocket().connected())
            genieApplication.getSocket().emit("pay online", jsonObject);
    }

    @Override
    public void onInit(int status) {
        if (!sharedPreferences.getBoolean("isMuted", true)) {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.getDefault());
                tts.setPitch(1f);
                tts.setSpeechRate(0.85f);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Crouton.makeText(BaseActivity.this,
                            getString(R.string.languagenotfound), Style.ALERT, R.id.body).show();
                }
            } else {
                Crouton.makeText(BaseActivity.this,
                        getString(R.string.notsupportedtexttospeech), Style.ALERT, R.id.body).show();
            }
        }
    }

    public void speakOut(String text) {
        if (!sharedPreferences.getBoolean("isMuted", true) && tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ttsGreater21(text);
            } else {
                ttsUnder20(text);
            }
        }
    }

    public boolean startSpeech() {
        if (speech != null && recognizerIntent != null) {
            speech.startListening(recognizerIntent);
            return true;
        } else {
            Crouton.makeText(BaseActivity.this,
                    getString(R.string.speech_not_supported), Style.ALERT, R.id.body).show();
            return false;
        }
    }

    public void stopSpeech() {
        if (speech != null) {
            speech.stopListening();
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    public void setTTS() {
        tts = new TextToSpeech(this, this);
    }

    @Override
    public void onBeginningOfSpeech() {
        logging.LogI("onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        logging.LogI("onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof ChatFragment) {
                ((ChatFragment) fragment).closePopup();
            }
        }
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Crouton.makeText(BaseActivity.this,
                errorMessage, Style.ALERT, R.id.body).show();

        FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof ChatFragment) {
                ((ChatFragment) fragment).closePopup();
            }
        }
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
    }

    @Override
    public void onPartialResults(Bundle arg0) {
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof ChatFragment) {
                ((ChatFragment) fragment).postToMessageBox(matches.get(0));
            }
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "Error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}