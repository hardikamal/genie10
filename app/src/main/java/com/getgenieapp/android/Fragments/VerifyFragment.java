package com.getgenieapp.android.Fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.getgenieapp.android.Activities.RegisterActivity;
import com.getgenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.GenieFragment;
import com.getgenieapp.android.Objects.Register;
import com.getgenieapp.android.Objects.Verify;
import com.getgenieapp.android.R;
import com.github.mrengineer13.snackbar.SnackBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.halfbit.tinybus.Subscribe;

public class VerifyFragment extends GenieFragment {
    @InjectView(R.id.subText)
    TextView subText;
    @InjectView(R.id.char1)
    EditText char1;
    @InjectView(R.id.char2)
    EditText char2;
    @InjectView(R.id.char3)
    EditText char3;
    @InjectView(R.id.char4)
    EditText char4;
    @InjectView(R.id.parentLoadingView)
    LoadingView parentLoadingView;
    @InjectView(R.id.tapToResend)
    Button tapToResend;
    static int time = 60;
    final int entryLength = 1;
    Timer timer = new Timer();
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        rootView = inflater.inflate(R.layout.fragment_verify, container, false);
        ButterKnife.inject(this, rootView);

        time = 60;
        moveToNextMech();

        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.getBoolean("runtimer", false)) {
            timer.scheduleAtFixedRate(new TimerTask() {

                public void run() {
                    getActivity().runOnUiThread(new Runnable() {

                        public void run() {
                            if (time != 0) {
                                time -= 1;
                                int seconds = time % 60;
                                int minutes = time / 60;
                                String stringTime = String.format("%02d:%02d", minutes, seconds);
                                tapToResend.setText(stringTime);
                            } else {
                                tapToResend.setText(getResources().getString(R.string.taptoresend));
                            }
                        }
                    });
                }

            }, 0, 1000);
        }
        fontChangeCrawlerRegular.replaceFonts((ViewGroup) rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        logging.LogV("Showed", "on Start");
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        timer.cancel();
        logging.LogV("Showed", "on Stop");
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        tapToResend.setText(getResources().getString(R.string.taptoresend));
    }

    @Subscribe
    public void onSMSReceived(final String code) {
        logging.LogV("SMS Received Code", code);
        if (char1 != null && char2 != null && char3 != null && char4 != null
                && char1.getText().toString().trim().length() == 0 && char2.getText().toString().trim().length() == 0
                && char3.getText().toString().trim().length() == 0 && char4.getText().toString().trim().length() == 0) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(DataFields.smallTimeOut);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            char1.setText(code.substring(0, 1));
                        }
                    });
                    try {
                        Thread.sleep(DataFields.smallTimeOut * 2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            char2.setText(code.substring(1, 2));
                        }
                    });
                    try {
                        Thread.sleep(DataFields.smallTimeOut * 3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            char3.setText(code.substring(2, 3));
                        }
                    });
                    try {
                        Thread.sleep(DataFields.smallTimeOut * 4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            char4.setText(code.substring(3, 4));
                        }
                    });
                }
            }).start();
        }
    }

    private void moveToNextMech() {
        char1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                logging.LogV("Char 1", "on Text Changed");
                if (char1.getText().toString().trim().length() == entryLength)     //size as per your requirement
                {
                    logging.LogV("Char 1", "Text length 1");
                    char2.requestFocus();
                }
                checkFields();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        char2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                logging.LogV("Char 2", "on Text Changed");
                if (char2.getText().toString().trim().length() == entryLength)     //size as per your requirement
                {
                    logging.LogV("Char 2", "Text length 1");
                    char3.requestFocus();
                }
                checkFields();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        char3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                logging.LogV("Char 3", "on Text Changed");
                if (char3.getText().toString().trim().length() == entryLength)     //size as per your requirement
                {
                    logging.LogV("Char 3", "Text length 1");
                    char4.requestFocus();
                }
                checkFields();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        char4.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                logging.LogV("Char 4", "on Text Changed");
                if (char4.getText().toString().trim().length() == entryLength)     //size as per your requirement
                {
                    logging.LogV("Char 1", "Text length 1");
                    char1.requestFocus();
                }
                checkFields();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });
    }

    private void goNext(String code) {
        JSONObject json = new JSONObject();
        try {
            json.put("verification_code", Integer.parseInt(code));
            JsonObjectRequest req = new JsonObjectRequest(DataFields.getServerUrl() + DataFields.VERIFYURL, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response.toString());
                            if (response != null) {
                                try {
                                    if (response.has("verified") && response.getBoolean("verified")) {
                                        ((RegisterActivity) getActivity()).onSuccess(new Verify());
                                    } else {
                                        ((RegisterActivity) getActivity()).onError(new Verify());
                                        parentLoadingView.setLoading(false);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                ((RegisterActivity) getActivity()).onError(new Verify());
                                parentLoadingView.setLoading(false);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ((RegisterActivity) getActivity()).onError(new Register());
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.redoRegistration)
    public void RedoRegistration() {
        sharedPreferences.edit().clear().apply();
        ((RegisterActivity) getActivity()).onRedo(new Verify());
    }

    @OnClick(R.id.tapToResend)
    public void topToResend() {
        JsonObjectRequest req = new JsonObjectRequest(DataFields.getServerUrl() + DataFields.RESENDURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        ((RegisterActivity) getActivity()).showToast(genieApplication.getString(R.string.smsrequestresend), SnackBar.MED_SNACK, SnackBar.Style.CONFIRM);
                        if (response.has("token")) {
                            try {
                                sharedPreferences.edit().putString(DataFields.TOKEN, response.getString("token")).apply();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((RegisterActivity) getActivity()).onError(new Register());
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

    public interface onVerify {
        public void onSuccess(Verify verify);

        public void onRedo(Verify verify);

        public void onError(Verify verify);
    }

    public void checkFields() {
        if (char4.getText().toString().trim().length() == entryLength
                && char1.getText().toString().trim().length() == entryLength
                && char2.getText().toString().trim().length() == entryLength
                && char3.getText().toString().trim().length() == entryLength)     //size as per your requirement
        {
            logging.LogV("Char", "Texts length 1");
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(char4.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            parentLoadingView.setText("Verifying...");
            final String code = char1.getText().toString().trim() +
                    char2.getText().toString().trim() +
                    char3.getText().toString().trim() +
                    char4.getText().toString().trim();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(DataFields.VerifyTimeOut);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            parentLoadingView.setLoading(true);
                        }
                    });
                    goNext(code);
                }
            }).start();
        }
    }
}
