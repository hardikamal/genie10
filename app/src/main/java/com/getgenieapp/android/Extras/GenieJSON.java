package com.getgenieapp.android.Extras;

import android.content.Context;
import android.content.SharedPreferences;

import com.getgenieapp.android.GenieApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class GenieJSON extends JSONObject {
    public GenieJSON(Context c) {
        super();
        SharedPreferences sharedPreferences = GenieApplication.getInstance().getSecurePrefs();
        try {
            this.put("token", sharedPreferences.getString(DataFields.TOKEN, null));
        } catch (JSONException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException("Failed to create GenieJsonObject");
        }
    }
}