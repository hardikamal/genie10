package com.getgenieapp.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.Logging;
import com.getgenieapp.android.GenieApplication;

public class GenieFragment extends Fragment {
    public GenieApplication genieApplication;
    public FontChangeCrawler fontChangeCrawler;
    public Logging logging;

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        fontChangeCrawler = genieApplication.fontChanger;
        logging = GenieApplication.getInstance().loggingBuilder.setUp();
    }
}
