package com.getgenieapp.android.Activities;

import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.R;

public class ChatActivity extends GenieBaseActivity {
    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // ToDo set actionbar color above 16 or jellybean
        }
    }
}
