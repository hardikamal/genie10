package com.getgenieapp.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.Logging;
import com.getgenieapp.android.Extras.Utils;
import com.getgenieapp.android.SecurePreferences.SecurePreferences;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.gson.Gson;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by Raviteja on 6/6/2015.
 */
public class GenieActivity extends Activity {
    public Gson gson;
    public SecurePreferences sharedPreferences;
    public GenieApplication genieApplication;
    public FontChangeCrawler fontChangeCrawlerRegular;
    public FontChangeCrawler fontChangeCrawlerMedium;
    public FontChangeCrawler fontChangeCrawlerLight;
    public Logging logging;
    public TinyBus mBus;
    public ImageLoader imageLoader;
    public Utils utils;

    /**
     * @param savedInstance
     */
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        fontChangeCrawlerRegular = genieApplication.getFontChangeCrawlerRegular();
        fontChangeCrawlerMedium = genieApplication.getFontChangeCrawlerMedium();
        fontChangeCrawlerLight = genieApplication.getFontChangeCrawlerLight();
        sharedPreferences = genieApplication.getSecurePrefs();
        logging = genieApplication.getLoggingBuilder().setUp();
        mBus = genieApplication.getBus();
        gson = new Gson();
        imageLoader = genieApplication.getImageLoader();
        utils = new Utils(this);
    }

    public void setupUI(View view, final Activity activity) {
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
