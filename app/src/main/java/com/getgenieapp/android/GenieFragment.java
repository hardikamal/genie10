package com.getgenieapp.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.Logging;
import com.getgenieapp.android.GenieApplication;
import com.google.gson.Gson;

public class GenieFragment extends Fragment {
    public Gson gson;
    public GenieApplication genieApplication;
    public FontChangeCrawler fontChangeCrawler;
    public Logging logging;

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        fontChangeCrawler = genieApplication.fontChanger;
        logging = genieApplication.loggingBuilder.setUp();
        gson = new Gson();
    }
}
