package com.getgenieapp.android.Fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.getgenieapp.android.Activities.RegisterActivity;
import com.getgenieapp.android.Activities.SplashScreenActivity;
import com.getgenieapp.android.CustomViews.Misc.SnackBar;
import com.getgenieapp.android.CustomViews.ProgressBar.LoadingView;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.UIHelpers;
import com.getgenieapp.android.Objects.Register;
import com.getgenieapp.android.Objects.Verify;
import com.getgenieapp.android.R;
import com.getgenieapp.android.GenieFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    private UIHelpers uiHelpers;
    final int entryLength = 1;

    @Override
    public void onStart() {
        super.onStart();
        logging.LogV("Showed", "on Start");
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        logging.LogV("Showed", "on Stop");
        super.onStop();
    }

    @Subscribe
    public void onSMSReceived(final String code) {
        logging.LogV("SMS Received Code", code);
        if (char1 != null && char2 != null && char3 != null && char4 != null) {
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
                        Thread.sleep(DataFields.smallTimeOut);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            char2.setText(code.substring(1, 2));
                        }
                    });
                    try {
                        Thread.sleep(DataFields.smallTimeOut);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            char3.setText(code.substring(2, 3));
                        }
                    });
                    try {
                        Thread.sleep(DataFields.smallTimeOut);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        View rootView = inflater.inflate(R.layout.fragment_verify, container, false);
        ButterKnife.inject(this, rootView);
        moveToNextMech();
        uiHelpers = new UIHelpers();
//        subText.setTextSize(uiHelpers.determineMaxTextSize(getActivity().getString(R.string.verifycodetext), uiHelpers.getXYPixels(getActivity()).x / 4));
        fontChangeCrawlerRegular.replaceFonts((ViewGroup) rootView);
        return rootView;
    }

    private void moveToNextMech() {
        char1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                logging.LogV("Char 1", "on Text Changed");
                if (char1.getText().toString().trim().length() == entryLength)     //size as per your requirement
                {
                    logging.LogV("Char 1", "Text length 1");

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(DataFields.smallTimeOut);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    char2.requestFocus();
                                }
                            });
                        }
                    }).start();
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
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(DataFields.smallTimeOut);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    char3.requestFocus();
                                }
                            });
                        }
                    }).start();
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
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(DataFields.smallTimeOut);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    char4.requestFocus();
                                }
                            });
                        }
                    }).start();
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

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(DataFields.smallTimeOut);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    char1.requestFocus();
                                }
                            });
                        }
                    }).start();
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
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                ((RegisterActivity) getActivity()).onError(new Verify());
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
        startActivity(new Intent(getActivity(), SplashScreenActivity.class));
        getActivity().finish();
    }

    @OnClick(R.id.tapToResend)
    public void topToResend() {
        JsonObjectRequest req = new JsonObjectRequest(DataFields.getServerUrl() + DataFields.RESENDURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        SnackBar snackBar = new SnackBar(getActivity(), genieApplication.getString(R.string.smsrequestresend));
                        snackBar.show();
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
