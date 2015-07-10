package com.getgenieapp.android.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import com.getgenieapp.android.Extras.ChatHelper;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.MessageValues;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

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

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new CustomChatAdapter(messages, color, this);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.scrollToPosition(messages.size() - 1);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fly_in_from_center_100);
        recyclerView.setAnimation(anim);
        anim.start();

        message.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recyclerView.scrollToPosition(messages.size() - 1);
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

    @OnClick(R.id.send)
    public void onClickSend() {
        if (imageResource) {
            Messages messageObject = new Messages("1", 1, 1, 2, id, getLocation(), 1, 0, 0, 0);
            messages.add(messageObject);
            dbDataSource.addNormal(messageObject);
            chatAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messages.size() - 1);
        } else {
            String typedMessage = message.getText().toString().trim();
            message.setText("");
            MessageValues messageValues = new MessageValues(1, typedMessage);
            Messages messageObject = new Messages("1", 1, 1, 1, id, messageValues, 1, 0, 0, 0);
            messages.add(messageObject);
            dbDataSource.addNormal(messageObject);
            chatAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messages.size() - 1);
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
                        recyclerView.scrollToPosition(messages.size() - 1);
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

    public MessageValues getLocation() {
        double longitude = 0.00;
        double latitude = 0.00;
        String _Location = "";

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);

        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            longitude = locations.getLongitude();
            latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    Address adrs = listAddresses.get(0);

                    for (int i = 0; i < adrs.getMaxAddressLineIndex(); i++) {
                        _Location += adrs.getAddressLine(i) + " ";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SnackBar snackBar = new SnackBar(this, "Not able to access Location");
            snackBar.show();
        }
        return new MessageValues(3, _Location, longitude, latitude);
    }

}
