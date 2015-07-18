package com.getgenieapp.android.Objects;

/**
 * Created by Raviteja on 7/18/2015.
 */
public class Chat {
    int cid;
    int aid;
    int category;
    String text;
    int status;
    int sender_id;
    long created_at;
    long updated_at;
    String id;
    double lng;
    double lat;
    String url;

    public Chat(int cid, int aid, int category, String text, int status, int sender_id, long created_at, long updated_at, String id, double lng, double lat, String url) {
        this.cid = cid;
        this.aid = aid;
        this.category = category;
        this.text = text;
        this.status = status;
        this.sender_id = sender_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.id = id;
        this.lng = lng;
        this.lat = lat;
        this.url = url;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
