package com.getgenieapp.android.Extras;

import android.content.Context;

import com.getgenieapp.android.SharedPreference.TokenSharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

public class GenieJSON extends JSONObject {
    public GenieJSON(Context c){
        super();
        try {
            this.put("token", new TokenSharedPreference(c).getToken());
        } catch (JSONException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException("Failed to create GenieJsonObject");
        }
    }
}