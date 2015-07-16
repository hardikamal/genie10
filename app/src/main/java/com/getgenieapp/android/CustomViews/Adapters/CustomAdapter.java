package com.getgenieapp.android.CustomViews.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.getgenieapp.android.Extras.Logging;
import com.getgenieapp.android.Fragments.MainFragment;
import com.getgenieapp.android.GenieApplication;
import com.getgenieapp.android.Objects.Categories;
import com.getgenieapp.android.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CustomAdapter extends RecyclerView.Adapter {
    private ArrayList<Categories> categories;
    private Context context;
    private Logging logging;
    private ImageLoader imageLoader;
    private MainFragment.onSelect on_Select;

    public CustomAdapter(ArrayList<Categories> categories, Context context) {
        this.categories = categories;
        this.context = context;
        this.logging = GenieApplication.getInstance().getLoggingBuilder().setUp();
        this.imageLoader = GenieApplication.getInstance().getImageLoader();
        on_Select = (MainFragment.onSelect) context;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolderMain extends RecyclerView.ViewHolder {
        @InjectView(R.id.line1)
        TextView line1;
        @InjectView(R.id.line2)
        TextView line2;
        @InjectView(R.id.topbar)
        RelativeLayout topbar;
        @InjectView(R.id.middleBar)
        LinearLayout middlebar;
        @InjectView(R.id.lowerBar)
        LinearLayout lowerbar;
        @InjectView(R.id.emptyspace)
        View emptyspace;
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.image)
        NetworkImageView image;
        @InjectView(R.id.notification_count)
        Button notification_count;
        ViewTreeObserver vto;

        public ViewHolderMain(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            vto = itemView.getViewTreeObserver();
        }

        void showText(final String lastmessage) {
            if (vto.isAlive()) {
                vto.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        line1.setText(lastmessage);
                        Layout layout = line1.getLayout();
                        int lastLine = layout.getLineCount();
                        if (lastLine > 1) {
                            int lastlineindex = layout.getLineStart(1);
                            line1.setText(lastmessage.substring(0, lastlineindex));
                            line2.setText(lastmessage.substring(lastlineindex, lastmessage.length()));
                        } else {
                            line1.setText("");
                            line2.setText(lastmessage);
                        }
                    }
                });
            }
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
        viewHolderMain.lowerbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(category);
            }
        });

        viewHolderMain.topbar.setBackgroundColor(Color.parseColor(category.getBg_color()));
        viewHolderMain.middlebar.setBackgroundColor(Color.parseColor(category.getBg_color()));
        viewHolderMain.lowerbar.setBackgroundColor(Color.parseColor(category.getBg_color()));
        viewHolderMain.emptyspace.setBackgroundColor(Color.parseColor(category.getBg_color()));
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
        viewHolderMain.image.setImageUrl(category.getImage_url(), imageLoader);
        viewHolderMain.showText(category.getDescription());
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