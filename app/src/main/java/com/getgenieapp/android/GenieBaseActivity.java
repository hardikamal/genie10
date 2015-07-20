package com.getgenieapp.android;

import android.app.Activity;
import android.content.Context;
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
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.util.HashMap;

import de.halfbit.tinybus.TinyBus;

public class GenieBaseActivity extends AppCompatActivity {
    public Gson gson;
    public GenieApplication genieApplication;
    public SecurePreferences sharedPreferences;
    public FontChangeCrawler fontChangeCrawlerRegular;
    public Logging logging;
    public TinyBus mBus;
    public Utils utils;
    public DBDataSource dbDataSource;
    public MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mixpanel = MixpanelAPI.getInstance(this, getString(R.string.mixpanel));
        genieApplication = GenieApplication.getInstance();
        fontChangeCrawlerRegular = genieApplication.getFontChangeCrawlerRegular();
        logging = genieApplication.getLoggingBuilder().setUp();
        sharedPreferences = genieApplication.getSecurePrefs();
        mBus = genieApplication.getBus();
        gson = new Gson();
        utils = new Utils(this);
        dbDataSource = new DBDataSource(this);
        mixpanel.identify(utils.getDeviceSerialNumber());
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

    public void startFragmentNoEffect(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commit();
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
}
