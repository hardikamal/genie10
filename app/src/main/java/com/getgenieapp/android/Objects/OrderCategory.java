package com.getgenieapp.android.Objects;

/**
 * Created by Raviteja on 6/15/2015.
 */
public class OrderCategory {
    private String id;

    private String bg_color;

    private String image_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public OrderCategory(String id, String bg_color, String image_url) {
        this.id = id;
        this.bg_color = bg_color;
        this.image_url = image_url;
    }
}