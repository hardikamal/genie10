package com.getgenieapp.android.Fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.getgenieapp.android.Activities.RegisterActivity;
import com.getgenieapp.android.CustomViews.Button.ButtonRectangle;
import com.getgenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.UIHelpers;
import com.getgenieapp.android.GCMHelpers.QuickstartPreferences;
import com.getgenieapp.android.GCMHelpers.RegistrationIntentService;
import com.getgenieapp.android.GenieFragment;
import com.getgenieapp.android.Objects.Register;
import com.getgenieapp.android.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class RegisterFragment extends GenieFragment {

    UIHelpers uiHelpers;
    @InjectView(R.id.topText)
    TextView topText;
    @InjectView(R.id.subText)
    TextView subText;
    @InjectView(R.id.name)
    MaterialEditText name;
    @InjectView(R.id.number)
    MaterialEditText number;
    @InjectView(R.id.parentLoadingView)
    LoadingView parentLoadingView;
    @InjectView(R.id.getStarted)
    ButtonRectangle getStarted;
    View rootView;
    ViewGroup viewGroup;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

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

        uiHelpers = new UIHelpers();

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
                                    ((RegisterActivity) getActivity()).onSuccess(gson.fromJson(gson.toJson(response), Register.class));
                                } else {
                                    ((RegisterActivity) getActivity()).onError(new Register());
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

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.getStarted)
    public void onGetStartedButtonClick() {
        if (name.getText().toString().trim().length() == 0) {
            Crouton.makeText(getActivity(), getString(R.string.pleaseentername), Style.ALERT, viewGroup).show();
        } else if (number.getText().toString().trim().length() < 10) {
            Crouton.makeText(getActivity(), getString(R.string.pleaseentervalidnumber), Style.ALERT, viewGroup).show();
        } else {
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
}
