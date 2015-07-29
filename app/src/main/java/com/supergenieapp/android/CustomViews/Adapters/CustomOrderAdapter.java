package com.supergenieapp.android.CustomViews.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.supergenieapp.android.Activities.OrderDetailsActivity;
import com.supergenieapp.android.Database.DBDataSource;
import com.supergenieapp.android.Extras.Logging;
import com.supergenieapp.android.GenieApplication;
import com.supergenieapp.android.Objects.Categories;
import com.supergenieapp.android.Objects.Order;
import com.supergenieapp.android.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Manny on 6/16/2015.
 */
public class CustomOrderAdapter extends RecyclerView.Adapter {
    private ArrayList<Order> orders;
    private Context context;
    private Logging logging;
    private ImageLoader imageLoader;
    private DBDataSource dbDataSource;

    public CustomOrderAdapter(ArrayList<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
        this.logging = GenieApplication.getInstance().getLoggingBuilder().setUp();
        this.imageLoader = GenieApplication.getInstance().getImageLoader();
        this.dbDataSource = GenieApplication.getInstance().getDBDataSource();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolderMain extends RecyclerView.ViewHolder {
        @InjectView(R.id.category)
        LinearLayout category;
        @InjectView(R.id.categoryimage)
        NetworkImageView categoryimage;
        @InjectView(R.id.companyname)
        TextView companyname;
        @InjectView(R.id.rate)
        TextView rate;
        @InjectView(R.id.orderdetailstext)
        TextView orderdetailstext;
        @InjectView(R.id.repeatorder)
        Button repeatorder;

        public ViewHolderMain(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
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
        Categories currentOrderCategory = dbDataSource.getCategories(Integer.parseInt(order.getCategory_id()));
        final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;

        viewHolderMain.category.setBackgroundColor(Color.parseColor(currentOrderCategory.getBg_color()));

        viewHolderMain.categoryimage.setImageUrl(currentOrderCategory.getImage_url(), imageLoader);

        viewHolderMain.companyname.setText(order.getService_provider());
        DecimalFormat df = new DecimalFormat("#.00");
        viewHolderMain.rate.setText("Rs. " + df.format(Double.parseDouble(order.getCost())));
        viewHolderMain.orderdetailstext.setText(order.getDescription());
        viewHolderMain.repeatorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OrderDetailsActivity) context).mixpanelDataAdd.put("Pressed", "Repeat Order");
                ((OrderDetailsActivity) context).mixPanelBuild("Just Clicked Repeat Order");
                showToast("Just Clicked Repeat Order", Style.INFO);
            }
        });


    }

    public void showToast(String message, Style style) {
        Crouton.makeText((Activity) context, message, style, R.id.body).show();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}