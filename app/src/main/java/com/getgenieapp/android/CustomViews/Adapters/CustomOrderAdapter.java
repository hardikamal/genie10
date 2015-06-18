package com.getgenieapp.android.CustomViews.Adapters;

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
import android.widget.TextView;
import android.widget.Toast;

import com.getgenieapp.android.Objects.Order;
import com.getgenieapp.android.Objects.OrderCategory;
import com.getgenieapp.android.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomOrderAdapter extends BaseAdapter {

    Context context;
    static ArrayList<Order> items;

    public CustomOrderAdapter(Context context, ArrayList<Order> items) {
        this.context = context;
        CustomOrderAdapter.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Order getItem(int position) {
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
            v = vi.inflate(R.layout.orderlayout, null);

            vh = new ViewHolderItem();
            v.setTag(vh);

            Animation anim = AnimationUtils.loadAnimation(context, R.anim.fly_in_from_center);
            v.setAnimation(anim);
            anim.start();
        } else {
            vh = (ViewHolderItem) v.getTag();
        }

        Order currentOrder = getItem(arg0);
        OrderCategory currentOrderCategory = currentOrder.getCategory();

        LinearLayout category = (LinearLayout) v.findViewById(R.id.category);
        ImageView categoryimage = (ImageView) v.findViewById(R.id.categoryimage);
        TextView companyname = (TextView) v.findViewById(R.id.companyname);
        TextView rate = (TextView) v.findViewById(R.id.rate);
        TextView orderdetailstext = (TextView) v.findViewById(R.id.orderdetailstext);
        Button repeatorder = (Button) v.findViewById(R.id.repeatorder);

        category.setBackgroundColor(Color.parseColor(currentOrderCategory.getBg_color()));
        categoryimage.setBackgroundResource(R.drawable.genie_android_icons_97x97);
        companyname.setText(currentOrder.getService_provider());
        DecimalFormat df = new DecimalFormat("#.00");
        rate.setText("Rs " + df.format(currentOrder.getCost()));
        orderdetailstext.setText(currentOrder.getDescription());
        repeatorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Repeat This Order", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    static class ViewHolderItem {
    }
}