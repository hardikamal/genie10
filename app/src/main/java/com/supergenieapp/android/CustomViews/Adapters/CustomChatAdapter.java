package com.supergenieapp.android.CustomViews.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.supergenieapp.android.Activities.BaseActivity;
import com.supergenieapp.android.CustomViews.Button.ButtonFlat;
import com.supergenieapp.android.CustomViews.ProgressBar.LoadingViewFlat;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.Fragments.PaymentFragment;
import com.supergenieapp.android.GenieApplication;
import com.supergenieapp.android.Objects.MessageValues;
import com.supergenieapp.android.Objects.Messages;
import com.supergenieapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
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
    private String categoryUrl;
    private Utils utils;

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public CustomChatAdapter(ArrayList<Messages> messagesList, String color, String categoryUrl, Context context) {
        this.messagesList = messagesList;
        this.context = context;
        this.color = color;
        this.imageLoader = GenieApplication.getInstance().getImageLoader();
        this.categoryUrl = categoryUrl;
        utils = new Utils(context);
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
        ImageView mapView;
        @Optional
        @InjectView(R.id.imageView)
        ImageView imageView;
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
        ImageView catimage;
        @Optional
        @InjectView(R.id.paylayout)
        LinearLayout paylayout;
        @Optional
        @InjectView(R.id.viewLayout)
        RelativeLayout viewLayout;
        @Optional
        @InjectView(R.id.mainLayout)
        RelativeLayout mainLayout;
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
            if (messagesList.get(viewType).getDirection() == DataFields.INCOMING) {
                viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.incoming, parent, false), context);
            } else {
                viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.outgoing, parent, false), context);
            }
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
                            jsonObject.put("timestamp", Utils.getCurrentTimeMillis());
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
                final JSONObject object = new JSONObject(messageValues.getText());
                if (object.has("service_provider"))
                    viewHolderMain.companyName.setText(object.getString("service_provider"));
                if (object.has("cost"))
                    viewHolderMain.rate.setText("Rs. " + String.valueOf(object.getDouble("cost")));
                if (object.has("description"))
                    viewHolderMain.orderdetails.setText(object.getString("description"));
                if (object.has("cod") && object.getBoolean("cod")) {
                    viewHolderMain.payascod.setVisibility(View.VISIBLE);
                    viewHolderMain.paynow.setVisibility(View.GONE);
                } else {
                    viewHolderMain.payascod.setVisibility(View.GONE);
                    viewHolderMain.paynow.setVisibility(View.VISIBLE);
                }
                final String costToPay = String.valueOf(object.getDouble("cost"));
                viewHolderMain.payascod.setTextColor(Color.parseColor(color));
                viewHolderMain.paynow.setTextColor(Color.parseColor(color));
                viewHolderMain.paynow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BaseActivity) context).emitPayOnline(messages.getCreatedAt(), "payment");
                        showToast(context.getString(R.string.finishordertext), Style.INFO);
                        PaymentFragment paymentFragment = new PaymentFragment();
                        Bundle bundle = new Bundle();
                        String url = context.getString(R.string.website);
                        try {
                            if (object.has("payment_url") && !object.getString("payment_url").equals("")) {
                                url = object.getString("payment_url");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        bundle.putString("url", url);
                        bundle.putLong("created_at", messages.getCreatedAt());
                        paymentFragment.setArguments(bundle);
                        ((BaseActivity) context).startFragmentFromRight(R.id.body, paymentFragment);
                    }
                });

                viewHolderMain.payascod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BaseActivity) context).showCODAlert(costToPay);
                    }
                });

                String path = DataFields.TempFolder + "/" + utils.hashString(categoryUrl);
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    viewHolderMain.catimage.setImageBitmap(myBitmap);
                } else {
                    if (categoryUrl.matches("data:image.*base64.*")) {
                        String base_64_source = categoryUrl.replaceAll("data:image.*base64", "");
                        byte[] data = Base64.decode(base_64_source, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        if (bitmap != null) {
                            FileOutputStream out = null;
                            try {
                                out = new FileOutputStream(DataFields.TempFolder + "/" + utils.hashString(categoryUrl));
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
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
                            viewHolderMain.catimage.setImageBitmap(bitmap);
                        }
                    } else {
                        imageLoader.get(categoryUrl, new ImageLoader.ImageListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }

                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                if (response != null && response.getBitmap() != null) {
                                    viewHolderMain.catimage.setImageBitmap(response.getBitmap());
                                    FileOutputStream out = null;
                                    try {
                                        out = new FileOutputStream(DataFields.TempFolder + "/" + utils.hashString(categoryUrl));
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
                }
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
                viewHolderMain.time.setText(new Utils(context).convertLongToDate(Utils.convertCurrentTimeMillis(messages.getCreatedAt()), new SimpleDateFormat("HH:mm")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            viewHolderMain.time.setText(new Utils(context).convertLongToDate(Utils.convertCurrentTimeMillis(messages.getCreatedAt()), new SimpleDateFormat("HH:mm")));
            if (messages.getMessageType() == DataFields.LOCATION) {
                final String getMapURL = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=560x240&markers=size:mid|color:red|"
                        + messageValues.getLat()
                        + ","
                        + messageValues.getLng()
                        + "&sensor=false";
                if (messageValues.getLat() != 0.00 && messageValues.getLng() != 0.00) {
                    viewHolderMain.viewLayout.setVisibility(View.VISIBLE);
                    viewHolderMain.mapView.setVisibility(View.VISIBLE);

                    String path = DataFields.TempFolder + "/" + utils.hashString(getMapURL);
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        viewHolderMain.mapView.setImageBitmap(myBitmap);
                    } else {
                        imageLoader.get(getMapURL, new ImageLoader.ImageListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }

                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                if (response != null && response.getBitmap() != null) {
                                    viewHolderMain.mapView.setImageBitmap(response.getBitmap());
                                    FileOutputStream out = null;
                                    try {
                                        out = new FileOutputStream(DataFields.TempFolder + "/" + utils.hashString(getMapURL));
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
                }

                if (messages.getDirection() == DataFields.INCOMING) {
                    GradientDrawable gd = new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[]{Color.parseColor(color), Color.parseColor(color)});
                    gd.setCornerRadius(5f);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolderMain.viewLayout.setBackground(gd);
                    } else {
                        viewHolderMain.viewLayout.setBackgroundDrawable(gd);
                    }
                } else {
                    GradientDrawable gd = new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[]{context.getResources().getColor(R.color.white), context.getResources().getColor(R.color.white)});
                    gd.setCornerRadius(5f);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolderMain.viewLayout.setBackground(gd);
                    } else {
                        viewHolderMain.viewLayout.setBackgroundDrawable(gd);
                    }
                }
            }

            if (messages.getMessageType() == DataFields.IMAGE) {
                viewHolderMain.viewLayout.setVisibility(View.VISIBLE);
                viewHolderMain.imageView.setVisibility(View.VISIBLE);
                String path = DataFields.TempFolder + "/" + utils.hashString(messageValues.getUrl());
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    viewHolderMain.imageView.setImageBitmap(myBitmap);
                } else {
                    if (messageValues.getUrl().matches("data:image.*base64.*")) {
                        String base_64_source = messageValues.getUrl().replaceAll("data:image.*base64", "");
                        byte[] data = Base64.decode(base_64_source, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        if (bitmap != null) {
                            FileOutputStream out = null;
                            try {
                                out = new FileOutputStream(DataFields.TempFolder + "/" + utils.hashString(messageValues.getUrl()));
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
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
                            viewHolderMain.imageView.setImageBitmap(bitmap);
                        }
                    } else {
                        imageLoader.get(messageValues.getUrl(), new ImageLoader.ImageListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }

                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                if (response != null && response.getBitmap() != null) {
                                    viewHolderMain.imageView.setImageBitmap(response.getBitmap());
                                    FileOutputStream out = null;
                                    try {
                                        out = new FileOutputStream(DataFields.TempFolder + "/" + utils.hashString(messageValues.getUrl()));
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
                }

                viewHolderMain.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PaymentFragment paymentFragment = new PaymentFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("url", messageValues.getUrl());
                        bundle.putBoolean("image", true);
                        paymentFragment.setArguments(bundle);
                        ((BaseActivity) context).startFragment(R.id.body, paymentFragment);
                    }
                });

                if (messages.getDirection() == DataFields.INCOMING) {
                    GradientDrawable gd = new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[]{Color.parseColor(color), Color.parseColor(color)});
                    gd.setCornerRadius(5f);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolderMain.viewLayout.setBackground(gd);
                    } else {
                        viewHolderMain.viewLayout.setBackgroundDrawable(gd);
                    }
                } else {
                    GradientDrawable gd = new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[]{context.getResources().getColor(R.color.white), context.getResources().getColor(R.color.white)});
                    gd.setCornerRadius(5f);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolderMain.viewLayout.setBackground(gd);
                    } else {
                        viewHolderMain.viewLayout.setBackgroundDrawable(gd);
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
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    viewHolderMain.mainLayout.setGravity(Gravity.LEFT);
                } else {
                    viewHolderMain.mainLayout.setGravity(Gravity.START);
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
