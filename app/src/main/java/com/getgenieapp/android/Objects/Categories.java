package com.getgenieapp.android.Objects;

/**
 * Created by Raviteja on 6/9/2015.
 */
public class Categories {
    String title;
    int catId;
    String catColor;
    String catResourceId;
    String lastMsg;
    String time;

    public Categories(String title, int catId, String catColor, String catResourceId, String lastMsg, String time) {
        this.title = title;
        this.catId = catId;
        this.catColor = catColor;
        this.catResourceId = catResourceId;
        this.lastMsg = lastMsg;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getCatColor() {
        return catColor;
    }

    public void setCatColor(String catColor) {
        this.catColor = catColor;
    }

    public String getCatResourceId() {
        return catResourceId;
    }

    public void setCatResourceId(String catResourceId) {
        this.catResourceId = catResourceId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
