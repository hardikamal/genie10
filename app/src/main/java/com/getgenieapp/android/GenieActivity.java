package com.getgenieapp.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;

import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.Logging;
import com.google.gson.Gson;

/**
 * Created by Raviteja on 6/6/2015.
 */
public class GenieActivity extends Activity {
    public Gson gson;
    public SharedPreferences sharedPreferences;
    public GenieApplication genieApplication;
    public FontChangeCrawler fontChangeCrawler;
    public Logging logging;

    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        fontChangeCrawler = genieApplication.fontChanger;
        sharedPreferences = genieApplication.getSecurePrefs();
        logging = genieApplication.loggingBuilder.setUp();
        gson = new Gson();
    }
}
