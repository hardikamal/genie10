package com.getgenieapp.android.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

// Order details activity
// User can view their precious orders here.
// If the user comes from Chat activity. It only show the orders from that specific category
// Recycler view for showing the list of previous orders
// Adapter is CustomOrderAdapter

public class OrderDetailsActivity extends GenieBaseActivity {
    @InjectView(R.id.loadingview)
    LoadingView loadingView;
    @InjectView(R.id.orderList)
    RecyclerView orderList;
    boolean canClose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Socket connection status : " + genieApplication.getSocket().connected());
        setContentView(R.layout.activity_order_details);
        ButterKnife.inject(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getUserOrders();
        if (getIntent().getExtras() != null) {
            canClose = getIntent().getBooleanExtra("canclose", false);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_orders, menu);
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
                onBackPressed();
                return true;
            case R.id.action_profile:
                Intent profileIntent = new Intent(this, UserProfileActivity.class);
                profileIntent.putExtra("canclose", true);
                startActivity(profileIntent);
                if (canClose)
                    finish();
                return true;
            case R.id.action_home:
                Intent intent = new Intent(this, BaseActivity.class);
                intent.putExtra("page", "categories");
                startActivity(intent);
                finish();
                return true;
            case R.id.action_share:
                String shareBody = getString(R.string.bodytext);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.trygenie));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.shareus)));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}