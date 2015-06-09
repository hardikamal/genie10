package com.getgenieapp.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.getgenieapp.android.Extras.FontChangeCrawler;
import com.getgenieapp.android.Extras.Logging;
import com.google.gson.Gson;

public class GenieBaseActivity extends AppCompatActivity {
    public Gson gson;
    public GenieApplication genieApplication;
    public SharedPreferences sharedPreferences;
    public FontChangeCrawler fontChangeCrawler;
    public Logging logging;

    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        genieApplication = GenieApplication.getInstance();
        fontChangeCrawler = genieApplication.fontChanger;
        logging = genieApplication.loggingBuilder.setUp();
        sharedPreferences = genieApplication.getSecurePrefs();
        gson = new Gson();
    }

    public void startFragment(int container, Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commit();
    }
}
