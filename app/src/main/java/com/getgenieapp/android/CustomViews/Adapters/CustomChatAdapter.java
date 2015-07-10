package com.getgenieapp.android.CustomViews.Adapters;

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
import com.getgenieapp.android.Objects.MessageValues;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;
import com.google.android.gms.maps.MapView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

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
        @InjectView(R.id.textTop)
        TextView textTop;
        @InjectView(R.id.textBottom)
        TextView textBottom;
        @InjectView(R.id.textTime)
        TextView textTime;
        public ViewTreeObserver vto;

        public ViewHolderChat(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
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
        final Messages messages = messagesList.get(position);
        final ViewHolderChat viewHolderMain = (ViewHolderChat) holder;
        final MessageValues messageValues = messages.getMessageValues();
        viewHolderMain.textTop.setText(messageValues.getText());
        viewHolderMain.showText(messageValues.getText());
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
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                sourceDrawable = context.getResources().getDrawable(R.drawable.money_genie_ticket_main, context.getTheme());
//            } else {
//                sourceDrawable = context.getResources().getDrawable(R.drawable.money_genie_ticket_main);
//            }

//            Bitmap sourceBitmap =utils.convertDrawableToBitmap(sourceDrawable);
//
//            layout.setBackground(new BitmapDrawable(context.getResources(), utils.changeImageColor(sourceBitmap, colorCode)));
        }
    }
}
