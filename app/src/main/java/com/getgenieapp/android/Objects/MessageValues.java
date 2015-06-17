package com.getgenieapp.android.Objects;

/**
 * Created by Raviteja on 6/16/2015.
 */
public class MessageValues {
    private String text;
    private String url;
    private String caption;
    private double lng;
    private double lat;

    public MessageValues(String text) {
        this.text = text;
    }

    public MessageValues(String url, String caption) {
        this.url = url;
        this.caption = caption;
    }

    public MessageValues(double lat, double lng) {
        this.lng = lng;
        this.lat = lat;
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
}