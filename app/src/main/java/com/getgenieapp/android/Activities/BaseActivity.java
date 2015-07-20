package com.getgenieapp.android.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.NotificationHandler;
import com.getgenieapp.android.Fragments.ChatFragment;
import com.getgenieapp.android.Fragments.MainFragment;
import com.getgenieapp.android.Fragments.PaymentFragment;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Categories;
import com.getgenieapp.android.Objects.Chat;
import com.getgenieapp.android.Objects.MessageValues;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BaseActivity extends GenieBaseActivity implements MainFragment.onSelect {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.screen)
    LinearLayout screen;
    private Socket mSocket;
    private static Categories categorie_selected = null;
    private HashMap<String, Object> mixpanelDataAdd = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.inject(this);
        mSocket = genieApplication.getSocket();
        getWindow().setBackgroundDrawableResource(R.drawable.wallpaper_wallpaper);
        if (getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("page").equalsIgnoreCase("categories")) {
                mixpanelDataAdd.put("Page", "Load Categories");
                setSupportActionBar(mToolbar);
                startFragment(R.id.body, new MainFragment());
                mToolbar.setLogo(R.drawable.genie_logo);
            }
            if (getIntent().getStringExtra("page").contains("message")) {
                mixpanelDataAdd.put("Page", "Go to Chat Screen");
                int id = sharedPreferences.getInt("catid", 0);
                mixpanelDataAdd.put("Page", "Go to Category " + id);
                new NotificationHandler(this).resetNotification();
                if (id == 0) {
                    mixpanelDataAdd.put("Page", "Load Categories Multiple Messages");
                    mixPanelBuild("Multiple Category Notification");
                    setSupportActionBar(mToolbar);
                    startFragment(R.id.body, new MainFragment());
                    mToolbar.setLogo(R.drawable.genie_logo);
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mToolbar.setTitle("");
                } else {
                    categorie_selected = dbDataSource.getCategories(id);
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
            } else if (fragment != null && fragment.isVisible() && fragment instanceof ChatFragment) {
                mixpanelDataAdd.put("Back from", "Chat Fragment");
                goBack();
            } else if (fragment != null && fragment.isVisible() && fragment instanceof MainFragment) {
                mixpanelDataAdd.put("Back from", "Main Fragment");
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mixpanelDataAdd.put("Activity", "Resumed");
        logging.LogV("Socket Checking");
        if (!mSocket.connected()) {
            mixpanelDataAdd.put("Socket", "Opened");
            logging.LogV("Socket Opened");
            mSocket.on("reset connection", reset_connection);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("agents offline", agentOffline);
            mSocket.on("agents online", agentOnline);
            mSocket.on("incoming agent message", onMessageReceived);
            mSocket.on("typing", onTyping);
            mSocket.on("server_error", onServerError);
            mSocket.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mixPanelTimerStart(BaseActivity.class.getName());
        logging.LogI("On Start");
    }

    @Override
    protected void onDestroy() {
        logging.LogI("On Destroy");
        mixPanelTimerStop(BaseActivity.class.getName());
        mixPanelBuildHashMap("General Run " + BaseActivity.class.getName(), mixpanelDataAdd);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mixpanelDataAdd.put("Activity", "Paused");
        logging.LogV("Socket Checking");
        if (mSocket.connected()) {
            mixpanelDataAdd.put("Socket", "Closed");
            logging.LogV("Socket Closed");
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off("incoming agent message", onMessageReceived);
            mSocket.off("agents offline", agentOffline);
            mSocket.off("agents online", agentOnline);
            mSocket.off("typing", onTyping);
            mSocket.off("server_error", onServerError);
            mSocket.off("reset connection", reset_connection);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof ChatFragment && data != null) {
                mixpanelDataAdd.put("Location", "Activity Returned");
                fragment.onActivityResult(requestCode, resultCode, data);
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
        startFragment(R.id.body, chatFragment);

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
        bundle.putString("color", categorie_selected.getBg_color());
        bundle.putLong("hide_time", categorie_selected.getHide_chats_time());
        bundle.putString("url", categorie_selected.getImage_url());
        chatFragment.setArguments(bundle);
        startFragment(R.id.body, chatFragment);
    }

    public void onReceiveFromLeft(Categories categories) {
        mixpanelDataAdd.put("Opened", "Category " + categories.getName() + " From Payments");
        categorie_selected = categories;
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", categorie_selected.getId());
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
            mSocket.emit("register user", sharedPreferences.getString(DataFields.TOKEN, null));
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
                        JSONObject jsonObject = new JSONObject(args[0].toString());
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
                    dbDataSource.addNormal(messageObject);
                    mBus.post(messageObject);
                    FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
                    List<Fragment> fragments = fragmentManager.getFragments();
                    for (Fragment fragment : fragments) {
                        if (fragment != null && fragment.isVisible() && fragment instanceof MainFragment) {
                            mixpanelDataAdd.put("Message", "Received in category Screen");
                            mixPanelBuild("Notification Set from category page");
                            Crouton.cancelAllCroutons();
                            Crouton.makeText(BaseActivity.this, getString(R.string.newmessagereceived), Style.CONFIRM, R.id.body).show();
                            Categories categories = dbDataSource.getCategories(messageObject.getCategory());
                            dbDataSource.UpdateCatNotification(messageObject.getCategory(), categories.getNotification_count() + 1);
                            ((MainFragment) fragment).refreshDataFromLocal();
                        }
                    }
                }
            });
        }
    };

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
}
