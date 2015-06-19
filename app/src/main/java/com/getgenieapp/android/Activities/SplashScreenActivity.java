package com.getgenieapp.android.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getgenieapp.android.CustomViews.TextView.AutoResizeTextView;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.UIHelpers;
import com.getgenieapp.android.Extras.Utils;
import com.getgenieapp.android.GCMHelpers.QuickstartPreferences;
import com.getgenieapp.android.GCMHelpers.UpdateIntentService;
import com.getgenieapp.android.GenieActivity;
import com.getgenieapp.android.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SplashScreenActivity extends GenieActivity {

    @InjectView(R.id.version)
    TextView version;
    @InjectView(R.id.companyname)
    AutoResizeTextView companyName;
    @InjectView(R.id.copyrights)
    TextView copyrights;
    @InjectView(R.id.welcomemsg)
    TextView welcomemsg;

    Utils utils;
    UIHelpers uiHelpers;
    long start = 0;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public SplashScreenActivity() {
        utils = new Utils(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logging.LogD("Splash Screen", "Entered");
        // Butter knife injects all the elements in to objects
        ButterKnife.inject(this);

        // ToDo Remove this later
        copyrights.setVisibility(View.GONE);

        uiHelpers = new UIHelpers();
        welcomemsg.setTextSize(uiHelpers.determineMaxTextSize(getString(R.string.welcomeMessage), uiHelpers.getXYPixels(this).x / 4));
        companyName.setTextSize(uiHelpers.determineMaxTextSize(getString(R.string.companyName), uiHelpers.getXYPixels(this).x / 4));

        start = System.currentTimeMillis();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                logging.LogD("GCM BroadCast", "Received");
                logging.LogD("Read Preference", "Yes");
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                logging.LogV("Sent To Server", String.valueOf(sentToken));
                if (sentToken) {
                    logging.LogV("Go to Main Page");
                    runToMainPage();
                } else {
                    logging.LogV("Got to Register Page");
                    runToRegisterPage();
                }
            }
        };

        try {
            logging.LogD("Set Version Before", version.getText().toString());
            // Version number is set here based on app build if it throws exception it will disappear.
            // Better to hide instead of showing some weird text
            version.setText(this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0).versionName);
            logging.LogD("Set Version After", version.getText().toString());
        } catch (PackageManager.NameNotFoundException e) {
            version.setVisibility(View.GONE);
        }
        // As app requires internet to perform any task. This is a check post to check internet connectivity.
        //ToDo check later
//        if (utils.isConnectedMobile() || utils.isConnectedWifi()) {
        logging.LogD("Internet", "Available");
        if (checkPlayServices()) {
            logging.LogD("Play Services", "Up to date");
            // Start IntentService to register this application with GCM.
            logging.LogD("Register", "GCM Start");
            if (sharedPreferences.getString(DataFields.TOKEN, null) != null) {
                Intent intent = new Intent(SplashScreenActivity.this, UpdateIntentService.class);
                startService(intent);
            } else {
                logging.LogV("Register", "Token Not found");
                runToRegisterPage();
            }
        }
//        } else {
//            logging.LogD("Internet", "Show Alert");
//            showAlertToUser();
//        }
        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    private void showAlertToUser() {
        AlertDialog.Builder alertDialogBuilder;
        AlertDialog alert;
        alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage(R.string.splashScreenAlertBoxMessage)
                .setCancelable(false)
                .setPositiveButton(R.string.openSettings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logging.LogD("Settings", "Clicked");
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

    void runToRegisterPage() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    logging.LogD("Time Left to Run splash", String.valueOf(DataFields.SplashScreenGeneralTimeOut - (System.currentTimeMillis() - start)));
                    Thread.sleep(Math.max(DataFields.SplashScreenGeneralTimeOut - (System.currentTimeMillis() - start), 0));
                } catch (Exception err) {
                }
                logging.LogI("Start Register Activity");
                startActivity(new Intent(SplashScreenActivity.this, RegisterActivity.class));
//                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }).start();
    }

    void runToMainPage() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    logging.LogD("Time Left to Run splash", String.valueOf(DataFields.SplashScreenGeneralTimeOut - (System.currentTimeMillis() - start)));
                    Thread.sleep(Math.max(DataFields.SplashScreenGeneralTimeOut - (System.currentTimeMillis() - start), 0));
                } catch (Exception err) {
                }
                logging.LogI("Start Main Activity");
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }).start();
    }

    protected void onResume() {
        super.onResume();
        logging.LogI("On Resume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        logging.LogI("On Pause");
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
                logging.LogI("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}