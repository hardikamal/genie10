package com.getgenieapp.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.getgenieapp.android.Extras.FontChangeCrawler;

public class GenieBaseActivity extends AppCompatActivity {
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
