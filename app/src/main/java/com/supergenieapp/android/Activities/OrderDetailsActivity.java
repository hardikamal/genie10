package com.supergenieapp.android.Activities;

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

import com.supergenieapp.android.CustomViews.Adapters.CustomOrderAdapter;
import com.supergenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.supergenieapp.android.GenieBaseActivity;
import com.supergenieapp.android.Objects.Order;
import com.supergenieapp.android.Objects.OrderCategory;
import com.supergenieapp.android.R;

import java.util.ArrayList;
import java.util.HashMap;

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

    public HashMap<String, Object> mixpanelDataAdd = new HashMap<>();

    @Override
    protected void onStart() {
        super.onStart();
        mixPanelTimerStart(OrderDetailsActivity.class.getName());
        logging.LogI("On Start");
    }

    @Override
    protected void onDestroy() {
        logging.LogI("On Destroy");
        mixPanelTimerStop(OrderDetailsActivity.class.getName());
        mixPanelBuildHashMap("General Run " + OrderDetailsActivity.class.getName(), mixpanelDataAdd);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setupOrders(orders);
    }

    private void setupOrders(ArrayList<Order> orders) {
        loadingView.setLoading(false);
        mixpanelDataAdd.put("Size Orders", "Returned Size " + orders.size());
        orderList.removeAllViews();
        orderList.setHasFixedSize(true);
        orderList.setLayoutManager(new LinearLayoutManager(this));
        orderList.setAdapter(new CustomOrderAdapter(orders, this));
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fly_in_from_center);
        orderList.setAnimation(anim);
        anim.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_orders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mixpanelDataAdd.put("Pressed", "Home/Back Menu");
                mixPanelBuild("Home/Back Menu Pressed");
                onBackPressed();
                return true;
            case R.id.action_profile:
                mixpanelDataAdd.put("Pressed", "Profile Menu");
                mixPanelBuild("Profile Menu Pressed");
                Intent profileIntent = new Intent(this, UserProfileActivity.class);
                profileIntent.putExtra("canclose", true);
                startActivity(profileIntent);
                if (canClose)
                    finish();
                return true;
            case R.id.action_share:
                mixpanelDataAdd.put("Pressed", "Share Menu");
                mixPanelBuild("Profile Share Pressed");
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