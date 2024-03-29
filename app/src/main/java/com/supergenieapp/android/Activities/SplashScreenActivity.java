package com.supergenieapp.android.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.localytics.android.Localytics;
import com.supergenieapp.android.Database.DBHandler;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.GCMHelpers.QuickstartPreferences;
//import com.supergenieapp.android.GCMHelpers.UpdateIntentService;
import com.supergenieapp.android.GenieActivity;
import com.supergenieapp.android.R;
import com.supergenieapp.android.Slides.WalkThroughActivity;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SplashScreenActivity extends GenieActivity {

    long start = 0;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    HashMap<String, String> dataAdd = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences.edit().putBoolean("syncwithserver", true).apply();

        // Butter knife injects all the elements in to objects
        ButterKnife.inject(this);

        start = System.currentTimeMillis();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                logging.LogD("GCM BroadCast", "Received");
                logging.LogD("Read Preference", "Yes");
                dataAdd.put("GCM BroadCast", "Received");
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

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Splash Screen");
        Localytics.upload();
        // As app requires internet to perform any task. This is a check post to check internet connectivity.
        if (utils.isOnline() && utils.isNetworkAvailable()) {
            logging.LogD("Internet", "Available");
            dataAdd.put("Internet", "Available");
            logging.LogD("Play Services", "Up to date");
            dataAdd.put("Play Services", "Up to date");
            // Start IntentService to register this application with GCM.
            logging.LogD("Register", "GCM Start");
            if (sharedPreferences.getString(DataFields.TOKEN, null) != null) {
                Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                intent.putExtra("page", "Register");
                startService(intent);
            } else {
                logging.LogV("Register", "Token Not found");
                dataAdd.put("Register", "Token Not found");
                runToRegisterPage(true);
            }
        } else {
            localyticsBuild("Internet not found");
            logging.LogD("Internet", "Show Alert");
            dataAdd.put("Internet not found", "Show Alert");
            showAlertToUser();
        }
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
        localyticsBuildHashMap("Splash Screen Activity", dataAdd);
        super.onDestroy();
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
                        dataAdd.put("Settings", "Clicked");
                        // opens the settings page
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                }).setNegativeButton(R.string.exitapp, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dataAdd.put("Alert", "Exit App");
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
                    dataAdd.put("Activity", "Walkthru");
                    localyticsBuild("Walkthru");
                    logging.LogI("Start Walk Thru Activity");
                    Intent intent = new Intent(SplashScreenActivity.this, WalkThroughActivity.class);
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
                dataAdd.put("Activity", "Verify");
                localyticsBuild("Verify Account On Return");
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
            int visitedCount = sharedPreferences.getInt("visitedcount", 0);
            if (visitedCount > DataFields.RATEWHENCOUNT && sharedPreferences.getBoolean("trackvisitcount", true)) {
                showRatingAlert();
            } else {
                if (sharedPreferences.getBoolean("trackvisitcount", true)) {
                    sharedPreferences.edit().putInt("visitedcount", visitedCount + 1).apply();
                }
                getCategories();
            }
        } else {
            goToVerify();
        }
    }

    private void getCategories() {
        dataAdd.put("Server Call", "Categories");
        JsonArrayRequest req = new JsonArrayRequest(DataFields.getServerUrl() + DataFields.CATEGORIES,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        dataAdd.put("Server Call", "Categories Success");
                        if (response.length() > 0) {
                            new Thread(new Runnable() {
                                public void run() {
                                    ArrayList<String> categoriesList = new ArrayList<>();
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            categoriesList.add(response.getJSONObject(i).toString());
                                            JSONObject jsonObject = new JSONObject(response.getJSONObject(i).toString());
                                            if (jsonObject.getLong("notification_count") != 0)
                                                dbDataSource.UpdateMessages(jsonObject.getInt("id"), jsonObject.getLong("hide_chats_time"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    logging.LogI("Start Main Activity");
                                    try {
                                        logging.LogD("Time Left to Run splash", String.valueOf(DataFields.SplashScreenGeneralTimeOut - (System.currentTimeMillis() - start)));
                                        Thread.sleep(Math.max(DataFields.SplashScreenGeneralTimeOut - (System.currentTimeMillis() - start), 0));
                                    } catch (Exception err) {
                                        err.printStackTrace();
                                    }
                                    dataAdd.put("Activity", "MainActivity");

                                    Intent intent = new Intent(SplashScreenActivity.this, BaseActivity.class);
                                    intent.putExtra("page", "categories");
                                    intent.putStringArrayListExtra("category", categoriesList);
                                    startActivity(intent);
                                    finish();
                                }
                            }).start();
                        } else {
                            dataAdd.put("Server Call", "Categories Error");
                            localyticsBuild(DataFields.getServerUrl() + DataFields.CATEGORIES + " Error");
                            Crouton.makeText(SplashScreenActivity.this, getString(R.string.errortryagain), Style.INFO).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dataAdd.put("Server Call", "Categories Server 500 Error");
                localyticsBuild(DataFields.getServerUrl() + DataFields.CATEGORIES + " 500 Error");
                Crouton.makeText(SplashScreenActivity.this, getString(R.string.errortryagain), Style.INFO).show();
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

    private void showRatingAlert() {
        localyticsBuild("Showed Rating alert");
        AlertDialog.Builder alertDialogBuilder;
        AlertDialog alert;
        alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("If you enjoy using " + getString(R.string.app_name) + ", please take a moment to rate it. Thanks for your support!")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.rateit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        localyticsBuild("App is Rated");
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.supergenieapp.android")));
                        sharedPreferences.edit().putBoolean("trackvisitcount", false).apply();
                        sharedPreferences.edit().putInt("visitedcount", 0).apply();
                        dialog.dismiss();
                    }
                }).setNegativeButton(getString(R.string.remindmelater), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                localyticsBuild("App rating (Remind me later)");
                dialog.dismiss();
                sharedPreferences.edit().putInt("visitedcount", DataFields.RATEREMINDMERESET).apply();
                getCategories();
            }
        }).setNeutralButton(getString(R.string.dontaskagain), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                localyticsBuild("App rating (don't Ask)");
                sharedPreferences.edit().putBoolean("trackvisitcount", false).apply();
                sharedPreferences.edit().putInt("visitedcount", 0).apply();
                dialog.dismiss();
                getCategories();
            }
        });
        alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        this.setIntent(newIntent);

        if (newIntent.getExtras() != null) {
            localyticsBuild("User Clicked on Notification");
        }
    }
}