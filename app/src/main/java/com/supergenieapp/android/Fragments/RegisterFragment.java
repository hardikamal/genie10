package com.supergenieapp.android.Fragments;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.localytics.android.Localytics;
import com.supergenieapp.android.Activities.EULAActivity;
import com.supergenieapp.android.Activities.RegisterActivity;
import com.supergenieapp.android.CustomViews.Button.ButtonRectangle;
import com.supergenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.GCMHelpers.QuickstartPreferences;
import com.supergenieapp.android.GCMHelpers.RegistrationIntentService;
import com.supergenieapp.android.GenieFragment;
import com.supergenieapp.android.Objects.Register;
import com.supergenieapp.android.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class RegisterFragment extends GenieFragment {

    @InjectView(R.id.name)
    MaterialEditText name;
    @InjectView(R.id.number)
    MaterialEditText number;
    @InjectView(R.id.parentLoadingView)
    LoadingView parentLoadingView;
    @InjectView(R.id.getStarted)
    ButtonRectangle getStarted;
    @InjectView(R.id.areacode)
    EditText areaCode;
    View rootView;
    ViewGroup viewGroup;
    HashMap<String, String> dataAdd = new HashMap<>();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    public void onStart() {
        super.onStart();
        Localytics.openSession();
        Localytics.tagScreen("Registration Fragment");
        hideKeyboard(getActivity());
        logging.LogV("Showed", "on Start");
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        localyticsBuildHashMap("General Run RegisterFragment", dataAdd);
        logging.LogV("Showed", "on Stop");
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        logging.LogI("On Resume");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.SENT_TOKEN_TO_SERVER));
    }

    @Override
    public void onPause() {
        logging.LogI("On Pause");
        parentLoadingView.setLoading(false);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        this.viewGroup = container;
        rootView = inflater.inflate(R.layout.fragment_register_new, container, false);
        ButterKnife.inject(this, rootView);

        getStarted.setEnabled(false);

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
                    logging.LogV("Register User");
                    RegisterUser();
                } else {
                    logging.LogE("Error in getting GCM token");
                    ((RegisterActivity) getActivity()).onError(new Register());
                }
            }
        };

        number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    logging.LogV("Presed Done on Keyboard");
                    onGetStartedButtonClick();
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(number.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        areaCode.setText("+" + utils.GetCountryZipCode());

        name.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (name.getText().toString().trim().length() > 0 && number.getText().toString().trim().length() == 10) {
                    getStarted.setEnabled(true);
                    getStarted.setTextColor(getActivity().getResources().getColor(R.color.white));
                } else {
                    getStarted.setEnabled(false);
                    getStarted.setTextColor(getActivity().getResources().getColor(R.color.color999));
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        number.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (name.getText().toString().trim().length() > 0 && number.getText().toString().trim().length() == 10) {
                    getStarted.setEnabled(true);
                    getStarted.setTextColor(getActivity().getResources().getColor(R.color.white));
                } else {
                    getStarted.setEnabled(false);
                    getStarted.setTextColor(getActivity().getResources().getColor(R.color.color999));
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) rootView);
        return rootView;
    }

    private void RegisterUser() {
        localyticsBuild("User Registered");
        JSONObject json = new JSONObject();
        try {
            json.put("name", name.getText().toString());
            json.put("phone", "+" + utils.GetCountryZipCode() + number.getText().toString());
            json.put("device_serial_number", utils.getDeviceSerialNumber());
            json.put("mac_id", utils.getMacId());
            json.put("gcm_token", sharedPreferences.getString(DataFields.GCM_TOKEN, null));
            if (json.has(DataFields.GCM_TOKEN) && json.getString(DataFields.GCM_TOKEN) != null) {

                logging.LogV("GCM Token", json.getString(DataFields.GCM_TOKEN));
                JsonObjectRequest req = new JsonObjectRequest(DataFields.getServerUrl() + DataFields.REGISTERURL, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                parentLoadingView.setLoading(false);
                                if (response != null) {
                                    logging.LogV("GCM Token", response.toString());
                                    if (response.has("token")) {
                                        try {
                                            sharedPreferences.edit().putString(DataFields.TOKEN, response.getString("token")).apply();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (getActivity() != null) {
                                        ((RegisterActivity) getActivity()).onSuccess(gson.fromJson(gson.toJson(response), Register.class));
                                    }
                                } else {
                                    if (getActivity() != null) {
                                        ((RegisterActivity) getActivity()).onError(new Register());
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        parentLoadingView.setLoading(false);
                        System.out.println("Error " + error.toString());
                        ((RegisterActivity) getActivity()).onError(new Register());
                    }
                });

                genieApplication.addToRequestQueue(req);
            } else {
                logging.LogE("GCM Token not found");
                parentLoadingView.setLoading(false);
                Crouton.makeText(getActivity(), genieApplication.getString(R.string.notabletoregisteruser), Style.ALERT, viewGroup).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.getStarted)
    public void onGetStartedButtonClick() {
        if (name.getText().toString().trim().length() == 0) {
            dataAdd.put("Didnt Entered", "Name");
            Crouton.makeText(getActivity(), getString(R.string.pleaseentername), Style.ALERT, viewGroup).show();
        } else if (number.getText().toString().trim().length() < 10) {
            dataAdd.put("Didnt Entered", "Number");
            Crouton.makeText(getActivity(), getString(R.string.pleaseentervalidnumber), Style.ALERT, viewGroup).show();
        } else {
            dataAdd.put("Clicked", "Get Started");
            parentLoadingView.setText(getResources().getString(R.string.registeringuser));
            parentLoadingView.setLoading(true);
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(getStarted.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            Intent intent = new Intent(getActivity(), RegistrationIntentService.class);
            getActivity().startService(intent);
        }
    }

    public interface onRegister {
        public void onSuccess(Register register);

        public void onError(Register register);
    }

    @OnClick(R.id.terms)
    public void onCLickTerms() {
        localyticsBuild("User Clicked Terms and Conditions");
        getActivity().startActivity(new Intent(getActivity(), EULAActivity.class));
    }
}
