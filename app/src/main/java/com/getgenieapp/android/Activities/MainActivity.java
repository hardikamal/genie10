package com.getgenieapp.android.Activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.getgenieapp.android.GenieActivity;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.R;

public class MainActivity extends GenieBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
