package com.getgenieapp.android.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;

import com.getgenieapp.android.CustomViews.Adapters.CustomAdapter;
import com.getgenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Categories;
import com.getgenieapp.android.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends GenieBaseActivity {
    @InjectView(R.id.loadingView)
    LoadingView loadingView;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        logging.LogV("Main Activity");
        loadingView.setText("Loading Categories...");
        loadingView.setLoading(true);
        loadCategories();
        fontChangeCrawler.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    private void loadCategories() {
        logging.LogV("Get Categories");
//        if (sharedPreferences.getString(DataFields.TOKEN, null) != null) {

        // ToDo add volley
        ArrayList<Categories> categoriesList = new ArrayList<>();
        categoriesList.add(new Categories(1, 1, "#0088CC", "food", "Eat More, Eat Faster, Eat Healthier.", "Food", 1433985369));
        categoriesList.add(new Categories(2, 0, "#444444", "cake", "Ordering a cake is now a piece of cake.", "Cake", 1433985369));
        categoriesList.add(new Categories(3, 0, "#f44336", "groceries", "Fresh supplies for you refrigerator.", "Groceries", 1433985369));
        categoriesList.add(new Categories(4, 1, "#3f5185", "hotel", "Need a place to stay, it's a chat away.", "Hotel", 1433985369));
        categoriesList.add(new Categories(5, 0, "#ff9800", "mobile", "Ran out of Balance? Balance is very important in life.", "Recharge", 1433985369));
        categoriesList.add(new Categories(6, 1, "#ff5722", "movies", "Best price, Seat and Timing make for a great movie experience.", "Movies", 1433985369));
        categoriesList.add(new Categories(7, 0, "#4caf50", "shopping", "We get you what you want, no hassle, no clutter and best prices.", "Shopping", 1433985369));
        categoriesList.add(new Categories(8, 0, "#ec407a", "travel", "It's a flight, it's a bus, it's a train.", "Travel", 1433985369));
        setupCategories(categoriesList);
//        } else {
//            startActivity(new Intent(this, RegisterActivity.class));
//            finish();
//        }
    }

    /**
     * @param categoriesList
     */
    private void setupCategories(ArrayList<Categories> categoriesList) {
        loadingView.setLoading(false);
        logging.LogV("Close Loading View and Set Recycler");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new CustomAdapter(categoriesList, this));
        // Set the Required Animation to GridView and start the Animation
        // use fly_in_from_center to have 2nd type of animation effect (snapshot 2)
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fly_in_from_center_200);
        recyclerView.setAnimation(anim);
        anim.start();
    }

    /**
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
