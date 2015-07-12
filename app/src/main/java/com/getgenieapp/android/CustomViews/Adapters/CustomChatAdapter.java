package com.getgenieapp.android.CustomViews.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.getgenieapp.android.Activities.PaymentActivity;
import com.getgenieapp.android.CustomViews.Misc.SnackBar;
import com.getgenieapp.android.CustomViews.ProgressBar.LoadingViewFlat;
import com.getgenieapp.android.Extras.Utils;
import com.getgenieapp.android.GenieApplication;
import com.getgenieapp.android.Objects.MessageValues;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Raviteja on 6/16/2015.
 */
public class CustomChatAdapter extends RecyclerView.Adapter {
    private ArrayList<Messages> messagesList;
    private Context context;
    private ImageLoader imageLoader;
    private String color;
    Messages messages;

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public CustomChatAdapter(ArrayList<Messages> messagesList, String color, Context context) {
        this.messagesList = messagesList;
        this.context = context;
        this.color = color;
        this.imageLoader = GenieApplication.getInstance().getImageLoader();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolderMain extends RecyclerView.ViewHolder {
        @Optional
        @InjectView(R.id.text)
        TextView text;
        @Optional
        @InjectView(R.id.time)
        TextView time;
        @Optional
        @InjectView(R.id.tick)
        ImageView tick;
        @Optional
        @InjectView(R.id.mapView)
        NetworkImageView mapView;
        @Optional
        @InjectView(R.id.loadingview)
        LoadingViewFlat loadingview;
        @Optional
        @InjectView(R.id.loadmessages)
        Button loadmessages;
        @Optional
        @InjectView(R.id.date)
        Button date;
        @Optional
        @InjectView(R.id.companyname)
        TextView companyName;
        @Optional
        @InjectView(R.id.rate)
        TextView rate;
        @Optional
        @InjectView(R.id.orderdetails)
        TextView orderdetails;
        @Optional
        @InjectView(R.id.payascod)
        Button payascod;
        @Optional
        @InjectView(R.id.paynow)
        Button paynow;

        public ViewHolderMain(View itemView, Context context) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolderMain viewHolderMain;
        Messages currentMessage = messagesList.get(viewType);
        if (currentMessage.getMessageType() == 8) {
            viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.loadearliermessages, parent, false), context);
        } else if (currentMessage.getMessageType() == 9) {
            viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.datelayout, parent, false), context);
        } else if (currentMessage.getMessageType() == 5) {
            viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.paynow, parent, false), context);
        } else {
//        if (messagesList.get(viewType).getDirection() == 1)
            if (viewType % 2 == 0)
                viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.incoming, parent, false), context);
            else
                viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.outgoing, parent, false), context);
        }
        return viewHolderMain;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Messages messages = messagesList.get(position);
        final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;
        final MessageValues messageValues = messages.getMessageValues();

        if (messages.getMessageType() == 8) {
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{Color.parseColor(color), Color.parseColor(color)});
            gd.setAlpha(50);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewHolderMain.loadingview.setBackground(gd);
            } else {
                viewHolderMain.loadingview.setBackgroundDrawable(gd);
            }
            viewHolderMain.loadmessages.setTextColor(Color.parseColor(color));
            viewHolderMain.loadmessages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolderMain.loadingview.setLoading(true);
                }
            });
        } else if (messages.getMessageType() == 9) {
            Utils utils = new Utils(context);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
            viewHolderMain.date.setText(utils.getIfItsToday(utils.convertLongToDate(messages.getCreatedAt(), simpleDateFormat), simpleDateFormat));
            viewHolderMain.date.setTextColor(Color.parseColor(color));
        } else if (messages.getMessageType() == 5) {
            try {
                JSONObject object = new JSONObject(messageValues.getText());
                if (object.has("companyname"))
                    viewHolderMain.companyName.setText(object.getString("companyname"));
                if (object.has("rate"))
                    viewHolderMain.rate.setText("Rs. " + String.valueOf(object.getDouble("rate")));
                if (object.has("details"))
                    viewHolderMain.orderdetails.setText(object.getString("details"));
                if (object.has("cod") && object.getBoolean("cod"))
                    viewHolderMain.payascod.setVisibility(View.VISIBLE);
                viewHolderMain.paynow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SnackBar snackBar = new SnackBar((Activity) context, context.getString(R.string.finishordertext));
                        snackBar.show();
                        Intent intent = new Intent(context, PaymentActivity.class);
                        intent.putExtra("url", messageValues.getUrl());
                        ((Activity) context).startActivityForResult(intent, 1);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            viewHolderMain.time.setText(new Utils(context).convertLongToDate(messages.getCreatedAt(), new SimpleDateFormat("HH:mm")));
            if (messages.getMessageType() == 2) {
                String getMapURL = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=560x240&markers=size:mid|color:red|"
                        + messageValues.getLat()
                        + ","
                        + messageValues.getLng()
                        + "&sensor=false";
                viewHolderMain.mapView.setVisibility(View.VISIBLE);
                viewHolderMain.mapView.setImageUrl(getMapURL, imageLoader);

//        if (messages.getDirection() == 1) {
                if (position % 2 == 0) {
                    GradientDrawable gd = new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[]{Color.parseColor(color), Color.parseColor(color)});
                    gd.setCornerRadius(5f);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolderMain.mapView.setBackground(gd);
                    } else {
                        viewHolderMain.mapView.setBackgroundDrawable(gd);
                    }
                } else {
                    GradientDrawable gd = new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[]{context.getResources().getColor(R.color.white), context.getResources().getColor(R.color.white)});
                    gd.setCornerRadius(5f);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolderMain.mapView.setBackground(gd);
                    } else {
                        viewHolderMain.mapView.setBackgroundDrawable(gd);
                    }
                }
            }

//        if (messages.getDirection() == 1) {
            if (position % 2 == 0) {
                viewHolderMain.text.setText(messageValues.getText() + " " + context.getResources().getString(R.string.space10char));
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{Color.parseColor(color), Color.parseColor(color)});
                gd.setCornerRadius(10f);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolderMain.text.setBackground(gd);
                } else {
                    viewHolderMain.text.setBackgroundDrawable(gd);
                }
            } else {
                viewHolderMain.text.setText(messageValues.getText() + " " + context.getResources().getString(R.string.space12char));
                viewHolderMain.text.setTextColor(Color.parseColor(color));
                viewHolderMain.time.setTextColor(Color.parseColor(color));
                if (messages.getStatus() == 1) {
                    viewHolderMain.tick.setBackgroundResource(R.drawable.ic_done_black_24dp);
                } else if (messages.getStatus() == 2) {
                    viewHolderMain.tick.setBackgroundResource(R.drawable.ic_done_all_black_24dp);
                } else if (messages.getStatus() == 3) {
                    viewHolderMain.tick.setBackgroundResource(R.drawable.check_all);
                }
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{context.getResources().getColor(R.color.white), context.getResources().getColor(R.color.white)});
                gd.setCornerRadius(10f);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolderMain.text.setBackground(gd);
                } else {
                    viewHolderMain.text.setBackgroundDrawable(gd);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
