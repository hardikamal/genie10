package com.getgenieapp.android.Objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raviteja on 6/16/2015.
 */
public class MessageValues {
    private int _id;
    private String text;
    private String url;
    private String caption;
    private double lng;
    private double lat;

    public MessageValues() {
    }

    public MessageValues(int _id, String text) {
        this._id = _id;
        this.text = text;
    }

    public MessageValues(int _id, String url, String caption) {
        this._id = _id;
        this.url = url;
        this.caption = caption;
    }

    public MessageValues(int _id, String caption, double lng, double lat) {
        this._id = _id;
        this.caption = caption;
        this.lng = lng;
        this.lat = lat;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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
            if (getCaption() != null)
                jsonObject.put("caption", getCaption());
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