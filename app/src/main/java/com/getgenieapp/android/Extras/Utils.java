package com.getgenieapp.android.Extras;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import com.getgenieapp.android.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
    Context context;

    public Utils(Context context) {
        this.context = context;
    }

    NetworkInfo info;
    ConnectivityManager cm;

    public String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "91";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

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

    /**
     * Convert Dp to Pixel
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static int getRelativeTop(View myView) {
//	    if (myView.getParent() == myView.getRootView())
        if (myView.getId() == android.R.id.content)
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    public static int getRelativeLeft(View myView) {
//	    if (myView.getParent() == myView.getRootView())
        if (myView.getId() == android.R.id.content)
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    public Bitmap changeImageColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }


    public Drawable covertBitmapToDrawable(Context context, Bitmap bitmap) {
        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
        return d;
    }

    public Bitmap convertDrawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public String getDeviceSerialNumber() {
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tManager.getDeviceId();
    }

//    public String getPhoneNumber()
//    {
//        TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//        return tManager.getLine1Number();
//    }

    public String getMacId() {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    public boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public String convertLongToDate(long date, SimpleDateFormat simpleDateFormat) {
        Date convertedDate = new Date(date);
        if (date < 86400000 * 1000L) {
            convertedDate = new Date(date * 1000L);
        }
        System.out.print(TimeZone.getDefault().toString());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(convertedDate);
    }
}
