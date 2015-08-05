package com.supergenieapp.android.Objects;

/**
 * Created by Raviteja on 8/6/2015.
 */
public class MenuItems {
    String title;
    String image;
    String color;

    public MenuItems(String title, String image, String color) {
        this.title = title;
        this.image = image;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
