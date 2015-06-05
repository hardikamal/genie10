package com.getgenieapp.android.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.getgenieapp.android.CustomViews.SplashScreenProgressCircle;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.Utils;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SplashScreenActivity extends Activity {

    @InjectView(R.id.version)
    TextView version;
    Utils utils;

    public SplashScreenActivity()
    {
        utils = new Utils(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.inject(this);
        try {
            version.setText(this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            version.setVisibility(View.GONE);
        }
        if (utils.isConnectedMobile() || utils.isConnectedWifi()) {
            runToNextPage();
        } else {
            showAlertToUser();
        }
    }

    private void showAlertToUser() {
        AlertDialog.Builder alertDialogBuilder;
        AlertDialog alert;
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Attention!\n\nPlease enable internet to shop.\nWould you like to enable them now?")
                .setCancelable(false)
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                }).setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        alert = alertDialogBuilder.create();
        alert.show();
    }

    void runToNextPage() {
        new Thread(new Runnable() {
            public void run() {
                SystemClock.sleep(DataFields.SplashScreenGeneralTimeOut);
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }).start();
    }
}