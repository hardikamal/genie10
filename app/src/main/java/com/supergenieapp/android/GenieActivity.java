package com.supergenieapp.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.supergenieapp.android.Database.DBDataSource;
import com.supergenieapp.android.Extras.FontChangeCrawler;
import com.supergenieapp.android.Extras.Logging;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.SecurePreferences.SecurePreferences;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

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
    public MixpanelAPI mixpanel;

    /**
     * @param savedInstance
     */
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        fontChangeCrawlerRegular = genieApplication.getFontChangeCrawlerRegular();
        sharedPreferences = genieApplication.getSecurePrefs();
        logging = genieApplication.getLoggingBuilder().setUp();
        mBus = genieApplication.getBus();
        gson = new Gson();
        imageLoader = genieApplication.getImageLoader();
        utils = new Utils(this);
        dbDataSource = genieApplication.getDBDataSource();
        mixpanel = MixpanelAPI.getInstance(this, getString(R.string.mixpanel));
        mixpanel.identify(utils.getDeviceSerialNumber());
        mixpanel.getPeople().identify(utils.getDeviceSerialNumber());
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

    public void mixPanelBuildHashMap(String eventName, HashMap<String, Object> myValues) {
        memoryConsumption(myValues);
        mixpanel.trackMap(eventName, myValues);
    }

    public void mixPanelBuildJSON(String eventName, JSONObject jsonObject) {
        mixpanel.track(eventName, jsonObject);
    }

    public void mixPanelBuild(String eventName) {
        mixpanel.track(eventName);
    }

    public void mixPanelFlush() {
        mixpanel.flush();
    }

    public void mixPanelTimerStart(String timerName) {
        mixpanel.timeEvent(timerName);
    }

    public void mixPanelTimerStop(String timerName) {
        mixpanel.track(timerName);
    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }

    public void memoryConsumption(HashMap<String, Object> map) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        if (mi.lowMemory) {
            mixPanelBuild("Low Memory Detected");
        }
        map.put("Is Memory Low", mi.lowMemory);
        map.put("Memory Consumption", mi.availMem / 1048576L);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            map.put("Memory Total", mi.totalMem / 1048576L);
            map.put("Memory free percentage", (mi.availMem * 100) / mi.totalMem);
        }
    }
}