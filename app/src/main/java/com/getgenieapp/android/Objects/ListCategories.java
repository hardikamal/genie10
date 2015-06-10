package com.getgenieapp.android.Objects;

import java.util.List;

/**
 * Created by Raviteja on 6/9/2015.
 */
public class ListCategories {
    List<Categories> categoriesList;

    public ListCategories(List<Categories> categoriesList) {
        this.categoriesList = categoriesList;
    }

    public List<Categories> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(List<Categories> categoriesList) {
        this.categoriesList = categoriesList;
    }
}
