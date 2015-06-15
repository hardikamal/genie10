package com.getgenieapp.android.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;

import com.getgenieapp.android.CustomViews.CustomAdapter;
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
        fontChangeCrawler.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    private void loadCategories() {
//        if (sharedPreferences.getString(DataFields.TOKEN, null) != null) {
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

        // ToDo add volley

            ArrayList<Categories> categoriesList = new ArrayList<>();
            categoriesList.add(new Categories("Food", 1, "#1976d2", "food", false, 1,"abcdsddssfe fgfsdvkwnsfedivujbifsd jifegnviskjfdvniclsjkfdvnucsfkjbsvnuclskjdfnvulikjsfdlnvculfkjdbsncuilskjfdn", 1433985369));
            categoriesList.add(new Categories("Cake", 2, "#7d7d7d", "cake", false, 0, "abcd", 1433985369));
            categoriesList.add(new Categories("Groceries", 3, "#f44336", "groceries", false, 0, "abcd", 1433985369));
            categoriesList.add(new Categories("Hotel", 4, "#3f5185", "hotel", false, 1, "abcd", 1433835369));
            categoriesList.add(new Categories("Misc", 5, "#009688", "misc", false, 1, "abcd", 1433835369));
            categoriesList.add(new Categories("Mobile", 6, "#ff9800", "mobile", false, 1, "abcd", 1433435369));
            categoriesList.add(new Categories("Movies", 7, "#ff5722", "movies", false, 1, "abcd", 1433435369));
            categoriesList.add(new Categories("Shopping", 8, "#4caf50", "shopping", false, 1, "abcd", 1433435369));
            categoriesList.add(new Categories("Travel", 9, "#ec407a", "travel", false, 1, "abcd", 1433435369));
            setupCategories(categoriesList);
//        } else {
//            startActivity(new Intent(this, RegisterActivity.class));
//            finish();
//        }
    }

    /**
     *
     * @param categoriesList
     */
    private void setupCategories(ArrayList<Categories> categoriesList) {
        loadingView.setLoading(false);
        CustomAdapter adapter = new CustomAdapter(this, categoriesList);

        // Set the Adapter to GridView
        gridView.setAdapter(adapter);
        gridView.setNumColumns(2);
        // Set the Required Animation to GridView and start the Animation
        // use fly_in_from_center to have 2nd type of animation effect (snapshot 2)
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fly_in_from_center_200);
        gridView.setAnimation(anim);
        anim.start();
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     */
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
