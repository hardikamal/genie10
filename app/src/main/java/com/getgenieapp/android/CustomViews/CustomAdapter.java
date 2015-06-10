package com.getgenieapp.android.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.gridlayout, null);


            LinearLayout layout = (LinearLayout) v.findViewById(R.id.layout);
            TextView name = (TextView) v.findViewById(R.id.title);
            TextView notification_count = (TextView) v.findViewById(R.id.title);
            TextView lastmessage = (TextView) v.findViewById(R.id.lastMessage);
            TextView time = (TextView) v.findViewById(R.id.time);
            ImageView image = (ImageView) v.findViewById(R.id.image);



            if (layout != null) {
                layout.setBackgroundColor(Color.parseColor(gridObject.getLastmsg()));
            }
            if (gridObject != null) {

                if (tt1 != null) {
                    tt1.setText(gridObject.getTitle());
                }

                if (tt2 != null) {
                    tt2.setText(gridObject.getLastmsg());
                }

                if (tt3 != null) {
                    tt3.setText(gridObject.getTime());
                }
            }
        }
        else
        {
            LinearLayout layout = (LinearLayout) v.findViewById(R.id.layout);
            if (layout != null) {
                layout.setBackgroundColor(Color.parseColor(gridObject.getLastmsg()));
            }
        }

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fly_in_from_center);
        v.setAnimation(anim);
        anim.start();

        return v;
    }
}