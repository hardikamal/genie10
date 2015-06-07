package com.getgenieapp.android.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.Utils;
import com.getgenieapp.android.GCMHelpers.QuickstartPreferences;
import com.getgenieapp.android.GCMHelpers.RegistrationIntentService;
import com.getgenieapp.android.GenieActivity;
import com.getgenieapp.android.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SplashScreenActivity extends GenieActivity {

    @InjectView(R.id.version)
    TextView version;

    Utils utils;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public SplashScreenActivity() {
        utils = new Utils(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // Butter knife injects all the elements in to objects
        ButterKnife.inject(this);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
            }
        };

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
        fontChangeCrawler.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
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
                if (checkPlayServices()) {
                    // Start IntentService to register this application with GCM.
                    Intent intent = new Intent(SplashScreenActivity.this, RegistrationIntentService.class);
                    startService(intent);
                }

                // wait for x secs specified in datafields class
                SystemClock.sleep(DataFields.SplashScreenGeneralTimeOut);
                // check point to check if the token is available and exists.

                startActivity(new Intent(SplashScreenActivity.this, RegisterActivity.class));
                finish();
            }
        }).start();
    }

    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}