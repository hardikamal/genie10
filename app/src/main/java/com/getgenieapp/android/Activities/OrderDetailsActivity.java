package com.getgenieapp.android.Activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import com.getgenieapp.android.CustomViews.LoadingView;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Order;
import com.getgenieapp.android.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class OrderDetailsActivity extends GenieBaseActivity {
    @InjectView(R.id.loadingview)
    LoadingView loadingView;
    @InjectView(R.id.orderlist)
    ListView orderList;

    List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.inject(this);

        getUserOrders();
    }

    private void getUserOrders() {
        loadingView.setLoading(true);

    }
}
