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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.getgenieapp.android.GenieApplication;
import com.getgenieapp.android.Objects.MessageValues;
import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Raviteja on 7/10/2015.
 */
public class CustomPlaceAdapter extends RecyclerView.Adapter {
    private ArrayList<MessageValues> messagesList;
    private Context context;

    public ArrayList<MessageValues> getMessages() {
        return messagesList;
    }

    public void setMessages(ArrayList<MessageValues> messagesList) {
        this.messagesList = messagesList;
    }

    public CustomPlaceAdapter(ArrayList<MessageValues> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolderMain extends RecyclerView.ViewHolder {
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.address)
        TextView address;
        @InjectView(R.id.main)
        LinearLayout main;

        public ViewHolderMain(View itemView, Context context) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.savedplace, parent, false), context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MessageValues messages = messagesList.get(position);
        final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;
        viewHolderMain.title.setText(messages.getName());
        viewHolderMain.address.setText(messages.getText());
        viewHolderMain.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("lat", messages.getLat());
                intent.putExtra("lng", messages.getLng());
                intent.putExtra("address", messages.getText());
                ((Activity) context).setResult(1, intent);
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
