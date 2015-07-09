package com.getgenieapp.android.Activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.getgenieapp.android.CustomViews.Adapters.CustomOrderAdapter;
import com.getgenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Order;
import com.getgenieapp.android.Objects.OrderCategory;
import com.getgenieapp.android.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderDetailsActivity extends GenieBaseActivity {
    @InjectView(R.id.loadingview)
    LoadingView loadingView;
    @InjectView(R.id.orderList)
    RecyclerView orderList;

    List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.inject(this);

        getUserOrders();
        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    private void getUserOrders() {
        loadingView.setLoading(true);
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order(12, "Playdoh", new OrderCategory(2, "#4caf50", "shopping"), "Flipkart", "Playdoh combo set.", 1241251281, 2, 123, 243.00, "_1as121as21"));
        orders.add(new Order(12, "Bus Ticket Hyderabad to Pune", new OrderCategory(7, "#ec407a", "travel"), "Red Bus", "Bus ticket from Hyderbad to Pune, 26th January", 1241251281, 2, 121, 950.00, "_12jh1bi1"));
        setupOrders(orders);
    }

    private void setupOrders(ArrayList<Order> orders) {
        loadingView.setLoading(false);

        orderList.setHasFixedSize(true);
        orderList.setLayoutManager(new LinearLayoutManager(this));
        orderList.setAdapter(new CustomOrderAdapter(orders, this));

        // Set the Required Animation to GridView and start the Animation
        // use fly_in_from_center to have 2nd type of animation effect (snapshot 2)
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fly_in_from_center_200);
        orderList.setAnimation(anim);
        anim.start();
    }
}