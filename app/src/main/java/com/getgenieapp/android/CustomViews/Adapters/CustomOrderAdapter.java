package com.getgenieapp.android.CustomViews.Adapters;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getgenieapp.android.Extras.Logging;
import com.getgenieapp.android.GenieApplication;
import com.getgenieapp.android.Objects.Order;
import com.getgenieapp.android.Objects.OrderCategory;
import com.getgenieapp.android.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Manny on 6/16/2015.
 */
public class CustomOrderAdapter extends RecyclerView.Adapter {
    private ArrayList<Order> orders;
    private Context context;
    private Logging logging;

    public CustomOrderAdapter(ArrayList<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
        this.logging = GenieApplication.getInstance().getLoggingBuilder().setUp();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolderMain extends RecyclerView.ViewHolder {
        LinearLayout category;
        ImageView categoryimage;
        TextView companyname;
        TextView rate;
        TextView orderdetailstext;
        Button repeatorder;

        public ViewHolderMain(View itemView) {
            super(itemView);
            category = (LinearLayout) itemView.findViewById(R.id.category);
            categoryimage = (ImageView) itemView.findViewById(R.id.categoryimage);
            companyname = (TextView) itemView.findViewById(R.id.companyname);
            rate = (TextView) itemView.findViewById(R.id.rate);
            orderdetailstext = (TextView) itemView.findViewById(R.id.orderdetailstext);
            repeatorder = (Button) itemView.findViewById(R.id.repeatorder);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        logging.LogV("Item Position Order Activity " + viewType);
        return new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.orderlayout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Order order = orders.get(position);
        OrderCategory currentOrderCategory = order.getCategory();
        final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;

        viewHolderMain.category.setBackgroundColor(Color.parseColor(currentOrderCategory.getBg_color()));

        int id = context.getResources().getIdentifier(currentOrderCategory.getImage_url(), "drawable", context.getPackageName());
        viewHolderMain.categoryimage.setBackgroundResource(id);

        viewHolderMain.companyname.setText(order.getService_provider());
        DecimalFormat df = new DecimalFormat("#.00");
        viewHolderMain.rate.setText("Rs " + df.format(order.getCost()));
        viewHolderMain.orderdetailstext.setText(order.getDescription());
        viewHolderMain.repeatorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Just Clicked Repeat Order", Style.INFO);
            }
        });
    }

    public void showToast(String message, Style style) {
        Crouton.makeText((Activity) context, message, style).show();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}