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
        // Butter knife injects all the elements in to objects
        ButterKnife.inject(this);
        try {
            // Version number is set here based on app build if it throws exception it will disappear.
            // Better to hide instead of showing some weird text
            version.setText(this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            version.setVisibility(View.GONE);
        }
        // As app requires internet to perform any task. This is a check post to check internet connectivity.
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

        alertDialogBuilder.setMessage(R.string.splashScreenAlertBoxMessage)
                .setCancelable(false)
                .setPositiveButton(R.string.openSettings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // opens the settings page
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                }).setNegativeButton(R.string.exitapp, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // kills the page and exists the app
                finish();
            }
        });
        alert = alertDialogBuilder.create();
        alert.show();
    }

    void runToNextPage() {
        new Thread(new Runnable() {
            public void run() {
                // wait for x secs specified in datafields class
                SystemClock.sleep(DataFields.SplashScreenGeneralTimeOut);
                // check point to check if the token is available and exists.
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }).start();
    }
}