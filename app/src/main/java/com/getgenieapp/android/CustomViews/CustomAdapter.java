package com.getgenieapp.android.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getgenieapp.android.Extras.GetDate;
import com.getgenieapp.android.Objects.Categories;
import com.getgenieapp.android.R;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    Context context;
    static ArrayList<Categories> items;

    public CustomAdapter(Context context, ArrayList<Categories> items) {
        this.context = context;
        CustomAdapter.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Categories getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        View v = convertView;
        Categories categories = getItem(arg0);
        if (v == null && categories != null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            if (arg0 % 2 == 0)
                v = vi.inflate(R.layout.gridlayout_left, null);
            else
                v = vi.inflate(R.layout.gridlayout_right, null);

            RelativeLayout topbar = (RelativeLayout) v.findViewById(R.id.topbar);
            LinearLayout middlebar = (LinearLayout) v.findViewById(R.id.middleBar);
            LinearLayout lowermiddlebar = (LinearLayout) v.findViewById(R.id.lowerMiddleBar);
            LinearLayout lowerbar = (LinearLayout) v.findViewById(R.id.lowerBar);

            TextView name = (TextView) v.findViewById(R.id.title);
            Button notification_count = (Button) v.findViewById(R.id.notification_count);

            TextView lastmessage = (TextView) v.findViewById(R.id.lastMessage);
            TextView time = (TextView) v.findViewById(R.id.time);
            ImageView image = (ImageView) v.findViewById(R.id.image);
            View emptyspace = (View) v.findViewById(R.id.emptyspace);

            if (topbar != null && middlebar != null && lowermiddlebar != null && lowerbar != null && emptyspace != null) {
                topbar.setBackgroundColor(Color.parseColor(categories.getColor()));
                middlebar.setBackgroundColor(Color.parseColor(categories.getColor()));
                lowermiddlebar.setBackgroundColor(Color.parseColor(categories.getColor()));
                lowerbar.setBackgroundColor(Color.parseColor(categories.getColor()));
                emptyspace.setBackgroundColor(Color.parseColor(categories.getColor()));
            }
            if (name != null && notification_count != null && lastmessage != null && time != null && image != null) {
                name.setText(categories.getName());
                if (categories.getNotification_count() > 0) {
                    notification_count.setText(String.valueOf(categories.getNotification_count()));
                    notification_count.setTextColor(Color.parseColor(categories.getColor()));
                } else {
                    notification_count.setBackgroundResource(android.R.color.transparent);
                }
//                Ion.with(image)
//                        .load("https://www.projectplace.com/Global/images_NEW/icons/large/security-icon.png");

                // add volley

                lastmessage.setText(categories.getDescription());
                time.setText(new GetDate().convertLongToDate(categories.getHideTime()));
            }

        } else {
            RelativeLayout topbar = (RelativeLayout) v.findViewById(R.id.topbar);
            LinearLayout middlebar = (LinearLayout) v.findViewById(R.id.middleBar);
            LinearLayout lowermiddlebar = (LinearLayout) v.findViewById(R.id.lowerMiddleBar);
            LinearLayout lowerbar = (LinearLayout) v.findViewById(R.id.lowerBar);
            View emptyspace = (View) v.findViewById(R.id.emptyspace);

            if (topbar != null && middlebar != null && lowermiddlebar != null && lowerbar != null && emptyspace != null) {
                topbar.setBackgroundColor(Color.parseColor(categories.getColor()));
                middlebar.setBackgroundColor(Color.parseColor(categories.getColor()));
                lowermiddlebar.setBackgroundColor(Color.parseColor(categories.getColor()));
                lowerbar.setBackgroundColor(Color.parseColor(categories.getColor()));
                emptyspace.setBackgroundColor(Color.parseColor(categories.getColor()));
            }
        }

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fly_in_from_center);
        v.setAnimation(anim);
        anim.start();

        return v;
    }
}