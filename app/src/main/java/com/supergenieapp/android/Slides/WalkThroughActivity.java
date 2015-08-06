package com.supergenieapp.android.Slides;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.supergenieapp.android.Activities.RegisterActivity;
import com.supergenieapp.android.CustomViews.Misc.AppIntro;
import com.supergenieapp.android.R;

public class WalkThroughActivity extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(SampleSlide.newInstance(R.layout.slide1));
        addSlide(SampleSlide.newInstance(R.layout.slide2));
        addSlide(SampleSlide.newInstance(R.layout.slide3));
    }

    private void loadMainActivity() {
        if (getIntent().getExtras() == null) {
            Intent intent = new Intent(WalkThroughActivity.this, RegisterActivity.class);
            intent.putExtra("page", "Register");
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    public void getStarted(View v) {
        loadMainActivity();
    }
}
