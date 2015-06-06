package com.getgenieapp.android.Extras;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetDate {
    public static String getLogDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_HH_mm");
        Date date = new Date();
        String sDate = dateFormat.format(date);
        return sDate;
    }
}
