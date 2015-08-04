package com.supergenieapp.android.CustomViews.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.supergenieapp.android.Activities.OrderDetailsActivity;
import com.supergenieapp.android.Database.DBDataSource;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Extras.Logging;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.GenieApplication;
import com.supergenieapp.android.Objects.Categories;
import com.supergenieapp.android.Objects.Order;
import com.supergenieapp.android.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Utils utils;

    public CustomOrderAdapter(ArrayList<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
        this.logging = GenieApplication.getInstance().getLoggingBuilder().setUp();
        this.imageLoader = GenieApplication.getInstance().getImageLoader();
        this.dbDataSource = GenieApplication.getInstance().getDBDataSource();
        this.utils = new Utils(context);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolderMain extends RecyclerView.ViewHolder {
        @InjectView(R.id.category)
        LinearLayout category;
        @InjectView(R.id.categoryimage)
        ImageView categoryimage;
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
        final Categories currentOrderCategory = dbDataSource.getCategories(Integer.parseInt(order.getCategory_id()));
        final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;

        viewHolderMain.category.setBackgroundColor(Color.parseColor(currentOrderCategory.getBg_color()));

        String path = DataFields.TempFolder + "/" + utils.hashString(currentOrderCategory.getImage_url());
        File imgFile = new File(path);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            viewHolderMain.categoryimage.setImageBitmap(myBitmap);
        } else {
            imageLoader.get(currentOrderCategory.getImage_url(), new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response != null && response.getBitmap() != null) {
                        viewHolderMain.categoryimage.setImageBitmap(response.getBitmap());
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(DataFields.TempFolder + "/" + utils.hashString(currentOrderCategory.getImage_url()));
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

        viewHolderMain.companyname.setText(order.getTitle());
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