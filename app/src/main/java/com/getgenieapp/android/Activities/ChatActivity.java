package com.getgenieapp.android.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getgenieapp.android.CustomViews.Adapters.CustomChatAdapter;
import com.getgenieapp.android.Extras.ChatHelper;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ChatActivity extends GenieBaseActivity {
    @InjectView(R.id.list)
    RecyclerView recyclerView;
    @InjectView(R.id.send)
    Button send;
    @InjectView(R.id.sendBackground)
    LinearLayout sendBackground;

    String title = "Chat";
    String description = "Chat Window";
    String color = "#1976d2";
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

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

        setSendButtonBasedOnSelection();
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

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    private void setSendButtonBasedOnSelection() {
        if (color.equalsIgnoreCase("#444444"))
            sendBackground.setBackgroundResource(R.drawable.bubble444444);
        else if (color.equalsIgnoreCase("#f44336")) {
            sendBackground.setBackgroundResource(R.drawable.bubblef44336);
        } else if (color.equalsIgnoreCase("#3f5185")) {
            sendBackground.setBackgroundResource(R.drawable.bubble3f5185);
        } else if (color.equalsIgnoreCase("#ff9800")) {
            sendBackground.setBackgroundResource(R.drawable.bubbleff9800);
        } else if (color.equalsIgnoreCase("#ff5722")) {
            sendBackground.setBackgroundResource(R.drawable.bubbleff5722);
        } else if (color.equalsIgnoreCase("#4caf50")) {
            sendBackground.setBackgroundResource(R.drawable.bubble4caf50);
        } else if (color.equalsIgnoreCase("#ec407a")) {
            sendBackground.setBackgroundResource(R.drawable.bubbleec407a);
        } else if (color.equalsIgnoreCase("#009688")) {
            sendBackground.setBackgroundResource(R.drawable.bubble009688);
        } else if (color.equalsIgnoreCase("#0088CC")) {
            sendBackground.setBackgroundResource(R.drawable.bubble0088cc);
        }
    }

    @OnClick(R.id.send)
    public void onClickSend(View buttonSend) {
        final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        buttonSend.startAnimation(animTranslate);
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
}
