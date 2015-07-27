package com.supergenieapp.android.Slides;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.supergenieapp.android.Activities.RegisterActivity;
import com.supergenieapp.android.CustomViews.Misc.AppIntro;
import com.supergenieapp.android.R;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class WalkThroughActivity extends AppIntro {
    MixpanelAPI mixpanelAPI;

    @Override
    public void init(Bundle savedInstanceState) {
        mixpanelAPI = MixpanelAPI.getInstance(this, getString(R.string.projectId));
        addSlide(SampleSlide.newInstance(R.layout.slide1));
        addSlide(SampleSlide.newInstance(R.layout.slide2));
        addSlide(SampleSlide.newInstance(R.layout.slide3));
    }

    private void loadMainActivity() {
        Intent intent = new Intent(WalkThroughActivity.this, RegisterActivity.class);
        intent.putExtra("page", "Register");
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onSkipPressed() {
        mixpanelAPI.track("Walk Thru Skip Pressed");
        loadMainActivity();
    }

    @Override
    public void onDonePressed() {
        mixpanelAPI.track("Walk Thru Done Pressed");
        loadMainActivity();
    }

    public void getStarted(View v) {
        loadMainActivity();
    }
}
