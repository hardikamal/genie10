package com.getgenieapp.android;

import android.app.Application;
import android.content.SharedPreferences;

import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.LoggingBuilder;
import com.securepreferences.SecurePreferences;

import de.halfbit.tinybus.TinyBus;

public class GenieApplication extends Application {
    private LoggingBuilder loggingBuilder;
    private static GenieApplication genieApplication;
    private FontChangeCrawler fontChanger;
    private SharedPreferences mSecurePrefs;
    private TinyBus mBus;

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
        mBus = TinyBus.from(this);
    }

    public SharedPreferences getSecurePrefs() {
        return mSecurePrefs;
    }

    public TinyBus getBus() {
        return mBus;
    }

    public LoggingBuilder getLoggingBuilder() {
        return loggingBuilder;

    }

    public FontChangeCrawler getFontChangeCrawler() {
        return fontChanger;
    }

    public static synchronized GenieApplication getInstance() {
        return genieApplication;
    }
}
