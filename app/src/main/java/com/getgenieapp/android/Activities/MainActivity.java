package com.getgenieapp.android.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.getgenieapp.android.CustomViews.LoadingView;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.GenieActivity;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Categories;
import com.getgenieapp.android.Objects.ListCategories;
import com.getgenieapp.android.Objects.Register;
import com.getgenieapp.android.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends GenieBaseActivity {
    @InjectView(R.id.loadingView)
    LoadingView loadingView;
    @InjectView(R.id.gridView)
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        loadingView.setText("Loading Categories...");
        loadingView.setLoading(true);
        loadCategories();
    }

    private void loadCategories() {
        if (sharedPreferences.getString(DataFields.TOKEN, null) != null) {
//            JsonObject jsonObject = new JsonObject();
//
//            Ion.with(this)
//                    .load(DataFields.getServerUrl() + DataFields.REGISTERURL)
//                    .setJsonObjectBody((JsonObject) new JsonParser().parse(jsonObject.toString()))
//                    .asJsonObject()
//                    .setCallback(new FutureCallback<JsonObject>() {
//                        @Override
//                        public void onCompleted(Exception e, JsonObject result) {
//                            loadingView.setLoading(false);
//                            setupCategories(gson.fromJson(result, ListCategories.class).getCategoriesList());
//                        }
//                    });

            List<Categories> categoriesList = new ArrayList<>();
            categoriesList.add(new Categories("Food", 1, "#1976d2", "R.drawable.food", "abcd", "abcde"));
            categoriesList.add(new Categories("Cake", 2, "#7d7d7d", "R.drawable.cake", "abcd", "abcde"));
            categoriesList.add(new Categories("Groceries", 3, "#f44336", "R.drawable.groceries", "abcd", "abcde"));
            categoriesList.add(new Categories("Hotel", 4, "#3f5185", "R.drawable.hotel", "abcd", "abcde"));
            categoriesList.add(new Categories("Misc", 5, "#009688", "R.drawable.misc", "abcd", "abcde"));
            categoriesList.add(new Categories("Mobile", 6, "#ff9800", "R.drawable.mobile", "abcd", "abcde"));
            categoriesList.add(new Categories("Movies", 7, "#ff5722", "R.drawable.movies", "abcd", "abcde"));
            categoriesList.add(new Categories("Shopping", 8, "#4caf50", "R.drawable.shopping", "abcd", "abcde"));
            categoriesList.add(new Categories("Travel", 9, "#ec407a", "R.drawable.travel", "abcd", "abcde"));
            setupCategories(categoriesList);
        } else {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
    }

    private void setupCategories(List<Categories> categoriesList) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
