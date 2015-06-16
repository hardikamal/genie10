package com.getgenieapp.android.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

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
        final ViewHolderItem vh;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);

            if (arg0 % 2 == 0)
                v = vi.inflate(R.layout.gridlayout_left, null);
            else
                v = vi.inflate(R.layout.gridlayout_right, null);

            vh = new ViewHolderItem();
            vh.line1 = (TextView) v.findViewById(R.id.line1);
            vh.line2 = (TextView) v.findViewById(R.id.line2);
            vh.vto = v.getViewTreeObserver();
            v.setTag(vh);

            Animation anim = AnimationUtils.loadAnimation(context, R.anim.fly_in_from_center);
            v.setAnimation(anim);
            anim.start();
        } else {
            vh = (ViewHolderItem) v.getTag();
        }
        RelativeLayout topbar = (RelativeLayout) v.findViewById(R.id.topbar);
        LinearLayout middlebar = (LinearLayout) v.findViewById(R.id.middleBar);
        LinearLayout lowerbar = (LinearLayout) v.findViewById(R.id.lowerBar);
        View emptyspace = (View) v.findViewById(R.id.emptyspace);

        TextView title = (TextView) v.findViewById(R.id.title);
        final TextView line1 = (TextView) v.findViewById(R.id.line1);
        final TextView line2 = (TextView) v.findViewById(R.id.line2);
        ImageView image = (ImageView) v.findViewById(R.id.image);
        Button notification_count = (Button) v.findViewById(R.id.notification_count);

        Categories categories = getItem(arg0);

        if (topbar != null && middlebar != null && lowerbar != null && emptyspace != null) {
            topbar.setBackgroundColor(Color.parseColor(categories.getColor()));
            middlebar.setBackgroundColor(Color.parseColor(categories.getColor()));
            lowerbar.setBackgroundColor(Color.parseColor(categories.getColor()));
            emptyspace.setBackgroundColor(Color.parseColor(categories.getColor()));
        }
        if (categories != null && title != null && notification_count != null && line2 != null && image != null) {
            title.setText(categories.getName());

            if (categories.getNotification_count() > 0) {
                notification_count.setText(String.valueOf(categories.getNotification_count()));
                notification_count.setTextColor(Color.parseColor(categories.getColor()));
            } else {
                notification_count.setBackgroundResource(android.R.color.transparent);
            }
            // add volley ToDo

            image.setBackgroundResource(R.drawable.genie_android_icons_97x97);
            final String lastmessage = categories.getDescription();

            vh.setValues(lastmessage);
        }

        return v;
    }

    static class ViewHolderItem {
        TextView line1;
        TextView line2;
        ViewTreeObserver vto;

        void setValues(final String lastmessage) {
            if (vto.isAlive()) {
                vto.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        line1.setText(lastmessage);
                        Layout layout = line1.getLayout();
                        int lastLine = layout.getLineCount();
                        System.out.println(lastLine);
                        if (lastLine > 0) {
                            int lastlineindex = layout.getLineStart(1);
                            System.out.println(lastlineindex);
                            line1.setText(lastmessage.substring(0, lastlineindex));
                            System.out.println(lastmessage.substring(0, lastlineindex));
                            line2.setText(lastmessage.substring(lastlineindex, lastmessage.length()));
                            System.out.println(lastmessage.substring(lastlineindex, lastmessage.length()));
                        } else {
                            line1.setText("");
                            line2.setText(lastmessage);
                        }
                    }
                });
            }
        }
    }
}