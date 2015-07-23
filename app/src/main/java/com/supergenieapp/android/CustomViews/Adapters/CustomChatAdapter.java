package com.supergenieapp.android.CustomViews.Adapters;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.supergenieapp.android.Activities.BaseActivity;
import com.supergenieapp.android.CustomViews.Button.ButtonFlat;
import com.supergenieapp.android.CustomViews.ProgressBar.LoadingViewFlat;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.Fragments.ChatFragment;
import com.supergenieapp.android.Fragments.PaymentFragment;
import com.supergenieapp.android.GenieApplication;
import com.supergenieapp.android.Objects.Chat;
import com.supergenieapp.android.Objects.MessageValues;
import com.supergenieapp.android.Objects.Messages;
import com.supergenieapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Raviteja on 6/16/2015.
 */
public class CustomChatAdapter extends RecyclerView.Adapter {
    private ArrayList<Messages> messagesList;
    private Context context;
    private ImageLoader imageLoader;
    private String color;
    Messages messages;
    private String catImageUrl;

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public CustomChatAdapter(ArrayList<Messages> messagesList, String color, String catImageUrl, Context context) {
        this.messagesList = messagesList;
        this.context = context;
        this.color = color;
        this.imageLoader = GenieApplication.getInstance().getImageLoader();
        this.catImageUrl = catImageUrl;
    }

