package com.getgenieapp.android.CustomViews.Adapters;

import android.content.Context;
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

import com.getgenieapp.android.Extras.Logging;
import com.getgenieapp.android.GenieApplication;
import com.getgenieapp.android.Objects.Categories;
import com.getgenieapp.android.R;

import java.util.ArrayList;

/**
 * Created by Manny on 6/16/2015.
 */
public class CustomAdapter extends RecyclerView.Adapter {
    private ArrayList<Categories> categories;
    private Context context;
    private Logging logging;

    public CustomAdapter(ArrayList<Categories> categories, Context context) {
        this.categories = categories;
        this.context = context;
        this.logging = GenieApplication.getInstance().getLoggingBuilder().setUp();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolderMain extends RecyclerView.ViewHolder {
        TextView line1;
        TextView line2;
        RelativeLayout topbar;
        LinearLayout middlebar;
        LinearLayout lowerbar;
        View emptyspace;
        TextView title;
        ImageView image;
        Button notification_count;
        ViewTreeObserver vto;

        public ViewHolderMain(View itemView) {
            super(itemView);
            topbar = (RelativeLayout) itemView.findViewById(R.id.topbar);
            middlebar = (LinearLayout) itemView.findViewById(R.id.middleBar);
            lowerbar = (LinearLayout) itemView.findViewById(R.id.lowerBar);
            emptyspace = (View) itemView.findViewById(R.id.emptyspace);
            title = (TextView) itemView.findViewById(R.id.title);
            line1 = (TextView) itemView.findViewById(R.id.line1);
            line2 = (TextView) itemView.findViewById(R.id.line2);
            image = (ImageView) itemView.findViewById(R.id.image);
            notification_count = (Button) itemView.findViewById(R.id.notification_count);
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
        if (viewType % 2 == 0)
            return new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.gridlayout_left, parent, false));
        else
            return new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.gridlayout_right, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Categories category = categories.get(position);
        final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;

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
        int id = context.getResources().getIdentifier(category.getImage_url(), "drawable", context.getPackageName());
        viewHolderMain.image.setBackgroundResource(id);
        viewHolderMain.showText(category.getDescription());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}