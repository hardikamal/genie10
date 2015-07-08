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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.getgenieapp.android.CustomViews.Adapters.CustomChatAdapter;
import com.getgenieapp.android.CustomViews.Button.CircularButton;
import com.getgenieapp.android.Extras.ChatHelper;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuPopup;

import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ChatActivity extends GenieBaseActivity {
    @InjectView(R.id.list)
    RecyclerView recyclerView;
    @InjectView(R.id.send)
    CircularButton send;

    String title = "Chat";
    String description = "Chat Window";
    String color = "#1976d2";
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    DroppyMenuPopup droppyMenu;
    @InjectView(R.id.message)
    EditText message;
    boolean imageResource = true;

    private ChatHelper chatHelper;

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

        droppyMenu = new DroppyMenuPopup.Builder(this, send).fromMenu(R.menu.menu_chat)
                .triggerOnAnchorClick(false)
                .setOnClick(new DroppyClickCallbackInterface() {
                    @Override
                    public void call(View v, int id) {
                        Log.d("Id:", String.valueOf(id));
                    }
                })
                .build();

        getWindow().setBackgroundDrawableResource(R.drawable.wallpaper_wallpaper);
        try {
            getSupportActionBar().setTitle(title);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Messages> messages = new ArrayList<>();
        recyclerView.setAdapter(new CustomChatAdapter(messages, this));

        message.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                        send.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_white_24dp, ChatActivity.this.getTheme()));
                    } else {
                        send.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_white_24dp));
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @OnClick(R.id.send)
    public void onClickSend(View buttonSend) {
        if (imageResource) {
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(200);
                    } catch (Exception err) { }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            droppyMenu.show();
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatHelper = new ChatHelper();
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
