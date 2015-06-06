package com.getgenieapp.android;

import android.app.Application;

import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.LoggingBuilder;

public class GenieApplication extends Application {
    public LoggingBuilder loggingBuilder;
    private static GenieApplication genieApplication;
    public FontChangeCrawler fontChanger;

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
    }

    public static synchronized GenieApplication getInstance() {
        return genieApplication;
    }
}
