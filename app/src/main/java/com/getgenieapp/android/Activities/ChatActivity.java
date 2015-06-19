package com.getgenieapp.android.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.getgenieapp.android.CustomViews.Adapters.CustomChatAdapter;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;

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
    String color = "#1976d2";

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("title");
            color = getIntent().getStringExtra("color");
        }

        setThemeBasedOnSelection();
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        setSendButtonBasedOnSelection();
        getWindow().setBackgroundDrawableResource(R.drawable.wallpaper_wallpaper);
        try {
            getSupportActionBar().setTitle(title);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setTheme(R.style.MyMaterialThemeWithToolBar7d7d7d);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Messages> messages = new ArrayList<>();
        recyclerView.setAdapter(new CustomChatAdapter(messages, this));
    }

    private void setThemeBasedOnSelection() {
        if (color.equalsIgnoreCase("#7d7d7d"))
            setTheme(R.style.MyMaterialThemeWithToolBar7d7d7d);
        else if (color.equalsIgnoreCase("#f44336")) {
            setTheme(R.style.MyMaterialThemeWithToolBarf44336);
        } else if (color.equalsIgnoreCase("#3f5185")) {
            setTheme(R.style.MyMaterialThemeWithToolBar3f5185);
        } else if (color.equalsIgnoreCase("#ff9800")) {
            setTheme(R.style.MyMaterialThemeWithToolBarff9800);
        } else if (color.equalsIgnoreCase("#ff5722")) {
            setTheme(R.style.MyMaterialThemeWithToolBarff5722);
        } else if (color.equalsIgnoreCase("#4caf50")) {
            setTheme(R.style.MyMaterialThemeWithToolBar4caf50);
        } else if (color.equalsIgnoreCase("#ec407a")) {
            setTheme(R.style.MyMaterialThemeWithToolBarec407a);
        } else if (color.equalsIgnoreCase("#009688")) {
            setTheme(R.style.MyMaterialThemeWithToolBar009688);
        }
    }

    private void setSendButtonBasedOnSelection() {
        if (color.equalsIgnoreCase("#7d7d7d"))
            sendBackground.setBackgroundResource(R.drawable.bubble7d7d7d);
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
        }
    }

    @OnClick(R.id.send)
    public void onClickSend(View buttonSend) {
        final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        buttonSend.startAnimation(animTranslate);
    }
}
