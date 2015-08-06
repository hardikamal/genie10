package com.supergenieapp.android.CustomViews.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.supergenieapp.android.Activities.BaseActivity;
import com.supergenieapp.android.Activities.FeedBackActivity;
import com.supergenieapp.android.Activities.OrderDetailsActivity;
import com.supergenieapp.android.Activities.UserProfileActivity;
import com.supergenieapp.android.Extras.Utils;
import com.supergenieapp.android.Objects.MenuItems;
import com.supergenieapp.android.R;
import com.supergenieapp.android.Slides.WalkThroughActivity;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Raviteja on 8/6/2015.
 */
public class CustomMenuAdapter extends RecyclerView.Adapter {
    private ArrayList<MenuItems> menuItems;
    private Context context;
    private Utils utils;
    private int color;

    public CustomMenuAdapter(Context context) {
        this.context = context;
        this.utils = new Utils(context);
        this.menuItems = setUpValues();
        this.color = context.getResources().getColor(R.color.colorPrimary);
    }

    private ArrayList<MenuItems> setUpValues() {
        ArrayList<MenuItems> menu = new ArrayList<>();
        String[] titles = new String[]{
                "Invite Friends", "Profile", "Order History", "Rate us", "Feedback", "Contact us", "Help"
        };
        MaterialDrawableBuilder.IconValue[] images = new MaterialDrawableBuilder.IconValue[]{
                MaterialDrawableBuilder.IconValue.ACCOUNT_MULTIPLE, MaterialDrawableBuilder.IconValue.ACCOUNT, MaterialDrawableBuilder.IconValue.TRUCK, MaterialDrawableBuilder.IconValue.STAR,
                MaterialDrawableBuilder.IconValue.MESSAGE_TEXT, MaterialDrawableBuilder.IconValue.EMAIL, MaterialDrawableBuilder.IconValue.HELP
        };
        if (titles.length == images.length) {
            for (int i = 0; i < titles.length; i++) {
                menu.add(new MenuItems(titles[i], images[i]));
            }
        }
        return menu;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class ViewHolderMain extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.image)
        MaterialIconView image;
        @InjectView(R.id.text)
        TextView text;
        Context localContext;

        public ViewHolderMain(View itemView, Context localContext) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            this.localContext = localContext;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (text.getText().equals("Profile")) {
                localContext.startActivity(new Intent(localContext, UserProfileActivity.class));
                if (localContext instanceof UserProfileActivity || localContext instanceof OrderDetailsActivity) {
                    ((Activity) localContext).finish();
                }
            } else if (text.getText().equals("Order History")) {
                localContext.startActivity(new Intent(localContext, OrderDetailsActivity.class));
                if (localContext instanceof UserProfileActivity || localContext instanceof OrderDetailsActivity) {
                    ((Activity) localContext).finish();
                }
            } else if (text.getText().equals("Rate us")) {
                localContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.supergenieapp.android")));
            } else if (text.getText().equals("Invite Friends")) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, localContext.getString(R.string.trygenie));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, localContext.getString(R.string.bodytext));
                localContext.startActivity(Intent.createChooser(sharingIntent, localContext.getString(R.string.shareus)));
            } else if (text.getText().equals("Contact us")) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", localContext.getString(R.string.supportemailaddress), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, localContext.getString(R.string.subjecttoemail));
                emailIntent.putExtra(Intent.EXTRA_TEXT, new Utils(localContext).getDeviceInformationFormEmail());
                localContext.startActivity(Intent.createChooser(emailIntent, localContext.getString(R.string.sendemail)));
            } else if (text.getText().equals("Help")) {
                Intent intent = new Intent(localContext, WalkThroughActivity.class);
                intent.putExtra("finish", true);
                localContext.startActivity(intent);
            } else if (text.getText().equals("Feedback")) {
                Intent intent = new Intent(localContext, FeedBackActivity.class);
                localContext.startActivity(intent);
            }
// // TODO: 8/6/2015
            ((BaseActivity) localContext).closeMenu();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.menuitem, parent, false), context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MenuItems menuItem = menuItems.get(position);
        final ViewHolderMain viewHolderMain = (ViewHolderMain) holder;
        viewHolderMain.image.setIcon(menuItem.getImage());
        viewHolderMain.text.setText(menuItem.getTitle());
    }
}