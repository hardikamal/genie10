package com.getgenieapp.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.Logging;

public class GenieBaseActivity extends AppCompatActivity {
    public GenieApplication genieApplication;
    public FontChangeCrawler fontChangeCrawler;
    public Logging logging;

    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        fontChangeCrawler = genieApplication.fontChanger;
        logging = GenieApplication.getInstance().loggingBuilder.setUp();

    }
}
