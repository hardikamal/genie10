package com.getgenieapp.android.Extras;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class Utils {
    Context context;

    public Utils(Context context) {
        this.context = context;
    }

    NetworkInfo info;
    ConnectivityManager cm;

    public boolean isConnectedMobile() {
        if (isPhone()) {
            info = getNetworkInfo();
            return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
        }
        return isConnectedWifi();
    }

    public boolean isConnectedWifi() {
        info = getNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public NetworkInfo getNetworkInfo() {
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public boolean isPhone() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getPhoneType() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
