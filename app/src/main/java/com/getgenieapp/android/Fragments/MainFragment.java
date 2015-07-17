package com.getgenieapp.android.Fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.getgenieapp.android.CustomViews.Adapters.CustomAdapter;
import com.getgenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.getgenieapp.android.GenieFragment;
import com.getgenieapp.android.Objects.Categories;
import com.getgenieapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }

        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        ButterKnife.inject(this, rootView);
        refreshData();
        logging.LogV("Main Activity");
        loadingView.setText("Loading Categories...");
        loadingView.setLoading(true);
        loadCategories();
        fontChangeCrawlerRegular.replaceFonts((ViewGroup) rootView);
        return rootView;
    }

    private void refreshData() {
        // todo refresh data
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
        CustomAdapter customAdapter = new CustomAdapter(categoriesList, getActivity());
        recyclerView.setAdapter(customAdapter);
//        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fly_in_from_center_200);
//        recyclerView.setAnimation(anim);
//        anim.start();
    }

    public interface onSelect {
        public void onClick(Categories categories);
    }
}
