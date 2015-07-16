package com.getgenieapp.android.Activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.getgenieapp.android.Fragments.ChatFragment;
import com.getgenieapp.android.Fragments.MainFragment;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Categories;
import com.getgenieapp.android.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BaseActivity extends GenieBaseActivity implements MainFragment.onSelect {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.inject(this);
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Categories categories) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("color", categories.getBg_color());
        bundle.putLong("hide_time", categories.getHide_chats_time());
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
}
