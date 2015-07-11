package com.getgenieapp.android.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.getgenieapp.android.Database.DBHandler;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.Utils;
import com.getgenieapp.android.GCMHelpers.QuickstartPreferences;
import com.getgenieapp.android.GCMHelpers.UpdateIntentService;
import com.getgenieapp.android.GenieActivity;
import com.getgenieapp.android.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;

public class SplashScreenActivity extends GenieActivity {

//    UIHelpers uiHelpers;
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
        logging.LogD("Splash Screen", "Entered ");
        // Butter knife injects all the elements in to objects
        ButterKnife.inject(this);
        // Start Database
        new DBHandler(this);
        start = System.currentTimeMillis();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                logging.LogD("GCM BroadCast", "Received");
                logging.LogD("Read Preference", "Yes");
                boolean sentToken = intent.getBooleanExtra("status", false);
                boolean verifyStatus = intent.getBooleanExtra("verify", false);

                logging.LogV("Sent To Server", String.valueOf(sentToken));
                if (sentToken) {
                    logging.LogV("Go to Main Page");
                    runToMainPage(verifyStatus);
                } else {
                    logging.LogV("Got to Register Page");
                    runToRegisterPage(verifyStatus);
                }
            }
        };

        // As app requires internet to perform any task. This is a check post to check internet connectivity.
        if (utils.isConnectedMobile() || utils.isConnectedWifi()) {
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
                    runToRegisterPage(true);
                }
            }
        } else {
            logging.LogD("Internet", "Show Alert");
            showAlertToUser();
        }
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

    void runToRegisterPage(boolean verified) {
        if (verified) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        logging.LogD("Time Left to Run splash", String.valueOf(DataFields.SplashScreenGeneralTimeOut - (System.currentTimeMillis() - start)));
                        Thread.sleep(Math.max(DataFields.SplashScreenGeneralTimeOut - (System.currentTimeMillis() - start), 0));
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                    logging.LogI("Start Register Activity");
                    Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                    intent.putExtra("page", "Register");
                    startActivity(intent);
                    finish();
                }
            }).start();
        } else {
            goToVerify();
        }
    }

    private void goToVerify() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    logging.LogD("Time Left to Run splash", String.valueOf(DataFields.SplashScreenGeneralTimeOut - (System.currentTimeMillis() - start)));
                    Thread.sleep(Math.max(DataFields.SplashScreenGeneralTimeOut - (System.currentTimeMillis() - start), 0));
                } catch (Exception err) {
                    err.printStackTrace();
                }
                logging.LogI("Start Verify Activity");
                Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                intent.putExtra("page", "Verify");
                startActivity(intent);
                finish();
            }
        }).start();
    }

    void runToMainPage(boolean verified) {
        if (verified) {
            getCategories();
        } else {
            goToVerify();
        }
    }

    private void getCategories() {
        JsonArrayRequest req = new JsonArrayRequest(DataFields.getServerUrl() + DataFields.CATEGORIES,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        System.out.println(response.toString());
                        if (response.length() > 0) {
                            new Thread(new Runnable() {
                                public void run() {
                                    ArrayList<String> categoriesList = new ArrayList<>();
                                    try {
                                        logging.LogD("Time Left to Run splash", String.valueOf(DataFields.SplashScreenGeneralTimeOut - (System.currentTimeMillis() - start)));
                                        Thread.sleep(Math.max(DataFields.SplashScreenGeneralTimeOut - (System.currentTimeMillis() - start), 0));
                                    } catch (Exception err) {
                                        err.printStackTrace();
                                    }
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            categoriesList.add(response.getJSONObject(i).toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    logging.LogI("Start Main Activity");
                                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                    intent.putStringArrayListExtra("category", categoriesList);
                                    startActivity(intent);
                                    finish();
                                }
                            }).start();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", sharedPreferences.getString(DataFields.TOKEN, ""));
                return params;
            }
        };

        genieApplication.addToRequestQueue(req);
    }

    @Override
    protected void onStart() {
        super.onStart();
        logging.LogI("On Start");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        logging.LogI("On Destroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onDestroy();
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