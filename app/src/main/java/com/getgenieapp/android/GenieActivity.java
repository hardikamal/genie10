package com.getgenieapp.android;

import android.app.Activity;
import android.os.Bundle;

import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.Logging;
import com.getgenieapp.android.SecurePreferences.SecurePreferences;
import com.google.gson.Gson;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by Raviteja on 6/6/2015.
 */
public class GenieActivity extends Activity {
    public Gson gson;
    public SecurePreferences sharedPreferences;
    public GenieApplication genieApplication;
    public FontChangeCrawler fontChangeCrawlerRegular;
    public FontChangeCrawler fontChangeCrawlerMedium;
    public FontChangeCrawler fontChangeCrawlerLight;
    public Logging logging;
    public TinyBus mBus;

    /**
     * @param savedInstance
     */
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        fontChangeCrawlerRegular = genieApplication.getFontChangeCrawlerRegular();
        fontChangeCrawlerMedium = genieApplication.getFontChangeCrawlerMedium();
        fontChangeCrawlerLight = genieApplication.getFontChangeCrawlerLight();
        sharedPreferences = genieApplication.getSecurePrefs();
        logging = genieApplication.getLoggingBuilder().setUp();
        mBus = genieApplication.getBus();
        gson = new Gson();
    }
}
