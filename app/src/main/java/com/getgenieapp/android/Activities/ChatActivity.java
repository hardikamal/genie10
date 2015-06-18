package com.getgenieapp.android.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

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
    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        getWindow().setBackgroundDrawableResource(R.drawable.wallpaper_wallpaper) ;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // ToDo set actionbar color above 16 or jellybean
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Messages> messages = new ArrayList<>();
        recyclerView.setAdapter(new CustomChatAdapter(messages, this));


    }

    @OnClick(R.id.send)
    public void onClickSend(View buttonSend)
    {
        final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        buttonSend.startAnimation(animTranslate);
    }
}
