package com.getgenieapp.android.Fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.getgenieapp.android.Activities.MainActivity;
import com.getgenieapp.android.Activities.RegisterActivity;
import com.getgenieapp.android.CustomViews.LoadingView;
import com.getgenieapp.android.CustomViews.SnackBar;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.GenieJSON;
import com.getgenieapp.android.Extras.UIHelpers;
import com.getgenieapp.android.GCMHelpers.QuickstartPreferences;
import com.getgenieapp.android.GCMHelpers.RegistrationIntentService;
import com.getgenieapp.android.GCMHelpers.UpdateIntentService;
import com.getgenieapp.android.Objects.Register;
import com.getgenieapp.android.Objects.RegisterUser;
import com.getgenieapp.android.R;
import com.getgenieapp.android.GenieFragment;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class RegisterFragment extends GenieFragment {

    UIHelpers uiHelpers;
    @InjectView(R.id.topText)
    TextView topText;
    @InjectView(R.id.subText)
    TextView subText;
    @InjectView(R.id.name)
    EditText name;
    @InjectView(R.id.number)
    EditText number;
    @InjectView(R.id.parentLoadingView)
    LoadingView parentLoadingView;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }

        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.inject(this, rootView);

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
                    RegisterUser();
                } else {
                    ((RegisterActivity) getActivity()).onError(new Register());
                }
            }
        };

        uiHelpers = new UIHelpers();
        topText.setTextSize(uiHelpers.determineMaxTextSize(getActivity().getString(R.string.topText), uiHelpers.getXYPixels(getActivity()).x / 4));
        subText.setTextSize(uiHelpers.determineMaxTextSize(getActivity().getString(R.string.subText), uiHelpers.getXYPixels(getActivity()).x / 4));

        fontChangeCrawler.replaceFonts((ViewGroup) rootView);
        return rootView;
    }

    private void RegisterUser() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name.getText().toString());
        json.addProperty("number", number.getText().toString());
        try {
            json.addProperty("token", InstanceID.getInstance(getActivity()).getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (json.get("token").isJsonNull()) {
            ((RegisterActivity) getActivity()).onError(new Register());
        } else {
            Ion.with(this)
                    .load(DataFields.getServerUrl() + DataFields.REGISTERURL)
                    .setJsonObjectBody((JsonObject) new JsonParser().parse(json.toString()))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            parentLoadingView.setLoading(false);
                            if(e != null)
                                ((RegisterActivity) getActivity()).onError(new Register());
                            if(result == null)
                                ((RegisterActivity) getActivity()).onError(new Register());
                            else
                                ((RegisterActivity) getActivity()).onSuccess(gson.fromJson(result, Register.class));
                        }
                    });
        }
    }

    @OnClick(R.id.getStarted)
    public void onGetStartedButtonClick() {
        if (name.getText().toString().trim().length() == 0) {
            SnackBar snackbar = new SnackBar(getActivity(), getResources().getString(R.string.pleaseentername));
            snackbar.show();
        } else if (number.getText().toString().trim().length() < 10) {
            SnackBar snackbar = new SnackBar(getActivity(), getResources().getString(R.string.pleaseentervalidnumber));
            snackbar.show();
        } else {
            parentLoadingView.setText(getResources().getString(R.string.registeringuser));
            parentLoadingView.setLoading(true);

            Intent intent = new Intent(getActivity(), RegistrationIntentService.class);
            getActivity().startService(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        logging.LogI("On Resume");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    public void onPause() {
        logging.LogI("On Pause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public interface onRegister {
        public void onSuccess(Register register);
        public void onError(Register register);
    }
}
