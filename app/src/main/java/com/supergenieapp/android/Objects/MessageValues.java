package com.supergenieapp.android.Objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raviteja on 6/16/2015.
 */
public class MessageValues {
    private int _id;
    private String text;
    private String url;
    private double lng;
    private double lat;
    private String name;

    public MessageValues() {
    }

    public MessageValues(int _id, String text) {
        this._id = _id;
        this.text = text;
    }

    public MessageValues(int _id, String url, String text) {
        this._id = _id;
        this.url = url;
        this.text = text;
    }

    public MessageValues(int _id, String text, double lng, double lat) {
        this._id = _id;
        this.text = text;
        this.lng = lng;
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("id", get_id());
            if (getText() != null)
                jsonObject.put("text", getText());
            if (getUrl() != null)
                jsonObject.put("url", getUrl());
            if (getLat() != 0f)
                jsonObject.put("lat", getLat());
            if (getLng() != 0f)
                jsonObject.put("lng", getLng());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}