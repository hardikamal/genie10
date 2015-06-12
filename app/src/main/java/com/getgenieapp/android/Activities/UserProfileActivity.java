package com.getgenieapp.android.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UserProfileActivity extends GenieBaseActivity
{
    @InjectView(R.id.orderdetails)
    Button orderdetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.inject(this);
        fontChangeCrawler.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @OnClick(R.id.orderdetails)
    public void onClickOrderDetails()
    {
        startActivity(new Intent(this, OrderDetailsActivity.class));
    }

    @OnClick(R.id.orderdetailsbutton)
    public void onClickOrderDetailsButton()
    {
        startActivity(new Intent(this, OrderDetailsActivity.class));
    }
}
