package com.supergenieapp.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.supergenieapp.android.Database.DBDataSource;
import com.supergenieapp.android.Extras.FontChangeCrawler;
import com.supergenieapp.android.Extras.Logging;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.SecurePreferences.SecurePreferences;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

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
    public MixpanelAPI mixpanel;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mixpanel = MixpanelAPI.getInstance(getActivity(), getString(R.string.mixpanel));
        genieApplication = GenieApplication.getInstance();
        sharedPreferences = genieApplication.getSecurePrefs();
        fontChangeCrawlerRegular = genieApplication.getFontChangeCrawlerRegular();
        logging = genieApplication.getLoggingBuilder().setUp();
        mBus = genieApplication.getBus();
        gson = new Gson();
        utils = new Utils(getActivity());
        dbDataSource = new DBDataSource(getActivity());
        mixpanel.identify(utils.getDeviceSerialNumber());
        mixpanel.getPeople().identify(utils.getDeviceSerialNumber());
    }

    @Override
    public void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public void mixPanelBuildHashMap(String eventName, HashMap<String, Object> myValues) {
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
}