package com.getgenieapp.android.Extras;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetDate {
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
}
