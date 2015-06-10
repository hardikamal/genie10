package com.getgenieapp.android.Objects;

/**
 * Created by Raviteja on 6/9/2015.
 */
public class Categories {
    String name;
    int id;
    String color;
    String imageSource;
    boolean image_updated;
    int notification_count;
    String description;
    long hideTime;

    public Categories(String name, int id, String color, String imageSource, boolean image_updated, int notification_count, String description, long hideTime) {
        this.name = name;
        this.id = id;
        this.color = color;
        this.imageSource = imageSource;
        this.image_updated = image_updated;
        this.notification_count = notification_count;
        this.description = description;
        this.hideTime = hideTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public boolean isImage_updated() {
        return image_updated;
    }

    public void setImage_updated(boolean image_updated) {
        this.image_updated = image_updated;
    }

    public int getNotification_count() {
        return notification_count;
    }

    public void setNotification_count(int notification_count) {
        this.notification_count = notification_count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getHideTime() {
        return hideTime;
    }

    public void setHideTime(long hideTime) {
        this.hideTime = hideTime;
    }
}