    public void showToast(String message, Style style) {

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolderMain extends RecyclerView.ViewHolder implements View.OnLongClickListener {
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
        ButtonFlat payascod;
        @Optional
        @InjectView(R.id.paynow)
        ButtonFlat paynow;
        @Optional
        @InjectView(R.id.catimage)
        NetworkImageView catimage;
        @Optional
        @InjectView(R.id.paylayout)
        LinearLayout paylayout;
        @Optional
        @InjectView(R.id.imageLayout)
        LinearLayout imageLayout;
        Context localContext;

        public ViewHolderMain(View itemView, Context context) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            this.localContext = context;
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
//            Toast.makeText(localContext, "Long Clicked", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolderMain viewHolderMain;
        Messages currentMessage = messagesList.get(viewType);
        if (currentMessage.getMessageType() == DataFields.LOADMORE) {
            viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.loadearliermessages, parent, false), context);
        } else if (currentMessage.getMessageType() == DataFields.DATESHOW) {
            viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.datelayout, parent, false), context);
        } else if (currentMessage.getMessageType() == DataFields.PAYNOW) {
            viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.paynow, parent, false), context);
        } else {
            if (messagesList.get(viewType).getDirection() == DataFields.INCOMING)
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

        if (messages.getMessageType() == DataFields.LOADMORE) {
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
                    JSONObject jsonObject = new JSONObject();
                    try {
                        for (Messages msg : messagesList) {
                            if (msg.getMessageType() == 1 || msg.getMessageType() == 2 || msg.getMessageType() == 3 || msg.getMessageType() == 5) {
                                jsonObject.put("cid", msg.getCategory());
                                jsonObject.put("timestamp", msg.getCreatedAt());
                                break;
                            }
                        }
                        if (!jsonObject.has("cid") && messagesList.size() > 0) {
                            jsonObject.put("cid", messagesList.get(0).getCategory());
                            jsonObject.put("timestamp", System.currentTimeMillis());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonObject.has("cid")) {
                        System.out.println("JSON for get all " + jsonObject.toString());
                        ((BaseActivity) context).sendLoadMoreMessagesCall(jsonObject);
                    }
                }
            });
        } else if (messages.getMessageType() == DataFields.DATESHOW) {
            Utils utils = new Utils(context);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
            viewHolderMain.date.setText(utils.getIfItsToday(utils.convertLongToDate(messages.getCreatedAt(), simpleDateFormat), simpleDateFormat));
            viewHolderMain.date.setTextColor(Color.parseColor(color));
        } else if (messages.getMessageType() == DataFields.PAYNOW) {
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
                viewHolderMain.payascod.setTextColor(Color.parseColor(color));
                viewHolderMain.paynow.setTextColor(Color.parseColor(color));
                viewHolderMain.paynow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showToast(context.getString(R.string.finishordertext), Style.INFO);
                        PaymentFragment paymentFragment = new PaymentFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("url", messageValues.getUrl());
                        paymentFragment.setArguments(bundle);
                        ((BaseActivity) context).startFragment(R.id.body, paymentFragment);
                    }
                });
                viewHolderMain.catimage.setImageUrl(catImageUrl, imageLoader);
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{Color.parseColor(color), Color.parseColor(color)});
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolderMain.imageLayout.setBackground(gd);
                } else {
                    viewHolderMain.imageLayout.setBackgroundDrawable(gd);
                }
                GradientDrawable gdRound = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{Color.parseColor(color), Color.parseColor(color)});
                gdRound.setCornerRadius(10f);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolderMain.paylayout.setBackground(gdRound);
                } else {
                    viewHolderMain.paylayout.setBackgroundDrawable(gdRound);
                }
                viewHolderMain.text.setText("Make Payment" + " " + context.getResources().getString(R.string.space10char));
                viewHolderMain.time.setText(new Utils(context).convertLongToDate(messages.getCreatedAt(), new SimpleDateFormat("HH:mm")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            viewHolderMain.time.setText(new Utils(context).convertLongToDate(messages.getCreatedAt(), new SimpleDateFormat("HH:mm")));
            if (messages.getMessageType() == DataFields.LOCATION) {
                String getMapURL = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=560x240&markers=size:mid|color:red|"
                        + messageValues.getLat()
                        + ","
                        + messageValues.getLng()
                        + "&sensor=false";
                if (messageValues.getLat() != 0.00 && messageValues.getLng() != 0.00) {
                    viewHolderMain.mapView.setVisibility(View.VISIBLE);
//                viewHolderMain.mapView.setDefaultImageResId(R.drawable.); todo set default
                    viewHolderMain.mapView.setImageUrl(getMapURL, imageLoader);
//                viewHolderMain.mapView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Uri gmmIntentUri = Uri.parse("geo:" + messageValues.getLat() + "," + messageValues.getLng() + "?z=15");
//                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                        mapIntent.setPackage("com.google.android.apps.maps");
//                        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
//                            context.startActivity(mapIntent);
//                        }
//                    }
//                });
                }

                if (messages.getDirection() == DataFields.INCOMING) {
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

            if (messages.getMessageType() == DataFields.IMAGE) {
                viewHolderMain.mapView.setVisibility(View.VISIBLE);
//                viewHolderMain.mapView.setDefaultImageResId(R.drawable.); todo set default
                viewHolderMain.mapView.setImageUrl(messageValues.getUrl(), imageLoader);
                viewHolderMain.mapView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // todo image view intent
                    }
                });

                if (messages.getDirection() == DataFields.INCOMING) {
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

            if (messages.getDirection() == DataFields.INCOMING) {
//            if (position % 2 == 0) {
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
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{context.getResources().getColor(R.color.white), context.getResources().getColor(R.color.white)});
                gd.setCornerRadius(10f);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolderMain.text.setBackground(gd);
                } else {
                    viewHolderMain.text.setBackgroundDrawable(gd);
                }
                viewHolderMain.tick.setVisibility(View.VISIBLE);
                if (messages.getStatus() == DataFields.SENT) {
                    viewHolderMain.tick.setBackgroundResource(R.drawable.ic_done_black_24dp);
                } else if (messages.getStatus() == DataFields.DELIVERED) {
                    viewHolderMain.tick.setBackgroundResource(R.drawable.ic_done_all_black_24dp);
                } else if (messages.getStatus() == DataFields.SEEN) {
                    viewHolderMain.tick.setBackgroundResource(R.drawable.check_all);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}