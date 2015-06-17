package com.getgenieapp.android.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.getgenieapp.android.Extras.Utils;
import com.getgenieapp.android.GenieApplication;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;
import com.google.android.gms.maps.MapView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Raviteja on 6/16/2015.
 */
public class CustomChatAdapter extends RecyclerView.Adapter {
    private ArrayList<Messages> messagesList;
    private Context context;
    private ImageLoader imageLoader;
    Messages messages;

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public CustomChatAdapter(ArrayList<Messages> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
        this.imageLoader = GenieApplication.getInstance().getImageLoader();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolderChat extends RecyclerView.ViewHolder {
        public TextView textTop;
        public TextView textBottom;
        public TextView textTime;
        public ImageView image;
        public MapView mapView;
        public LinearLayout linearLayout;
        public ViewTreeObserver vto;

        public ViewHolderChat(View itemView) {
            super(itemView);
            mapView = (MapView) itemView.findViewById(R.id.mapview);
            image = (ImageView) itemView.findViewById(R.id.imageview);
            textTop = (TextView) itemView.findViewById(R.id.textTop);
            textBottom = (TextView) itemView.findViewById(R.id.textBottom);
            textTime = (TextView) itemView.findViewById(R.id.textTime);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.color_layout);

            vto = itemView.getViewTreeObserver();
        }

        void showText(final String lastmessage) {
            if (vto.isAlive()) {
                vto.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        textTop.setText(lastmessage + " 23:59");
                        Layout layout = textTop.getLayout();
                        int lastLine = layout.getLineCount();
                        if (lastLine > 1) {
                            int lastLineIndex = layout.getLineStart(lastLine - 1);
                            if (lastLineIndex <= lastmessage.length())
                                textTop.setText(lastmessage.substring(0, lastLineIndex));
                            else
                                textTop.setText(lastmessage);
                            if (lastLineIndex > lastmessage.length())
                                textBottom.setText("");
                            else
                                textBottom.setText(lastmessage.substring(lastLineIndex, lastmessage.length()));
                        } else {
                            textTop.setText("");
                            textTop.setVisibility(View.GONE);
                            textBottom.setText(lastmessage);
                        }
                    }
                });
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Messages currentMessage = messagesList.get(viewType);
        if (currentMessage.getDirection() == 1) {
            return new ViewHolderChat(LayoutInflater.from(context).inflate(R.layout.incoming, parent, false));
        } else {
            return new ViewHolderChat(LayoutInflater.from(context).inflate(R.layout.outgoing, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

//        final Values value = values.get(position);
//        final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;
//        viewHolderMain.text3.setText(value.getDate());
//        setBackGround(viewHolderMain.linearLayout, value.getColor());
//
//        if (value.getUrl() != null) {
//
//            viewHolderMain.image.setVisibility(View.VISIBLE);
//            viewHolderMain.image.setAlpha(0.25f);
//
//            Cache cache = AppController.getInstance().getRequestQueue().getCache();
//            Cache.Entry entry = cache.get(value.getUrl());
//            if (entry != null) {
//                try {
//                    String data = new String(entry.data, "UTF-8");
//                    if (StringToBitMap(data) != null)
//                        viewHolderMain.image.setImageBitmap(StringToBitMap(data));
//                    else {
//                        imageLoader.get(value.getUrl(), new ImageLoader.ImageListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Log.e("", "Image Load Error: " + error.getMessage());
//                            }
//
//                            @Override
//                            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
//                                if (response.getBitmap() != null) {
//                                    viewHolderMain.image.setImageBitmap(response.getBitmap());
//                                }
//                            }
//                        });
//                    }
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                imageLoader.get(value.getUrl(), new ImageLoader.ImageListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("", "Image Load Error: " + error.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
//                        if (response.getBitmap() != null) {
//                            viewHolderMain.image.setImageBitmap(response.getBitmap());
//                        }
//                    }
//                });
//            }
//        }
//        viewHolderMain.showText(value.getValue());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void setBackGround(LinearLayout layout, String color) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Utils utils = new Utils(context);
            int colorCode = Color.parseColor(color);
            Drawable sourceDrawable;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                sourceDrawable = context.getResources().getDrawable(R.drawable.money_genie_ticket_main, context.getTheme());
            } else {
                sourceDrawable = context.getResources().getDrawable(R.drawable.money_genie_ticket_main);
            }

            Bitmap sourceBitmap =utils.convertDrawableToBitmap(sourceDrawable);

            layout.setBackground(new BitmapDrawable(context.getResources(), utils.changeImageColor(sourceBitmap, colorCode)));
        }
    }
}
