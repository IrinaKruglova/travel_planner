package com.toptal.travelplanner.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Util {

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        return dateFormat.format(date);
    }

    public static long getDaysLeft(Date date) {
        return getDateDiff(Calendar.getInstance().getTime(), date, TimeUnit.DAYS);
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillis = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillis,TimeUnit.MILLISECONDS);
    }
}
