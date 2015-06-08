package com.getgenieapp.android.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.getgenieapp.android.Fragments.RegisterFragment;
import com.getgenieapp.android.Fragments.VerifyFragment;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Register;
import com.getgenieapp.android.Objects.Verify;
import com.getgenieapp.android.R;

public class RegisterActivity extends GenieBaseActivity implements RegisterFragment.onRegister, VerifyFragment.onVerify {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Fragment fragment = new RegisterFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onSuccess(Register register) {

    }

    @Override
    public void onError(Register register) {

    }

    @Override
    public void onSuccess(Verify verify) {

    }

    @Override
    public void onError(Verify verify) {

    }
}
