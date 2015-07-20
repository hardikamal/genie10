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
import com.getgenieapp.android.Fragments.ChatFragment;
import com.getgenieapp.android.Fragments.MainFragment;
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
    private Categories categorie_selected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.inject(this);
        mSocket = genieApplication.getSocket();
        getWindow().setBackgroundDrawableResource(R.drawable.wallpaper_wallpaper);
        if (getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("page").equalsIgnoreCase("categories")) {
                setSupportActionBar(mToolbar);
                startFragment(R.id.body, new MainFragment());
                mToolbar.setLogo(R.drawable.genie_logo);
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof ChatFragment) {
                goBack();
            } else if (fragment != null && fragment.isVisible() && fragment instanceof MainFragment) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        logging.LogV("Socket Checking");
        if (!mSocket.connected()) {
            logging.LogV("Socket Opened");
            mSocket.on("reset connection", reset_connection);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("user message", sendUserMessage);
            mSocket.on("incoming agent message", onMessageReceived);
            mSocket.on("typing", onTyping);
            mSocket.on("server_error", onServerError);
            mSocket.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        logging.LogV("Socket Checking");
        if (mSocket.connected()) {
            logging.LogV("Socket Closed");
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off("incoming agent message", onMessageReceived);
            mSocket.off("user message", sendUserMessage);
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
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                goBack();
                return true;
            case R.id.action_profile:
                startActivity(new Intent(BaseActivity.this, UserProfileActivity.class));
                return true;
            case R.id.action_previous_orders:
                startActivity(new Intent(this, OrderDetailsActivity.class));
                return true;
            case R.id.action_share:
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
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setLogo(R.drawable.genie_logo);
        mToolbar.setTitle("");
        startFragmentFromLeft(R.id.body, new MainFragment());
    }

    @Override
    public void onClick(Categories categories) {
        this.categorie_selected = categories;
        System.out.println("Socket connection status : " + mSocket.connected());
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", categories.getId());
        bundle.putString("color", categories.getBg_color());
        bundle.putLong("hide_time", categories.getHide_chats_time());
        bundle.putString("url", categories.getImage_url());
        chatFragment.setArguments(bundle);
        startFragment(R.id.body, chatFragment);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mToolbar.setLogo(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        mToolbar.setTitle(categories.getName());
        mToolbar.setSubtitleTextAppearance(this, R.style.subText);

        mToolbar.setBackgroundColor(Color.parseColor(categories.getBg_color()));
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
            mSocket.emit("register user", sharedPreferences.getString(DataFields.TOKEN, null));
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                    ((ChatFragment) fragment).setEnable();
                } else {
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
                }
            });
        }
    };

    private Emitter.Listener sendUserMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

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

                }
            });
        }
    };

    float x1 = 0;
    float x2 = 0;
    int MIN_DISTANCE = 10;

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        View v = getCurrentFocus();
//        CircularButton btn = (CircularButton) findViewById(R.id.send);
//
//        if (v != null &&
//                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
//                v instanceof EditText &&
//                !v.getClass().getName().startsWith("android.webkit.")) {
//            int scrcoords[] = new int[2];
//            v.getLocationOnScreen(scrcoords);
//            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
//            float y = ev.getRawY() + v.getTop() - scrcoords[1];
//            if (btn != null) {
//                int[] pos = new int[2];
//                btn.getLocationOnScreen(pos);
//                if (ev.getY() <= (pos[1] + btn.getHeight()) && ev.getX() > pos[0]) //location button event
//                    return super.dispatchTouchEvent(ev);
//            }
//            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
//                hideKeyboard(this);
//        }
//
//        return super.dispatchTouchEvent(ev);
//    }
}
