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
import com.getgenieapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }

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
    public void onStart()
    {
        super.onStart();
        hideKeyboard(getActivity());
    }

    private void refreshData() {
        logging.LogV("Refreshing Data");
        JsonArrayRequest req = new JsonArrayRequest(DataFields.getServerUrl() + DataFields.CATEGORIES,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        System.out.println(response.toString());
                        if (response.length() > 0) {
                            ArrayList<Categories> catList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.getJSONObject(i).toString());
                                    catList.add(new Categories(jsonObject.getInt("id"), jsonObject.getInt("notification_count"),
                                            jsonObject.getString("bg_color"), jsonObject.getString("image_url"), jsonObject.getString("description"), jsonObject.getString("name"),
                                            jsonObject.getLong("hide_chats_time")));
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
        if (getActivity().getIntent().getExtras() != null && getActivity().getIntent().hasExtra("category")) {
            ArrayList<String> rawList = getActivity().getIntent().getStringArrayListExtra("category");
            if (rawList.size() > 0)
                categoriesList.clear();
            for (String raw : rawList) {
                try {
                    JSONObject jsonObject = new JSONObject(raw);
                    categoriesList.add(new Categories(jsonObject.getInt("id"), jsonObject.getInt("notification_count"),
                            jsonObject.getString("bg_color"), jsonObject.getString("image_url"), jsonObject.getString("description"), jsonObject.getString("name"),
                            jsonObject.getLong("hide_chats_time")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            refreshData();
        }
        setupCategories(categoriesList);
    }

    /**
     * @param categoriesList
     */
    private void setupCategories(ArrayList<Categories> categoriesList) {
        loadingView.setLoading(false);
        logging.LogV("Close Loading View and Set Recycler");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        customAdapter = new CustomAdapter(categoriesList, getActivity());
        recyclerView.setAdapter(customAdapter);
    }

    public interface onSelect {
        public void onClick(Categories categories);
    }
}
