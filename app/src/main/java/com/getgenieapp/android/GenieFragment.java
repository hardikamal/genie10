package com.getgenieapp.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class GenieFragment extends Fragment {
    public DBDataSource dbDataSource;
    public Gson gson;
    public GenieApplication genieApplication;
    public FontChangeCrawler fontChangeCrawlerRegular;
    public FontChangeCrawler fontChangeCrawlerMedium;
    public FontChangeCrawler fontChangeCrawlerLight;
    public SecurePreferences sharedPreferences;
    public Logging logging;
    public TinyBus mBus;
    public Utils utils;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        sharedPreferences = genieApplication.getSecurePrefs();
        fontChangeCrawlerRegular = genieApplication.getFontChangeCrawlerRegular();
        fontChangeCrawlerMedium = genieApplication.getFontChangeCrawlerMedium();
        fontChangeCrawlerLight = genieApplication.getFontChangeCrawlerLight();
        logging = genieApplication.getLoggingBuilder().setUp();
        mBus = genieApplication.getBus();
        gson = new Gson();
        utils = new Utils(getActivity());
        dbDataSource = new DBDataSource(getActivity());
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
}
