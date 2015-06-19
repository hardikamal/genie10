package com.getgenieapp.android.Activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.getgenieapp.android.Extras.GraphicsUtil;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UserProfileActivity extends GenieBaseActivity {
    @InjectView(R.id.orderdetails)
    Button orderdetails;
    @InjectView(R.id.name)
    EditText name;
    @InjectView(R.id.email)
    EditText email;
    @InjectView(R.id.address)
    EditText address;
    @InjectView(R.id.userPicture)
    ImageButton userPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.inject(this);
        getWindow().setBackgroundDrawableResource(R.drawable.wallpaper_wallpaper_small);
        logging.LogV("User Profile Activity");
        name.setText("Genie Admin");
        email.setText("admin@getgenieapp.com");
        address.setText("India");

//        userPicture.setImageBitmap(new GraphicsUtil().getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_white_24dp), R.dimen.profilepic));
        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @OnClick(R.id.orderdetails)
    public void onClickOrderDetails() {
        logging.LogV("Open Order Details");
        startActivity(new Intent(this, OrderDetailsActivity.class));
    }

    @OnClick(R.id.orderdetailsbutton)
    public void onClickOrderDetailsButton() {
        logging.LogV("Open Order Details");
        startActivity(new Intent(this, OrderDetailsActivity.class));
    }
}


