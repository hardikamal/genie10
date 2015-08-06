package com.supergenieapp.android.Objects;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

/**
 * Created by Raviteja on 8/6/2015.
 */
public class MenuItems {
    String title;
    MaterialDrawableBuilder.IconValue image;

    public MenuItems(String title, MaterialDrawableBuilder.IconValue image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MaterialDrawableBuilder.IconValue getImage() {
        return image;
    }

    public void setImage(MaterialDrawableBuilder.IconValue image) {
        this.image = image;
    }
}
