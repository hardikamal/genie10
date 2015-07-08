package com.getgenieapp.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.Logging;
import com.getgenieapp.android.Extras.Utils;
import com.getgenieapp.android.SecurePreferences.SecurePreferences;
import com.google.gson.Gson;

import de.halfbit.tinybus.TinyBus;

public class GenieFragment extends Fragment {
    public Gson gson;
    public GenieApplication genieApplication;
    public FontChangeCrawler fontChangeCrawlerRegular;
    public FontChangeCrawler fontChangeCrawlerMedium;
    public FontChangeCrawler fontChangeCrawlerLight;
    public SecurePreferences sharedPreferences;
    public Logging logging;
    public TinyBus mBus;
    public Utils utils;

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        sharedPreferences = genieApplication.getSecurePrefs();
        fontChangeCrawlerRegular = genieApplication.getFontChangeCrawlerRegular();
        fontChangeCrawlerMedium = genieApplication.getFontChangeCrawlerMedium();
        fontChangeCrawlerLight = genieApplication.getFontChangeCrawlerLight();
        logging = genieApplication.getLoggingBuilder().setUp();
        mBus = genieApplication.getBus();
        gson = new Gson();
        utils = new Utils(getActivity());
    }
}
