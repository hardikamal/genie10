package com.getgenieapp.android.Objects;

/**
 * Created by Raviteja on 7/18/2015.
 */
public class Chat {
    int category_Id;
    int direction;
    String text;
    int status;
    int type;
    long created_at;
    long updated_at;
    String id;
    double lng;
    double lat;
    String url;

    public Chat(String id, int category_Id, int direction, int status, long created_at, long updated_at, int type, String text) {
        this.category_Id = category_Id;
        this.direction = direction;
        this.text = text;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.id = id;
        this.type = type;
    }

    public Chat(String id, int category_Id, int direction, int status, long created_at, long updated_at, int type, String url, String text) {
        this.category_Id = category_Id;
        this.direction = direction;
        this.text = text;
        this.url = url;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.id = id;
        this.type = type;
    }

    public Chat(String id, int category_Id, int direction, int status, long created_at, long updated_at, int type, String text, double lng, double lat) {
        this.category_Id = category_Id;
        this.direction = direction;
        this.text = text;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.id = id;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
    }

    public int getCategory_Id() {
        return category_Id;
    }

    public void setCategory_Id(int category_Id) {
        this.category_Id = category_Id;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
