package com.getgenieapp.android.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.telephony.SmsMessage;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Fragments.RegisterFragment;
import com.getgenieapp.android.Fragments.VerifyFragment;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Register;
import com.getgenieapp.android.Objects.Verify;
import com.getgenieapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

// Register activity will navigate the registration process to user.
// This accepts the extra to check the user needs to register or verify
// Display specific fragment based on option
// This has the broadcastreceiver setted up to autofill the Verification code in verify fragment.
// broadcast will start on on resume and stops when on pause.
// Butterknife is used to inject view elements
// GetCategories server api call implemented here
// Callback listeners are implemented for both register and verify fragment
// tinybus is used here to pass the data from activity to fragment.
// Fragments Register fragment and verify fragment.

public class RegisterActivity extends GenieBaseActivity implements RegisterFragment.onRegister, VerifyFragment.onVerify {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    static int time = 61;
    HashMap<String, Object> mixpanelDataAdd = new HashMap<>();

    private BroadcastReceiver myBroadcastReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (!intent.getAction().equals(SMS_RECEIVED)) return;
                    final Bundle bundle = intent.getExtras();
                    try {
                        if (bundle != null) {
                            final Object[] pdusObj = (Object[]) bundle.get("pdus");
                            for (int i = 0; i < pdusObj.length; i++) {
                                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                                String message = currentMessage.getDisplayMessageBody();
                                String phrase = "code : ";
                                if (message.contains(phrase)) {
                                    int index = message.indexOf(phrase);
                                    String code = message.substring(index + phrase.length());
                                    code = code.trim();
                                    if (code.length() == 4) {
                                        mBus.post(code);
                                        mixpanelDataAdd.put("Verification Code", code);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            };

    @Override
    protected void onStart() {
        super.onStart();
        mixPanelTimerStart(RegisterActivity.class.getName());
        logging.LogI("On Start");
    }

    @Override
    protected void onDestroy() {
        logging.LogI("On Destroy");
        mixPanelTimerStop(RegisterActivity.class.getName());
        mixPanelBuildHashMap("General Run " + SplashScreenActivity.class.getName(), mixpanelDataAdd);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (new File(DataFields.profilePicturePath).exists())
            new File(DataFields.profilePicturePath).delete();
        setContentView(R.layout.activity_register);

        // clear all previous users data
        dbDataSource.cleanAll();

        getWindow().setBackgroundDrawableResource(R.drawable.pattern_signup);
        // 60 seconds timer reset
        time = 61;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.genie_logo);
            actionBar.setTitle("");
        }

        if (getIntent().getStringExtra("page").equals("Register")) {
            startFragment(R.id.body, new RegisterFragment());
            mixpanelDataAdd.put("Fragment", "Register Fragment");
        } else {
            startFragment(R.id.body, new VerifyFragment());
            mixpanelDataAdd.put("Fragment", "Verify Fragment");
        }

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(myBroadcastReceiver, filter);
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public void onSuccess(Register register) {
        sharedPreferences.edit().putString(DataFields.TOKEN, register.getToken());
        VerifyFragment verifyFragment = new VerifyFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("runtimer", true);
        verifyFragment.setArguments(bundle);
        startFragment(R.id.body, verifyFragment);
    }

    @Override
    public void onError(Register register) {
        Crouton.makeText(this, getString(R.string.unexpectederror), Style.ALERT).show();
    }

    @Override
    public void onSuccess(Verify verify) {
        mixpanelDataAdd.put("Server Call", "Categories");
        mixPanelTimerStart(DataFields.getServerUrl() + DataFields.CATEGORIES);
        JsonArrayRequest req = new JsonArrayRequest(DataFields.getServerUrl() + DataFields.CATEGORIES,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        mixPanelTimerStop(DataFields.getServerUrl() + DataFields.CATEGORIES);
                        mixpanelDataAdd.put("Server Call", "Categories Success");
                        if (response.length() > 0) {
                            new Thread(new Runnable() {
                                public void run() {
                                    ArrayList<String> categoriesList = new ArrayList<>();
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            categoriesList.add(response.getJSONObject(i).toString());
                                            JSONObject jsonObject = new JSONObject(response.getJSONObject(i).toString());
                                            dbDataSource.UpdateMessages(jsonObject.getInt("id"), jsonObject.getLong("hide_chats_time"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    logging.LogI("Start Main Activity");
                                    mixpanelDataAdd.put("Activity", "MainActivity");
                                    Intent intent = new Intent(RegisterActivity.this, BaseActivity.class);
                                    intent.putExtra("page", "categories");
                                    intent.putStringArrayListExtra("category", categoriesList);
                                    startActivity(intent);
                                    finish();
                                }
                            }).start();
                        } else {
                            mixpanelDataAdd.put("Server Call", "Categories Error");
                            mixPanelBuild(DataFields.getServerUrl() + DataFields.CATEGORIES + " Error");
                            Crouton.makeText(RegisterActivity.this, getString(R.string.errortryagain), Style.INFO).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mixPanelTimerStop(DataFields.getServerUrl() + DataFields.CATEGORIES);
                mixpanelDataAdd.put("Server Call", "Categories Server 500 Error");
                mixPanelBuild(DataFields.getServerUrl() + DataFields.CATEGORIES + " 500 Error");
                Crouton.makeText(RegisterActivity.this, getString(R.string.errortryagain), Style.INFO).show();
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
    public void onRedo(Verify verify) {
        mixPanelBuild("User redoing registration");
        startFragmentFromLeft(R.id.body, new RegisterFragment());
    }

    @Override
    public void onError(Verify verify) {
        Crouton.makeText(this, getString(R.string.servererrortryagain), Style.ALERT).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }
}
