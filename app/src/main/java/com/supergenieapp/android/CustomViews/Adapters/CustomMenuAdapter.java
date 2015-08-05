package com.supergenieapp.android.CustomViews.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.supergenieapp.android.Activities.BaseActivity;
import com.supergenieapp.android.Activities.UserProfileActivity;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.Objects.Categories;
import com.supergenieapp.android.Objects.MenuItems;
import com.supergenieapp.android.Objects.Order;
import com.supergenieapp.android.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Raviteja on 8/6/2015.
 */
public class CustomMenuAdapter extends RecyclerView.Adapter {
    private ArrayList<MenuItems> menuItems;
    private Context context;
    private Utils utils;

    public CustomMenuAdapter(Context context) {
        this.context = context;
        this.utils = new Utils(context);
        menuItems = setUpValues();
    }

    private ArrayList<MenuItems> setUpValues() {
        ArrayList<MenuItems> menu = new ArrayList<>();
        String[] titles = context.getResources().getStringArray(R.array.menu_items);
        String[] images = context.getResources().getStringArray(R.array.menu_items_draws);
        if (titles.length == images.length) {
            for (int i = 0; i < titles.length; i++) {
                menu.add(new MenuItems(titles[i], images[i], ""));
            }
        }
        return menu;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class ViewHolderMain extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.image)
        ImageView image;
        @InjectView(R.id.text)
        TextView text;
        Context localContext;

        public ViewHolderMain(View itemView, Context localContext) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            this.localContext = localContext;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            localContext.startActivity(new Intent(localContext, UserProfileActivity.class));
            ((BaseActivity) localContext).closeMenu();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.menuitem, parent, false), context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MenuItems menuItem = menuItems.get(position);
        final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;
//        viewHolderMain.image.set
        viewHolderMain.text.setText(menuItem.getTitle());
    }
}