package com.getgenieapp.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.getgenieapp.android.Extras.FontChangeCrawler;

/**
 * Created by Raviteja on 6/6/2015.
 */
public class GenieActivity extends Activity {

    public GenieApplication genieApplication;
    public FontChangeCrawler fontChangeCrawler;

    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        fontChangeCrawler = genieApplication.fontChanger;


    }
}
