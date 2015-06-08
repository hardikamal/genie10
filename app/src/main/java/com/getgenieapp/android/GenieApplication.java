package com.getgenieapp.android;

import android.app.Application;
import android.content.SharedPreferences;

import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.LoggingBuilder;
import com.securepreferences.SecurePreferences;

public class GenieApplication extends Application {
    public LoggingBuilder loggingBuilder;
    private static GenieApplication genieApplication;
    public FontChangeCrawler fontChanger;
    private SharedPreferences mSecurePrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        genieApplication = this;
        fontChanger = new FontChangeCrawler(getAssets(), "custom_regular.otf");
        loggingBuilder = new LoggingBuilder(getApplicationContext())
                .setCanDisplayOnLogCat(true)
                .setWriteToLog(false)
                .setFile(DataFields.logFile.getAbsolutePath())
                .setClassName("Genie Application");
        mSecurePrefs = new SecurePreferences(this);
    }

    public SharedPreferences getSecurePrefs()
    {
        return mSecurePrefs;
    }

    public static synchronized GenieApplication getInstance() {
        return genieApplication;
    }
}
