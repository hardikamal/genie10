package com.getgenieapp.android.CustomViews.Adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
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
        @InjectView(R.id.text)
        TextView text;
        @InjectView(R.id.time)
        TextView time;
        @InjectView(R.id.tick)
        ImageView tick;

        public ViewHolderMain(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolderMain viewHolderMain;
//        if (messagesList.get(viewType).getDirection() == 1)
        if (viewType % 2 == 0)
            viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.incoming, parent, false));
        else
            viewHolderMain = new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.outgoing, parent, false));
        return viewHolderMain;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Messages messages = messagesList.get(position);
        final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;
        final MessageValues messageValues = messages.getMessageValues();

//        if (messagesList.get(position).getDirection() == 1) {
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void TextMeasure(String text,
                             TextView tvl, TextView tvr) {

        int linesPerScreen = tvl.getHeight() / (tvl.getLineHeight() + (int) tvl.getLineSpacingExtra());

        Paint paint = tvl.getPaint();
        int textWidth = paint.breakText(text, 0, text.length(),
                true, tvl.getWidth(), null);

        int totalText = textWidth * linesPerScreen;
        String leftText = text.substring(0, totalText);
        String rightText = text.substring(totalText,
                text.length());
        tvl.setText(leftText);
        tvr.setText(rightText);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
