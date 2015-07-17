package com.getgenieapp.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.getgenieapp.android.Database.DBDataSource;
import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.Logging;
import com.getgenieapp.android.Extras.Utils;
import com.getgenieapp.android.SecurePreferences.SecurePreferences;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.gson.Gson;

import de.halfbit.tinybus.TinyBus;

public class GenieBaseActivity extends AppCompatActivity {
    public Gson gson;
    public GenieApplication genieApplication;
    public SecurePreferences sharedPreferences;
    public FontChangeCrawler fontChangeCrawlerRegular;
    public FontChangeCrawler fontChangeCrawlerMedium;
    public FontChangeCrawler fontChangeCrawlerLight;
    public Logging logging;
    public TinyBus mBus;
    public Utils utils;
    public DBDataSource dbDataSource;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        fontChangeCrawlerRegular = genieApplication.getFontChangeCrawlerRegular();
        fontChangeCrawlerMedium = genieApplication.getFontChangeCrawlerMedium();
        fontChangeCrawlerLight = genieApplication.getFontChangeCrawlerLight();
        logging = genieApplication.getLoggingBuilder().setUp();
        sharedPreferences = genieApplication.getSecurePrefs();
        mBus = genieApplication.getBus();
        gson = new Gson();
        utils = new Utils(this);
        dbDataSource = new DBDataSource(this);
    }

    public void startFragment(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commit();
    }

    public void startFragmentFromLeft(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commit();
    }

    public void setupUI(View view, final Activity activity) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }

            });
        }

        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView, activity);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
        // use this to save your snacks for later
        //saveState.putBundle(SAVED_SNACKBAR, mSnackBar.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle loadState) {
        super.onRestoreInstanceState(loadState);
        // use this to load your snacks for later
        //mSnackBar.onRestoreInstanceState(loadState.getBundle(SAVED_SNACKBAR));
    }

    public void showToast(String message, Short duration, SnackBar.Style style) {
        new SnackBar.Builder(this)
                .withMessage(message)
                .withStyle(style)
                .withDuration(duration)
                .show();
    }
}
