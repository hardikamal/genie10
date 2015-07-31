package com.supergenieapp.android.Extras;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import com.supergenieapp.android.GenieApplication;
import com.supergenieapp.android.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(convertedDate);
    }

    public long convertLongToong(long date) {
        Date convertedDate = new Date(date);
        if (date < 86400000 * 1000L) {
            convertedDate = new Date(date * 1000L);
        }
        return convertedDate.getTime();
    }

    public String getIfItsToday(String date, SimpleDateFormat simpleDateFormat) {
        Date convertedDate = new Date(System.currentTimeMillis());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        if (date.equals(simpleDateFormat.format(convertedDate))) {
            return context.getString(R.string.today);
        }
        return date;
    }

    public static String hashString(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));

            return convertByteArrayToHexString(hashedBytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            return String.valueOf(message.hashCode());
        }
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    public static String getLogDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_HH_mm");
        Date date = new Date();
        String sDate = dateFormat.format(date);
        return sDate;
    }

    public String convertLongToDate(long unixTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime * 1000);

        if (isSameDay(calendar, Calendar.getInstance())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm a");
            Date time = new Date(unixTime * 1000);
            return " " + dateFormat.format(time);
        } else if (isYesterday(calendar, Calendar.getInstance())) {
            return " Yesterday";
        } else {
            SimpleDateFormat dateFormatToday = new SimpleDateFormat("dd/MM/yyyy");
            Date time = new Date(unixTime * 1000);
            return " " + dateFormatToday.format(time);
        }
    }

    private boolean isYesterday(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == (cal2.get(Calendar.DAY_OF_YEAR) - 1));
    }

    public boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String sDate = dateFormat.format(date);
        return sDate;
    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long convertCurrentTimeMillis(long time) {
        return time;
    }

    public String getDeviceInformationFormEmail() {
        String emailBody = context.getString(R.string.hellosupergenie) + getDeviceSerialNumber();
        emailBody += context.getString(R.string.handsetinfo) + Build.MANUFACTURER + ", " + Build.BRAND + ", " + Build.MODEL;
        emailBody += context.getString(R.string.androidosinfo) + Build.VERSION.SDK_INT;
        PackageInfo pInfo = null;
        try {
            pInfo = GenieApplication.getInstance().getPackageManager().getPackageInfo(GenieApplication.getInstance().getPackageName(), 0);
            emailBody += context.getString(R.string.supergenieversion) + pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return emailBody;
    }
}
