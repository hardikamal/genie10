package com.supergenieapp.android.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.localytics.android.Localytics;
import com.supergenieapp.android.CustomViews.Adapters.CustomOrderAdapter;
import com.supergenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Fragments.NavigationDrawerFragment;
import com.supergenieapp.android.GenieBaseActivity;
import com.supergenieapp.android.Objects.Order;
import com.supergenieapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

// Order details activity
// User can view their precious orders here.
// If the user comes from Chat activity. It only show the orders from that specific category
// Recycler view for showing the list of previous orders
// Adapter is CustomOrderAdapter

public class OrderDetailsActivity extends GenieBaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    @InjectView(R.id.loadingview)
    LoadingView loadingView;
    @InjectView(R.id.orderList)
    RecyclerView orderList;
    @InjectView(R.id.noorders)
    TextView noOrders;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public HashMap<String, String> dataAdd = new HashMap<>();

    @Override
    public void onResume() {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Order Details");
        Localytics.upload();
    }

    @Override
    protected void onStart() {
        super.onStart();
        logging.LogI("On Start");
    }

    @Override
    protected void onDestroy() {
        logging.LogI("On Destroy");
        localyticsBuildHashMap("Order Details Activity", dataAdd);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!mNavigationDrawerFragment.isVisible()) {
            super.onBackPressed();
        } else {
            mNavigationDrawerFragment.toggleDrawerLayout();
        }
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

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    private void getUserOrders() {
        loadingView.setLoading(true);
        final ArrayList<Order> orders = new ArrayList<>();
        dataAdd.put("Server Call", "Get Orders");
        JsonArrayRequest req = new JsonArrayRequest(DataFields.getServerUrl() + DataFields.ORDERS,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        dataAdd.put("Server Call", "ORDERS data Success");
                        System.out.print(response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String payment_url = null;
                                String id = null;
                                String title = null;
                                String updated_at = null;
                                String service_provider = null;
                                String description = null;
                                String created_at = null;
                                String category_id = null;
                                String user_id = null;
                                String agent_id = null;
                                String cost = null;
                                String last_message_id = null;
                                if (jsonObject.has("payment_url")) {
                                    payment_url = jsonObject.getString("payment_url");
                                }
                                if (jsonObject.has("id")) {
                                    id = jsonObject.getString("id");
                                }
                                if (jsonObject.has("title")) {
                                    title = jsonObject.getString("title");
                                }
                                if (jsonObject.has("updated_at")) {
                                    updated_at = jsonObject.getString("updated_at");
                                }
                                if (jsonObject.has("service_provider")) {
                                    service_provider = jsonObject.getString("service_provider");
                                }
                                if (jsonObject.has("description")) {
                                    description = jsonObject.getString("description");
                                }
                                if (jsonObject.has("created_at")) {
                                    created_at = jsonObject.getString("created_at");
                                }
                                if (jsonObject.has("category_id")) {
                                    category_id = jsonObject.getString("category_id");
                                }
                                if (jsonObject.has("user_id")) {
                                    user_id = jsonObject.getString("user_id");
                                }
                                if (jsonObject.has("agent_id")) {
                                    agent_id = jsonObject.getString("agent_id");
                                }
                                if (jsonObject.has("cost")) {
                                    cost = jsonObject.getString("cost");
                                }
                                if (jsonObject.has("last_message_id")) {
                                    last_message_id = jsonObject.getString("last_message_id");
                                }
                                if (id != null)
                                    orders.add(new Order(id, payment_url, title, updated_at, service_provider, description, created_at, category_id, user_id, agent_id, cost, last_message_id));
                            }
                            setupOrders(orders);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // // TODO: 8/6/2015
                dataAdd.put("Server Call", "ORDERS Server 500 Error");
                localyticsBuild(DataFields.getServerUrl() + DataFields.ORDERS + " 500 Error");
                error.printStackTrace();
                ArrayList<Order> orders = new ArrayList<>();
                setupOrders(orders);
//                showAlert();
                loadingView.setLoading(false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", sharedPreferences.getString(DataFields.TOKEN, ""));
                return params;
            }
        };
        genieApplication.addToRequestQueue(req);
    }

    private void setupOrders(ArrayList<Order> orders) {
        Collections.sort(orders, Collections.reverseOrder());
        if (orders.size() == 0) {
            noOrders.setVisibility(View.VISIBLE);
            orderList.setVisibility(View.GONE);
//            showAlert();
        } else {
            orderList.setVisibility(View.VISIBLE);
            noOrders.setVisibility(View.GONE);
        }
        loadingView.setLoading(false);
        dataAdd.put("Size Orders", "Returned Size " + orders.size());
        orderList.removeAllViews();
        orderList.setHasFixedSize(true);
        orderList.setLayoutManager(new LinearLayoutManager(this));
        orderList.setAdapter(new CustomOrderAdapter(orders, this));
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fly_in_from_center);
        orderList.setAnimation(anim);
        anim.start();
    }

    private void showAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false)
                .setMessage(getString(R.string.noorderyet))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
                        dialog.dismiss();
                    }
                });
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                dataAdd.put("Pressed", "Home/Back Menu");
                onBackPressed();
                return true;
            case R.id.action_menu:
                mNavigationDrawerFragment.toggleDrawerLayout();
                return true;
            case R.id.action_share:
                if (mNavigationDrawerFragment.isVisible()) {
                    mNavigationDrawerFragment.toggleDrawerLayout();
                }
                dataAdd.put("Pressed", "Share Menu");
                localyticsBuild("Profile Share Pressed");
                localyticsBuild("Profile Share Pressed in Order Details");
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

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.body, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((OrderDetailsActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private void onSectionAttached(int anInt) {
        // // TODO: 8/6/2015
    }

    public void closeMenu() {
        if (mNavigationDrawerFragment.isVisible())
            mNavigationDrawerFragment.toggleDrawerLayout();
    }
}