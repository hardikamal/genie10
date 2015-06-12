package com.getgenieapp.android.Activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.R;

import butterknife.InjectView;
import butterknife.OnClick;

public class OrderDetailsActivity extends GenieBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
    }
}
