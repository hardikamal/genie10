package com.supergenieapp.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;

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

public class GenieFragment extends Fragment {
    public DBDataSource dbDataSource;
    public Gson gson;
    public GenieApplication genieApplication;
    public FontChangeCrawler fontChangeCrawlerRegular;
    public SecurePreferences sharedPreferences;
    public Logging logging;
    public TinyBus mBus;
    public Utils utils;
    public ImageLoader imageLoader;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        sharedPreferences = genieApplication.getSecurePrefs();
        fontChangeCrawlerRegular = genieApplication.getFontChangeCrawlerRegular();
        logging = genieApplication.getLoggingBuilder().setUp();
        mBus = genieApplication.getBus();
        imageLoader = genieApplication.getImageLoader();
        gson = new Gson();
        utils = new Utils(getActivity());
        dbDataSource = genieApplication.getDBDataSource();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
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
