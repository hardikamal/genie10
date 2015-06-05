package com.getgenieapp.android.Activities;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.getgenieapp.android.CustomViews.SplashScreenProgressCircle;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SplashScreenActivity extends Activity {

    @InjectView(R.id.version)
    TextView version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.inject(this);

        try {
            version.setText(this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            version.setVisibility(View.GONE);
        }

    }
}
