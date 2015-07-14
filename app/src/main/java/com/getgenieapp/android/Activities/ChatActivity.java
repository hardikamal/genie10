package com.getgenieapp.android.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getgenieapp.android.CustomViews.Adapters.CustomChatAdapter;
import com.getgenieapp.android.CustomViews.Button.CircularButton;
import com.getgenieapp.android.CustomViews.Misc.SnackBar;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.MessageValues;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

// This is chat activity. user can chat to servers in this activity.
// Elements injecting thru butterknife.
// Socket io is initialized here and connected on resume and disconnected on pause.
// Activity connections
// 1. Location Activity started for result.
// Recycler view adapter view is used to display list view
// Menu Options to Profile and orders
// Events registered under socket io
// EVENT_CONNECT_ERROR
// EVENT_CONNECT_TIMEOUT
// typing
// server_error
// init will move this to main activity if needed
// Adapter is CustomChatAdapter
// Toolbar is used instead of actionbar to make it customization more seamless

// category message types
// 1 text
// 2 location
// 3 images
// 5 Pay now
// 8 load more
// 9 date

public class ChatActivity extends GenieBaseActivity {
    @InjectView(R.id.list)
    RecyclerView recyclerView;
    @InjectView(R.id.send)
    CircularButton send;
    @InjectView(R.id.messageLayout)
    LinearLayout messageLayout;
    String title = "SuperGenie";
    String description = "Super Genie Chat Window";
    String color = "#26ACEC";
    int id = 0;
    long hide_time = 0;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.message)
    EditText message;
    boolean imageResource = true;
    private CustomChatAdapter chatAdapter;
    private ArrayList<Messages> messages = new ArrayList<>();
    private int LOCATIONRESULT = 1;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(DataFields.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("title");
            description = getIntent().getStringExtra("description");
            color = getIntent().getStringExtra("color");
            id = getIntent().getIntExtra("id", 0);
            hide_time = getIntent().getLongExtra("hide_chat", 0);
        }

        setContentView(R.layout.activity_chat);

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setTitle(title);
        mToolbar.setSubtitle(description);
        mToolbar.setSubtitleTextAppearance(this, R.style.subText);

        mToolbar.setBackgroundColor(Color.parseColor(color));
        send.setButtonColor(Color.parseColor(color));
        send.setShadowColor(Color.parseColor(color));
        message.setTextColor(Color.parseColor(color));

        getWindow().setBackgroundDrawableResource(R.drawable.wallpaper_wallpaper);
        try {
            getSupportActionBar().setTitle(title);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        messages = dbDataSource.getAllMessages();

        String present = "";
        String now = "";

        ArrayList<Integer> messageTypes = new ArrayList<>();
        messageTypes.add(1);
        messageTypes.add(2);
        messageTypes.add(3);

        for (int i = messages.size() - 1; i >= 0; i--) {
            now = utils.convertLongToDate(messages.get(i).getCreatedAt(), new SimpleDateFormat("yyyy MM dd"));
            if (!present.equals("") && !present.equals(now)) {
                messages.add(i, new Messages("0", 1, 1, 9, id, new MessageValues(), 0, messages.get(i).getCreatedAt(), 0, 0));
            }
            if (i == 0) {
                messages.add(i, new Messages("0", 1, 1, 9, id, new MessageValues(), 0, messages.get(i).getCreatedAt(), 0, 0));
            }
            present = now;
        }

        if (hide_time != 0) {
            messages.add(0, new Messages("0", 1, 1, 8, id, new MessageValues(), 0, 0, 0, 0));
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new CustomChatAdapter(messages, color, this);
        recyclerView.setAdapter(chatAdapter);
        scroll();
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fly_in_from_center_100);
        recyclerView.setAnimation(anim);
        anim.start();

        message.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scroll();
                if (message.getText().toString().trim().length() > 0) {
                    imageResource = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        send.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_white_24dp, ChatActivity.this.getTheme()));
                    } else {
                        send.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_white_24dp));
                    }
                } else {
                    imageResource = true;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        send.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_location_white_24dp, ChatActivity.this.getTheme()));
                    } else {
                        send.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_location_white_24dp));
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    send.performClick();
                    return true;
                }
                return false;
            }
        });

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    private void scroll() {
        if (messages.size() > 1) {
            recyclerView.smoothScrollToPosition(messages.size() - 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATIONRESULT) {
            if (resultCode == 1) {
                if (data.getExtras() != null) {
                    MessageValues messageValues = new MessageValues(3, data.getStringExtra("address"), data.getDoubleExtra("lng", 0.00), data.getDoubleExtra("lat", 0.00));
                    Messages messageObject = new Messages("1", 1, 1, 2, id, messageValues, 1, System.currentTimeMillis() / 1000L, 0, 0);
                    messages.add(messageObject);
                    dbDataSource.addNormal(messageObject);
                    chatAdapter.notifyDataSetChanged();
                    scroll();
                } else {
                    SnackBar snackBar = new SnackBar(this, getString(R.string.errorinaccessinglocation));
                    snackBar.show();
                }
            }
        }
    }

    @OnClick(R.id.send)
    public void onClickSend() {
        if (imageResource) {
            startActivityForResult(new Intent(this, LocationActivity.class), LOCATIONRESULT);
        } else {
            String typedMessage = message.getText().toString().trim();
            message.setText("");
            MessageValues messageValues = new MessageValues(1, typedMessage);
            Messages messageObject = new Messages("1", 1, 1, 1, id, messageValues, 1, System.currentTimeMillis() / 1000L, 0, 0);
            messages.add(messageObject);
            dbDataSource.addNormal(messageObject);
            chatAdapter.notifyDataSetChanged();
            scroll();
            if (typedMessage.equalsIgnoreCase("Pay Now")) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("companyname", "Genie");
                    jsonObject.put("rate", 775.00);
                    jsonObject.put("details", "This is some random text generated by me. Its being used by me to display some text at this place.");
                    jsonObject.put("cod", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MessageValues messageValues2 = new MessageValues(5, "www.google.com", jsonObject.toString());
                Messages messageObject2 = new Messages("1", 1, 1, 5, id, messageValues2, 1, System.currentTimeMillis() / 1000L, 0, 0);
                messages.add(messageObject2);
                dbDataSource.addNormal(messageObject2);
                chatAdapter.notifyDataSetChanged();
                scroll();
            }
        }
    }

    @OnClick(R.id.messageLayout)
    public void onClickMessageBox() {
        message.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(message, InputMethodManager.SHOW_IMPLICIT);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(DataFields.smallTimeOut);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        scroll();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSocket.on("init", onInit);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("message_received", onMessageReceived);
        mSocket.on("typing", onTyping);
        mSocket.on("server_error", onServerError);
        mSocket.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("message_received", onMessageReceived);
        mSocket.off("typing", onTyping);
        mSocket.off("server_error", onServerError);
        mSocket.off("init", onInit);
    }

    private Emitter.Listener onInit = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println(args[0].toString());
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "On Connection Error", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(args[0].toString());
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

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
            return true;
        }
        if (id == R.id.action_previous_orders) {
            startActivity(new Intent(this, OrderDetailsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}