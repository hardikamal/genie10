package com.supergenieapp.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.localytics.android.Localytics;
import com.supergenieapp.android.Database.DBDataSource;
import com.supergenieapp.android.Extras.FontChangeCrawler;
import com.supergenieapp.android.Extras.Logging;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.SecurePreferences.SecurePreferences;

import org.json.JSONObject;

import java.util.HashMap;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by Raviteja on 6/6/2015.
 */
public class GenieActivity extends Activity {
    public Gson gson;
    public SecurePreferences sharedPreferences;
    public GenieApplication genieApplication;
    public FontChangeCrawler fontChangeCrawlerRegular;
    public Logging logging;
    public TinyBus mBus;
    public ImageLoader imageLoader;
    public Utils utils;
    public DBDataSource dbDataSource;
    public int year;
    /**
     * @param savedInstance
     */
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Localytics.registerPush(getString(R.string.projectId));
        genieApplication = GenieApplication.getInstance();
        fontChangeCrawlerRegular = genieApplication.getFontChangeCrawlerRegular();
        sharedPreferences = genieApplication.getSecurePrefs();
        logging = genieApplication.getLoggingBuilder().setUp();
        mBus = genieApplication.getBus();
        gson = new Gson();
        imageLoader = genieApplication.getImageLoader();
        utils = new Utils(this);
        dbDataSource = genieApplication.getDBDataSource();
        year = genieApplication.DeviceYear();
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
    }

    @Override
    protected void onRestoreInstanceState(Bundle loadState) {
        super.onRestoreInstanceState(loadState);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public void localyticsBuildHashMap(String eventName, HashMap<String, String> myValues) {
        memoryConsumption(myValues);
        Localytics.tagEvent(eventName, myValues);
    }

    public void localyticsBuild(String eventName) {
        Localytics.tagEvent(eventName);
    }

    public void memoryConsumption(HashMap<String, String> map) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        if (mi.lowMemory) {
            localyticsBuild("Low Memory Detected");
        }
        map.put("Is Memory Low", String.valueOf(mi.lowMemory));
        map.put("Memory Consumption", String.valueOf(mi.availMem / 1048576L));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            map.put("Memory Total", String.valueOf(mi.totalMem / 1048576L));
            map.put("Memory free percentage", String.valueOf((mi.availMem * 100) / mi.totalMem));
        }
    }
}