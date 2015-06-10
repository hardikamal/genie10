package com.getgenieapp.android.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.MenuItem;

import com.getgenieapp.android.CustomViews.SnackBar;
import com.getgenieapp.android.Fragments.RegisterFragment;
import com.getgenieapp.android.Fragments.VerifyFragment;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Register;
import com.getgenieapp.android.Objects.Verify;
import com.getgenieapp.android.R;

public class RegisterActivity extends GenieBaseActivity implements RegisterFragment.onRegister, VerifyFragment.onVerify {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private BroadcastReceiver myBroadcastReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (!intent.getAction().equals(SMS_RECEIVED)) return;
                    final Bundle bundle = intent.getExtras();
                    try {
                        if (bundle != null) {
                            final Object[] pdusObj = (Object[]) bundle.get("pdus");
                            for (int i = 0; i < pdusObj.length; i++) {
                                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                                String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                                String senderNum = phoneNumber;
                                String message = currentMessage.getDisplayMessageBody();
                                String phrase = "Code : ";
                                if(message.contains(phrase))
                                {
                                    int index = message.indexOf(phrase);
                                    String code = message.substring(index + phrase.length());
                                    System.out.println(code);
                                    code = code.trim();
                                    mBus.post(code);
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        startFragment(R.id.body, new RegisterFragment());
    }

    @Override
    public void onSuccess(Register register) {
        sharedPreferences.edit().putString("token", register.getToken());
        startFragment(R.id.body, new VerifyFragment());
    }

    @Override
    public void onError(Register register) {
        SnackBar snackBar = new SnackBar(this, getString(R.string.unexpectederror));
        snackBar.show();
    }

    @Override
    public void onSuccess(Verify verify) {

    }

    @Override
    public void onError(Verify verify) {
        SnackBar snackBar = new SnackBar(this, "Server Error Try Again");
        snackBar.show();
    }
}
