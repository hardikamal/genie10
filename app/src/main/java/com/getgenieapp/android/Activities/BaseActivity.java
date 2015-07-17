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
import android.widget.Toast;

import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Fragments.ChatFragment;
import com.getgenieapp.android.Fragments.MainFragment;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Categories;
import com.getgenieapp.android.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BaseActivity extends GenieBaseActivity implements MainFragment.onSelect {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.screen)
    LinearLayout screen;
    private Socket mSocket;

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
    public void onResume() {
        super.onResume();
        System.out.println("Socket connection status : " + genieApplication.getSocket().connected());
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Socket connection status : " + genieApplication.getSocket().connected());
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
    public void onStart() {
        super.onStart();
        mSocket.on("reset connection", reset_connection);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("user message", sendUserMessage);
        mSocket.on("message_received", onMessageReceived);
        mSocket.on("typing", onTyping);
        mSocket.on("server_error", onServerError);
        mSocket.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("message_received", onMessageReceived);
        mSocket.off("user message", sendUserMessage);
        mSocket.off("typing", onTyping);
        mSocket.off("server_error", onServerError);
        mSocket.off("reset connection", reset_connection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager fragmentManager = BaseActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();

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

        System.out.println("Socket connection status : " + mSocket.connected());
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
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

        try {
            getSupportActionBar().setTitle(categories.getName());
        } catch (NullPointerException e) {
            e.printStackTrace();
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
