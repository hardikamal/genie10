package com.getgenieapp.android.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.telephony.SmsMessage;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.getgenieapp.android.CustomViews.Misc.SnackBar;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Fragments.RegisterFragment;
import com.getgenieapp.android.Fragments.VerifyFragment;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Register;
import com.getgenieapp.android.Objects.Verify;
import com.getgenieapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
                                String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                                String senderNum = phoneNumber;
                                String message = currentMessage.getDisplayMessageBody();
                                String phrase = "code : ";
                                if (message.contains(phrase)) {
                                    int index = message.indexOf(phrase);
                                    String code = message.substring(index + phrase.length());
                                    System.out.println(code);
                                    code = code.trim();
                                    if (code.length() == 4)
                                        mBus.post(code);
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (new File(DataFields.profilePicturePath).exists())
            new File(DataFields.profilePicturePath).delete();
        setContentView(R.layout.activity_register);
        getWindow().setBackgroundDrawableResource(R.drawable.pattern_signup);
        time = 61;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.genie_logo);
        actionBar.setTitle("");

        if (getIntent().getStringExtra("page").equals("Register"))
            startFragment(R.id.body, new RegisterFragment());
        else
            startFragment(R.id.body, new VerifyFragment());

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @Override
    public void onSuccess(Register register) {
        sharedPreferences.edit().putString("token", register.getToken());
        VerifyFragment verifyFragment = new VerifyFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("runtimer", true);
        verifyFragment.setArguments(bundle);
        startFragment(R.id.body, verifyFragment);
    }

    @Override
    public void onError(Register register) {
        SnackBar snackBar = new SnackBar(this, getString(R.string.unexpectederror));
        snackBar.show();
    }

    @Override
    public void onSuccess(Verify verify) {
        JsonArrayRequest req = new JsonArrayRequest(DataFields.getServerUrl() + DataFields.CATEGORIES,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        System.out.println(response.toString());
                        if (response.length() > 0) {
                            ArrayList<String> categoriesList = new ArrayList<String>();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    categoriesList.add(response.getJSONObject(i).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            logging.LogI("Start Main Activity");
                            Intent intent = new Intent(RegisterActivity.this, BaseActivity.class);
                            intent.putStringArrayListExtra("category", categoriesList);
                            startActivity(intent);
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent intent = new Intent(RegisterActivity.this, SplashScreenActivity.class);
                startActivity(intent);
                finish();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-access-token", sharedPreferences.getString(DataFields.TOKEN, ""));
                return params;
            }
        };

        genieApplication.addToRequestQueue(req);
    }

    @Override
    public void onRedo(Verify verify) {
        startFragmentFromLeft(R.id.body, new RegisterFragment());
    }

    @Override
    public void onError(Verify verify) {
        SnackBar snackBar = new SnackBar(this, getString(R.string.servererrortryagain));
        snackBar.show();
    }
}
