package com.supergenieapp.android.Objects;

/**
 * Created by Raviteja on 6/9/2015.
 */
public class Categories {
    private int id;

    private int notification_count;

    private String bg_color;

    private String image_url;

    private String description;

    private String name;

    private long hide_chats_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNotification_count() {
        return notification_count;
    }

    public void setNotification_count(int notification_count) {
        this.notification_count = notification_count;
    }

    public String getBg_color() {
        return bg_color;
    }

    public void setBg_color(String bg_color) {
        this.bg_color = bg_color;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getHide_chats_time() {
        return hide_chats_time;
    }

    public void setHide_chats_time(long hide_chats_time) {
        this.hide_chats_time = hide_chats_time;
    }

    public Categories(int id, int notification_count, String bg_color, String image_url, String description, String name, long hide_chats_time) {
        this.id = id;
        this.notification_count = notification_count;
        this.bg_color = bg_color;
        this.image_url = image_url;
        this.description = description;
        this.name = name;
        this.hide_chats_time = hide_chats_time;
    }
}
