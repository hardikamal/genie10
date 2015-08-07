package com.supergenieapp.android.CustomViews.Adapters;

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
import com.supergenieapp.android.Activities.LocationActivity;
import com.supergenieapp.android.GenieApplication;
import com.supergenieapp.android.Objects.FavValues;
import com.supergenieapp.android.Objects.Messages;
import com.supergenieapp.android.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Raviteja on 7/10/2015.
 */
public class CustomPlaceAdapter extends RecyclerView.Adapter {
    private ArrayList<FavValues> messagesList;
    private Context context;

    public ArrayList<FavValues> getMessages() {
        return messagesList;
    }

    public void setMessages(ArrayList<FavValues> messagesList) {
        this.messagesList = messagesList;
    }

    public CustomPlaceAdapter(ArrayList<FavValues> messagesList, Context context) {
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FavValues messages = messagesList.get(position);
        final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;
        viewHolderMain.title.setText(messages.getName());
        viewHolderMain.address.setText(messages.getText());
        viewHolderMain.main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((LocationActivity) context).deletePlace(messagesList.get(position));
//                Crouton.makeText((Activity) context, messagesList.get(position).getName() + " is Deleted", Style.INFO, R.id.list).show();
                return true;
            }
        });
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
