package com.getgenieapp.android.Activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;

import com.getgenieapp.android.CustomViews.CustomAdapter;
import com.getgenieapp.android.CustomViews.CustomOrderAdapter;
import com.getgenieapp.android.CustomViews.LoadingView;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Categories;
import com.getgenieapp.android.Objects.Order;
import com.getgenieapp.android.Objects.OrderCategory;
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
        fontChangeCrawler.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
        getUserOrders();
    }

    private void getUserOrders() {
        loadingView.setLoading(true);
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order(12, "Playdoh", new OrderCategory(2, "#142141", null), "Flipkart", "Playdoh combo set.", 1241251281, 2, 123, 243.00, "_1as121as21"));
        orders.add(new Order(12, "Bus Ticket Hyderabad to Pune", new OrderCategory(7, "#142141", null), "Red Bus", "Bus ticket from Hyderbad to Pune, 26th January", 1241251281, 2, 121, 950.00, "_12jh1bi1"));
        setupOrders(orders);
    }

    private void setupOrders(ArrayList<Order> orders) {
        loadingView.setLoading(false);
        CustomOrderAdapter adapter = new CustomOrderAdapter(this, orders);

        // Set the Adapter to GridView
        orderList.setAdapter(adapter);
        // Set the Required Animation to GridView and start the Animation
        // use fly_in_from_center to have 2nd type of animation effect (snapshot 2)
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fly_in_from_center_200);
        orderList.setAnimation(anim);
        anim.start();
    }
}