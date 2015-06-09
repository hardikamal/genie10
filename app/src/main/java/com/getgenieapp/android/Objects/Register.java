package com.getgenieapp.android.Objects;

/**
 * Created by Raviteja on 6/7/2015.
 */
public class Register {
    String token;

    public Register(String token) {
        this.token = token;
    }

    public Register() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
