package com.getgenieapp.android.Fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.getgenieapp.android.CustomViews.Adapters.CustomAdapter;
import com.getgenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.GenieFragment;
import com.getgenieapp.android.Objects.Categories;
import com.getgenieapp.android.Objects.Chat;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.halfbit.tinybus.Subscribe;

/**
 * Created by Raviteja on 7/15/2015.
 */
public class MainFragment extends GenieFragment {
    @InjectView(R.id.loadingView)
    LoadingView loadingView;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    static ArrayList<Categories> categoriesList = new ArrayList<>();
    onSelect on_Select;
    CustomAdapter customAdapter = null;
    ViewGroup viewGroup;
    HashMap<String, Object> mixpanelDataAdd = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        this.viewGroup = container;
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        ButterKnife.inject(this, rootView);

        logging.LogV("Main Activity");
        loadingView.setText("Loading Categories...");
        loadingView.setLoading(true);
        loadCategories();
        fontChangeCrawlerRegular.replaceFonts((ViewGroup) rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        hideKeyboard(getActivity());
        mixPanelTimerStart(MainFragment.class.getName());
        logging.LogV("Showed", "on Start");
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        mixPanelTimerStop(MainFragment.class.getName());
        mixPanelBuildHashMap("General Run " + MainFragment.class.getName(), mixpanelDataAdd);
        logging.LogV("Showed", "on Stop");
        super.onStop();
    }

    public void refreshDataFromLocal() {
        mixpanelDataAdd.put("Refresh Categories", "From Local");
        ArrayList<Categories> catList = dbDataSource.getAllCategories();
        if (catList.size() > 0) {
            categoriesList.clear();
            for (Categories categories : catList) {
                categoriesList.add(categories);
            }
            if (customAdapter != null) {
                logging.LogV("Updating View");
                View v = recyclerView.getChildAt(0);
                int top = (v == null) ? 0 : v.getTop();
                customAdapter.notifyDataSetChanged();
                if (top < categoriesList.size())
                    recyclerView.scrollToPosition(top);

            }
            setupCategories(categoriesList);
        }
    }

    private void refreshData() {
        mixpanelDataAdd.put("Refresh Categories", "From Server");
        logging.LogV("Refreshing Data");
        mixPanelTimerStart(DataFields.getServerUrl() + DataFields.CATEGORIES);
        JsonArrayRequest req = new JsonArrayRequest(DataFields.getServerUrl() + DataFields.CATEGORIES,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        mixPanelTimerStop(DataFields.getServerUrl() + DataFields.CATEGORIES);
                        System.out.println(response.toString());
                        if (response.length() > 0) {
                            ArrayList<Categories> localCategories = dbDataSource.getAllCategories();
                            ArrayList<Categories> catList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.getJSONObject(i).toString());
                                    Categories categories = new Categories(jsonObject.getInt("id"), caculateNotificationCount(localCategories, jsonObject.getInt("id"), jsonObject.getInt("notification_count")),
                                            jsonObject.getString("bg_color"), jsonObject.getString("image_url"), jsonObject.getString("description"), jsonObject.getString("name"),
                                            jsonObject.getLong("hide_chats_time"));
                                    catList.add(categories);
                                    dbDataSource.addNormalCategories(categories);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (catList.size() > 0) {
                                categoriesList.clear();
                                for (Categories categories : catList) {
                                    categoriesList.add(categories);
                                }
                                if (customAdapter != null) {
                                    logging.LogV("Updating View");
                                    View v = recyclerView.getChildAt(0);
                                    int top = (v == null) ? 0 : v.getTop();
                                    customAdapter.notifyDataSetChanged();
                                    if (top < categoriesList.size())
                                        recyclerView.scrollToPosition(top);

                                } else {
                                    setupCategories(categoriesList);
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mixPanelTimerStop(DataFields.getServerUrl() + DataFields.CATEGORIES);
                error.printStackTrace();
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

    private void loadCategories() {
        logging.LogV("Get Categories");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            refreshDataFromLocal();
        } else {
            if (getActivity().getIntent().getExtras() != null && getActivity().getIntent().hasExtra("category")) {
                ArrayList<Categories> localCategories = dbDataSource.getAllCategories();
                ArrayList<String> rawList = getActivity().getIntent().getStringArrayListExtra("category");
                if (rawList.size() > 0)
                    categoriesList.clear();
                for (String raw : rawList) {
                    try {
                        JSONObject jsonObject = new JSONObject(raw);
                        categoriesList.add(new Categories(jsonObject.getInt("id"), caculateNotificationCount(localCategories, jsonObject.getInt("id"), jsonObject.getInt("notification_count")),
                                jsonObject.getString("bg_color"), jsonObject.getString("image_url"), jsonObject.getString("description"), jsonObject.getString("name"),
                                jsonObject.getLong("hide_chats_time")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dbDataSource.cleanCatTable();
                dbDataSource.addFastCategories(categoriesList);
            } else {
                refreshData();
            }
        }

        setupCategories(categoriesList);
    }

    private int caculateNotificationCount(ArrayList<Categories> localCategories, int id, int notification_count) {
        if (notification_count != 0 || id == 0)
            return notification_count;
        if (localCategories.size() == 0)
            return notification_count;
        for (Categories cat : localCategories) {
            if (cat.getId() == id)
                return cat.getNotification_count();
        }
        return 0;
    }

    /**
     * @param categoriesList
     */
    private void setupCategories(ArrayList<Categories> categoriesList) {
        loadingView.setLoading(false);
        logging.LogV("Close Loading View and Set Recycler");
        recyclerView.removeAllViews();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        customAdapter = new CustomAdapter(categoriesList, getActivity());
        recyclerView.setAdapter(customAdapter);
    }

    public interface onSelect {
        public void onClick(Categories categories);
    }
}
