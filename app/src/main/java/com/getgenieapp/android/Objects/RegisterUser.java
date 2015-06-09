package com.getgenieapp.android.Objects;

import com.google.gson.JsonElement;

/**
 * Created by Raviteja on 6/8/2015.
 */
public class RegisterUser {
    String name;
    String number;
    String token;

    public RegisterUser(String name, String number, String token) {
        this.name = name;
        this.number = number;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
