package com.supergenieapp.android.CustomViews.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Extras.Logging;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.Fragments.MainFragment;
import com.supergenieapp.android.GenieApplication;
import com.supergenieapp.android.Objects.Categories;
import com.supergenieapp.android.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CustomAdapter extends RecyclerView.Adapter {
    private ArrayList<Categories> categories;
    private Context context;
    private Logging logging;
    private ImageLoader imageLoader;
    private MainFragment.onSelect on_Select;
    private Utils utils;

    public CustomAdapter(ArrayList<Categories> categories, Context context) {
        this.categories = categories;
        this.context = context;
        this.logging = GenieApplication.getInstance().getLoggingBuilder().setUp();
        this.imageLoader = GenieApplication.getInstance().getImageLoader();
        on_Select = (MainFragment.onSelect) context;
        this.utils = new Utils(context);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolderMain extends RecyclerView.ViewHolder {
        @InjectView(R.id.topbar)
        RelativeLayout topbar;
        @InjectView(R.id.middleBar)
        LinearLayout middlebar;
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.image)
        ImageView image;
        @InjectView(R.id.notification_count)
        Button notification_count;
        ViewTreeObserver vto;

        public ViewHolderMain(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            vto = itemView.getViewTreeObserver();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        logging.LogV("Item Position Main Activity " + viewType);
        ViewHolderMain viewHolderMain;
        if (viewType % 2 == 0)
            viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.gridlayout_left, parent, false));
        else
            viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.gridlayout_right, parent, false));
        return viewHolderMain;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Categories category = categories.get(position);
        final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;

        viewHolderMain.topbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(category);
            }
        });
        viewHolderMain.middlebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(category);
            }
        });

        viewHolderMain.topbar.setBackgroundColor(Color.parseColor(category.getBg_color()));
        viewHolderMain.middlebar.setBackgroundColor(Color.parseColor(category.getBg_color()));
        viewHolderMain.title.setBackgroundColor(Color.parseColor(category.getBg_color()));
        viewHolderMain.title.setText(category.getName());

        if (category.getNotification_count() > 0) {
            logging.LogV("Set Notification count " + category.getNotification_count());
            viewHolderMain.notification_count.setText(String.valueOf(category.getNotification_count()));
            viewHolderMain.notification_count.setTextColor(Color.parseColor(category.getBg_color()));
        } else {
            logging.LogV("Set Notification 0");
            viewHolderMain.notification_count.setText(String.valueOf(category.getNotification_count()));
            viewHolderMain.notification_count.setTextColor(Color.parseColor(category.getBg_color()));
            viewHolderMain.notification_count.setBackgroundResource(android.R.color.transparent);
        }

        String path = DataFields.TempFolder + "/" + utils.hashString(category.getImage_url());
        File imgFile = new File(path);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            viewHolderMain.image.setImageBitmap(myBitmap);
        } else {
            imageLoader.get(category.getImage_url(), new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response != null && response.getBitmap() != null) {
                        viewHolderMain.image.setImageBitmap(response.getBitmap());
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(DataFields.TempFolder + "/" + utils.hashString(category.getImage_url()));
                            response.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (out != null) {
                                    out.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    private void goToChatActivity(Categories category) {
        on_Select.onClick(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private Categories getCategory(int position) {
        return categories.get(position);
    }
}